package eu.germanrp.addon.core.services;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.GermanRPAddon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.serverapi.api.payload.io.PayloadWriter;

import static eu.germanrp.addon.core.services.VehicleService.VehicleCommand.*;
import static net.labymod.api.Laby.labyAPI;

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
        sendPayloadToServer(TOGGLE_ENGINE);
    }

    private void applyCruiseControlSpeed() {
        this.addon.getPlayer().sendServerMessage("/tempomat " + this.cruiseControlSpeed);
    }

    public void toggleSignalLeft() {
        sendPayloadToServer(TOGGLE_SIGNAL_LEFT);
    }

    public void toggleSignalRight() {
        sendPayloadToServer(TOGGLE_SIGNAL_RIGHT);
    }

    public void toggleHazardWarnSignal() {
        sendPayloadToServer(TOGGLE_SIGNAL_HAZARD_WARN);
    }

    public void increaseCruiseControlSpeed() {
        this.cruiseControlSpeed += 5;
        applyCruiseControlSpeed();
    }

    public void decreaseCruiseControlSpeed() {
        if (this.cruiseControlSpeed <= 0) {
            return;
        }

        this.cruiseControlSpeed -= 5;
        applyCruiseControlSpeed();
    }

    public void toggleEmergencySignal() {
        sendPayloadToServer(TOGGLE_SOSI);
    }

    public void toggleEmergencySignalSound() {
        sendPayloadToServer(TOGGLE_SOSI_MUTE);
    }

    @Getter
    @AllArgsConstructor
    public enum VehicleCommand {
        TOGGLE_ENGINE("TOGGLEENGINE"),
        TOGGLE_SIGNAL_LEFT("TOGGLESIGNALLEFT"),
        TOGGLE_SIGNAL_RIGHT("TOGGLESIGNALRIGHT"),
        TOGGLE_SIGNAL_HAZARD_WARN("TOGGLEWARNSIGNAL"),
        TOGGLE_SOSI("TOGGLESOSI"),
        TOGGLE_SOSI_MUTE("TOGGLESOSIMUTE");

        private final String command;
    }

    private void sendPayloadToServer(final VehicleCommand command) {
        final PayloadWriter payloadWriter = new PayloadWriter();
        final JsonObject optionsObject = new JsonObject();

        optionsObject.addProperty("Fahrzeugdata", command.getCommand());
        payloadWriter.writeString(command.getCommand());
        payloadWriter.writeString(optionsObject.toString());

        labyAPI().serverController().sendPayload(ResourceLocation.create("labymod3", "main"), payloadWriter.toByteArray());
    }
}
