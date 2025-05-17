package eu.germanrp.addon.api.events.plant;

import eu.germanrp.addon.api.models.Plant;
import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class PlantNeedsFertilizerEvent implements Event {

    private final Plant plant;

}
