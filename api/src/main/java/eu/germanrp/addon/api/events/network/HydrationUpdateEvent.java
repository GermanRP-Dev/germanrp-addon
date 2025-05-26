package eu.germanrp.addon.api.events.network;

import net.labymod.api.event.Event;

public record HydrationUpdateEvent(double amount) implements Event {
}
