package eu.germanrp.addon.core.common.events;

import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class MajorWidgetUpdateEvent implements Event {
    private final String majorEventName;
    private final int countDownTime;

}