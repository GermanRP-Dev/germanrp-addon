package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.common.events.PayDayPacketRecieveEvent;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;

import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.HIDDEN;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.VISIBLE;
import static net.labymod.api.util.I18n.getTranslation;

@Getter
@Setter
public class PayDayWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private boolean majorEvent;

    private static final Component FGEHALT_KEY = Component.translatable("germanrpaddon.widget.payDay.fgehaltKey");
    private static final Component JGEHALT_KEY = Component.translatable("germanrpaddon.widget.payDay.jgehaltKey");
    private static final Component PAYDAY_KEY = Component.translatable("germanrpaddon.widget.payDay.paydayKey");
    private static final String FGEHALT_VALUE = "germanrpaddon.widget.payDay.fGehaltValue";
    private static final String JGEHALT_VALUE = "germanrpaddon.widget.payDay.jGehaltValue";
    private static final String PAYDAY_VALUE = "germanrpaddon.widget.payDay.paydayValue";

    private TextLine frakGehaltLine;
    private TextLine jobGehaltLine;
    private TextLine payDayTimeLine;

    public PayDayWidget(GermanRPAddon addon, HudWidgetCategory category) {
        super("payDay");
        this.bindCategory(category);
        this.addon = addon;
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        final String i18nFGehaltValue = getTranslation(FGEHALT_VALUE);
        final String i18nJGehaltValue = getTranslation(JGEHALT_VALUE);
        final String i18PaydayValue = getTranslation(PAYDAY_VALUE);

        this.frakGehaltLine = this.createLine(FGEHALT_KEY, i18nFGehaltValue);
        this.jobGehaltLine = this.createLine(JGEHALT_KEY, i18nJGehaltValue);
        this.payDayTimeLine = this.createLine(PAYDAY_KEY, i18PaydayValue);
    }
    @Subscribe
    public void onServerJoin(JustJoinedEvent e){
        if(e.isJustJoined()){
            this.frakGehaltLine.setState(VISIBLE);
            this.jobGehaltLine.setState(VISIBLE);
            this.payDayTimeLine.setState(VISIBLE);
        }else {
            this.payDayTimeLine.setState(HIDDEN);
            this.frakGehaltLine.setState(HIDDEN);
            this.jobGehaltLine.setState(HIDDEN);
        }
    }
    @Subscribe
    public void onPayDayPacketRecieve(PayDayPacketRecieveEvent e) {
        this.frakGehaltLine.updateAndFlush(String.format("%.2f €", e.getFSalary()));
        this.jobGehaltLine.updateAndFlush(String.format("%.2f €", e.getJSalary()));
        this.payDayTimeLine.updateAndFlush(String.format("%02d/60", e.getPaydayTime()));
    }
}
