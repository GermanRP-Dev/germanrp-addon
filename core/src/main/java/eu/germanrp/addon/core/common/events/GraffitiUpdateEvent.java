package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.models.Graffiti;
import lombok.Data;
import net.labymod.api.event.Event;

import java.time.Duration;

@Data
public class GraffitiUpdateEvent implements Event {

    private final Graffiti graffiti;
    private final Duration remainingTime;
}
