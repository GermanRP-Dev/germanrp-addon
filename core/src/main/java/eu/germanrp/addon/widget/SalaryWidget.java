package eu.germanrp.addon.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.common.enums.SalaryEnum;
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

import java.text.DecimalFormat;

public class SalaryWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private TextLine line;
    private double salaryBoth;
    private double salaryJob;
    private double salaryFaction;
    private SalaryEnum salaryEnum = SalaryEnum.BOTH;
    private boolean salaryUpdate;
    private int ticks;

    public SalaryWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("salary");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/salary.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.salary"), "0.00€");
    }

    @Override
    public boolean isVisibleInGame() {
        if (salaryEnum == SalaryEnum.BOTH) {
            return salaryBoth > 0;
        }
        if (salaryEnum == SalaryEnum.JOB) {
            return salaryJob > 0;
        }
        if (salaryEnum == SalaryEnum.FACTION) {
            return salaryFaction > 0;
        }

        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        ticks++;
        if (ticks == 10) {
            if (salaryEnum != addon.configuration().widgets().salarySetting().get()) {
                salaryEnum = addon.configuration().widgets().salarySetting().get();
                salaryUpdate = true;
            }
            ticks = 0;
        }
        if (salaryUpdate) {
            DecimalFormat df = new DecimalFormat("#0.00");
            if (salaryEnum == SalaryEnum.BOTH) {
                line.updateAndFlush(df.format(salaryBoth) + "€");
            }
            if (salaryEnum == SalaryEnum.JOB) {
                line.updateAndFlush(df.format(salaryJob) + "€");
            }
            if (salaryEnum == SalaryEnum.FACTION) {
                line.updateAndFlush(df.format(salaryFaction) + "€");
            }
            salaryUpdate = false;
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

                        if (obj.has("salary")) {
                            JsonObject cashObj = obj.getAsJsonObject("salary");
                            salaryJob = cashObj.get("job").getAsDouble();
                            salaryFaction = cashObj.get("faction").getAsDouble();
                            salaryUpdate = true;
                            salaryBoth = (salaryJob + salaryFaction);
                        }
                    }
                }
            } catch (PayloadReaderException ignored) {
            }
        }
    }
}

