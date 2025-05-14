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
  }

    public abstract @NotNull PlantType getType();

}
