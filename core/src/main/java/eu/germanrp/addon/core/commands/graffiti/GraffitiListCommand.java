package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.AddonPlayer;
import lombok.val;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.format.TextColor;

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
        val labelColor =
                config.labelColor().get().isChroma() ? config.labelColor().defaultValue() : config.labelColor()
                        .get();
        val bracketColor = config.bracketColor().get().isChroma() ? config.bracketColor()
                .defaultValue() : config.bracketColor().get();
        val valueColor =
                config.valueColor().get().isChroma() ? config.valueColor().defaultValue() : config.valueColor()
                        .get();

        stream(Graffiti.values()).forEach(graffiti -> {
            val position = graffiti.getPosition();
            val remainingTime = GRAFFITI_REMAINING_TIMES.getOrDefault(graffiti, ZERO);

            val naviCommand = "/navi " + position.getX() + " " + position.getY() + " " + position.getZ();

            val key = text(graffiti.getName()).color(TextColor.color(labelColor.get()));
            val value = text(
                    remainingTime != ZERO ? addon.getUtilService().text()
                            .parseTimer(remainingTime.toSeconds()) : "Cooldown abgelaufen",
                    TextColor.color(valueColor.get())
            );

            val formatting = config.formatting().get();
            val textMessage = formatting.build(
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
