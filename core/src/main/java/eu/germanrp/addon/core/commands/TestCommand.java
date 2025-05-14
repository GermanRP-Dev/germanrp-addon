package eu.germanrp.addon.core.commands;

import eu.germanrp.addon.core.GRUtilsAddon;
import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import net.labymod.api.client.chat.command.Command;

public class TestCommand extends Command {

    private final PlaySoundExecutor playSoundExecutor;

    public TestCommand(GRUtilsAddon addon) {
        super("test");
        this.playSoundExecutor = addon.getPlaySoundExecutor();
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        playSoundExecutor.playNotePlingSound();
        return true;
    }

}
