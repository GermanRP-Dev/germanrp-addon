package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.events.plant.PlantCreateEvent;
import eu.germanrp.addon.api.events.plant.PlantDestroyEvent;
import eu.germanrp.addon.api.events.plant.PlantNeedsFertilizerEvent;
import eu.germanrp.addon.api.events.plant.PlantNeedsWaterEvent;
import eu.germanrp.addon.api.events.plant.PlantPacketReceiveEvent;
import eu.germanrp.addon.api.events.plant.PlantReadyToHarvestEvent;
import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantHeilkraut;
import eu.germanrp.addon.api.models.PlantRose;
import eu.germanrp.addon.api.models.PlantStoff;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
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
    private static final String PLANT_HARVEST_MESSAGE = "germanrpaddon.message.plant.harvest";
    private static final String PLANT_FERTILIZE_MESSAGE = "germanrpaddon.message.plant.fertilize";
    private static final String PLANT_WATER_MESSAGE = "germanrpaddon.message.plant.water";
    private static final TextColor NOTIFICATION_COLOR = TextColor.color(0xFF75151E);

    private final PlaySoundExecutor playSoundExecutor;
    private final GermanRPAddon addon;

    private TextLine progressLine;
    private TextLine yieldLine;

    private @Nullable Plant plant;
    private boolean hudNeedsUpdate = false;

    protected PlantHudWidget(final String id,
                             final HudWidgetCategory category,
                             final Icon icon,
                             final PlaySoundExecutor playSoundExecutor,
                             GermanRPAddon addon) {
        super(id);
        this.bindCategory(category);
        this.setIcon(icon);
        this.playSoundExecutor = playSoundExecutor;
        this.addon = addon;
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

        if (!hudNeedsUpdate) {
            return;
        }

        this.hudNeedsUpdate = false;
        updateLines();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGermanRPAddonTickEvent(final GermanRPAddonTickEvent event) {
        if (event.isPhase(GermanRPAddonTickEvent.Phase.SECOND)) {
            this.hudNeedsUpdate = true;
        }
    }

    public abstract Plant getDummyPlant();

    public abstract PlantType getPlantType();

    @Subscribe
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public void onPlantPacketReceiveEvent(final PlantPacketReceiveEvent event) {
        val packet = event.getPlantPacket();

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
    @SuppressWarnings("unused")
    public void onPlantDestroyEvent(final PlantDestroyEvent event) {
        if (!event.getType().equals(getPlantType())) {
            return;
        }

        this.plant = null;
        this.progressLine.setState(State.HIDDEN);
        this.yieldLine.setState(State.HIDDEN);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlantReadyToHarvestEvent(final PlantReadyToHarvestEvent event) {
        if (!event.getPlant().getType().equals(getPlantType())) {
            return;
        }

        playSoundExecutor.playNotificationSound();

        this.addon.getPlayer().sendInfoMessage(Component.translatable(
                PLANT_HARVEST_MESSAGE,
                Component.text(event.getPlant().getType().getDisplayName())
        ));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlantNeedsFertilizerEvent(final PlantNeedsFertilizerEvent event) {
        if (!event.getPlant().getType().equals(getPlantType())) {
            return;
        }

        playSoundExecutor.playNotificationSound();
        this.addon.getPlayer().sendInfoMessage(
                Component.translatable(
                        PLANT_FERTILIZE_MESSAGE,
                        Component.text(event.getPlant().getType().getDisplayName())
                )
        );
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPlantNeedsWaterEvent(final PlantNeedsWaterEvent event) {
        if (!event.getPlant().getType().equals(getPlantType())) {
            return;
        }

        playSoundExecutor.playNotificationSound();
        this.addon.getPlayer().sendInfoMessage(
                Component.translatable(
                        PLANT_WATER_MESSAGE,
                        Component.text(event.getPlant().getType().getDisplayName())
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
