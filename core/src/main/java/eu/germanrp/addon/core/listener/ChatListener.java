package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.Setter;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;

public class ChatListener {

    private final GermanRPAddon addon;
    private boolean justJoined;
    private boolean chatShowsMemberInfo;
    private boolean wanted;
    private boolean bounty;
    private boolean wasAFK;

    @Setter
    private int emptyMessages;
    private boolean memberInfoWasShown;

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
                        if (this.wasAFK) {
                            Laby.references().chatExecutor().chat("/afk");
                        }
                        this.memberInfoWasShown = true;
                        this.addon.getPlayer().setPlayerFaction(Faction.NONE);
                    }
                    case "Rettungsdienst" -> this.addon.getPlayer().setPlayerFaction(Faction.RETTUNGSDIENST);
                    case "Rousseau Familie" -> this.addon.getPlayer().setPlayerFaction(Faction.ROUSSEAU);
                    case "Polizei" -> this.addon.getPlayer().setPlayerFaction(Faction.POLIZEI);
                    case "Camorra" -> this.addon.getPlayer().setPlayerFaction(Faction.CAMORRA);
                    case "The Establishment" -> this.addon.getPlayer().setPlayerFaction(Faction.ESTABLISHMENT);
                    case "MT-Fashion" -> this.addon.getPlayer().setPlayerFaction(Faction.MTFASHION);
                    case "Presseagentur" ->this.addon.getPlayer().setPlayerFaction(Faction.PRESSE);
                    case "Sinaloa Kartell" -> this.addon.getPlayer().setPlayerFaction(Faction.SINALOAKARTELL);
                    case "Medellín Kartell" -> this.addon.getPlayer().setPlayerFaction(Faction.KARTELL);
                    case "VanceCity Investment" -> this.addon.getPlayer().setPlayerFaction(Faction.VCI);
                    case "Cartel de Cayo Perico" -> this.addon.getPlayer().setPlayerFaction(Faction.KARTELLCAYOPERICO);
                    case "Iron Serpents" -> this.addon.getPlayer().setPlayerFaction(Faction.IRON_SERPENTS);
                    case "Bratva Gang" -> this.addon.getPlayer().setPlayerFaction(Faction.BRATVA_GANG);
                    default -> {
                        this.addon.getPlayer().setPlayerFaction(Faction.NONE);
                        this.addon.getPlayer().sendErrorMessage("Deine Fraktion wurde nicht gefunden... Bitte hier reporten:");
                        this.addon.getPlayer().sendErrorMessage("https://germanrp.eu/forum/index.php?board/296-bug-labymod-addon/");
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
                this.emptyMessages++;
                if (this.emptyMessages > 2) {
                    event.setCancelled(true);
                }
            }
        }
        Faction faction = this.addon.getPlayer().getPlayerFaction();
        if (faction == null) {
            return;
        }
        if (faction == Faction.NONE) {
            if (!this.wasAFK) {
                this.justJoined = false;
                return;
            }
            return;
        }
        if (TITLE_FACTION_MEMBER_LIST.getPattern().matcher(message).find()) {
            event.setCancelled(true);
            this.chatShowsMemberInfo = true;
            return;
        }

        if (this.chatShowsMemberInfo) {
            event.setCancelled(true);
            final Matcher matcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
            if (!matcher.find()) {
                if (!message.startsWith("        (Insgesamt: ") || !message.endsWith(" verfügbar)")) {
                    return;
                }
                this.memberInfoWasShown = true;
                this.chatShowsMemberInfo = false;
                return;
            }
            this.addon.getNameTagService().getMembers().add(matcher.group(1).replace("[GR]", ""));
            return;
        }
        switch (faction.getType()) {
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
                }
            }
            case NEUTRAL,MEDIC -> {
                if (!this.memberInfoWasShown) return;
                if (this.wasAFK) {
                    this.addon.getPlayer().sendServerMessage("/afk");
                    this.wasAFK = false;
                    return;
                }
                this.memberInfoWasShown = false;
                this.justJoined = false;
            }
        }
    }

    @Subscribe
    public void onChatReceiveListsChange(ChatReceiveEvent event) {

        Faction faction = this.addon.getPlayer().getPlayerFaction();
        if (faction == null || !justJoined) {
            return;
        }
        String message = event.chatMessage().getPlainText();
        switch (faction.getType()) {
            case CRIME -> {

                final Matcher nametagBountyUpdateList = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);

                if (message.contentEquals("            KOPFGELDER")) {
                    this.bounty = true;
                    return;
                }
                if (this.bounty) {
                    if(nametagBountyUpdateList.matches()){
                        this.addon.getNameTagService().getBounties().add(nametagBountyUpdateList.group(1).replace("[GR]",""));
                    }

                }

            }
            case STAAT -> {
                final Matcher nametagWantedRemoveMatcher = WANTED_REMOVE.getPattern().matcher(message);
                final Matcher nametagWantedAddMatcher = WANTED_ADD.getPattern().matcher(message);
                final Matcher nametagWantedInJailedMatcher = WANTED_INJAILED.getPattern().matcher(message);
                final Matcher wantedListTitle = TITLE_WANTED_LIST.getPattern().matcher(message);
                final Matcher wantedListUpdate = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);


                if (nametagWantedRemoveMatcher.find()) {
                    this.addon.getNameTagService().getWantedPlayers().remove(nametagWantedRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                if (nametagWantedAddMatcher.find()) {
                    this.addon.getNameTagService().getWantedPlayers().add(nametagWantedAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }
                if (nametagWantedInJailedMatcher.find()) {
                    this.addon.getNameTagService().getWantedPlayers().add(nametagWantedAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }
                if (wantedListTitle.find()){
                    this.wanted = true;
                    return;
                }
                if (this.wanted){
                    if (wantedListUpdate.matches()){
                        this.addon.getNameTagService().getWantedPlayers().add(wantedListUpdate.group(1).replace("[GR]",""));
                    }
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
            if (this.addon.getPlayer().getPlayerXP() >= this.addon.getPlayer().getPlayerNeededXP()){
                this.addon.getPlayer().setPlayerXP(this.addon.getPlayer().getPlayerXP()-this.addon.getPlayer().getPlayerNeededXP());
            }
        }
    }

    @Subscribe
    public void onCommandSend(ChatMessageSendEvent event) {
        if(event.isMessageCommand()){
            String message = event.getMessage();
            String[] messageStart = message.split(" ");
            event.changeMessage(messageStart[0].toLowerCase() + message.replace(messageStart[0], ""));
        }
    }

    @Subscribe
    public void onPanicDeactivate(ChatReceiveEvent event) {
        if(justJoined) return;
        String message  = event.chatMessage().getPlainText();
        if(!GermanRPAddon.getInstance().getPlayer().getPlayerFaction().equals(Faction.POLIZEI)) return;
        Matcher matcher = PANIC_DEACTIVATE.getPattern().matcher(message);
         if(!matcher.find()) return;
        GermanRPAddon.getInstance().getPlayer().setPlayPanic(false);

    }
}
