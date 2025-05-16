package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.math.position.Position;

import java.time.Duration;

import static eu.germanrp.addon.core.GermanRPAddon.utilService;
import static eu.germanrp.addon.core.widget.GraffitiHudWidget.GRAFFITI_REMAINING_TIMES;
import static java.time.Duration.ZERO;
import static java.util.Arrays.stream;
import static net.labymod.api.client.component.Component.empty;
import static net.labymod.api.client.component.Component.text;
import static net.labymod.api.client.component.event.ClickEvent.runCommand;
import static net.labymod.api.client.component.event.HoverEvent.showText;
import static net.labymod.api.client.component.format.NamedTextColor.DARK_GRAY;
import static net.labymod.api.client.component.format.NamedTextColor.GOLD;
import static net.labymod.api.client.component.format.NamedTextColor.GRAY;

public class GraffitiListCommand extends SubCommand {

    private final GermanRPAddon addon;

    public GraffitiListCommand(GermanRPAddon addon) {
        super("list");
        this.addon = addon;
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        stream(Graffiti.values()).forEach(graffiti -> {
            Position position = graffiti.getPosition();
            Duration remainingTime = GRAFFITI_REMAINING_TIMES.getOrDefault(graffiti, ZERO);

            String naviCommand = "/navi " + position.getX() + " " + position.getY() + " " + position.getZ();

            Component component = empty()
                    .append(text(graffiti.getName(), GOLD)
                            .hoverEvent(showText(text(naviCommand)))
                            .clickEvent(runCommand(naviCommand)))
                    .append(text(": ", DARK_GRAY))
                    .append(text(remainingTime != ZERO ? utilService.text().parseTimer(remainingTime.toSeconds()) : "Cooldown abgelaufen", GRAY));

            this.addon.getPlayer().sendMessage(component);
        });

        return true;
    }
}
