package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.chat.command.Command;

public class GraffitiCommand extends Command {

    public GraffitiCommand(final GermanRPAddon addon) {
        super("graffiti");
        this.withSubCommand(new GraffitiListCommand(addon));
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        return true;
    }
}
