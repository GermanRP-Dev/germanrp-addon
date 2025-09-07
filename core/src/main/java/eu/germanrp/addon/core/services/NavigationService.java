package eu.germanrp.addon.core.services;

import eu.germanrp.addon.api.Nearest;
import net.labymod.api.util.math.position.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

import static java.util.Comparator.comparingDouble;

public class NavigationService {

    public <T extends Nearest> Optional<T> getNearest(@NotNull Position position, @NotNull Collection<T> elements) {
        if (elements.isEmpty()) {
            return Optional.empty();
        }

        return elements.stream().min(comparingDouble(t -> t.getPosition().distanceSquared(position)));
    }
}
