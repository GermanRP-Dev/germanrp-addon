package eu.germanrp.addon.api.models;

import eu.germanrp.addon.api.events.plant.PlantReadyToHarvestEvent;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import static net.labymod.api.Laby.fireEvent;

@Data
public abstract sealed class Plant permits PlantHeilkraut, PlantRose, PlantStoff {

    protected final String yieldUnit;
    protected boolean active;
    protected int value;
    protected int currentTime;
    protected int maxTime;
    protected int missedTimes;

    private final PlantType type;

    protected Plant(@NotNull PlantType plantType, boolean active, int quantity, int currentTime) {
        this.type = plantType;
        this.yieldUnit = plantType.getYieldUnit();
        this.active = active;
        this.value = quantity;
        this.currentTime = currentTime;
        this.maxTime = plantType.getMaxTime();
        this.missedTimes = 0;
    }

    public void tick(final int newQuantity) {
        this.currentTime += 1;

        if (this.currentTime == this.maxTime) {
            fireEvent(new PlantReadyToHarvestEvent(this));
        }

        if (this.currentTime >= this.maxTime) {
            this.missedTimes += 1;
        }

        this.value = newQuantity;
    }
}
