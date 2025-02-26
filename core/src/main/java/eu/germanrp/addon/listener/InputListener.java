package eu.germanrp.addon.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.enums.HydrationEnum;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.exception.PayloadReaderException;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import net.labymod.serverapi.api.payload.io.PayloadWriter;

import java.util.Map;

public class InputListener {

    private final GermanRPAddon addon;

    private final Map<String, String> yakuzaWords = Map.of(
            "tschüss", "Sayonara",
            "hallo", "Kon'nichiwa",
            "danke", "Arigato",
            "nein", "Ie",
            "natürlich", "Tozen",
            "vielleicht", "Tabun");

    private final Map<String, String> establishmentWords = Map.of(
            "polizist", "Bobbie",
            "entschuldigung", "I beg you pardon",
            "danke", "My sincere appreciation",
            "hallo", "Hello",
            "ja", "Yes",
            "nein", "No",
            "tschüss", "Goodbye",
            "selbstverständlich", "Of course");

    private final Map<String, String> camorraWords = Map.of(
            "hallo", "Ciao",
            "tschüss", "Ciao",
            "hey", "Ciao");

    private final Map<String, String> medellinWords = Map.of(
            "hallo", "Holá",
            "danke", "Gracias",
            "freund", "amigo",
            "frau", "señora",
            "herr", "señor",
            "bruder", "hermano",
            "schwester", "hermana",
            "mutter", "madre",
            "vater", "padre");

    private final Map<String, String> medellinWordsTwo = Map.of(
            "onkel", "tío",
            "tante", "tía",
            "opa", "abuelo",
            "oma", "abuelita",
            "sohn", "hijo",
            "tochter", "hija",
            "ich", "I",
            "bitte", "por favor",
            "entschuldigung", "Lo siento",
            "moment", "Momento");

    private final Map<String, String> ocallaghanWords = Map.of(
            "hallo", "Dia dhuit",
            "ja", "Tá",
            "nein", "Níl",
            "gerne", "Go sásta",
            "idiot", "leathcheann",
            "arschloch", "asshole",
            "ich", "mé",
            "chefin", "Bórd",
            "chef", "Bórd",
            "kollege", "Comhghleacaí");

