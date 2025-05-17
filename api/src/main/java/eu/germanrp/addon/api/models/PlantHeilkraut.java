package eu.germanrp.addon.api.models;

import eu.germanrp.addon.api.events.plant.PlantNeedsFertilizerEvent;
import eu.germanrp.addon.api.events.plant.PlantNeedsWaterEvent;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static eu.germanrp.addon.api.models.PlantType.HEILKRAUTPFLANZE;
import static net.labymod.api.Laby.fireEvent;

@Getter
@Setter
public non-sealed class PlantHeilkraut extends Plant {

    private static final int FERTILIZE_TIME = 4;
    private static final int WATER_TIME = 10;

    private boolean fertilized = false;
    private boolean watered = false;

    protected PlantHeilkraut(boolean active, int value, int currentTime) {
        super(HEILKRAUTPFLANZE, active, value, currentTime);
    }

    @Override
    public @NotNull PlantType getType() {
        return HEILKRAUTPFLANZE;
    }

    @Override
    public void tick(final int newYield) {
        super.tick(newYield);

        if (currentTime == FERTILIZE_TIME) {
            fireEvent(new PlantNeedsFertilizerEvent(this));
        }

        if (currentTime == WATER_TIME) {
            fireEvent(new PlantNeedsWaterEvent(this));
        }

        if (currentTime > FERTILIZE_TIME && !fertilized) {
            missedTimes++;
        }

        if (currentTime > WATER_TIME && !watered) {
            missedTimes++;
        }
    }
}
