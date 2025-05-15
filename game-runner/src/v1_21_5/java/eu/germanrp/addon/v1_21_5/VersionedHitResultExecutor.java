package eu.germanrp.addon.v1_21_5;

import eu.germanrp.addon.core.executor.HitResultExecutor;
import net.labymod.api.models.Implements;
import net.labymod.api.util.math.vector.IntVector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Implements(HitResultExecutor.class)
public class VersionedHitResultExecutor implements HitResultExecutor {

    @Override
    public Optional<IntVector3> getBlockLookingAt() {
        final HitResult hitResult = Minecraft.getInstance().hitResult;

        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return Optional.empty();
        }

        final BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        final BlockPos blockPos = blockHitResult.getBlockPos();
        final int x = blockPos.getX();
        final int y = blockPos.getY();
        final int z = blockPos.getZ();

        return Optional.of(new IntVector3(x, y, z));
    }
}
