package eu.germanrp.addon.api.models;

import org.jetbrains.annotations.NotNull;

import static eu.germanrp.addon.api.models.PlantType.ROSE;

public non-sealed class PlantRose extends Plant {

    protected PlantRose(boolean active, int value, int currentTime) {
        super(ROSE, active, value, currentTime);
    }

    @Override
    public @NotNull PlantType getType() {
        return ROSE;
    }
}
