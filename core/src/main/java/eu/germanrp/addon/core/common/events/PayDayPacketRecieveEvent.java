package eu.germanrp.addon.core.common.events;

import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class PayDayPacketRecieveEvent implements Event {
    private final int paydayTime;
    private final float fSalary;
    private final float jSalary;
}
