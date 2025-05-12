package eu.germanrp.addon.api.models;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public non-sealed class PlantHeilkraut extends Plant {

  public static final int FERTILIZE_TIME = 4;
  public static final int WATER_TIME = 10;

  private boolean fertilized = false;
  private boolean watered = false;

  protected PlantHeilkraut(boolean active, int value, int currentTime) {
    super(PlantType.HEILKRAUTPFLANZE.getYieldUnit(), active, value, currentTime,
        PlantType.HEILKRAUTPFLANZE.getMaxTime());
  }

  @Override
  public @NotNull PlantType getType() {
    return PlantType.HEILKRAUTPFLANZE;
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
