package eu.germanrp.addon.core.common.events;

import net.labymod.api.event.Event;

public record PoppyRemoveFromPouchEvent(
        int amount
) implements Event {
}
