package eu.germanrp.addon.core.common.events;

import net.labymod.api.event.Event;

public record PoppyReceiveEvent(
        int addedAmount,
        int total
) implements Event {
}
