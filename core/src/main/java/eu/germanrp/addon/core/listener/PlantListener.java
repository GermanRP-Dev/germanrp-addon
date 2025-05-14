package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantFactory;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.api.network.PaketFactory;
import eu.germanrp.addon.api.network.PlantPaket;
import eu.germanrp.addon.core.GRUtilsAddon;
import eu.germanrp.addon.core.Utils;
import eu.germanrp.addon.core.executor.PlaySoundExecutor;
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
import java.util.regex.Pattern;

import static eu.germanrp.addon.api.models.PlantHeilkraut.FERTILIZE_TIME;
import static eu.germanrp.addon.api.models.PlantHeilkraut.WATER_TIME;

public class PlantListener {

    private static final Pattern HARVEST_PATTERN = Pattern.compile(
            "^► Du hast \\S* (\\S+) mit \\d+(?: Stück|x|g)? Erlös geerntet\\.$", Pattern.CANON_EQ);
    private static final String PLANT_DIED_MESSAGE = "► Du hast deine Pflanze nicht rechtzeitig geerntet.";
    public static final String HEILKRAUT_FERTILIZE_MESSAGE = "germanrputils.message.plant.heilkrautpflanze.fertilize";
    public static final String HEILKRAUT_WATER_MESSAGE = "germanrputils.message.plant.heilkrautpflanze.water";
    public static final String PLANT_HARVEST_MESSAGE = "germanrputils.message.plant.harvest";
    public static final TextColor NOTIFICATION_COLOR = TextColor.color(0x75, 0x15, 0x1E);

    private final GRUtilsAddon addon;
    private final HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private final RoseHudWidget roseHudWidget;
    private final StoffHudWidget stoffHudWidget;
    private final PlaySoundExecutor playSoundExecutor;

    public PlantListener(
            final GRUtilsAddon addon
    ) {
        this.addon = addon;
        this.heilkrautpflanzeHudWidget = addon.getHeilkrautpflanzeHudWidget();
        this.roseHudWidget = addon.getRoseHudWidget();
        this.stoffHudWidget = addon.getStoffHudWidget();
        this.playSoundExecutor = addon.getPlaySoundExecutor();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveEvent(final ChatReceiveEvent event) {
        final String message = event.chatMessage().getPlainText();

        if (message.equals(PLANT_DIED_MESSAGE)) {
            // TODO somehow find out which plant died...
            return;
        }

        if (beginPlantIfSowMessage(message)) {
            return;
        }

        final Matcher matcher = HARVEST_PATTERN.matcher(message);

        if (!matcher.matches()) {
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
    @SuppressWarnings("unused")
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

            handleHeilkrautNotifications(plantPaket);

            if (plantPaket.getCurrentTime() != plantPaket.getMaxTime()) {
                return;
            }

            sendHarvestNotification(plantPaket);
        });
    }

    private void sendHarvestNotification(PlantPaket plantPaket) {
        addon.displayMessage(Component.translatable(
                        PLANT_HARVEST_MESSAGE,
                        Component.text(plantPaket.getType().getDisplayName())
                )
                .color(NOTIFICATION_COLOR));
        playSoundExecutor.playNotePlingSound();
    }

    private void handleHeilkrautNotifications(final PlantPaket plantPaket) {
        if (plantPaket.getType() != PlantType.HEILKRAUTPFLANZE) {
            return;
        }

        if (plantPaket.getCurrentTime() == FERTILIZE_TIME && plantPaket.isActive()) {
            addon.displayMessage(Component.translatable(HEILKRAUT_FERTILIZE_MESSAGE)
                    .color(NOTIFICATION_COLOR));
            playSoundExecutor.playNotePlingSound();
        }

        if (plantPaket.getCurrentTime() == WATER_TIME && plantPaket.isActive()) {
            addon.displayMessage(Component.translatable(HEILKRAUT_WATER_MESSAGE)
                    .color(NOTIFICATION_COLOR));
            playSoundExecutor.playNotePlingSound();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onServerDisconnectEvent(final ServerDisconnectEvent ignored) {
        this.heilkrautpflanzeHudWidget.reset();
        this.roseHudWidget.reset();
        this.stoffHudWidget.reset();
    }

    /**
     * Checks if the given message corresponds to a sow action for a specific plant type and
     * initializes the respective HUD widget if applicable.
     *
     * @param message the chat message to be checked for sow actions
     * @return true if the message corresponds to a sow action and the HUD widget was updated; false
     * otherwise
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
