package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.GraffitiService;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.math.vector.IntVector3;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GraffitiListCommand extends SubCommand {

    private final GermanRPAddon addon;
    private final GraffitiService graffitiService;

    public GraffitiListCommand(GermanRPAddon addon) {
        super("list");
        this.addon = addon;
        this.graffitiService = addon.getGraffitiService();
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        graffitiService.getGraffitiMap().forEach((graffiti, instant) -> {
            final Component nameComponent = Component.text(graffiti.getName() + ": ");

            final IntVector3 position = graffiti.getPosition();
            final Component positionComponent =
                    Component.text(String.format(" (%d, %d, %d)", position.getX(), position.getY(), position.getZ()));

            final Component timeComponent = Component.text(String.format(
                    " - %s",
                    DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault()).format(instant)
            ));

            addon.displayMessage(nameComponent
                    .append(positionComponent)
                    .append(timeComponent)
            );
        });
        return true;
    }
}
