package eu.germanrp.addon.api.events.plant;

import eu.germanrp.addon.api.models.PlantType;
import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class PlantDestroyEvent implements Event {

    private final PlantType type;
}
