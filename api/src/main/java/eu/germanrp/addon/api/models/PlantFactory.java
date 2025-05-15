package eu.germanrp.addon.api.models;

public final class PlantFactory {

    private PlantFactory() {
        // Hide public constructor
    }

    public static Plant createPlant(final PlantType type) {
        return createPlant(type, true, 0, 0);
    }

    public static Plant createPlant(final PlantType type, final boolean active, final int value, final int currentTime) {
        return switch (type) {
            case HEILKRAUTPFLANZE -> new PlantHeilkraut(active, value, currentTime);
            case ROSE -> new PlantRose(active, value, currentTime);
            case STOFF -> new PlantStoff(active, value, currentTime);
        };
    }
}
