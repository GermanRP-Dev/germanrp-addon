package eu.germanrp.addon.core.common.events;

import net.labymod.api.event.Event;

/**
 * This event is fired when the player joins a server with the respective {@link #isGR} set.
 * @param isGR {@code true} when the joined server is GR, otherwise {@code false}
 */
public record AddonServerJoinEvent(
        boolean isGR
) implements Event {
}
