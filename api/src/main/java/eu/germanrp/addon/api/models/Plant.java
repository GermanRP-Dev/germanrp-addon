package eu.germanrp.addon.api.models;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public abstract sealed class Plant permits PlantHeilkraut, PlantRose, PlantStoff {

  protected final String yieldUnit;

  protected boolean active;
  protected int value;
  protected int currentTime;
  protected int maxTime;
  protected int missedTimes;

  protected Plant(
      String yieldUnit,
      boolean active,
      int value,
      int currentTime,
      int maxTime
  ) {
    this.yieldUnit = yieldUnit;
    this.active = active;
    this.value = value;
    this.currentTime = currentTime;
    this.maxTime = maxTime;
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