    private final Map<String, String> ocallaghanWordsTwo = Map.of(
            "kollegin", "comhoibrí",
            "freund", "Cara",
            "frau", "Bean",
            "herr", "Mistir",
            "mutter", "Máthair",
            "vater", "Athair",
            "onkel", "Uncail",
            "tante", "Aintín",
            "hurensohn", "Mac soith");

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
        if (event.identifier().getNamespace().equals("labymod3") & event.identifier().getPath()
                .equals("main")) {
            try {
                PayloadReader reader = new PayloadReader(event.getPayload());
                String messageKey = reader.readString();
                String messageContent = reader.readString();
                JsonElement serverMessage = GsonUtil.DEFAULT_GSON.fromJson(messageContent,
                        JsonElement.class);
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
    public void onMessageSend(ChatMessageSendEvent event) {

        String message = event.getMessage();
        boolean antiChatOOC = addon.configuration().antichatooc().get();

        if (antiChatOOC) {
            event.changeMessage(changeOOCMessage(message));
        }

        boolean camorra = addon.configuration().languages().camorra().get();
        boolean establishment = addon.configuration().languages().establishment().get();
        boolean medellin = addon.configuration().languages().medellin().get();
        boolean ocallaghan = addon.configuration().languages().ocallaghan().get();
        boolean yakuza = addon.configuration().languages().yakuza().get();

        if (camorra || establishment || medellin || ocallaghan || yakuza) {
            event.changeMessage(changeLanguage(message.toLowerCase()));
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
            HydrationEnum hydrationType = addon.configuration().hydration().notificationtype().get();
            if (hydrationType == HydrationEnum.NONE) {
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
            HydrationEnum hydrationType = addon.configuration().hydration().notificationtype().get();
            if (hydrationType == HydrationEnum.NONE || hydrationType == HydrationEnum.UNDERTEN) {
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
            HydrationEnum hydrationType = addon.configuration().hydration().notificationtype().get();
            if (hydrationType != HydrationEnum.ALL) {
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

    private String changeLanguage(String message) {
        if (message.startsWith("/tc") || message.startsWith("/ac")
                || message.startsWith("/gr") || message.startsWith("/l")
                || message.startsWith("/lg") || message.startsWith("/ooc")
                || message.startsWith("/nc") || message.startsWith("/f")
                || message.startsWith("/gc")) {
            return message;
        }
        if (!message.startsWith("/say") && ticket) {
            return message;
        }
        boolean camorra = addon.configuration().languages().camorra().get();
        boolean establishment = addon.configuration().languages().establishment().get();
        boolean medellin = addon.configuration().languages().medellin().get();
        boolean ocallaghan = addon.configuration().languages().ocallaghan().get();
        boolean yakuza = addon.configuration().languages().yakuza().get();

        if (yakuza) {
            for (String s : message.split(" ")) {
                if (yakuzaWords.containsKey(s)) {
                    message = message.replace(s, yakuzaWords.get(s));
                }
            }
            return message;
        }

        if (establishment) {
            for (String s : message.split(" ")) {
                if (establishmentWords.containsKey(s)) {
                    message = message.replace(s, establishmentWords.get(s)).replace("Bobbieen", "Bobbies");
                }
            }
            return message;
        }

        if (camorra) {
            for (String s : message.split(" ")) {
                if (camorraWords.containsKey(s)) {
                    message = message.replace(s, camorraWords.get(s));
                }
            }
            return message;
        }

        if (medellin) {

            for (String s : message.split(" ")) {
                if (medellinWords.containsKey(s)) {
                    message = message.replace(s, medellinWords.get(s)).replace("amigoin", "amiga");
                } else if (medellinWordsTwo.containsKey(s)) {
                    message = message.replace(s, medellinWordsTwo.get(s));
                }
            }
            return message;
        }

        if (ocallaghan) {
            for (String s : message.split(" ")) {
                if (ocallaghanWords.containsKey(s)) {
                    message = message.replace(s, ocallaghanWords.get(s));
                } else if (ocallaghanWordsTwo.containsKey(s)) {
                    message = message.replace(s, ocallaghanWordsTwo.get(s)).replace("Carain", "Chailín");
                }
            }
            return message;
        }
        return message;
    }

    private String changeOOCMessage(String message) {
        if (message.toLowerCase().contains(" ooc ") || message.toLowerCase().contains(" oos ")) {
            return "/ooc " + message.replace(" ooc ", " ").replace(" oos ", " ");
        }
        if (message.toLowerCase().startsWith("ooc ") || message.toLowerCase()
                .startsWith("oos ")) {
            return "/ooc " + message.replace("ooc ", "").replace("oos ", "");
        }
        if (message.toLowerCase().endsWith(" ooc") || message.toLowerCase().endsWith(" oos")) {
            return "/ooc " + message.replace(" ooc", "").replace(" oos", "");
        }
        return message;
    }

    private void Hotkey(Key key) {

        Key cruiseControl = addon.configuration().hotkeys().cruiseControlKey().get();
        Key cruiseControlUp = addon.configuration().hotkeys().cruiseControlUp().get();
        Key cruiseControlDown = addon.configuration().hotkeys().cruiseControlDown().get();
        Key job = addon.configuration().hotkeys().jobKey().get();
        Key engine = addon.configuration().hotkeys().engineOnOffKey().get();
        Key sosiOnOff = addon.configuration().hotkeys().sosiOnOffKey().get();
        Key sosiMute = addon.configuration().hotkeys().sosiMuteKey().get();
        Key signalLeft = addon.configuration().hotkeys().signalLeftKey().get();
        Key signalRight = addon.configuration().hotkeys().signalRightKey().get();
        Key warnSignal = addon.configuration().hotkeys().warnSignalKey().get();

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
        addon.labyAPI().serverController()
                .sendPayload(ResourceLocation.create("labymod3", "main"), payloadWriter.toByteArray());
    }
}
