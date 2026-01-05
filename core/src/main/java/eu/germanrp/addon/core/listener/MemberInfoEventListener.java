package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.PlayerMemberInfoEvent;
import lombok.val;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import static eu.germanrp.addon.core.common.DefaultAddonPlayer.ADDON_PREFIX_SYMBOL;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.BOUNTY_MEMBER_WANTED_LIST_ENTRY;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.TITLE_FACTION_MEMBER_LIST;
import static net.labymod.api.Laby.fireEvent;

public class MemberInfoEventListener {

    private final GermanRPAddon addon;

    /**
     * We use this variable to determine if the player just joined the server
     * and the incoming chat messages might be incoming from the initial join routine.
     */
    private boolean justJoined = false;
    private boolean processingMemberInfoList = false;

    public MemberInfoEventListener(GermanRPAddon addon) {
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
     * We subscribe to chat messages and determine if the message is related to the member list.
     * If so, we fire a {@link eu.germanrp.addon.core.common.events.PlayerMemberInfoEvent} to do whatever we need to do.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessageReceive(final ChatReceiveEvent event) {
        val addonPlayer = addon.getPlayer();

        // We only want to handle the member list for players in a faction,
        // so we return early if the player is not in a faction
        val playerFaction = addonPlayer.getPlayerFaction();
        if (playerFaction == null || playerFaction == Faction.NONE || playerFaction == Faction.UNKNOWN) {
            return;
        }

        val message = event.chatMessage().getPlainText();

        if (message.startsWith(ADDON_PREFIX_SYMBOL)) {
            return;
        }

        val memberListTitleMatcher = TITLE_FACTION_MEMBER_LIST.getPattern().matcher(message);
        if (memberListTitleMatcher.find()) {
            this.processingMemberInfoList = true;

            // Clear the list before processing new entries
            addon.getNameTagService().getMembers().clear();

            if (justJoined) {
                event.setCancelled(true);
            }

            return;
        }

        if (processingMemberInfoList) {
            val nametagMemberInfoListEntryMatcher = BOUNTY_MEMBER_WANTED_LIST_ENTRY.getPattern().matcher(message);
            if (nametagMemberInfoListEntryMatcher.find()) {

                if (justJoined) {
                    event.setCancelled(true);
                }

                val playerName = nametagMemberInfoListEntryMatcher.group(1);
                val playerMemberInfoEvent = new PlayerMemberInfoEvent(true, new ServerPlayer(playerName));
                addonPlayer.sendDebugMessage("MemberInfoEventListener, onChatMessageReceive, nametagMemberInfoListEntryMatcher, fireEvent: playerMemberInfoEvent = %s".formatted(playerMemberInfoEvent));
                fireEvent(playerMemberInfoEvent);

            } else if (processingMemberInfoList) {
                if (message.startsWith("        (Insgesamt: ") || message.endsWith(" verfügbar)")) {

                    if (justJoined) {
                        event.setCancelled(true);
                    }

                    this.processingMemberInfoList = false;
                    addon.getJoinWorkflowManager().finishTask("memberinfo");
                }
            }
        }

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlayerMemberInfoEvent(final PlayerMemberInfoEvent event) {
        val addonPlayer = addon.getPlayer();
        val memberInfoTarget = event.player();
        if (event.added()) {
            addonPlayer.sendDebugMessage("MemberInfoEventListener, onPlayerMemberInfoEvent: %s added to the member list".formatted(memberInfoTarget.name()));
            addon.getNameTagService().getMembers().add(memberInfoTarget.name());
        } else {
            addonPlayer.sendDebugMessage("MemberInfoEventListener, onPlayerMemberInfoEvent: %s removed from the member list".formatted(memberInfoTarget.name()));
            addon.getNameTagService().getMembers().remove(memberInfoTarget.name());
        }
    }

}
