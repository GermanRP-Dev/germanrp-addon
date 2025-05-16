package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

public class MajorEventWidget  extends TextHudWidget<TextHudWidgetConfig> {

    private final Object addon;
    private boolean majorEvent;

    public MajorEventWidget(GermanRPAddon addon) {
        super();
        this.addon = addon;
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
    }
    @Subscribe
    public void onGermanRPAddonTick(GermanRPAddonTickEvent e) {
        if (this.majorEvent) {
            /* Updatelogik */
        }
    }
}
