package eu.germanrp.addon.api.events.network;

import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class HydrationUpdateEvent implements Event {

    private final double amount;
}
