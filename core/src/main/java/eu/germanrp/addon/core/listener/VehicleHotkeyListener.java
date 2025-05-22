package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.GlobalRegexRegistry;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import eu.germanrp.addon.core.services.VehicleService;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.input.KeyEvent;

import java.util.List;
import java.util.Map;

public class VehicleHotkeyListener {

    private int cruiseControlSpeed = 50;

    private boolean cruiseControlEnabled = false;

    private final GermanRPAddon addon;
    private final VehicleService vehicleService;
    private final List<Map.Entry<ConfigProperty<Key>, Runnable>> keyActionList;

    public VehicleHotkeyListener(GermanRPAddon addon) {
        this.addon = addon;
        this.vehicleService = addon.getVehicleService();
        this.keyActionList = initializeKeyActionList();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessageReceiveEvent(final ChatReceiveEvent event) {
        final String plainText = event.chatMessage().getPlainText();

        if(plainText.equals("► Du sitzt in keinem Fahrzeug.") || plainText.equals("► Tempomat wurde deaktiviert.")) {
            vehicleService.setCruiseControlEnabled(false);
        }

        if (GlobalRegexRegistry.CRUISE_CONTROL_START.getPattern().matcher(plainText).matches()) {
            vehicleService.setCruiseControlEnabled(true);
        }

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
        keyActionList.stream()
                .filter(entry -> entry.getKey().get().equals(key))
                .forEach(entry -> entry.getValue().run());
    }

    private List<Map.Entry<ConfigProperty<Key>, Runnable>> initializeKeyActionList() {
        final VehicleHotkeyConfig config = addon.configuration().vehicleHotkeyConfig();
        final List<Map.Entry<ConfigProperty<Key>, Runnable>> actionList = new ArrayList<>();
        actionList.add(Map.entry(config.toggleEngine(), vehicleService::toggleEngine));
        actionList.add(Map.entry(config.toggleCruiseControl(), vehicleService::toggleCruiseControl));
        actionList.add(Map.entry(config.toggleTurnSignalLeft(), vehicleService::toggleSignalLeft));
        actionList.add(Map.entry(config.toggleTurnSignalRight(), vehicleService::toggleSignalRight));
        actionList.add(Map.entry(config.toggleHazardWarnSignal(), vehicleService::toggleHazardWarnSignal));
        actionList.add(Map.entry(config.toggleEmergencySignal(), vehicleService::toggleEmergencySignal));
        actionList.add(Map.entry(config.toggleEmergencySignalSound(), vehicleService::toggleEmergencySignalSound));
        actionList.add(Map.entry(config.increaseCruiseControlSpeed(), vehicleService::increaseCruiseControlSpeed));
        actionList.add(Map.entry(config.decreaseCruiseControlSpeed(), vehicleService::decreaseCruiseControlSpeed));
        return actionList;
    }

}
