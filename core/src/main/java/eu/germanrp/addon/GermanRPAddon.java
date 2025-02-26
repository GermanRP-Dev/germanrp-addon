package eu.germanrp.addon;

import eu.germanrp.addon.config.GermanRPConfig;
import eu.germanrp.addon.listener.InputListener;
import eu.germanrp.addon.listener.NameTagListener;
import eu.germanrp.addon.widget.BankWidget;
import eu.germanrp.addon.widget.BlackmarketTimerWidget;
import eu.germanrp.addon.widget.CashWidget;
import eu.germanrp.addon.widget.GangwarWidget;
import eu.germanrp.addon.widget.HealthWidget;
import eu.germanrp.addon.widget.HydrationWidget;
import eu.germanrp.addon.widget.LevelWidget;
import eu.germanrp.addon.widget.PayDayWidget;
import eu.germanrp.addon.widget.PlantWidget;
import eu.germanrp.addon.widget.SalaryWidget;
import eu.germanrp.addon.widget.SkillWidget;
import eu.germanrp.addon.widget.TimerWidget;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class GermanRPAddon extends LabyAddon<GermanRPConfig> {

    @Override
    protected void enable() {
        this.logger().info("Loading GermanRP Utils...");
        registerSettingCategory();
        GermanRPCategory category = new GermanRPCategory();
        labyAPI().hudWidgetRegistry().categoryRegistry().register(category);
        this.registerListener(new InputListener(this));
        this.registerListener(new NameTagListener(this));

        labyAPI().hudWidgetRegistry().register(new CashWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new BankWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new SalaryWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new HydrationWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new PayDayWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new BlackmarketTimerWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new TimerWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new PlantWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new GangwarWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new LevelWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new HealthWidget(this, category));
        labyAPI().hudWidgetRegistry().register(new SkillWidget(this, category));

        this.logger().info("GermanRP Utils started successfully!");
    }

    @Override
    protected Class<? extends GermanRPConfig> configurationClass() {
        return GermanRPConfig.class;
    }
}
