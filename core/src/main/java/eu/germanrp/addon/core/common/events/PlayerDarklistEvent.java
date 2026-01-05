package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.models.ServerPlayer;
import net.labymod.api.event.Event;

/**
 * This event is fired when a player should be added or removed from the {@link eu.germanrp.addon.core.common.AddonPlayer}'s Faction Darklist.
 *
 * @param added      {@code true} when the player should be added to the darklist, {@code false} when removed
 * @param player     the player that should be added or removed from the darklist
 */
public record PlayerDarklistEvent(boolean added, ServerPlayer player) implements Event {
}
