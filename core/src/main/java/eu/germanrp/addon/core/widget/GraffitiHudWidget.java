package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import eu.germanrp.addon.core.common.events.GraffitiUpdateEvent;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.event.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND;
import static eu.germanrp.addon.core.widget.GraffitiHudWidget.GraffitiHudWidgetConfig;
import static java.time.Duration.ZERO;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.HIDDEN;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.VISIBLE;

public class GraffitiHudWidget extends TextHudWidget<GraffitiHudWidgetConfig> {

    public static final Map<Graffiti, Duration> GRAFFITI_REMAINING_TIMES = new EnumMap<>(Graffiti.class);

    private final GermanRPAddon addon;

    public GraffitiHudWidget(HudWidgetCategory widgetCategory, Icon icon, GermanRPAddon addon) {
        super("graffiti", GraffitiHudWidgetConfig.class);
        bindCategory(widgetCategory);
        setIcon(icon);
        this.addon = addon;
    }

    @Override
    public void load(GraffitiHudWidgetConfig config) {
        super.load(config);

        for(Graffiti graffiti : Graffiti.values()){
            TextLine textLine = createLine(graffiti.getName(), ZERO);
            textLine.setState(HIDDEN);
        }
    }

    @Subscribe
    public void onGraffitiUpdate(@NotNull GraffitiUpdateEvent event) {
        Graffiti graffiti = event.getGraffiti();
        Duration remainingTime = event.getRemainingTime();

        GRAFFITI_REMAINING_TIMES.put(graffiti, remainingTime);
        updateTextLines();
    }

    @Subscribe
    public void onGermanRPAddonTick(@NotNull GermanRPAddonTickEvent event) {
        if (event.isPhase(SECOND)) {
            updateTextLines();
        }
    }

    private void updateTextLines() {
        GRAFFITI_REMAINING_TIMES.forEach((graffiti, remainingTime) -> {
            TextLine textLine = this.lines.get(graffiti.ordinal());

            if (remainingTime.isZero()) {
                textLine.setState(HIDDEN);
                return;
            }

            remainingTime = remainingTime.minusSeconds(1);
            GRAFFITI_REMAINING_TIMES.put(graffiti, remainingTime);

            textLine.setState(VISIBLE);
            textLine.updateAndFlush(this.addon.getUtilService().text().parseTimer(remainingTime.toSeconds()));
        });
    }

    public static class GraffitiHudWidgetConfig extends TextHudWidgetConfig {
    }
}
