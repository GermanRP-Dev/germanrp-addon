package eu.germanrp.addon.api.network;

import eu.germanrp.addon.api.models.PlantType;
import lombok.Data;

@Data
public class PlantPaket implements GRPaket {

    private final boolean active;
    private final PlantType type;
    private final int value;
    private final int currentTime;
    private final int maxTime;
}
