package eu.germanrp.addon.core.commands;

import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.chat.command.Command;
import org.jetbrains.annotations.NotNull;

public class TogglePanicCommand extends Command {



    public TogglePanicCommand() {
        super("panicoff","togglepanic");
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        GermanRPAddon.getInstance().getPlayer().setPlayPanic(true);
        return true;
    }
}
