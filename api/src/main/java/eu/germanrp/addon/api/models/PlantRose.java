package eu.germanrp.addon.api.models;

import org.jetbrains.annotations.NotNull;

public non-sealed class PlantRose extends Plant {

  protected PlantRose(
      boolean active,
      int value,
      int currentTime
  ) {
    super(
        PlantType.ROSE.getYieldUnit(),
        active,
        value,
        currentTime,
        PlantType.ROSE.getMaxTime()
    );
  }

  @Override
  public @NotNull PlantType getType() {
    return PlantType.ROSE;
  }

}
