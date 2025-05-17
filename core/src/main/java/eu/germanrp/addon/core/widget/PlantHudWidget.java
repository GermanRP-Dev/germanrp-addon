package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantFactory;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import eu.germanrp.addon.core.common.events.plant.PlantCreateEvent;
import eu.germanrp.addon.core.common.events.plant.PlantDestroyEvent;
import eu.germanrp.addon.core.common.events.plant.PlantPacketReceiveEvent;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlantHudWidget extends TextHudWidget<TextHudWidgetConfig> {

    private static final Component PROGRESS_KEY = Component.translatable(
            "germanrpaddon.widget.plant.progressKey");
    private static final Component YIELD_KEY = Component.translatable(
            "germanrpaddon.widget.plant.yieldKey");
    private static final String PROGRESS_TRANSLATABLE_VALUE = "germanrpaddon.widget.plant.progressValue";
    private static final String YIELD_TRANSLATABLE_VALUE = "germanrpaddon.widget.plant.yieldValue";

    private TextLine progressLine;
    private TextLine yieldLine;

    private @Nullable Plant plant;

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
            updateLines(getDummyPlant());
            return;
        }

        updateLines();
    }

    public abstract Plant getDummyPlant();

    public abstract PlantType getPlantType();

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlantCreateEvent(final PlantCreateEvent event) {
        if (!event.getType().equals(getPlantType())) {
            return;
        }

        this.plant = PlantFactory.createPlant(event.getType());
        updateLines();
        this.progressLine.setState(State.VISIBLE);
        this.yieldLine.setState(State.VISIBLE);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlantPacketReceiveEvent(final PlantPacketReceiveEvent event) {
        val packet = event.getPlantPacket();

        if (event.getPlantPacket().getType() != getPlantType()) {
            return;
        }

        if(plant == null) {
            return;
        }

        this.plant.tick(packet.getValue());
        updateLines();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlantDestroyEvent(final PlantDestroyEvent event) {
        if (!event.getType().equals(getPlantType())) {
            return;
        }

        this.plant = null;
        this.progressLine.setState(State.HIDDEN);
        this.yieldLine.setState(State.HIDDEN);
    }

    private void updateLines() {
        if (this.plant == null) {
            return;
        }

        updateLines(this.plant);
    }

    private void updateLines(final @NotNull Plant plant) {
        this.progressLine.updateAndFlush(
                I18n.getTranslation(
                        PROGRESS_TRANSLATABLE_VALUE,
                        plant.getCurrentTime(),
                        plant.getMaxTime()
                )
        );
        this.yieldLine.updateAndFlush(
                I18n.getTranslation(
                        YIELD_TRANSLATABLE_VALUE,
                        plant.getValue(),
                        plant.getYieldUnit(),
                        plant.getType().getSubstanceName()
                ));
    }

}
