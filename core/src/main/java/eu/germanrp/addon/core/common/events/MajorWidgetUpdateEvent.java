package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.models.Graffiti;
import lombok.Data;
import net.labymod.api.event.Event;

import java.time.Duration;
@Data
public class MajorWidgetUpdateEvent implements Event {
    private final String majorEventName;
    private final int countDownTime;

}
