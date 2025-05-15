package eu.germanrp.addon.api.models;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static eu.germanrp.addon.api.models.PlantType.HEILKRAUTPFLANZE;

@Getter
@Setter
public non-sealed class PlantHeilkraut extends Plant {

    public static final int FERTILIZE_TIME = 4;
    public static final int WATER_TIME = 10;

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
        if (currentTime > FERTILIZE_TIME && !fertilized) {
            missedTimes++;
        }

        if (currentTime > WATER_TIME && !watered) {
            missedTimes++;
        }

        super.tick(newYield);
    }
}
