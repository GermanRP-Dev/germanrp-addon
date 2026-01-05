package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.models.ServerPlayer;
import net.labymod.api.event.Event;

/**
 * This event is fired when a player should be added or removed from the {@link eu.germanrp.addon.core.common.AddonPlayer}'s Faction wanted list.
 *
 * @param added      {@code true} when the player should be added to the wanted list, {@code false} when removed
 * @param player     the player that should be added or removed from the wanted list
 */
public record PlayerWantedEvent(boolean added, ServerPlayer player) implements Event {
}
