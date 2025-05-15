package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.services.GraffitiService;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class GraffitiHudWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GraffitiService graffitiService;

    public GraffitiHudWidget(HudWidgetCategory widgetCategory, Icon icon, GraffitiService graffitiService) {
        super("graffiti");
        this.graffitiService = graffitiService;
        this.bindCategory(widgetCategory);
        this.setIcon(icon);
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);


        Arrays.stream(Graffiti.values())
                .forEach(graffiti -> {
                    final TextLine line = this.createLine(graffiti.getName(), Duration.ZERO);
                    line.setState(TextLine.State.HIDDEN);
                });

    }

    @Override
    public void onTick(boolean isEditorContext) {
        super.onTick(isEditorContext);

        graffitiService.getGraffitiMap().forEach(this::renderGraffiti);
    }

    private void renderGraffiti(final Graffiti graffiti, final Instant endInstant) {
        final Instant now = Instant.now();

        final Duration between = Duration.between(now, endInstant);

        final TextLine textLine = lines.get(graffiti.ordinal());

        if (now.isAfter(endInstant)) {
            textLine.updateAndFlush("verfügbar");
            return;
        }

        textLine.setState(TextLine.State.VISIBLE);
        textLine.updateAndFlush(String.format("%02d:%02d", between.toMinutes(), between.toSecondsPart()));
    }

}
