package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.PlayerDarklistEvent;
import lombok.val;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static net.labymod.api.Laby.fireEvent;

public class DarklistEventListener {

    public static final String DARKLIST_LIST_HEADER = "► [Darklist] Darklist deiner Fraktion:";
    private final GermanRPAddon addon;

    /**
     * We use this variable to determine if the player just joined the server
     * and the incoming chat messages might be incoming from the initial join routine.
     */
    private boolean justJoined = false;
    private boolean processingDarklist = false;

    public DarklistEventListener(final GermanRPAddon addon) {
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
     * We subscribe to chat messages and determine if the message is related to the darklist.
     * If so, we fire a {@link PlayerDarklistEvent} to do whatever we need to do.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessageReceive(final ChatReceiveEvent event) {
        val addonPlayer = addon.getPlayer();

        // We only want to handle the darklist for crime factions,
        // so we return early if the player is not in a crime faction
        val playerFaction = addonPlayer.getPlayerFaction();
        if (playerFaction == null || playerFaction.getType() != Faction.Type.CRIME) {
            return;
        }

        val message = event.chatMessage().getPlainText();

        if (message.contentEquals(DARKLIST_LIST_HEADER) && justJoined) {
            event.setCancelled(true);
            processingDarklist = true;
            return;
        }

        val nametagDarkListAddMatcher = DARK_LIST_ADD.getPattern().matcher(message);
        if (nametagDarkListAddMatcher.find()) {
            val playerName = nametagDarkListAddMatcher.group(1);
            val playerDarklistEvent = new PlayerDarklistEvent(true, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("DarklistEventListener, onChatMessageReceive, nametagDarkListAddMatcher, fireEvent: playerDarklistEvent = %s".formatted(playerDarklistEvent));
            fireEvent(playerDarklistEvent);
            return;
        }

        val nametagDarkListRemoveMatcher = DARK_LIST_REMOVE.getPattern().matcher(message);
        if (nametagDarkListRemoveMatcher.find()) {
            val playerName = nametagDarkListRemoveMatcher.group(2);
            val playerDarklistEvent = new PlayerDarklistEvent(false, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("DarklistEventListener, onChatMessageReceive, nametagDarkListRemoveMatcher, fireEvent: playerDarklistEvent = %s".formatted(playerDarklistEvent));
            fireEvent(playerDarklistEvent);
            return;
        }

        val nametagDarkListEntryMatcher = DARK_LIST_ENTRY.getPattern().matcher(message);
        if (nametagDarkListEntryMatcher.find()) {

            if (justJoined) {
                event.setCancelled(true);
            }

            val playerName = nametagDarkListEntryMatcher.group(1);
            val playerDarklistEvent = new PlayerDarklistEvent(true, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("DarklistEventListener, onChatMessageReceive, nametagDarkListEntryMatcher, fireEvent: playerDarklistEvent = %s".formatted(playerDarklistEvent));
            fireEvent(playerDarklistEvent);
        } else if (processingDarklist) {
            // Because the darklist always starts with a header,
            // we can stop processing as soon as the message is not a darklist entry.
            processingDarklist = false;
            justJoined = false;
        }

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlayerDarklistEvent(final PlayerDarklistEvent event) {
        val addonPlayer = addon.getPlayer();
        val darklistTarget = event.player();
        if (event.added()) {
            addonPlayer.sendDebugMessage("DarklistEventListener, onPlayerDarklistEvent: %s added to darklist".formatted(darklistTarget.name()));
            addon.getNameTagService().getDarklist().add(darklistTarget.name());
        } else {
            addonPlayer.sendDebugMessage("DarklistEventListener, onPlayerDarklistEvent: %s removed from darklist".formatted(darklistTarget.name()));
            addon.getNameTagService().getDarklist().remove(darklistTarget.name());
        }
    }

}
