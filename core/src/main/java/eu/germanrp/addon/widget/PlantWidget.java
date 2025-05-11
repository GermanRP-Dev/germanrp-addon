package eu.germanrp.addon.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.common.enums.PlantEnum;
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
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.exception.PayloadReaderException;
import net.labymod.serverapi.api.payload.io.PayloadReader;

public class PlantWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private int currentTime = 0;
    private int maxTime = 0;
    private int value = 0;
    private String plantType = "Keine";
    private TextLine timeLine;
    private TextLine valueLine;
    private PlantEnum plantEnum = PlantEnum.BOTH;
    private boolean activePlant = false;
    private boolean plantUpdate = false;

    public PlantWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("plant");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/plant.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        timeLine = super.createLine(Component.translatable("germanrpaddon.widget.plant"), "0/0min");
        valueLine = super.createLine(Component.translatable("germanrpaddon.widget.plant"),
                "0g Plantagentyp");
    }

    @Override
    public boolean isVisibleInGame() {
        return activePlant;
    }

    @Override
    public void onTick(boolean isEditorContext) {

        if (activePlant) {
            if (plantEnum == addon.configuration().widgets().plantSetting().get() && !plantUpdate) {
                return;
            }
            if (plantEnum != addon.configuration().widgets().plantSetting().get()) {
                plantEnum = addon.configuration().widgets().plantSetting().get();
                plantUpdate = true;
            }
            if (!plantUpdate) {
                return;
            }
            if (plantEnum == PlantEnum.BOTH) {
                timeLine.setState(State.VISIBLE);
                valueLine.setState(State.VISIBLE);
            }
            if (plantEnum == PlantEnum.RESULT) {

                timeLine.setState(State.HIDDEN);
                valueLine.setState(State.VISIBLE);
            }
            if (plantEnum == PlantEnum.TIMER) {

                timeLine.setState(State.VISIBLE);
                valueLine.setState(State.HIDDEN);
            }
            timeLine.updateAndFlush(currentTime + "/" + maxTime + "min");
            valueLine.updateAndFlush(value + "g " + plantType);
        }
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        currentTime = 0;
        maxTime = 0;
        value = 0;
        plantType = "Plantagentyp";
        activePlant = false;
    }

    @Subscribe
    public void onNetworkPlayload(NetworkPayloadEvent event) {
        if (event.identifier().getNamespace().equals("labymod3") & event.identifier().getPath()
                .equals("main")) {
            try {
                PayloadReader reader = new PayloadReader(event.getPayload());
                String messageKey = reader.readString();
                String messageContent = reader.readString();

                JsonElement serverMessage = GsonUtil.DEFAULT_GSON.fromJson(messageContent, JsonElement.class);

                if (messageKey.equals("GRAddon-Plant")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();

                        boolean active = obj.get("active").getAsBoolean();
                        if (!active) {
                            activePlant = false;
                        }
                        if (active) {

                            activePlant = active;
                            JsonObject timeObj = obj.getAsJsonObject("time");
                            currentTime = timeObj.get("current").getAsInt();
                            maxTime = timeObj.get("max").getAsInt();
                            value = obj.get("value").getAsInt();
                            plantType = obj.get("type").toString().replace("\"", "");
                            plantUpdate = true;
                        }
                    }
                }
            } catch (PayloadReaderException ignored) {
            }
        }
    }
}
