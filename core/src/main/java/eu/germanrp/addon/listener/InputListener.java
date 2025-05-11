package eu.germanrp.addon.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.common.enums.HydrationNotification;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.exception.PayloadReaderException;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import net.labymod.serverapi.api.payload.io.PayloadWriter;

public class InputListener {

    private final GermanRPAddon addon;

    private String cruiseControlAmount = "50";
    private String jobHotkeyString = null;
    private boolean activeCruiseControl = false;
    private boolean ticket = false;

    public InputListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onKey(KeyEvent event) {
        if (Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            return;
        }
        if (event.state() == State.PRESS) {
            Hotkey(event.key());
        }
    }

    @Subscribe
    public void onNetworkPlayload(NetworkPayloadEvent event) {
        ResourceLocation resourceLocation = event.identifier();
        if (resourceLocation.getNamespace().equals("labymod3") & resourceLocation.getPath().equals("main")) {
            try {
                PayloadReader reader = new PayloadReader(event.getPayload());
                String messageKey = reader.readString();
                String messageContent = reader.readString();
                JsonElement serverMessage = GsonUtil.DEFAULT_GSON.fromJson(messageContent, JsonElement.class);
                if (serverMessage.isJsonObject()) {
                    JsonObject obj = serverMessage.getAsJsonObject();
                    if (messageKey.equals("CURRENTTEMPOMAT")) {
                        if (obj.has("value")) {
                            String value = obj.get("value").toString();
                            if (!value.equals("-1")) {
                                cruiseControlAmount = value;
                            }
                        }
                    }
                    if (messageKey.equals("GRAddon-JobHotkey")) {
                        if (obj.has("value")) {
                            jobHotkeyString = obj.get("value").toString();
                        }
                    }
                }
            } catch (PayloadReaderException ignored) {
            }
        }
    }

    @Subscribe
    public void onMessageReceive(ChatReceiveEvent event) {
        String message = event.chatMessage().getOriginalPlainText();
        if (message.equals("► Du sitzt in keinem Fahrzeug.")) {
            activeCruiseControl = false;
        }
        if (message.equals("► Tempomat wurde deaktiviert.")) {
            activeCruiseControl = false;
        }
        if (message.equals("► [Support] Nutze /say [Nachricht], wenn du etwas außerhalb des Tickets sagen möchtest. Schreibe ansonsten im normalen Chat.")) {
            ticket = true;
        }
        if (message.startsWith("► [Support] ") && message.contains("beendet.")) {
            ticket = false;
        }

        //>10%
        if (message.equals("► Du bist sehr durstig. (Trinke etwas, um nicht zu dehydrieren!)")) {
            HydrationNotification hydrationType = addon.configuration().hydration().notificationtype().get();
            if (hydrationType == HydrationNotification.NONE) {
                event.setCancelled(true);
                return;
            }
            boolean inActionBar = addon.configuration().hydration().actionbar().get();
            if (inActionBar) {
                addon.labyAPI().minecraft().chatExecutor().displayActionBar(event.message());
                event.setCancelled(true);
            }
        }
        //>30%
        if (message.equals("► Du bist sehr durstig.")) {
            HydrationNotification hydrationType = addon.configuration().hydration().notificationtype().get();
            if (hydrationType == HydrationNotification.NONE || hydrationType == HydrationNotification.UNDERTEN) {
                event.setCancelled(true);
                return;
            }
            boolean inActionBar = addon.configuration().hydration().actionbar().get();
            if (inActionBar) {
                addon.labyAPI().minecraft().chatExecutor().displayActionBar(event.message());

                event.setCancelled(true);
            }
        }
        //>60%
        if (message.equals("► Du bist durstig.")) {
            HydrationNotification hydrationType = addon.configuration().hydration().notificationtype().get();
            if (hydrationType != HydrationNotification.ALL) {
                event.setCancelled(true);
                return;
            }
            boolean inActionBar = addon.configuration().hydration().actionbar().get();
            if (inActionBar) {
                addon.labyAPI().minecraft().chatExecutor().displayActionBar(event.message());
                event.setCancelled(true);
            }
        }
    }

    private void Hotkey(Key key) {

        Key cruiseControl = addon.configuration().hotkeys().cruiseControlToggle().get();
        Key cruiseControlUp = addon.configuration().hotkeys().cruiseControlUp().get();
        Key cruiseControlDown = addon.configuration().hotkeys().cruiseControlDown().get();
        Key job = addon.configuration().hotkeys().job().get();
        Key engine = addon.configuration().hotkeys().engineToggle().get();
        Key sosiOnOff = addon.configuration().hotkeys().emergencySignalToggle().get();
        Key sosiMute = addon.configuration().hotkeys().emergencySignalMute().get();
        Key signalLeft = addon.configuration().hotkeys().turnSignalToggleLeft().get();
        Key signalRight = addon.configuration().hotkeys().turnSignalToggleRight().get();
        Key warnSignal = addon.configuration().hotkeys().hazardLightsToggle().get();

        // Tempomat \\
        if (key == cruiseControl) {
            activeCruiseControl = !activeCruiseControl;
            if (!activeCruiseControl) {
                sendVehicleDataToSever("TOGGLECRUISECONTROL");
            } else {
                if (cruiseControlAmount.equals("-1") || cruiseControlAmount.equals("0")) {
                    cruiseControlAmount = "5";
                }
                Laby.references().chatExecutor().chat("/tempomat " + cruiseControlAmount);
            }
        }
        if (activeCruiseControl) {
            if (key == cruiseControlUp) {
                sendVehicleDataToSever("ADDCRUISECONTROL");
            } else if (key == cruiseControlDown) {
                sendVehicleDataToSever("REMOVECRUISECONTROL");
            }
        }
        // Job-Befehl \\
        if (key == job) {
            if (jobHotkeyString == null) {
                return;
            }
            Laby.references().chatExecutor().chat(jobHotkeyString.replace("\"", ""));
        }
        // Motor \\
        if (key == engine) {
            sendVehicleDataToSever("TOGGLEENGINE");
        }
        // Sondersignal \\
        if (key == sosiOnOff) {
            sendVehicleDataToSever("TOGGLESOSI");
        }
        if (key == sosiMute) {
            sendVehicleDataToSever("TOGGLESOSIMUTE");
        }
        // Blinker \\
        if (key == signalLeft) {
            sendVehicleDataToSever("TOGGLESIGNALLEFT");
        }
        if (key == signalRight) {
            sendVehicleDataToSever("TOGGLESIGNALRIGHT");
        }
        if (key == warnSignal) {
            sendVehicleDataToSever("TOGGLEWARNSIGNAL");
        }
    }

    private void sendVehicleDataToSever(String key) {
        PayloadWriter payloadWriter = new PayloadWriter();
        JsonObject optionsObject = new JsonObject();
        optionsObject.addProperty("Fahrzeugdata", key);
        payloadWriter.writeString(key);
        payloadWriter.writeString(key);
        payloadWriter.writeString(optionsObject.toString());
        this.addon.labyAPI().serverController()
                .sendPayload(ResourceLocation.create("labymod3", "main"), payloadWriter.toByteArray());
    }
}
