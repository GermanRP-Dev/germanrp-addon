package eu.germanrp.addon.core.commands.test;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.chat.command.Command;

import java.util.Arrays;

public class TestCommand extends Command {

    private final GermanRPAddon addon;

    public TestCommand(final GermanRPAddon addon) {
        super("test");
        this.addon = addon;
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        final Faction playerFaction = this.addon.getPlayer().getPlayerFactionName();
        this.addon.getPlayer().sendDebugMessage(playerFaction.name());
        this.addon.getPlayer().sendDebugMessage(Arrays.toString(this.addon.getNameTagService().getMembers().toArray()));
        return true;
    }
}
