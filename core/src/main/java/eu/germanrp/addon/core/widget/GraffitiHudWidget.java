package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Graffiti;
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
import java.util.HashMap;
import java.util.Map;

import static eu.germanrp.addon.core.GermanRPAddon.utilService;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND;
import static java.time.Duration.ZERO;
import static java.util.Arrays.stream;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.HIDDEN;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.VISIBLE;

public class GraffitiHudWidget extends TextHudWidget<TextHudWidgetConfig> {

    public static final Map<Graffiti, Duration> GRAFFITI_REMAINING_TIMES = new HashMap<>();

    public GraffitiHudWidget(HudWidgetCategory widgetCategory, Icon icon) {
        super("graffiti");
        bindCategory(widgetCategory);
        setIcon(icon);
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);

        stream(Graffiti.values())
                .forEach(graffiti -> {
                    TextLine textLine = createLine(graffiti.getName(), ZERO);
                    textLine.setState(HIDDEN);
                });
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
            textLine.updateAndFlush(utilService.text().parseTimer(remainingTime.toSeconds()));
        });
    }
}
