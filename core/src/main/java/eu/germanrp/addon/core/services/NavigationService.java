package eu.germanrp.addon.core.services;

import eu.germanrp.addon.api.Nearest;
import net.labymod.api.util.math.position.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Comparator.comparing;

public class NavigationService {

    public <T extends Nearest> T getNearest(Position position, @NotNull List<T> elements) {
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements must not be empty");
        }

        return elements.stream().min(comparing(t -> t.getPosition().distanceSquared(position))).get();
    }
}
