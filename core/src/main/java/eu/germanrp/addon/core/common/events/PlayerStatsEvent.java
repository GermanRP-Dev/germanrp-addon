package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.models.Faction;
import net.labymod.api.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when /stats data is parsed.
 *
 * @param playerXP       the player's current XP, or {@code null} if not updated
 * @param playerNeededXP the XP required for the next level, or {@code null} if not updated
 * @param faction        the player's faction, or {@code null} if not updated
 */
public record PlayerStatsEvent(@Nullable Integer playerXP,
                               @Nullable Integer playerNeededXP,
                               @Nullable Faction faction) implements Event {
}
