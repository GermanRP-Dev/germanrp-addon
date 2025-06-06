package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.AddonPlayer;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.hud.hudwidget.text.Formatting;
import net.labymod.api.util.Color;
import net.labymod.api.util.math.position.Position;

import java.time.Duration;

import static eu.germanrp.addon.core.widget.GraffitiHudWidget.GRAFFITI_REMAINING_TIMES;
import static eu.germanrp.addon.core.widget.GraffitiHudWidget.GraffitiHudWidgetConfig;
import static java.time.Duration.ZERO;
import static java.util.Arrays.stream;
import static net.labymod.api.client.component.Component.text;
import static net.labymod.api.client.component.event.ClickEvent.runCommand;
import static net.labymod.api.client.component.event.HoverEvent.showText;

public class GraffitiListCommand extends SubCommand {

    private final GermanRPAddon addon;
    private final AddonPlayer player;
    private final GraffitiHudWidgetConfig config;

    public GraffitiListCommand(GermanRPAddon addon, GraffitiHudWidgetConfig config) {
        super("list");
        this.addon = addon;
        this.player = addon.getPlayer();
        this.config = config;
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        final Color labelColor =
                config.labelColor().get().isChroma() ? config.labelColor().defaultValue() : config.labelColor()
                        .get();
        final Color bracketColor = config.bracketColor().get().isChroma() ? config.bracketColor()
                .defaultValue() : config.bracketColor().get();
        final Color valueColor =
                config.valueColor().get().isChroma() ? config.valueColor().defaultValue() : config.valueColor()
                        .get();

        stream(Graffiti.values()).forEach(graffiti -> {
            final Position position = graffiti.getPosition();
            final Duration remainingTime = GRAFFITI_REMAINING_TIMES.getOrDefault(graffiti, ZERO);

            final String naviCommand = "/navi " + position.getX() + " " + position.getY() + " " + position.getZ();

            final TextComponent key = text(graffiti.getName()).color(TextColor.color(labelColor.get()));
            final TextComponent value = text(
                    remainingTime != ZERO ? addon.getUtilService().text()
                            .parseTimer(remainingTime.toSeconds()) : "Cooldown abgelaufen",
                    TextColor.color(valueColor.get())
            );

            final Formatting formatting = config.formatting().get();
            final Component textMessage = formatting.build(
                            key,
                            value,
                            true,
                            bracketColor.get()
                    )
                    .hoverEvent(showText(text(naviCommand)))
                    .clickEvent(runCommand(naviCommand));

            player.sendMessage(textMessage);
        });

        return true;
    }
}
