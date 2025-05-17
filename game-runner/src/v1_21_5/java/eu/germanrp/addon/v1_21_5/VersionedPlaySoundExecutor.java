package eu.germanrp.addon.v1_21_5;

import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

@Implements(PlaySoundExecutor.class)
public class VersionedPlaySoundExecutor implements PlaySoundExecutor {

    @Override
    public void playNotificationSound() {
        final Minecraft mc = Minecraft.getInstance();
        final SoundManager soundManager = mc.getSoundManager();

        final ClientLevel level = mc.level;
        final LocalPlayer player = mc.player;

        if (level == null || player == null) {
            return;
        }

        soundManager.play(new SimpleSoundInstance(
                SoundEvents.NOTE_BLOCK_PLING.value(),
                SoundSource.MASTER,
                1.0F,
                1.0F,
                level.random,
                player.blockPosition()
        ));
    }

}
