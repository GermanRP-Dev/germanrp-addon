package eu.germanrp.addon.api.events.vehicle;

import net.labymod.api.event.Event;

public record CurrentTempomatReceiveEvent(int value) implements Event {
}
