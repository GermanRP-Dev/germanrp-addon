package eu.germanrp.addon.api.models;

import static eu.germanrp.addon.api.models.PlantType.ROSE;

public final class PlantRose extends Plant {

    public PlantRose(boolean active, int value, int currentTime) {
        super(ROSE, active, value, currentTime);
    }
}
