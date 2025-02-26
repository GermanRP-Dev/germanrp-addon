package eu.germanrp.addon.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.exception.PayloadReaderException;
import net.labymod.serverapi.api.payload.io.PayloadReader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimerWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private long businessTime = 0;
    private long pharmacyTime = 0;
    private long jewelryTime = 0;
    private long museumTime = 0;
    private long hackTime = 0;
    private long bombTime = 0;
    private TextLine businessLine;
    private TextLine pharmacyLine;
    private TextLine jewelryLine;
    private TextLine museumLine;
    private TextLine hackLine;
    private TextLine bombLine;
    private boolean isBusinessTimer = false;
    private boolean isPharmacyTimer = false;
    private boolean isJewelryTimer = false;
    private boolean isMuseumTimer = false;
    private boolean isHackTimer = false;
    private boolean isBombTimer = false;
    private int ticks;

    public TimerWidget(GermanRPAddon addon, GermanRPCategory category) {
        super("timer");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/timer.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        businessLine = super.createLine(Component.translatable("germanrpaddon.widget.business"), "0:00");
        pharmacyLine = super.createLine(Component.translatable("germanrpaddon.widget.pharmacy"), "0:00");
        jewelryLine = super.createLine(Component.translatable("germanrpaddon.widget.jewelry"), "0:00");
        museumLine = super.createLine(Component.translatable("germanrpaddon.widget.museum"), "0:00");
        hackLine = super.createLine(Component.translatable("germanrpaddon.widget.hack"), "0:00");
        bombLine = super.createLine(Component.translatable("germanrpaddon.widget.bomb"), "0:00");
        setState(businessLine, State.HIDDEN);
        setState(pharmacyLine, State.HIDDEN);
        setState(jewelryLine, State.HIDDEN);
        setState(museumLine, State.HIDDEN);
        setState(hackLine, State.HIDDEN);
        setState(bombLine, State.HIDDEN);
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        ticks++;
        if (ticks != 10) {
            return;
        }
        ticks = 0;
        //Gewerberaub
        if (isBusinessTimer) {
            boolean businessTimer = addon.configuration().timer().businessrobtimer().get();
            if (businessTimer) {
                String timeString = formatTime(System.currentTimeMillis() - businessTime);
                setState(businessLine, State.VISIBLE);
                setText(businessLine, timeString);
                if (timeString.equals("10:00")) {
                    isBusinessTimer = false;
                    setText(businessLine, timeString);
                    setState(businessLine, State.HIDDEN);
                }
            }
        } else {
            setText(businessLine, "0:00");
            setState(businessLine, State.HIDDEN);
        }
        //Apothekenraub
        if (isPharmacyTimer) {
            boolean pharmacyTimer = addon.configuration().timer().pharmacyrobtimer().get();
            if (pharmacyTimer) {
                String timeString = formatTime(System.currentTimeMillis() - pharmacyTime);
                setState(pharmacyLine, State.VISIBLE);
                setText(pharmacyLine, timeString);
                if (timeString.equals("20:00")) {
                    isPharmacyTimer = false;
                    setText(pharmacyLine, timeString);
                    setState(pharmacyLine, State.HIDDEN);
                }
            }
        } else {
            setText(pharmacyLine, "0:00");
            setState(pharmacyLine, State.HIDDEN);
        }
        //Juwelenraub
        if (isJewelryTimer) {
            boolean jewelryTimer = addon.configuration().timer().jewelryrobtimer().get();
            if (jewelryTimer) {
                String timeString = formatTime(System.currentTimeMillis() - jewelryTime);
                setState(jewelryLine, State.VISIBLE);
                setText(jewelryLine, timeString);
                if (timeString.equals("30:00")) {
                    isJewelryTimer = false;
                    setText(jewelryLine, timeString);
                    setState(jewelryLine, State.HIDDEN);
                }
            }
        } else {
            setText(jewelryLine, "0:00");
            setState(jewelryLine, State.HIDDEN);
        }
        //Museumsraub
        if (isMuseumTimer) {
            boolean museumTimer = addon.configuration().timer().museumrobtimer().get();
            if (museumTimer) {
                String timeString = formatTime(System.currentTimeMillis() - museumTime);
                setState(museumLine, State.VISIBLE);
                setText(museumLine, timeString);
                if (timeString.equals("30:00")) {
                    isMuseumTimer = false;
                    setText(museumLine, timeString);
                    setState(museumLine, State.HIDDEN);
                }
            }
        } else {
            setText(museumLine, "0:00");
            setState(museumLine, State.HIDDEN);
        }
        //Hackangriff
        if (isHackTimer) {
            boolean hackTimer = addon.configuration().timer().hacktimer().get();
            if (hackTimer) {
                String timeString = formatTime(System.currentTimeMillis() - hackTime);
                setState(hackLine, State.VISIBLE);
                setText(hackLine, timeString);
                if (timeString.equals("20:00")) {
                    isHackTimer = false;
                    setText(hackLine, timeString);
                    setState(hackLine, State.HIDDEN);
                }
            }
        } else {
            setText(hackLine, "0:00");
            setState(hackLine, State.HIDDEN);
        }
        //Bombe
        if (isBombTimer) {
            boolean bombTimer = addon.configuration().timer().bombtimer().get();
            if (bombTimer) {
                String timeString = formatTime(System.currentTimeMillis() - bombTime);
                setState(bombLine, State.VISIBLE);
                setText(bombLine, timeString);
                if (timeString.equals("20:00")) {
                    isBombTimer = false;
                    setText(bombLine, timeString);
                    setState(bombLine, State.HIDDEN);
                }
            }
        } else {
            setText(bombLine, "0:00");
            setState(bombLine, State.HIDDEN);
        }
    }

    @Subscribe
    public void onNetworkPlayload(NetworkPayloadEvent event) {
        if (event.identifier().getNamespace().equals("labymod3") & event.identifier().getPath().equals("main")) {
            try {

                PayloadReader reader = new PayloadReader(event.getPayload());
                String messageKey = reader.readString();
                String messageContent = reader.readString();
                JsonElement serverMessage = GsonUtil.DEFAULT_GSON.fromJson(messageContent, JsonElement.class);
                if (messageKey.equals("GRAddon-Timer")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        if (obj.has("name")) {
                            String name = obj.get("name").toString().replace("\"", "").toLowerCase();
                            if (name.equals("keiner") || name.equals("-")) {
                                stopTimer();
                                return;
                            }

                            boolean active = obj.get("active").getAsBoolean();
                            if (!active) {
                                stopTimer();
                                return;
                            }

                            if (obj.has("start")) {
                                startTimer(name, Long.parseLong(obj.get("start").toString().replace("\"", "")));
                            } else {
                                startTimer(name, System.currentTimeMillis());
                            }
                        }
                    }
                }
            } catch (PayloadReaderException ignored) {
            }
        }
    }

    private void setState(TextLine line, State state) {
        if (line.state() == State.VISIBLE) {
            line.setState(state);
        }
        if (line.state() == State.HIDDEN) {
            line.setState(state);
        }
    }

    private void setText(TextLine line, String text) {
        if (line.state() != State.HIDDEN) {
            line.updateAndFlush(text);
        }
    }

    private void stopTimer() {
        isBusinessTimer = false;
        isPharmacyTimer = false;
        isJewelryTimer = false;
        isMuseumTimer = false;
        isHackTimer = false;
        isBombTimer = false;
    }

    private void startTimer(String type, long startTime) {
        switch (type) {
            case "raub" -> {
                if (isBusinessTimer) {
                    return;
                }
                isBusinessTimer = true;
                businessTime = startTime;
            }
            case "apothekenraub" -> {
                if (isPharmacyTimer) {
                    return;
                }
                isPharmacyTimer = true;
                pharmacyTime = startTime;
            }
            case "juwelier" -> {
                if (isJewelryTimer) {
                    return;
                }
                isJewelryTimer = true;
                jewelryTime = startTime;
            }
            case "bombe" -> {
                if (isBombTimer) {
                    return;
                }
                isBombTimer = true;
                bombTime = startTime;
            }
        }
        if (type.contains("hack")) {
            if (isHackTimer) {
                return;
            }
            isHackTimer = true;
            hackTime = startTime;
        }
        if (type.contains("museum")) {
            if (isMuseumTimer) {
                return;
            }
            isMuseumTimer = true;
            museumTime = startTime;
        }
    }

    private String formatTime(long time) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("mm:ss");
        return DATE_FORMAT.format(new Date(time));
    }
}
