package eu.germanrp.addon.core.commands;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.chat.command.Command;

public class TogglePanicCommand extends Command {



    public TogglePanicCommand() {
        super("panicoff","togglepanic");
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        if(!GermanRPAddon.getInstance().getPlayer().getPlayerFaction().equals(Faction.POLIZEI)) {
            GermanRPAddon.getInstance().getPlayer().sendInfoMessage("Du bist nicht in der Polizei");
            return true;
        }
        GermanRPAddon.getInstance().getPlayer().setPlayPanic(true);
        GermanRPAddon.getInstance().getPlayer().sendInfoMessage("Der Panic sound wurde bis zum nächsten Panic deaktiviert");
        return true;
    }
}
