package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.network.PayDayPacket;
import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class PayDayPacketRecieveEvent implements Event {
    private final int paydayTime;
    private final float fSalary;
    private final float jSalary;
}
