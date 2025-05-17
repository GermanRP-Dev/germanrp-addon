package eu.germanrp.addon.api.models;

import org.jetbrains.annotations.NotNull;

import static eu.germanrp.addon.api.models.PlantType.STOFF;

public non-sealed class PlantStoff extends Plant {

    protected PlantStoff(boolean active, int value, int currentTime) {
        super(STOFF, active, value, currentTime);
    }

    @Override
    public @NotNull PlantType getType() {
        return STOFF;
    }
}
