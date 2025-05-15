package eu.germanrp.addon.core.executor;

import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.math.vector.IntVector3;

import java.util.Optional;

@Referenceable
public interface HitResultExecutor {

    Optional<IntVector3> getBlockLookingAt();
}
