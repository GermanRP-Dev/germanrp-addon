package eu.germanrp.addon.api.events.plant;

import eu.germanrp.addon.api.network.PlantPacket;
import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class PlantPacketReceiveEvent implements Event {

    private final PlantPacket plantPacket;

}
