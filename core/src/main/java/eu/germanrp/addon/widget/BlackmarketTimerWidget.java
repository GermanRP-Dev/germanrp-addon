package eu.germanrp.addon.widget;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class BlackmarketTimerWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private final ZoneId zoneId = ZoneId.of("Europe/Berlin");
    private TextLine line;
    private ZonedDateTime now = ZonedDateTime.now(zoneId);
    private ZonedDateTime hourLater = now.plusHours(1);
    private ZonedDateTime firstMomentOfNextHour = hourLater.truncatedTo(ChronoUnit.HOURS);
    private long duration = Duration.between(now, firstMomentOfNextHour).toMillis();
    private int ticks = 0;

    public BlackmarketTimerWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("blackmarket");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/timer.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.blackmarket"), formatTime(duration));
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        ticks++;
        if (ticks == 10) {
            now = ZonedDateTime.now(zoneId);
            hourLater = now.plusHours(1);
            firstMomentOfNextHour = hourLater.truncatedTo(ChronoUnit.HOURS);
            duration = Duration.between(now, firstMomentOfNextHour).toMillis();

            String timeString = formatTime(duration);
            line.updateAndFlush(timeString);
            ticks = 0;
        }
    }

    private String formatTime(long time) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("mm:ss");
        return DATE_FORMAT.format(new Date(time));
    }
}
