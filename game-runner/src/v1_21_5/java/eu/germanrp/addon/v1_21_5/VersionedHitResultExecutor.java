package eu.germanrp.addon.v1_21_5;

import eu.germanrp.addon.core.executor.HitResultExecutor;
import net.labymod.api.models.Implements;
import net.labymod.api.util.math.vector.IntVector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.inject.Singleton;

@Singleton
@Implements(HitResultExecutor.class)
public class VersionedHitResultExecutor implements HitResultExecutor {

    @Override
    public IntVector3 getBlockLookingAt() {
        IntVector3 blockPosition = null;
        final HitResult hitResult = Minecraft.getInstance().hitResult;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            final BlockPos blockPos = blockHitResult.getBlockPos();
            final int x = blockPos.getX();
            final int y = blockPos.getY();
            final int z = blockPos.getZ();
            blockPosition = new IntVector3(x, y, z);
        }

        return blockPosition;
    }

}
