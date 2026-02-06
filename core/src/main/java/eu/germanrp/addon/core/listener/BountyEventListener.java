package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.events.GermanRPChatReceiveEvent;
import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.PlayerBountyEvent;
import lombok.val;
import net.labymod.api.event.Subscribe;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static net.labymod.api.Laby.fireEvent;

public class BountyEventListener {

    public static final String BOUNTY_LIST_HEADER = "            KOPFGELDER";
    public static final String NO_ACTIVE_BOUNTIES = "    » Derzeit hat niemand Kopfgeld";
    private final GermanRPAddon addon;

    /**
     * We use this variable to determine if the player just joined the server
     * and the incoming chat messages might be incoming from the initial join routine.
     */
    private boolean justJoined = false;
    private boolean processingBountyList = false;

    public BountyEventListener(final GermanRPAddon addon) {
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
     * If so, we fire a {@link PlayerBountyEvent} to do whatever we need to do.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessageReceive(final GermanRPChatReceiveEvent event) {
        val addonPlayer = addon.getPlayer();

        // We only want to handle the bounty list for crime factions,
        // so we return early if the player is not in a crime faction
        val playerFaction = addonPlayer.getPlayerFaction();
        if (playerFaction == null || playerFaction.getType() != Faction.Type.CRIME) {
            return;
        }

        val message = event.chatMessage().getPlainText();

        if (message.contentEquals(BOUNTY_LIST_HEADER)) {
            this.processingBountyList = true;

            // Clear the list before processing new entries
            addon.getNameTagService().getBounties().clear();

            if (justJoined) {
                event.setCancelled(true);
            }
            return;
        }

        val nametagBountyAddMatcher = BOUNTY_ADD.getPattern().matcher(message);
        if (nametagBountyAddMatcher.find()) {
            val playerName = nametagBountyAddMatcher.group(1);
            val playerBountyEvent = new PlayerBountyEvent(true, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("BountyEventListener, onChatMessageReceive, nametagBountyAddMatcher, fireEvent: playerBountyEvent = %s".formatted(playerBountyEvent));
            fireEvent(playerBountyEvent);
            return;
        }

        val nametagBountyRemoveMatcher = BOUNTY_REMOVE.getPattern().matcher(message);
        if (nametagBountyRemoveMatcher.find()) {
            val playerName = nametagBountyRemoveMatcher.group(1);
            val playerBountyEvent = new PlayerBountyEvent(false, new ServerPlayer(playerName));
            addonPlayer.sendDebugMessage("BountyEventListener, onChatMessageReceive, nametagBountyRemoveMatcher, fireEvent: playerBountyEvent = %s".formatted(playerBountyEvent));
            fireEvent(playerBountyEvent);
            return;
        }

        if (processingBountyList) {

            if(message.contentEquals(NO_ACTIVE_BOUNTIES)) {

                if (justJoined) {
                    event.setCancelled(true);
                }

                // When no active bounties are available, we can stop processing the bounty list.
                processingBountyList = false;
                addon.getJoinWorkflowManager().finishTask("bounties");
                return;
            }

            val nametagBountyEntryMatcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
            if (nametagBountyEntryMatcher.find()) {

                if (justJoined) {
                    event.setCancelled(true);
                }

                val playerName = nametagBountyEntryMatcher.group(1);
                val playerBountyEvent = new PlayerBountyEvent(true, new ServerPlayer(playerName));
                addonPlayer.sendDebugMessage("DarklistEventListener, onChatMessageReceive, nametagBountyEntryMatcher, fireEvent: playerBountyEvent = %s".formatted(playerBountyEvent));
                fireEvent(playerBountyEvent);
            } else if (processingBountyList) {
                // Because the bounty list always starts with a header,
                // we can stop processing as soon as the message is not a bounty list entry.
                processingBountyList = false;
                addon.getJoinWorkflowManager().finishTask("bounties");
            }
        }

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlayerBountyEvent(final PlayerBountyEvent event) {
        val addonPlayer = addon.getPlayer();
        val bountyTarget = event.player();
        if (event.added()) {
            addonPlayer.sendDebugMessage("BountyEventListener, onPlayerBountyEvent: %s added to the bounty list".formatted(bountyTarget.name()));
            addon.getNameTagService().getBounties().add(bountyTarget.name());
        } else {
            addonPlayer.sendDebugMessage("BountyEventListener, onPlayerBountyEvent: %s removed from the bounty list".formatted(bountyTarget.name()));
            addon.getNameTagService().getBounties().remove(bountyTarget.name());
        }
    }

}
