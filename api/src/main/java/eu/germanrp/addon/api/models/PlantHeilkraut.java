package eu.germanrp.addon.api.models;

import eu.germanrp.addon.api.events.plant.PlantNeedsFertilizerEvent;
import eu.germanrp.addon.api.events.plant.PlantNeedsWaterEvent;
import lombok.Getter;
import lombok.Setter;

import static eu.germanrp.addon.api.models.PlantType.HEILKRAUTPFLANZE;
import static net.labymod.api.Laby.fireEvent;

@Getter
@Setter
public final class PlantHeilkraut extends Plant {

    private static final int FERTILIZE_TIME = 4;
    private static final int WATER_TIME = 10;

    private boolean fertilized = false;
    private boolean watered = false;

    public PlantHeilkraut() {
        super(HEILKRAUTPFLANZE, true, 0, 0);
    }

    public PlantHeilkraut(boolean active, int value, int currentTime) {
        super(HEILKRAUTPFLANZE, active, value, currentTime);
    }

    @Override
    public void tick(final int newQuantity) {
        super.tick(newQuantity);

        if (this.currentTime == FERTILIZE_TIME) {
            fireEvent(new PlantNeedsFertilizerEvent(this));
        }

        if (this.currentTime == WATER_TIME) {
            fireEvent(new PlantNeedsWaterEvent(this));
        }

        if (this.currentTime > FERTILIZE_TIME && !this.fertilized) {
            this.missedTimes++;
        }

        if (this.currentTime > WATER_TIME && !this.watered) {
            this.missedTimes++;
        }
    }
}
