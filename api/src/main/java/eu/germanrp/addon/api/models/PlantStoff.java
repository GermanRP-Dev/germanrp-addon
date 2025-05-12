package eu.germanrp.addon.api.models;

import org.jetbrains.annotations.NotNull;

public non-sealed class PlantStoff extends Plant {

  protected PlantStoff(
      boolean active,
      int value,
      int currentTime
  ) {
    super(
        PlantType.STOFF.getYieldUnit(),
        active,
        value,
        currentTime,
        PlantType.STOFF.getMaxTime()
    );
  }

  @Override
  public @NotNull PlantType getType() {
    return PlantType.STOFF;
  }

}
