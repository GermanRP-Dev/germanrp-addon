package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.events.EffectTimerEvent;
import eu.germanrp.addon.serverapi.packet.EffectPacket;
import lombok.val;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.io.LabyExecutors;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.labymod.api.Laby.labyAPI;

public final class EffectHudWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final Map<EffectPacket.EffectType, Instant> effectEndTimes = HashMap.newHashMap(EffectPacket.EffectType.values().length);
    private final Map<EffectPacket.EffectType, TextLine> textLines = HashMap.newHashMap(EffectPacket.EffectType.values().length);

    private static final ScheduledExecutorService effectDecay = LabyExecutors.newSingleThreadScheduledExecutor("Effect Decay Executor");

    public EffectHudWidget(final HudWidgetCategory category) {
        super("effects");
        this.bindCategory(category);
        effectDecay.scheduleAtFixedRate(() -> labyAPI().minecraft().executeOnRenderThread(this::updateTextLines), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);

        for (val type : EffectPacket.EffectType.values()) {
            val line = createLine(type.getDisplayName(), format(Duration.ZERO));
            this.effectEndTimes.put(type, Instant.now());
            this.textLines.put(type, line);
        }

        updateTextLines();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEffectTimer(final EffectTimerEvent event) {
        val type = event.type();
        effectEndTimes.put(type, event.end());
        updateTextLines();
    }

    private void updateTextLines() {
        for (val type : EffectPacket.EffectType.values()) {
            val now = Instant.now();
            val endTime = effectEndTimes.getOrDefault(type, Instant.now());
            val line = textLines.get(type);
            val remaining = Duration.between(now, endTime);

            if (remaining.isZero() || remaining.isNegative()) {
                line.setState(TextLine.State.HIDDEN);
                continue;
            }

            line.updateAndFlush(format(remaining));
            line.setState(TextLine.State.VISIBLE);
        }
    }

    private String format(final Duration duration) {
        val minutes = duration.toMinutesPart();
        val seconds = duration.toSecondsPart();
        return String.format("%02d:%02d", minutes, seconds);
    }

}
