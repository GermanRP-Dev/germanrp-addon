package eu.germanrp.addon.api.models;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public abstract sealed class Plant permits PlantHeilkraut, PlantRose, PlantStoff {

    protected final String yieldUnit;
    protected boolean active;
    protected int value;
    protected int currentTime;
    protected int maxTime;
    protected int missedTimes;

    public Plant(PlantType plantType, boolean active, int value, int currentTime) {
        this.yieldUnit = plantType.getYieldUnit();
        this.active = active;
        this.value = value;
        this.currentTime = currentTime;
        this.maxTime = plantType.getMaxTime();
        this.missedTimes = 0;
    }

    public abstract @NotNull PlantType getType();

    public void tick(final int newYield) {
        currentTime += 1;
        if (currentTime >= maxTime) {
            missedTimes += 1;
        }
        this.value = newYield;
    }
}
