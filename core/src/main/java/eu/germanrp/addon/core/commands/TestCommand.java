package eu.germanrp.addon.core.commands;

import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.core.GermanRPAddon;
import lombok.val;
import net.labymod.api.client.chat.command.Command;

public class TestCommand extends Command {

    public TestCommand() {
        super("test");
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        val addon = GermanRPAddon.getInstance();
        val player = addon.getPlayer();
        val charInfoMap = addon.configuration().characterInfoMap();
        val charInfo = new CharacterInfo(player.getUniqueId(), player.getName(), String.join(" ", arguments));
        charInfoMap.put(player.getUniqueId(), charInfo);
        player.sendInfoMessage("Character \"%s\" saved".formatted(charInfo));
        return false;
    }

}
