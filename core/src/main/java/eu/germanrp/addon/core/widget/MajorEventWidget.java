package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MajorEventWidget  extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private boolean majorEvent;
    private ZonedDateTime countdownTarget;
    private static final Component EVENT_KEY = Component.translatable("germanrpaddon.widget.majorEvent.eventKey");
    private static final Component COUNTDOWN_KEY = Component.translatable("germanrpaddon.widget.majorEvent.countdownKey");
    private static final String EVENT_VALUE = "germanrpaddon.widget.majorEvent.eventValue";
    private static final String COUNTDOWN_VALUE = "germanrpaddon.widget.majorEvent.countdownValue";

    private TextLine eventNameLine;
    private TextLine countDownLine;


    public MajorEventWidget(GermanRPAddon addon, HudWidgetCategory category) {
        super("majorEvent");
        this.bindCategory(category);
        this.addon = addon;

    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        final String i18nProgressValue = I18n.getTranslation(EVENT_VALUE, 0, 0);
        final String i18nYieldValue = I18n.getTranslation(COUNTDOWN_VALUE, 0, "", 0);

        this.eventNameLine = this.createLine(EVENT_KEY, i18nProgressValue);
        this.countDownLine = this.createLine(COUNTDOWN_KEY, i18nYieldValue);

        this.eventNameLine.setState(TextLine.State.VISIBLE);
        this.countDownLine.setState(TextLine.State.VISIBLE);
    }
    public void reset() {
        this.majorEvent = false;
        this.eventNameLine.setState(TextLine.State.HIDDEN);
        this.countDownLine.setState(TextLine.State.HIDDEN);
    }
    @Subscribe
    public void onGermanRPAddonTick(GermanRPAddonTickEvent e) {
        if (!e.isPhase(GermanRPAddonTickEvent.Phase.SECOND) || !this.majorEvent) {
            return;
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(countdownTarget.isBefore(now)){
            reset();
            return;
        }
        Duration duration = Duration.between(now, countdownTarget);
        this.countDownLine.updateAndFlush(String.format("\n%02d:%02d", duration.toMinutes(), duration.toSeconds()));


    }
}
