package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.ExperienceUpdateEvent;
import eu.germanrp.addon.core.common.events.LevelUPEvent;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;
@Getter @Setter
public class PlayerExperienceWidget extends TextHudWidget<TextHudWidgetConfig> {

    private static final Component EVENT_KEY = Component.translatable("germanrpaddon.widget.playerExperience.playerXPKey");
    private static final Component COUNTDOWN_KEY = Component.translatable("germanrpaddon.widget.playerExperience.xpLeftKey");
    private static final String EVENT_VALUE = "germanrpaddon.widget.playerExperience.playerXPValue";
    private static final String COUNTDOWN_VALUE = "germanrpaddon.widget.playerExperience.xpLeftValue";
    private final GermanRPAddon addon;

    private TextLine currentXPfromNeededXP;
    private TextLine xpLeft;

    public PlayerExperienceWidget(GermanRPAddon germanRPAddon, HudWidgetCategory category, Icon icon) {
            super("playerExperience");
            this.addon = germanRPAddon;
            this.bindCategory(category);
            this.setIcon(icon);
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        final String i18nProgressValue = I18n.getTranslation(EVENT_VALUE, 0, 0);
        final String i18nYieldValue = I18n.getTranslation(COUNTDOWN_VALUE, 0, "", 0);

        this.currentXPfromNeededXP = this.createLine(EVENT_KEY, i18nProgressValue);
        this.xpLeft = this.createLine(COUNTDOWN_KEY, i18nYieldValue);

        this.currentXPfromNeededXP.setState(TextLine.State.VISIBLE);
        this.xpLeft.setState(TextLine.State.VISIBLE);
    }



    @Subscribe
    public void experienceUpdate(ExperienceUpdateEvent event){
        if(this.addon.getPlayer().getPlayerXP() >= this.addon.getPlayer().getPlayerNeededXP()){
            Laby.fireEvent(new LevelUPEvent());
        }
        this.currentXPfromNeededXP.updateAndFlush(String.format("\n%02d/%02d",
                this.addon.getPlayer().getPlayerXP(),
                this.addon.getPlayer().getPlayerNeededXP()));

        this.xpLeft.updateAndFlush(this.addon.getPlayer().getPlayerNeededXP() - this.addon.getPlayer().getPlayerXP());
    }
}
