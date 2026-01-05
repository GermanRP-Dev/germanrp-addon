package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.PlayerWantedEvent;
import lombok.val;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import static eu.germanrp.addon.core.common.DefaultAddonPlayer.ADDON_PREFIX_SYMBOL;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static net.labymod.api.Laby.fireEvent;

public class WantedEventListener {

    private final GermanRPAddon addon;

    /**
     * We use this variable to determine if the player just joined the server
     * and the incoming chat messages might be incoming from the initial join routine.
     */
    private boolean justJoined = false;
    private boolean processingWantedList = false;

    public WantedEventListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    /**
     * We subscribe to the {@link JustJoinedEvent} because we want to hide some messages as they might be part of the initial join routine.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onJustJoined(final JustJoinedEvent event) {
        this.justJoined = event.isJustJoined();
    }

    /**
     * We subscribe to chat messages and determine if the message is related to the bounty list.
     * If so, we fire a {@link PlayerWantedEvent} to do whatever we need to do.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessageReceive(final ChatReceiveEvent event) {
        val addonPlayer = addon.getPlayer();

        // We only want to handle the wanted list for state factions,
        // so we return early if the player is not in a state faction
        val playerFaction = addonPlayer.getPlayerFaction();
        if (playerFaction == null || playerFaction.getType() != Faction.Type.STAAT) {
            return;
        }

        val message = event.chatMessage().getPlainText();

        if(message.startsWith(ADDON_PREFIX_SYMBOL)) {
            return;
        }

        val wantedListTitleMatcher = TITLE_WANTED_LIST.getPattern().matcher(message);
        if (wantedListTitleMatcher.find()) {
            this.processingWantedList = true;

            // Clear the list before processing new entries
            addon.getNameTagService().getWantedPlayers().clear();

            if (justJoined) {
                event.setCancelled(true);
            }
            return;
        }

        val nametagWantedAddMatcher = WANTED_ADD.getPattern().matcher(message);
        if (nametagWantedAddMatcher.find()) {
            val playerName = nametagWantedAddMatcher.group(1);
            val playerWantedEvent = new PlayerWantedEvent(true, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("WantedEventListener, onChatMessageReceive, nametagWantedAddMatcher, fireEvent: playerWantedEvent = %s".formatted(playerWantedEvent));
            fireEvent(playerWantedEvent);
            return;
        }

        val nametagWantedRemoveMatcher = WANTED_REMOVE.getPattern().matcher(message);
        if (nametagWantedRemoveMatcher.find()) {
            val playerName = nametagWantedRemoveMatcher.group(2);
            val playerWantedEvent = new PlayerWantedEvent(false, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("WantedEventListener, onChatMessageReceive, nametagWantedRemoveMatcher, fireEvent: playerWantedEvent = %s".formatted(playerWantedEvent));
            fireEvent(playerWantedEvent);
            return;
        }

        val nametagWantedInjailedMatcher = WANTED_INJAILED.getPattern().matcher(message);
        if (nametagWantedInjailedMatcher.find()) {
            val playerName = nametagWantedInjailedMatcher.group(1);
            val playerWantedEvent = new PlayerWantedEvent(false, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("WantedEventListener, onChatMessageReceive, nametagWantedInjailedMatcher, fireEvent: playerWantedEvent = %s".formatted(playerWantedEvent));
            fireEvent(playerWantedEvent);
            return;
        }

        if (processingWantedList) {
            val nametagBountyEntryMatcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
            if (nametagBountyEntryMatcher.find()) {

                if (justJoined) {
                    event.setCancelled(true);
                }

                val playerName = nametagBountyEntryMatcher.group(1);
                val playerWantedEvent = new PlayerWantedEvent(true, new ServerPlayer(playerName));
                addonPlayer.sendDebugMessage("WantedEventListener, onChatMessageReceive, nametagBountyEntryMatcher, fireEvent: playerWantedEvent = %s".formatted(playerWantedEvent));
                fireEvent(playerWantedEvent);
            } else if (processingWantedList) {
                // Because the bounty list always starts with a header,
                // we can stop processing as soon as the message is not a bounty list entry.
                processingWantedList = false;
                addon.getJoinWorkflowManager().finishTask("wanteds");
            }
        }

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlayerWantedEvent(final PlayerWantedEvent event) {
        val addonPlayer = addon.getPlayer();
        val wantedTarget = event.player();
        if (event.added()) {
            addonPlayer.sendDebugMessage("WantedEventListener, onPlayerWantedEvent: %s added to the wanted list".formatted(wantedTarget.name()));
            addon.getNameTagService().getWantedPlayers().add(wantedTarget.name());
        } else {
            addonPlayer.sendDebugMessage("WantedEventListener, onPlayerWantedEvent: %s removed from the wanted list".formatted(wantedTarget.name()));
            addon.getNameTagService().getWantedPlayers().remove(wantedTarget.name());
        }
    }

}
