package eu.germanrp.addon.api.models;

import static eu.germanrp.addon.api.models.PlantType.STOFF;

public final class PlantStoff extends Plant {

    public PlantStoff() {
        super(STOFF, true, 0, 0);
    }

    public PlantStoff(boolean active, int value, int currentTime) {
        super(STOFF, active, value, currentTime);
    }
}
