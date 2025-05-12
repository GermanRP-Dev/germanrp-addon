package eu.germanrp.addon.core.executor;

import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.math.vector.IntVector3;

@Referenceable
public interface HitResultExecutor {

    IntVector3 getBlockLookingAt();

}
