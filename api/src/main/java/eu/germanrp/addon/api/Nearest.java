package eu.germanrp.addon.api;

import net.labymod.api.util.math.position.Position;

/**
 * Represents an entity or object that has a position in space and can be identified as the nearest to a given reference point. This
 * interface provides a method to retrieve the position of the object.
 */
public interface Nearest {

    /**
     * Retrieves the position of this object in space.
     *
     * @return the {@link Position} of this object. This method should not return {@code null}.
     */
    Position getPosition();
}
