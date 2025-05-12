package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantFactory;
import eu.germanrp.addon.api.models.PlantPaketReceiver;
import eu.germanrp.addon.api.network.PlantPaket;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlantHudWidget extends TextHudWidget<TextHudWidgetConfig> implements
    PlantPaketReceiver {

  private static final Component PROGRESS_KEY = Component.translatable(
      "germanrputils.widget.plant.progressKey");
  private static final Component YIELD_KEY = Component.translatable(
      "germanrputils.widget.plant.yieldKey");
  private static final String PROGRESS_TRANSLATABLE_VALUE = "germanrputils.widget.plant.progressValue";
  private static final String YIELD_TRANSLATABLE_VALUE = "germanrputils.widget.plant.yieldValue";

  private TextLine progressLine;
  private TextLine yieldLine;

  private Plant plant;

  protected PlantHudWidget(final String id, final HudWidgetCategory category, final Icon icon) {
    super(id);
    this.bindCategory(category);
    this.setIcon(icon);
  }

  @Override
  public void load(final TextHudWidgetConfig config) {
    super.load(config);

    final String i18nProgressValue = I18n.getTranslation(PROGRESS_TRANSLATABLE_VALUE, 0, 0);
    final String i18nYieldValue = I18n.getTranslation(YIELD_TRANSLATABLE_VALUE, 0, "", 0);

    this.progressLine = this.createLine(PROGRESS_KEY, i18nProgressValue);
    this.yieldLine = this.createLine(YIELD_KEY, i18nYieldValue);

    this.progressLine.setState(State.HIDDEN);
    this.yieldLine.setState(State.HIDDEN);
  }

  @Override
  public void onTick(final boolean isEditorContext) {
    super.onTick(isEditorContext);

    if (isEditorContext) {
      renderPlant(getDummyPlant());
      return;
    }

    if (this.plant == null) {
      return;
    }

    renderPlant(this.plant);
  }

  public abstract Plant getDummyPlant();

  public void reset() {
    this.plant = null;
    this.progressLine.setState(State.HIDDEN);
    this.yieldLine.setState(State.HIDDEN);
  }

  public void updatePlant(final @Nullable Plant plant) {
    this.plant = plant;
    this.progressLine.setState(State.VISIBLE);
    this.yieldLine.setState(State.VISIBLE);
  }

  protected void renderPlant(final @NotNull Plant plant) {
    this.progressLine.updateAndFlush(
        I18n.getTranslation(PROGRESS_TRANSLATABLE_VALUE,
            plant.getCurrentTime(),
            plant.getMaxTime()
        )
    );
    this.yieldLine.updateAndFlush(
        I18n.getTranslation(YIELD_TRANSLATABLE_VALUE, plant.getValue(), plant.getYieldUnit(),
            plant.getType().getSubstanceName()));
  }

  @Override
  public void onPaketReceive(final @NotNull PlantPaket paket) {
    if (!paket.isActive()) {
      reset();
      return;
    }

    if (this.plant == null) {
      final Plant updatedPlant = PlantFactory.createPlant(
          paket.getType(),
          true,
          paket.getValue(),
          paket.getCurrentTime()
      );
      updatePlant(updatedPlant);
      return;
    }

    this.plant.tick(paket.getValue());
  }

}
