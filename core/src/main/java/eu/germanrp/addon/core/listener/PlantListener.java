package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantFactory;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.api.network.PaketFactory;
import eu.germanrp.addon.api.network.PlantPaket;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.Utils;
import eu.germanrp.addon.core.widget.HeilkrautpflanzeHudWidget;
import eu.germanrp.addon.core.widget.RoseHudWidget;
import eu.germanrp.addon.core.widget.StoffHudWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.util.GsonUtil;

import java.util.regex.Matcher;

import static eu.germanrp.addon.api.models.PlantHeilkraut.FERTILIZE_TIME;
import static eu.germanrp.addon.api.models.PlantHeilkraut.WATER_TIME;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.PLANT_HARVEST;

public class PlantListener {

    public static final String HEILKRAUT_FERTILIZE_MESSAGE = "germanrpaddon.message.plant.heilkrautpflanze.fertilize";
    public static final String HEILKRAUT_WATER_MESSAGE = "germanrpaddon.message.plant.heilkrautpflanze.water";
    public static final String PLANT_HARVEST_MESSAGE = "germanrpaddon.message.plant.harvest";
    public static final TextColor NOTIFICATION_COLOR = TextColor.color(0x75, 0x15, 0x1E);

    private static final String PLANT_DIED_MESSAGE = "► Du hast deine Pflanze nicht rechtzeitig geerntet.";

    private final GermanRPAddon addon;
    private final HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private final RoseHudWidget roseHudWidget;
    private final StoffHudWidget stoffHudWidget;

    public PlantListener(
            final GermanRPAddon addon
    ) {
        this.addon = addon;
        this.heilkrautpflanzeHudWidget = addon.getHeilkrautpflanzeHudWidget();
        this.roseHudWidget = addon.getRoseHudWidget();
        this.stoffHudWidget = addon.getStoffHudWidget();
    }

    @Subscribe
    public void onChatReceiveEvent(final ChatReceiveEvent event) {
        final String message = event.chatMessage().getPlainText();

        if (message.equals(PLANT_DIED_MESSAGE)) {
            // TODO somehow find out which plant died...
            return;
        }

        if (beginPlantIfSowMessage(message)) {
            return;
        }

        final Matcher matcher = PLANT_HARVEST.getPattern().matcher(message);

        if (!matcher.find()) {
            return;
        }

        final String displayName = matcher.group(1);
        PlantType.fromDisplayName(displayName).ifPresent(plantType -> {
            switch (plantType) {
                case HEILKRAUTPFLANZE -> this.heilkrautpflanzeHudWidget.reset();
                case ROSE -> this.roseHudWidget.reset();
                case STOFF -> this.stoffHudWidget.reset();
            }
        });
    }

    @Subscribe
    public void onNetworkPayloadEvent(final NetworkPayloadEvent event) {
        if (!Utils.isLegacyAddonPacket(event.identifier())) {
            return;
        }

        PaketFactory.createPaket(event.getPayload()).ifPresent(paket -> {

            this.addon.logger().info("Received packet: " + GsonUtil.DEFAULT_GSON.toJson(paket));

            // Ignore unknown pakets
            if (!(paket instanceof PlantPaket plantPaket)) {
                return;
            }

            switch (plantPaket.getType()) {
                case HEILKRAUTPFLANZE -> this.heilkrautpflanzeHudWidget.onPaketReceive(plantPaket);
                case ROSE -> this.roseHudWidget.onPaketReceive(plantPaket);
                case STOFF -> this.stoffHudWidget.onPaketReceive(plantPaket);
            }

            if (plantPaket.getCurrentTime() == plantPaket.getMaxTime()) {
                addon.displayMessage(Component.translatable(
                                PLANT_HARVEST_MESSAGE,
                                Component.text(plantPaket.getType().getDisplayName())
                        )
                        .color(NOTIFICATION_COLOR));
            }

            if (plantPaket.getType() != PlantType.HEILKRAUTPFLANZE) {
                return;
            }

            if (plantPaket.getCurrentTime() == FERTILIZE_TIME && plantPaket.isActive()) {
                addon.displayMessage(Component.translatable(HEILKRAUT_FERTILIZE_MESSAGE)
                        .color(NOTIFICATION_COLOR));
            }

            if (plantPaket.getCurrentTime() == WATER_TIME && plantPaket.isActive()) {
                addon.displayMessage(Component.translatable(HEILKRAUT_WATER_MESSAGE)
                        .color(NOTIFICATION_COLOR));
            }
        });
    }

    @Subscribe
    public void onServerDisconnectEvent(final ServerDisconnectEvent event) {
        this.heilkrautpflanzeHudWidget.reset();
        this.roseHudWidget.reset();
        this.stoffHudWidget.reset();
    }

    /**
     * Checks if the given message corresponds to a sow action for a specific plant type and initializes the respective HUD widget if
     * applicable.
     *
     * @param message the chat message to be checked for sow actions
     *
     * @return true if the message corresponds to a sow action and the HUD widget was updated; false otherwise
     */
    private boolean beginPlantIfSowMessage(final String message) {
        return PlantType.fromSowMessage(message).map(type -> {

            // This is to makes the widget display the plant right after planting it
            // because the server does not send a packet of the plant until it first ticks
            final Plant plant = PlantFactory.createPlant(type);
            switch (type) {
                case HEILKRAUTPFLANZE -> this.heilkrautpflanzeHudWidget.updatePlant(plant);
                case ROSE -> this.roseHudWidget.updatePlant(plant);
                case STOFF -> this.stoffHudWidget.updatePlant(plant);
            }

            return true;
        }).orElse(false);
    }
}
