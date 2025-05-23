package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.widget.GraffitiHudWidget.GraffitiHudWidgetConfig;
import net.labymod.api.client.chat.command.Command;

public class GraffitiCommand extends Command {

    public GraffitiCommand(final GermanRPAddon addon, final GraffitiHudWidgetConfig config) {
        super("graffiti");
        this.withSubCommand(new GraffitiListCommand(addon, config));
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        return true;
    }
}
