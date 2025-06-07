package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.network.TimerPacket;
import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class MajorWidgetUpdateEvent implements Event {

    private final TimerPacket timerPacket;

}
