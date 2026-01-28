package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.PlayerStatsEvent;
import lombok.val;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.FRAKTION_NAME_STATS;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.STATS_VOTESTREAK;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.XP_READER_STATS;
import static net.labymod.api.Laby.fireEvent;

public class PlayerStatsEventListener {

    private final GermanRPAddon addon;
    private boolean justJoined;
    private boolean processingStats;

    public PlayerStatsEventListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onJustJoined(final JustJoinedEvent event) {
        this.justJoined = event.isJustJoined();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessageReceive(final ChatReceiveEvent event) {
        final String message = event.chatMessage().getPlainText();

        if (message.startsWith("► [System] Statistiken von ")) {
            this.processingStats = true;
            if (this.justJoined) {
                event.setCancelled(true);
            }
            return;
        }

        if (this.processingStats && this.justJoined && message.startsWith("► [System] ")) {
            event.setCancelled(true);
        }

        Matcher matcher = XP_READER_STATS.getPattern().matcher(message);
        if (matcher.find()) {
            fireEvent(new PlayerStatsEvent(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    null
            ));
        }

        matcher = FRAKTION_NAME_STATS.getPattern().matcher(message);
        if (matcher.find()) {
            val factionName = matcher.group(1);
            this.addon.getPlayer().sendDebugMessage("PlayerStatsEventListener, onChatMessageReceive, FRAKTION_NAME_STATS, factionName = %s".formatted(factionName));
            val faction = Faction.fromDisplayName(factionName);
            fireEvent(new PlayerStatsEvent(null, null, faction));

            if (faction == null || faction == Faction.UNKNOWN) {
                this.addon.getPlayer().sendErrorMessage("Deine Fraktion wurde nicht gefunden... Bitte hier reporten:");
                this.addon.getPlayer().sendErrorMessage("https://germanrp.eu/forum/index.php?board/296-bug-labymod-addon/");
            }

            if (this.justJoined) {
                this.addon.getServerJoinListener().onFactionNameGet();
            }
        }

        matcher = STATS_VOTESTREAK.getPattern().matcher(message);
        if (matcher.find()) {
            this.processingStats = false;
            this.addon.getJoinWorkflowManager().finishTask("stats");
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlayerStatsEvent(final PlayerStatsEvent event) {
        if (event.playerXP() != null) {
            addon.getPlayer().setPlayerXP(event.playerXP());
        }
        if (event.playerNeededXP() != null) {
            addon.getPlayer().setPlayerNeededXP(event.playerNeededXP());
        }
        if (event.faction() != null) {
            addon.getPlayer().setPlayerFaction(event.faction());
        }
    }
}
