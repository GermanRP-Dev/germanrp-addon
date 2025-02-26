package eu.germanrp.addon.widget;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.serverapi.EconomyUpdateEvent;

import java.text.DecimalFormat;

public class CashWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private final DecimalFormat df = new DecimalFormat("#0.00");
    private TextLine line;
    private double cashBalance;
    private boolean cashUpdate;

    public CashWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("cash");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/cash.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.cash"), "0.00€");
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {

        if (cashUpdate) {
            cashUpdate = false;
            line.updateAndFlush(df.format(cashBalance) + "€");
        }
    }

    @Subscribe
    public void onCashUpdate(EconomyUpdateEvent event) {
        if (event.economy().getKey().equalsIgnoreCase("cash")) {
            cashBalance = event.economy().getBalance();
            cashUpdate = true;
        }
    }
}
