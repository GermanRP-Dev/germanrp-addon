package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.Setter;
import lombok.val;
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

    @Setter
    private int emptyMessages;
    private boolean memberInfoWasShown;

    @Setter
    private int afkEmptyMessages;

    public ChatListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onGRJoin(JustJoinedEvent event) {
        this.justJoined = event.isJustJoined();
    }

    @Subscribe
    public void onChatReceiveAFK(ChatReceiveEvent event) {
        String message = event.chatMessage().getPlainText();
        if (this.addon.getJoinWorkflowManager().isReturningToAFK()) {
            switch (message) {
                case "► [System] Du bist jetzt als abwesend markiert.",
                     "► Verwende erneut \"/afk\", um den AFK-Modus zu verlassen." -> {
                    event.setCancelled(true);
                }
                case "" -> {
                    event.setCancelled(true);
                    this.afkEmptyMessages++;
                    if (this.afkEmptyMessages >= 2) {
                        // We expect two empty messages: one before and one after the AFK status
                        this.addon.getJoinWorkflowManager().setReturningToAFK(false);
                        this.afkEmptyMessages = 0;
                    }
                }
            }
            return;
        }

        switch (message) {
            case "► [System] Du bist jetzt wieder anwesend." -> {
                if (justJoined) {
                    event.setCancelled(true);
                    this.addon.getJoinWorkflowManager().setWasAFK(true);
                }
            }
            case "► Verwende erneut \"/afk\", um den AFK-Modus zu verlassen." -> {
                // This is likely manually triggered if not returningToAFK
                // But we still want to complete workflow if somehow a task was stuck
                if (justJoined) {
                    this.addon.getJoinWorkflowManager().completeWorkflow();
                }
            }
        }
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
                        this.memberInfoWasShown = true;
                        this.addon.getPlayer().setPlayerFaction(Faction.NONE);
                        this.addon.getJoinWorkflowManager().completeWorkflow(); // Faction NONE means no further join lists
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

        if (message.isEmpty()) {
            this.emptyMessages++;
            if (this.emptyMessages > 2) {
                event.setCancelled(true);
            }
        }
        Faction faction = this.addon.getPlayer().getPlayerFaction();
        if (faction == null) {
            return;
        }
        if (faction == Faction.NONE) {
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
                this.addon.getJoinWorkflowManager().finishTask("memberinfo");
                return;
            }
            this.addon.getNameTagService().getMembers().add(matcher.group(1).replace("[GR]", ""));
            return;
        }
        switch (faction.getType()) {
            case NEUTRAL,MEDIC -> {
                // For neutral/medic, once memberinfo is shown, we are mostly done with join tasks in ChatListener
                if (this.memberInfoWasShown) {
                    this.memberInfoWasShown = false;
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
        val message = event.getMessage().toLowerCase();
        if (message.startsWith("/afk")) {
            // If the player manually toggles AFK, we should stop trying to manage it automatically
            this.addon.getJoinWorkflowManager().setWasAFK(false);
            this.addon.getJoinWorkflowManager().setReturningToAFK(false);
        }

        if(event.isMessageCommand()){
            String[] messageStart = event.getMessage().split(" ");
            event.changeMessage(messageStart[0].toLowerCase() + event.getMessage().replace(messageStart[0], ""));
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
