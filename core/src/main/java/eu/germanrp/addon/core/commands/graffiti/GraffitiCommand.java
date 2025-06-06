package eu.germanrp.addon.core.commands.graffiti;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.widget.GraffitiHudWidget.GraffitiHudWidgetConfig;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.component.Component;

public class GraffitiCommand extends Command {

    private final GermanRPAddon addon;

    public GraffitiCommand(final GermanRPAddon addon, final GraffitiHudWidgetConfig config) {
        super("graffiti");
        this.addon = addon;
        this.withSubCommand(new GraffitiListCommand(addon, config));
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {

        getSubCommands().forEach(subCommand ->
                addon.getPlayer().sendInfoMessage(Component.text("/" + this.prefix + " " + subCommand.getPrefix()))
        );

        return true;
    }
}
