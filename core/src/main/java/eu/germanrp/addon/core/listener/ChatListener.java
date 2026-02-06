package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.events.GermanRPChatReceiveEvent;
import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.Setter;
import lombok.val;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.PANIC_DEACTIVATE;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.XP_ADD_CHAT;

public class ChatListener {

    private final GermanRPAddon addon;
    private boolean justJoined;

    @Setter
    private int emptyMessages;

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
    public void onChatReceiveAFK(final GermanRPChatReceiveEvent event) {
        val message = event.chatMessage().getPlainText();
        if (this.addon.getJoinWorkflowManager().isReturningToAFK()) {
            if (message.equals("► [System] Du bist jetzt als abwesend markiert.") || message.equals("► Verwende erneut \"/afk\", um den AFK-Modus zu verlassen.")) {
                event.setCancelled(true);
            } else if (message.isEmpty()) {
                event.setCancelled(true);
                this.afkEmptyMessages++;
                if (this.afkEmptyMessages >= 2) {
                    // We expect two empty messages: one before and one after the AFK status
                    this.addon.getJoinWorkflowManager().setReturningToAFK(false);
                    this.afkEmptyMessages = 0;
                }
            }
            return;
        }

        if (message.equals("► [System] Du bist jetzt wieder anwesend.")) {
            if (justJoined) {
                event.setCancelled(true);
                this.addon.getJoinWorkflowManager().setWasAFK(true);
            }
        } else if (message.equals("► Verwende erneut \"/afk\", um den AFK-Modus zu verlassen.") && justJoined) {
            this.addon.getJoinWorkflowManager().completeWorkflow();
        }

    }

    @Subscribe
    public void onChatReceiveJustJoined(final GermanRPChatReceiveEvent event) {
        if (!this.justJoined) {
            return;
        }

        String message = event.chatMessage().getPlainText();
        if (message.isEmpty()) {
            this.emptyMessages++;
            if (this.emptyMessages > 2) {
                event.setCancelled(true);
            }
        }
    }

    @Subscribe
    public void onChatReceiveUpdateStats(final @NotNull GermanRPChatReceiveEvent event) {
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
    public void onPanicDeactivate(final GermanRPChatReceiveEvent event) {
        if(justJoined) return;
        String message  = event.chatMessage().getPlainText();
        val playerFaction = GermanRPAddon.getInstance().getPlayer().getPlayerFaction();
        if(playerFaction == null || !playerFaction.equals(Faction.POLIZEI)) return;
        Matcher matcher = PANIC_DEACTIVATE.getPattern().matcher(message);
         if(!matcher.find()) return;
        GermanRPAddon.getInstance().getPlayer().setPlayPanic(false);

    }
}
