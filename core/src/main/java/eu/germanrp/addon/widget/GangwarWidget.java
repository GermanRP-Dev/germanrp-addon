package eu.germanrp.addon.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.enums.GangwarEnum;
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

public class GangwarWidget extends TextHudWidget<TextHudWidgetConfig> {

    boolean updateGangWar = false;
    private final GermanRPAddon addon;
    private String pointsTeamOne = "-1";
    private String pointsTeamTwo = "-1";
    private String teamOne = "Polizei";
    private String teamTwo = "Rettungsdienst";
    private String gangZone = "Krankenhaus";
    private TextLine teamLine;
    private TextLine zoneLine;
    private TextLine valueLine;
    private GangwarEnum gangwarEnum = GangwarEnum.ALL;
    private boolean activeGangwar = false;

    public GangwarWidget(GermanRPAddon addon, GermanRPCategory category) {
        super("gangwar");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/gangwar.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        teamLine = super.createLine(Component.translatable("germanrpaddon.widget.gangwar"),
                teamOne + " vs " + teamTwo);
        zoneLine = super.createLine(Component.translatable("germanrpaddon.widget.gangwar"), gangZone);
        valueLine = super.createLine(Component.translatable("germanrpaddon.widget.gangwar"),
                pointsTeamOne.replace("-", "") + " - " + pointsTeamTwo.replace("-", ""));
    }

    @Override
    public boolean isVisibleInGame() {
        return activeGangwar;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if (!activeGangwar) {
            return;
        }
        if (gangwarEnum != addon.configuration().widgets().gangwarSetting().get()) {
            gangwarEnum = addon.configuration().widgets().gangwarSetting().get();
            updateGangWar = true;
        }
        if (updateGangWar) {
            if (gangwarEnum == GangwarEnum.ALL) {
                teamLine.setState(State.VISIBLE);
                zoneLine.setState(State.VISIBLE);
                valueLine.setState(State.VISIBLE);
                teamLine.updateAndFlush(teamOne + " vs " + teamTwo);
                zoneLine.updateAndFlush(gangZone);
                valueLine.updateAndFlush(
                        pointsTeamOne.replace("-", "") + " - " + pointsTeamTwo.replace("-", ""));
            }
            if (gangwarEnum == GangwarEnum.FACTIONS) {
                teamLine.setState(State.VISIBLE);
                zoneLine.setState(State.HIDDEN);
                valueLine.setState(State.HIDDEN);
                teamLine.updateAndFlush(teamOne + " vs " + teamTwo);
            }
            if (gangwarEnum == GangwarEnum.POINTS) {
                teamLine.setState(State.HIDDEN);
                zoneLine.setState(State.HIDDEN);
                valueLine.setState(State.VISIBLE);
                valueLine.updateAndFlush(
                        pointsTeamOne.replace("-", "") + " - " + pointsTeamTwo.replace("-", ""));
            }
            if (gangwarEnum == GangwarEnum.ZONE) {
                teamLine.setState(State.HIDDEN);
                zoneLine.setState(State.VISIBLE);
                valueLine.setState(State.HIDDEN);
                zoneLine.updateAndFlush(gangZone);
            }
            if (gangwarEnum == GangwarEnum.FACTIONSANDPOINTS) {
                teamLine.setState(State.VISIBLE);
                zoneLine.setState(State.HIDDEN);
                valueLine.setState(State.VISIBLE);
                teamLine.updateAndFlush(teamOne + " vs " + teamTwo);
                valueLine.updateAndFlush(
                        pointsTeamOne.replace("-", "") + " - " + pointsTeamTwo.replace("-", ""));
            }
            if (gangwarEnum == GangwarEnum.FACTIONSANDZONE) {
                teamLine.setState(State.VISIBLE);
                zoneLine.setState(State.VISIBLE);
                valueLine.setState(State.HIDDEN);
                teamLine.updateAndFlush(teamOne + " vs " + teamTwo);
                zoneLine.updateAndFlush(gangZone);
            }
            if (gangwarEnum == GangwarEnum.POINTSANDZONE) {
                teamLine.setState(State.HIDDEN);
                zoneLine.setState(State.VISIBLE);
                valueLine.setState(State.VISIBLE);

                zoneLine.updateAndFlush(gangZone);
                valueLine.updateAndFlush(
                        pointsTeamOne.replace("-", "") + " - " + pointsTeamTwo.replace("-", ""));
            }
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

                JsonElement serverMessage = GsonUtil.DEFAULT_GSON.fromJson(messageContent, JsonElement.class);
                if (messageKey.equals("GANGWAR-POINTS-Attacker")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        activeGangwar = true;
                        pointsTeamOne = obj.get("value").toString().replace("\"", "");
                        if (pointsTeamOne.equals("-1")) {
                            activeGangwar = false;
                            gangZone = "Krankenhaus";
                            teamOne = "Polizei";
                            teamTwo = "Rettungsdienst";
                            pointsTeamOne = "-1";
                            pointsTeamTwo = "-1";
                            return;
                        }
                    }
                }
                if (messageKey.equals("GANGWAR-POINTS-Defender")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        activeGangwar = true;
                        pointsTeamTwo = obj.get("value").toString().replace("\"", "");
                        if (pointsTeamTwo.equals("-1")) {
                            activeGangwar = false;
                            gangZone = "Krankenhaus";
                            teamOne = "Polizei";
                            teamTwo = "Rettungsdienst";
                            pointsTeamOne = "-1";
                            pointsTeamTwo = "-1";
                            return;
                        }
                    }
                }
                if (messageKey.equals("GANGWAR-Attacker")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        activeGangwar = true;
                        teamOne = obj.get("value").toString().replace("\"", "");
                        updateGangWar = true;
                    }
                }
                if (messageKey.equals("GANGWAR-Defender")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        activeGangwar = true;
                        teamTwo = obj.get("value").toString().replace("\"", "");
                        updateGangWar = true;
                    }
                }

                if (messageKey.equals("GANGWAR-Gangzone")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        activeGangwar = true;
                        gangZone = obj.get("value").toString().replace("\"", "");
                        updateGangWar = true;
                    }
                }
            } catch (PayloadReaderException ignored) {
            }
        }
    }
}
