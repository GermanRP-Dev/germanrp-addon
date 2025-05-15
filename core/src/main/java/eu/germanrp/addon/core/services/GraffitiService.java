package eu.germanrp.addon.core.services;

import eu.germanrp.addon.api.models.Graffiti;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public class GraffitiService {

    /**
     * This map holds {@link Graffiti} and their respective {@link Instant} when they can be sprayed again.
     */
    private static final Map<Graffiti, Instant> GRAFFITI_AVAILABLILITY_MAP = new EnumMap<>(Graffiti.class);

    public Map<Graffiti, Instant> getGraffitiMap() {
        return GRAFFITI_AVAILABLILITY_MAP;
    }

    public Instant getGraffitiEndInstant(Graffiti graffiti) {
        return GRAFFITI_AVAILABLILITY_MAP.get(graffiti);
    }

    public void updateGraffiti(Graffiti graffiti, Instant instant) {
        GRAFFITI_AVAILABLILITY_MAP.put(graffiti, instant);
    }
}
