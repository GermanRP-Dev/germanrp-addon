package eu.germanrp.addon.v1_21_4;

import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;

import javax.inject.Singleton;

@Singleton
@Implements(PlaySoundExecutor.class)
public class VersionedPlaySoundExecutor implements PlaySoundExecutor {

    @Override
    public void playNotePlingSound() {
        final LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        player.playSound(SoundEvents.NOTE_BLOCK_PLING.value());
    }

}
