package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class BlackMarketWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private static final Component COUNTDOWN_KEY = Component.translatable("germanrpaddon.widget.blackMarketWidget.eventKey");
    private static final String COUNTDOWN_VALUE = "germanrpaddon.widget.blackMarketWidget.eventValue";
    private TextLine countDownLine;

    public BlackMarketWidget(GermanRPAddon addon, HudWidgetCategory category) {
        super("blackMarketWidget");
        this.bindCategory(category);
        this.addon = addon;
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        final String i18nYieldValue = I18n.getTranslation(COUNTDOWN_VALUE, 0, "", 0);

        this.countDownLine = this.createLine(COUNTDOWN_KEY, i18nYieldValue);
    }
    @Subscribe
    public void onServerJoin(JustJoinedEvent e){
        if(e.isJustJoined()){
            this.countDownLine.setState(TextLine.State.VISIBLE);
        }else {
            this.countDownLine.setState(TextLine.State.HIDDEN);
        }
    }
    @Subscribe
    public void onGermanRPAddonTick(GermanRPAddonTickEvent e) {
        if (!e.isPhase(GermanRPAddonTickEvent.Phase.SECOND)) {
            return;
        }
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(now, now.plusHours(1).truncatedTo(ChronoUnit.HOURS));
        this.countDownLine.updateAndFlush(String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart()));
    }
}
