package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.events.plant.*;
import eu.germanrp.addon.api.models.*;
import eu.germanrp.addon.api.network.PlantPacket;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND;
import static net.labymod.api.client.component.Component.text;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.HIDDEN;
import static net.labymod.api.util.I18n.getTranslation;

public abstract class PlantHudWidget extends TextHudWidget<TextHudWidgetConfig> {

    private static final Component PROGRESS_KEY = Component.translatable(
            "germanrpaddon.widget.plant.progressKey");
    private static final Component YIELD_KEY = Component.translatable(
            "germanrpaddon.widget.plant.yieldKey");
    private static final String PROGRESS_TRANSLATABLE_VALUE = "germanrpaddon.widget.plant.progressValue";
    private static final String YIELD_TRANSLATABLE_VALUE = "germanrpaddon.widget.plant.yieldValue";
    private static final String PLANT_HARVEST_MESSAGE = "germanrpaddon.message.plant.harvest";
    private static final String PLANT_FERTILIZE_MESSAGE = "germanrpaddon.message.plant.fertilize";
    private static final String PLANT_WATER_MESSAGE = "germanrpaddon.message.plant.water";

    private final GermanRPAddon addon;

    private TextLine progressLine;
    private TextLine yieldLine;

    private @Nullable Plant plant;
    private boolean hudNeedsUpdate = false;

    protected PlantHudWidget(final String id,
                             final HudWidgetCategory category,
                             final Icon icon,
                             GermanRPAddon addon) {
        super(id);
        this.bindCategory(category);
        this.setIcon(icon);
        this.addon = addon;
    }

    @Override
    public void load(final TextHudWidgetConfig config) {
        super.load(config);

        final String i18nProgressValue = getTranslation(PROGRESS_TRANSLATABLE_VALUE, 0, 0);
        final String i18nYieldValue = getTranslation(YIELD_TRANSLATABLE_VALUE, 0, "", 0);

        this.progressLine = this.createLine(PROGRESS_KEY, i18nProgressValue);
        this.yieldLine = this.createLine(YIELD_KEY, i18nYieldValue);

        this.progressLine.setState(HIDDEN);
        this.yieldLine.setState(HIDDEN);
    }

    @Override
    public void onTick(final boolean isEditorContext) {
        super.onTick(isEditorContext);

        if (isEditorContext) {
            updateLines(getDummyPlant());
            return;
        }

        if (!hudNeedsUpdate) {
            return;
        }

        this.hudNeedsUpdate = false;
        updateLines();
    }

    @Subscribe
    public void onGermanRPAddonTickEvent(final GermanRPAddonTickEvent event) {
        if (event.isPhase(SECOND)) {
            this.hudNeedsUpdate = true;
        }
    }

    public abstract Plant getDummyPlant();

    public abstract PlantType getPlantType();

    @Subscribe
    public void onPlantCreateEvent(final PlantCreateEvent event) {
        if (!event.getType().equals(getPlantType())) {
            return;
        }

        this.plant = switch (event.getType()) {
            case HEILKRAUTPFLANZE -> new PlantHeilkraut();
            case ROSE -> new PlantRose();
            case STOFF -> new PlantStoff();
        };

        updateLines();
        this.progressLine.setState(State.VISIBLE);
        this.yieldLine.setState(State.VISIBLE);
    }

    @Subscribe
    public void onPlantPacketReceiveEvent(final PlantPacketReceiveEvent event) {
        final PlantPacket packet = event.getPlantPacket();

        if (event.getPlantPacket().getType() != getPlantType()) {
            return;
        }

        if (plant == null) {
            return;
        }

        this.plant.tick(packet.getValue());
        updateLines();
    }

    @Subscribe
    public void onPlantDestroyEvent(final PlantDestroyEvent event) {
        if (!event.getType().equals(getPlantType())) {
            return;
        }

        this.plant = null;
        this.progressLine.setState(HIDDEN);
        this.yieldLine.setState(HIDDEN);
    }

    @Subscribe
    public void onPlantReadyToHarvestEvent(final PlantReadyToHarvestEvent event) {
        if (!event.getPlant().getType().equals(getPlantType())) {
            return;
        }

        this.addon.getPlayer().playSound(ResourceLocation.parse("germanrp:chat.notify.info"), 1, 1);

        this.addon.getPlayer().sendInfoMessage(Component.translatable(
                PLANT_HARVEST_MESSAGE,
                text(event.getPlant().getType().getDisplayName())
        ));
    }

    @Subscribe
    public void onPlantNeedsFertilizerEvent(final PlantNeedsFertilizerEvent event) {
        if (!event.getPlant().getType().equals(getPlantType())) {
            return;
        }

        this.addon.getPlayer().playSound(ResourceLocation.parse("germanrp:chat.notify.info"), 1, 1);

        this.addon.getPlayer().sendInfoMessage(
                Component.translatable(
                        PLANT_FERTILIZE_MESSAGE,
                        text(event.getPlant().getType().getDisplayName())
                )
        );
    }

    @Subscribe
    public void onPlantNeedsWaterEvent(final PlantNeedsWaterEvent event) {
        if (!event.getPlant().getType().equals(getPlantType())) {
            return;
        }

        this.addon.getPlayer().playSound(ResourceLocation.parse("germanrp:chat.notify.info"), 1, 1);

        this.addon.getPlayer().sendInfoMessage(
                Component.translatable(
                        PLANT_WATER_MESSAGE,
                        text(event.getPlant().getType().getDisplayName())
                )
        );
    }

    private void updateLines() {
        if (this.plant == null) {
            return;
        }

        updateLines(this.plant);
    }

    private void updateLines(final @NotNull Plant plant) {
        this.progressLine.updateAndFlush(
                getTranslation(
                        PROGRESS_TRANSLATABLE_VALUE,
                        plant.getCurrentTime(),
                        plant.getMaxTime()
                )
        );
        this.yieldLine.updateAndFlush(
                getTranslation(
                        YIELD_TRANSLATABLE_VALUE,
                        plant.getValue(),
                        plant.getYieldUnit(),
                        plant.getType().getSubstanceName()
                ));
    }
}
