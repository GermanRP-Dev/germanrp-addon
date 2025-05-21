package eu.germanrp.addon.core.listener;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.Utils;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.serverapi.api.payload.io.PayloadWriter;

import java.util.Map;

public class VehicleHotkeyListener {

    private int cruiseControlSpeed = 50;

    private boolean cruiseControlEnabled = false;

    private final GermanRPAddon addon;
    private final Map<Key, VehicleCommand> keyVehicleCommandMap;

    public VehicleHotkeyListener(GermanRPAddon addon) {
        this.addon = addon;
        this.keyVehicleCommandMap = initializeCommandMap();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onKeyPressed(final KeyEvent event) {
        if(event.state() != KeyEvent.State.PRESS) {
            return;
        }

        if (this.addon.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            return;
        }

        pressedKey(event.key());
    }

    private void pressedKey(final Key key) {
        final VehicleCommand vehicleCommand = this.keyVehicleCommandMap.get(key);

        if(vehicleCommand == null) {
            return;
        }

        sendPayloadToServer(vehicleCommand);
    }

    private void sendPayloadToServer(final VehicleCommand command) {
        final PayloadWriter payloadWriter = new PayloadWriter();
        final JsonObject optionsObject = new JsonObject();

        optionsObject.addProperty("Fahrzeugdata", command.getCommand());
        payloadWriter.writeString(command.getCommand());
        payloadWriter.writeString(command.getCommand()); // I don't know why this is added two times, but that's how it was done in the old addon
        payloadWriter.writeString(optionsObject.toString());

        this.addon.labyAPI().serverController()
                .sendPayload(ResourceLocation.create("labymod3", "main"), payloadWriter.toByteArray());
    }

    @Getter
    @AllArgsConstructor
    private enum VehicleCommand {
        TOGGLE_ENGINE("TOGGLEENGINE"),
        TOGGLE_SIGNAL_LEFT("TOGGLESIGNALLEFT"),
        TOGGLE_SIGNAL_RIGHT("TOGGLESIGNALRIGHT"),
        TOGGLE_SIGNAL_HAZARD_WARN("TOGGLEWARNSIGNAL"),
        TOGGLE_CRUISE_CONTROL("TOGGLECRUISECONTROL"),
        INCREASE_CRUISE_CONTROL("ADDCRUISECONTROL"),
        DECREASE_CRUISE_CONTROL("REMOVECRUISECONTROL"),
        TOGGLE_SOSI("TOGGLESOSI"),
        TOGGLE_SOSI_MUTE("TOGGLESOSIMUTE");

        private final String command;

    }

    private Map<Key, VehicleCommand> initializeCommandMap() {
        final VehicleHotkeyConfig config = addon.configuration().vehicleHotkeyConfig();
        return Map.of(
                config.toggleEngine().get(), VehicleCommand.TOGGLE_ENGINE,
                config.toggleTurnSignalLeft().get(), VehicleCommand.TOGGLE_SIGNAL_LEFT,
                config.toggleTurnSignalRight().get(), VehicleCommand.TOGGLE_SIGNAL_RIGHT,
                config.toggleHazardWarnSignal().get(), VehicleCommand.TOGGLE_SIGNAL_HAZARD_WARN,
                config.toggleCruiseControl().get(), VehicleCommand.TOGGLE_CRUISE_CONTROL,
                config.increaseCruiseControlSpeed().get(), VehicleCommand.INCREASE_CRUISE_CONTROL,
                config.decreaseCruiseControlSpeed().get(), VehicleCommand.DECREASE_CRUISE_CONTROL,
                config.toggleEmergencySignal().get(), VehicleCommand.TOGGLE_SOSI,
                config.toggleEmergencySignalSound().get(), VehicleCommand.TOGGLE_SOSI_MUTE
        );
    }

}
