package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.FactionName;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.AddonPlayer;
import eu.germanrp.addon.core.common.AddonVariables;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.MajorWidgetUpdateEvent;
import eu.germanrp.addon.core.widget.MajorEventWidget;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;


public class ChatListener {

    private final GermanRPAddon addon;
    private final MajorEventWidget majorEventWidget;
    private final AddonPlayer player;
    private final AddonVariables addonVariables;
    private boolean playerStats;
    private boolean justJoined;
    private boolean faction;
    private boolean wanted;
    private boolean bounty;
    private boolean wasAFK;

    private int emptyMessages;

    public ChatListener(GermanRPAddon addon) {
        this.addon = addon;
        this.majorEventWidget = this.addon.getMajorEventWidget();
        this.player = this.addon.getPlayer();
        this.addonVariables = this.addon.getVariables();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveMajorEvent(ChatReceiveEvent e){
        if(this.majorEventWidget.isMajorEvent()){
            return;
        }
        String m = e.chatMessage().getPlainText();
        final Matcher ApothekenRaubMatcher = APOTHEKEN_RAUB.getPattern().matcher(m);
        final Matcher ShopRaubMatcher = SHOP_RAUB.getPattern().matcher(m);
        final Matcher BombeStartMatcher = BOMBE_START.getPattern().matcher(m);
        final Matcher JuwelenRaubMatcher = JUWELEN_RAUB.getPattern().matcher(m);
        final Matcher HackangriffStartMatcher = HACKANGRIFF_START.getPattern().matcher(m);

        if(ShopRaubMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Shopraub", 3));
            return;
        }

        if (ApothekenRaubMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Apothekenraub", 8));
            return;
        }

        if (JuwelenRaubMatcher.find()) {
            Laby.fireEvent(new MajorWidgetUpdateEvent("Juwelenraub", 3));
            return;
        }

        if (BombeStartMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Bombe", 10));
            return;
        }
        if (e.chatMessage().getPlainText().equals("► Du nimmst am Hackangriff deiner Fraktion teil.")){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Hackangriff", 8));
            return;
        }
        if (HackangriffStartMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent(HackangriffStartMatcher.group(1), 8));
            return;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGRJoin(JustJoinedEvent event){
    this.justJoined = true;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveJustJoined(ChatReceiveEvent event) {
        if (!this.justJoined) {
            return;
        }

        String message = event.chatMessage().getPlainText();
        if (message.startsWith("► [System] ") && !message.endsWith("anwesend.")){
            event.setCancelled(true);
            Matcher matcher = XP_READER_STATS.getPattern().matcher(message);
            if (matcher.find()) {
                this.player.setPlayerNeededXP(Integer.parseInt(matcher.group(2)));
                this.player.setPlayerXP(Integer.parseInt(matcher.group(1)));
            }
            matcher = FRAKTION_NAME_STATS.getPattern().matcher(message);
            if(matcher.find()){
                switch (matcher.group(1)){
                    case "Keine (Zivilist)" -> this.player.setPlayerFactionName(FactionName.NONE);
                    case "Rousseau Familie" -> this.player.setPlayerFactionName(FactionName.ROUSSEAU);
                    case "Polizei" -> this.player.setPlayerFactionName(FactionName.POLIZEI);
                    case "Camorra" -> this.player.setPlayerFactionName(FactionName.CAMORRA);
                    case "The Establishment" -> this.player.setPlayerFactionName(FactionName.ESTABLISHMENT);
                    case "MT-Fashion" -> this.player.setPlayerFactionName(FactionName.MTFASHION);
                    case "Presseagentur" -> this.player.setPlayerFactionName(FactionName.PRESSE);
                    case "Sinaloa Kartell" -> this.player.setPlayerFactionName(FactionName.SINALOAKARTELL);
                    case "Medellín Kartell" -> this.player.setPlayerFactionName(FactionName.KARTELL);
                    default ->{
                        this.player.setPlayerFactionName(FactionName.NONE);
                        this.player.sendErrorMessage("Deine Fraktion wurde nicht gefunden... Bitte hier reporten:");
                        this.player.sendErrorMessage("""
                                https://germanrp.eu/forum/index.php?thread/25432-germanrp-addon-labymod-4-addon/""");


                    }
                }
                this.addon.getServerJoinListener().onFactionNameGet();
                return;
            }
            return;
        }
        FactionName factionName = this.player.getPlayerFactionName();
        if (factionName == null){
            return;
        }
        if (factionName.equals(FactionName.NONE)) {
            this.justJoined = false;
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
            this.addonVariables.getMembers().add(matcher.group(1).replace("[GR]", ""));
        }
        switch (factionName.getType()) {
            case BADFRAK -> {
                if (message.startsWith("► [Darklist] ")) {
                    event.setCancelled(true);
                    final Matcher matcher = DARK_LIST_ENTRY.getPattern().matcher(message);
                    if (!matcher.find()) {
                        return;
                    }
                    this.addonVariables.getDarklist().add(matcher.group(1).replace("[GR]", ""));
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
                        if(this.wasAFK){
                            Laby.references().chatExecutor().chat("/afk");
                            this.wasAFK = false;
                            return;
                        }
                        this.justJoined = false;
                        return;
                    }
                    this.addonVariables.getBounties().add(matcher.group(1).replace("[GR]", ""));
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
                        if(this.wasAFK){

                            Laby.references().chatExecutor().chat("/afk");
                            this.wasAFK = false;
                            return;
                        }
                        this.justJoined = false;
                        return;
                    }
                    this.addonVariables.getWantedPlayers().add(matcher.group(1).replace("[GR]", ""));
                    return;
                }
            }
        }
        switch (message) {
            case "► [System] Du bist jetzt als abwesend markiert." -> {
                event.setCancelled(true);
            }
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
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveListsChange(ChatReceiveEvent event) {

        FactionName factionName = this.player.getPlayerFactionName();
        if (factionName == null) {
            return;
        }
        String message = event.chatMessage().getPlainText();
        switch (factionName.getType()) {
            case BADFRAK -> {
                final Matcher nametagDarkListAddMatcher = DARK_LIST_ADD.getPattern().matcher(message);

                if (nametagDarkListAddMatcher.find()) {
                    this.addonVariables.getDarklist().add(nametagDarkListAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagDarkListRemoveMatcher = DARK_LIST_REMOVE.getPattern().matcher(message);
                if (nametagDarkListRemoveMatcher.find()) {
                    this.addonVariables.getDarklist().remove(nametagDarkListRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagBountyAddMatcher = BOUNTY_ADD.getPattern().matcher(message);
                if (nametagBountyAddMatcher.find()) {
                    this.addonVariables.getBounties().add(nametagBountyAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagBountyRemoveMatcher = BOUNTY_REMOVE.getPattern().matcher(message);
                if (nametagBountyRemoveMatcher.find()){
                    this.addonVariables.getBounties().remove(nametagBountyRemoveMatcher.group(1).replace("[GR]", ""));
                }

            }
            case STAAT -> {
                final Matcher nametagWantedRemoveMatcher = WANTED_REMOVE.getPattern().matcher(message);
                final Matcher nametagWantedAddMatcher = WANTED_ADD.getPattern().matcher(message);

                if (nametagWantedRemoveMatcher.matches()) {
                    this.addonVariables.getWantedPlayers().
                            remove(nametagWantedRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                if (nametagWantedAddMatcher.matches()) {
                    this.addonVariables.getWantedPlayers().add(nametagWantedAddMatcher.group(1).replace("[GR]", ""));
                }

            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveUpdateStats(@NotNull ChatReceiveEvent event){
        @NotNull String message = event.chatMessage().getPlainText();
        Matcher matcher = XP_ADD_CHAT.getPattern().matcher(message);
        if(matcher.find()){
            String x = matcher.group(2);
            int i = 1;
            if (x.contains("2")) {
                i = 2;
            }else if(x.contains("3")){
                i = 3;
            }
            player.addPlayerXP(Integer.parseInt(matcher.group(1))*i);
        }
    }
}