package eu.germanrp.addon.core.common.events;

import net.labymod.api.event.Event;

public record PoppyRemoveFromInventoryEvent(
        int amount
) implements Event {
}
