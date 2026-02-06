package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.events.GermanRPChatReceiveEvent;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.GlobalRegexRegistry;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import eu.germanrp.addon.core.services.VehicleService;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

import static net.labymod.api.Laby.labyAPI;

public class VehicleHotkeyListener {

    private final GermanRPAddon addon;
    private final VehicleService vehicleService;

    public VehicleHotkeyListener(GermanRPAddon addon) {
        this.addon = addon;
        this.vehicleService = addon.getVehicleService();
    }

    @Subscribe
    public void onChatMessageReceiveEvent(final GermanRPChatReceiveEvent event) {
        final String plainText = event.chatMessage().getPlainText();

        if (plainText.equals("► Du sitzt in keinem Fahrzeug.") || plainText.equals("► Tempomat wurde deaktiviert.")) {
            this.vehicleService.setCruiseControlEnabled(false);
        }

        if (GlobalRegexRegistry.CRUISE_CONTROL_START.getPattern().matcher(plainText).matches()) {
            this.vehicleService.setCruiseControlEnabled(true);
        }
    }

    @Subscribe
    public void onKeyPressed(final KeyEvent event) {
        if (event.state() != KeyEvent.State.PRESS) {
            return;
        }

        if (labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            return;
        }

        pressedKey(event.key());
    }

    private void pressedKey(final @NotNull Key key) {
        VehicleHotkeyConfig config = this.addon.configuration().vehicleHotkeyConfig();

        if (key.equals(config.toggleEngine().get())) {
            this.addon.getVehicleService().toggleEngine();
        } else if (key.equals(config.toggleTurnSignalLeft().get())) {
            this.addon.getVehicleService().toggleSignalLeft();
        } else if (key.equals(config.toggleTurnSignalRight().get())) {
            this.addon.getVehicleService().toggleSignalRight();
        } else if (key.equals(config.toggleHazardWarnSignal().get())) {
            this.addon.getVehicleService().toggleHazardWarnSignal();
        } else if (key.equals(config.toggleEmergencySignal().get())) {
            this.addon.getVehicleService().toggleEmergencySignal();
        } else if (key.equals(config.toggleEmergencySignalSound().get())) {
            this.addon.getVehicleService().toggleEmergencySignalSound();
        } else if (key.equals(config.increaseCruiseControlSpeed().get())) {
            this.addon.getVehicleService().increaseCruiseControlSpeed();
        } else if (key.equals(config.decreaseCruiseControlSpeed().get())) {
            this.addon.getVehicleService().decreaseCruiseControlSpeed();
        }
    }
}
