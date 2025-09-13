package eu.germanrp.addon.v1_21_5.mixins;

import eu.germanrp.addon.core.GermanRPAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void onPlay(SoundInstance si, CallbackInfo ci) {
        if (germanrpaddon$handleATM(si)){
            ci.cancel();
            return;
        }
        if (germanrpaddon$handlePanicRemind(si)){
            ci.cancel();
            return;
        }
        
    }

    @Unique
    private boolean germanrpaddon$handlePanicRemind(SoundInstance si) {
        if (!germanrpaddon$isPanicRemindDissabled()) {
            return false;
        }
        if (!germanrpaddon$isPanicRemind(si)) {
            return false;
        }
        return true;
    }

    @Unique
    public boolean germanrpaddon$handleATM(SoundInstance si){
        if (!germanrpaddon$isSilentATMEnabled()) {
            return false;
        }
        if (!germanrpaddon$isATMBreakSound(si)) {
            return false;
        }
        if (!germanrpaddon$isLookingAtATM()) {
            return false;
        }
        return true;
    }
    @Unique
    private static boolean germanrpaddon$isPanicRemindDissabled() {
        return !GermanRPAddon.getInstance().configuration().panicRemind().get();
    }

    @Unique
    private static boolean germanrpaddon$isPanicRemind(SoundInstance si) {
        final ResourceLocation location = si.getLocation();
        return location.getNamespace().equals("germanrp") && location.getPath().equals("faction.panic.remind");
    }

    @Unique
    private static boolean germanrpaddon$isSilentATMEnabled() {
        return GermanRPAddon.getInstance().configuration().silentATM().get();
    }

    @Unique
    private static boolean germanrpaddon$isATMBreakSound(SoundInstance si) {
        final ResourceLocation location = si.getLocation();
        return location.getNamespace().equals("minecraft") && location.getPath().equals("block.anvil.place");
    }

    @Unique
    private static boolean germanrpaddon$isLookingAtATM() {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        final ClientLevel level = mc.level;
        final HitResult hitResult = mc.hitResult;

        if (player == null || level == null || hitResult == null) {
            return false;
        }

        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return false;
        }

        return level.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.DROPPER;
    }
}
