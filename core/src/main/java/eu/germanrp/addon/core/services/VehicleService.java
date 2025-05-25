package eu.germanrp.addon.core.services;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.GermanRPAddon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.Laby;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.serverapi.api.payload.io.PayloadWriter;

@Getter
@Setter
public class VehicleService {

    private final GermanRPAddon addon;

    private boolean cruiseControlEnabled = false;
    private int cruiseControlSpeed = 50;

    public VehicleService(GermanRPAddon addon) {
        this.addon = addon;
    }

    public void toggleEngine() {
        sendPayloadToServer(VehicleCommand.TOGGLE_ENGINE);
    }

    private void applyCruiseControlSpeed() {
        Laby.references().chatExecutor().chat("/tempomat " + cruiseControlSpeed, false);
    }

    public void toggleSignalLeft() {
        sendPayloadToServer(VehicleCommand.TOGGLE_SIGNAL_LEFT);
    }

    public void toggleSignalRight() {
        sendPayloadToServer(VehicleCommand.TOGGLE_SIGNAL_RIGHT);
    }

    public void toggleHazardWarnSignal() {
        sendPayloadToServer(VehicleCommand.TOGGLE_SIGNAL_HAZARD_WARN);
    }

    public void increaseCruiseControlSpeed() {
        cruiseControlSpeed += 5;
        applyCruiseControlSpeed();
    }

    public void decreaseCruiseControlSpeed() {
        if(cruiseControlSpeed <= 0) {
            return;
        }

        cruiseControlSpeed -= 5;
        applyCruiseControlSpeed();
    }

    public void toggleEmergencySignal() {
        sendPayloadToServer(VehicleCommand.TOGGLE_SOSI);
    }

    public void toggleEmergencySignalSound() {
        sendPayloadToServer(VehicleCommand.TOGGLE_SOSI_MUTE);
    }


    @Getter
    @AllArgsConstructor
    public enum VehicleCommand {
        TOGGLE_ENGINE("TOGGLEENGINE"),
        TOGGLE_SIGNAL_LEFT("TOGGLESIGNALLEFT"),
        TOGGLE_SIGNAL_RIGHT("TOGGLESIGNALRIGHT"),
        TOGGLE_SIGNAL_HAZARD_WARN("TOGGLEWARNSIGNAL"),
        INCREASE_CRUISE_CONTROL("ADDCRUISECONTROL"),
        DECREASE_CRUISE_CONTROL("REMOVECRUISECONTROL"),
        TOGGLE_SOSI("TOGGLESOSI"),
        TOGGLE_SOSI_MUTE("TOGGLESOSIMUTE");

        private final String command;

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

}
