package eu.germanrp.addon.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.exception.PayloadReaderException;
import net.labymod.serverapi.api.payload.io.PayloadReader;

public class PayDayWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private TextLine line;
    private String payDayTime;
    private boolean payDayUpdate;

    public PayDayWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("payday");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/payday.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.payday"), "0/60");
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if (payDayUpdate) {
            line.updateAndFlush(payDayTime + "/60");
            payDayUpdate = false;
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

                if (messageKey.equals("GRAddon-PayDay")) {
                    if (serverMessage.isJsonObject()) {
                        JsonObject obj = serverMessage.getAsJsonObject();
                        if (obj.has("time")) {
                            String payDayObjTime = obj.get("time").toString();
                            payDayUpdate = true;
                            payDayTime = payDayObjTime;
                        }
                    }
                }
            } catch (PayloadReaderException ignored) {

            }
        }
    }
}

