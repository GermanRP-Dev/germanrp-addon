package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantFactory;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.api.network.PaketFactory;
import eu.germanrp.addon.api.network.PlantPaket;
import eu.germanrp.addon.core.GRUtilsAddon;
import eu.germanrp.addon.core.Utils;
import eu.germanrp.addon.core.widget.HeilkrautpflanzeHudWidget;
import eu.germanrp.addon.core.widget.RoseHudWidget;
import eu.germanrp.addon.core.widget.StoffHudWidget;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.util.GsonUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantListener {

    private static final Pattern HARVEST_PATTERN = Pattern.compile(
            "^► Du hast \\S* (\\S+) mit \\d+(?: Stück|x|g)? Erlös geerntet\\.$", Pattern.CANON_EQ);
    private static final String PLANT_DIED_MESSAGE = "► Du hast deine Pflanze nicht rechtzeitig geerntet.";

    private final GRUtilsAddon addon;
    private final HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private final RoseHudWidget roseHudWidget;
    private final StoffHudWidget stoffHudWidget;

    public PlantListener(
            final GRUtilsAddon addon
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

        final Matcher matcher = HARVEST_PATTERN.matcher(message);

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

        });
    }

    @Subscribe
    public void onServerDisconnectEvent(final ServerDisconnectEvent event) {
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
