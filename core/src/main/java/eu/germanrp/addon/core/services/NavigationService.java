package eu.germanrp.addon.core.services;

import eu.germanrp.addon.api.Nearest;
import net.labymod.api.util.math.position.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class NavigationService {

    public <T extends Nearest> Optional<T> getNearest(@NotNull Position position, @NotNull Collection<T> elements) {
        if (elements.isEmpty()) {
            return Optional.empty();
        }

        T nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (T element : elements) {
            double distance = element.getPosition().distanceSquared(position);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = element;
            }
        }

        return Optional.ofNullable(nearest);

    }
}
