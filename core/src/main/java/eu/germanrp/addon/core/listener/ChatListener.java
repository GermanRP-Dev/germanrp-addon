package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.FactionName;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.Setter;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.BOUNTY_ADD;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.BOUNTY_MEMBER_WANTED_LIST_ENTRY;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.BOUNTY_REMOVE;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.DARK_LIST_ADD;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.DARK_LIST_ENTRY;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.DARK_LIST_REMOVE;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.FRAKTION_NAME_STATS;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.TITLE_FACTION_MEMBER_LIST;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.TITLE_WANTED_LIST;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.WANTED_ADD;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.WANTED_REMOVE;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.XP_ADD_CHAT;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.XP_READER_STATS;

public class ChatListener {

    private final GermanRPAddon addon;
    private boolean justJoined;
    private boolean faction;
    private boolean wanted;
    private boolean bounty;
    private boolean wasAFK;

    @Setter
    private int emptyMessages;

    public ChatListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onGRJoin(JustJoinedEvent event) {
        this.justJoined = true;
    }

    @Subscribe
    public void onChatReceiveJustJoined(ChatReceiveEvent event) {
        if (!this.justJoined) {
            return;
        }

        String message = event.chatMessage().getPlainText();
        if (message.startsWith("► [System] ") && !message.endsWith("anwesend.")) {
            event.setCancelled(true);
            Matcher matcher = XP_READER_STATS.getPattern().matcher(message);
            if (matcher.find()) {
                this.addon.getPlayer().setPlayerNeededXP(Integer.parseInt(matcher.group(2)));
                this.addon.getPlayer().setPlayerXP(Integer.parseInt(matcher.group(1)));
            }
            matcher = FRAKTION_NAME_STATS.getPattern().matcher(message);
            if (matcher.find()) {
                switch (matcher.group(1)) {
                    case "Keine (Zivilist)" -> {
                        if(this.wasAFK){
                            Laby.references().chatExecutor().chat("/afk");
                        }
                        this.addon.getPlayer().setPlayerFactionName(FactionName.NONE);
                    }
                    case "Rousseau Familie" -> this.addon.getPlayer().setPlayerFactionName(FactionName.ROUSSEAU);
                    case "Polizei" -> this.addon.getPlayer().setPlayerFactionName(FactionName.POLIZEI);
                    case "Camorra" -> this.addon.getPlayer().setPlayerFactionName(FactionName.CAMORRA);
                    case "The Establishment" -> this.addon.getPlayer().setPlayerFactionName(FactionName.ESTABLISHMENT);
                    case "MT-Fashion" -> this.addon.getPlayer().setPlayerFactionName(FactionName.MTFASHION);
                    case "Presseagentur" -> this.addon.getPlayer().setPlayerFactionName(FactionName.PRESSE);
                    case "Sinaloa Kartell" -> this.addon.getPlayer().setPlayerFactionName(FactionName.SINALOAKARTELL);
                    case "Medellín Kartell" -> this.addon.getPlayer().setPlayerFactionName(FactionName.KARTELL);
                    default -> {
                        this.addon.getPlayer().setPlayerFactionName(FactionName.NONE);
                        this.addon.getPlayer().sendErrorMessage("Deine Fraktion wurde nicht gefunden... Bitte hier reporten:");
                        this.addon.getPlayer().sendErrorMessage("""
                                https://germanrp.eu/forum/index.php?thread/25432-germanrp-addon-labymod-4-addon/""");
                    }
                }
                this.addon.getServerJoinListener().onFactionNameGet();
                return;
            }
            return;
        }
        switch (message) {
            case "► [System] Du bist jetzt als abwesend markiert." -> event.setCancelled(true);
            case "► Verwende erneut \"/afk\", um den AFK-Modus zu verlassen." -> {
                event.setCancelled(true);
                this.justJoined = false;
            }
            case "► [System] Du bist jetzt wieder anwesend." -> {
                event.setCancelled(true);
                this.wasAFK = true;
            }
            case "" -> {
                emptyMessages++;
                if (emptyMessages > 2) {
                    event.setCancelled(true);
                }
            }
        }
        FactionName factionName = this.addon.getPlayer().getPlayerFactionName();
        if (factionName == null) {
            return;
        }
        if(factionName.equals(FactionName.NONE)) {
            if (!wasAFK){
                this.justJoined = false;
                return;
            }
            return;
        }
        if (TITLE_FACTION_MEMBER_LIST.getPattern().matcher(message).find()) {
            event.setCancelled(true);
            this.faction = true;
            return;
        }

        if (this.faction) {
            event.setCancelled(true);
            final Matcher matcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
            if (!matcher.find()) {
                if (!message.startsWith("        (Insgesamt ") || !message.endsWith(" verfügbar)")) {
                    return;
                }
                this.faction = false;
                return;
            }
            this.addon.getNameTagService().getMembers().add(matcher.group(1).replace("[GR]", ""));
        }
        switch (factionName.getType()) {
            case BADFRAK -> {
                if (message.startsWith("► [Darklist] ")) {
                    event.setCancelled(true);
                    final Matcher matcher = DARK_LIST_ENTRY.getPattern().matcher(message);
                    if (!matcher.find()) {
                        return;
                    }
                    this.addon.getNameTagService().getDarklist().add(matcher.group(1).replace("[GR]", ""));
                    return;
                }
                if (message.contentEquals("            KOPFGELDER")) {
                    event.setCancelled(true);
                    this.bounty = true;
                    return;
                }
                if (this.bounty) {
                    event.setCancelled(true);
                    final Matcher matcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
                    if (!matcher.find()) {
                        this.bounty = false;
                        if (this.wasAFK) {
                            this.addon.getPlayer().sendServerMessage("/afk");
                            this.wasAFK = false;
                            return;
                        }
                        this.justJoined = false;
                        return;
                    }
                    this.addon.getNameTagService().getBounties().add(matcher.group(1).replace("[GR]", ""));
                    return;
                }
            }

            case STAAT -> {
                if (TITLE_WANTED_LIST.getPattern().matcher(message).find()) {
                    event.setCancelled(true);
                    this.wanted = true;
                    return;
                }
                if (this.wanted) {
                    event.setCancelled(true);
                    final Matcher matcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
                    if (!matcher.find()) {
                        this.wanted = false;
                        if (this.wasAFK) {
                            this.addon.getPlayer().sendServerMessage("/afk");
                            this.wasAFK = false;
                            return;
                        }
                        this.justJoined = false;
                        return;
                    }
                    this.addon.getNameTagService().getWantedPlayers().add(matcher.group(1).replace("[GR]", ""));
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onChatReceiveListsChange(ChatReceiveEvent event) {

        FactionName factionName = this.addon.getPlayer().getPlayerFactionName();
        if (factionName == null) {
            return;
        }
        String message = event.chatMessage().getPlainText();
        switch (factionName.getType()) {
            case BADFRAK -> {
                final Matcher nametagDarkListAddMatcher = DARK_LIST_ADD.getPattern().matcher(message);

                if (nametagDarkListAddMatcher.find()) {
                    this.addon.getNameTagService().getDarklist().add(nametagDarkListAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagDarkListRemoveMatcher = DARK_LIST_REMOVE.getPattern().matcher(message);
                if (nametagDarkListRemoveMatcher.find()) {
                    this.addon.getNameTagService().getDarklist().remove(nametagDarkListRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagBountyAddMatcher = BOUNTY_ADD.getPattern().matcher(message);
                if (nametagBountyAddMatcher.find()) {
                    this.addon.getNameTagService().getBounties().add(nametagBountyAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagBountyRemoveMatcher = BOUNTY_REMOVE.getPattern().matcher(message);
                if (nametagBountyRemoveMatcher.find()) {
                    this.addon.getNameTagService().getBounties().remove(nametagBountyRemoveMatcher.group(1).replace("[GR]", ""));
                }
            }
            case STAAT -> {
                final Matcher nametagWantedRemoveMatcher = WANTED_REMOVE.getPattern().matcher(message);
                final Matcher nametagWantedAddMatcher = WANTED_ADD.getPattern().matcher(message);

                if (nametagWantedRemoveMatcher.matches()) {
                    this.addon.getNameTagService().getWantedPlayers().remove(nametagWantedRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                if (nametagWantedAddMatcher.matches()) {
                    this.addon.getNameTagService().getWantedPlayers().add(nametagWantedAddMatcher.group(1).replace("[GR]", ""));
                }
            }
        }
    }

    @Subscribe
    public void onChatReceiveUpdateStats(@NotNull ChatReceiveEvent event) {
        @NotNull String message = event.chatMessage().getPlainText();
        Matcher matcher = XP_ADD_CHAT.getPattern().matcher(message);
        if (matcher.find()) {
            String x = matcher.group(2);
            int i = 1;
            if (x.contains("2")) {
                i = 2;
            } else if (x.contains("3")) {
                i = 3;
            }
            this.addon.getPlayer().addPlayerXP(Integer.parseInt(matcher.group(1)) * i);
        }
    }
}
