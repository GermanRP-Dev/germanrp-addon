package eu.germanrp.addon.widget;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

import java.text.DecimalFormat;

public class HealthWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private final DecimalFormat df = new DecimalFormat("#0.0");
    private int ticks;
    private TextLine line;
    private double playerHealth = 20.0;

    public HealthWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("health");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/health.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.health"), "0");
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        ticks++;
        if (ticks == 10) {
            ticks = 0;
            if (addon.labyAPI().minecraft().getClientPlayer() == null) {
                return;
            }
            if (addon.labyAPI().minecraft().getClientPlayer().getHealth() == playerHealth) {
                return;
            }

            playerHealth = addon.labyAPI().minecraft().getClientPlayer().getHealth();
            line.updateAndFlush(df.format(playerHealth / 2));
        }
    }
}

