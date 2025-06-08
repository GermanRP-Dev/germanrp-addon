package eu.germanrp.addon.core.commands.test;

import eu.germanrp.addon.api.models.FactionName;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.chat.command.Command;

public class TestCommand extends Command {

    private final GermanRPAddon addon;

    public TestCommand(final GermanRPAddon addon) {
        super("test");
        this.addon = addon;
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        final FactionName playerFactionName = this.addon.getPlayer().getPlayerFactionName();
        this.addon.getPlayer().sendDebugMessage(playerFactionName.name());
        this.addon.getPlayer().sendDebugMessage(this.addon.getNameTagService().getMembers().toString());
        return true;
    }

}
