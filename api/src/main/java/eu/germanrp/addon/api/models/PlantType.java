package eu.germanrp.addon.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PlantType {

    HEILKRAUTPFLANZE("Heilkrautpflanze", "Heilkraut", "Heilkrautpflanze", 15, "g",
            "► Du hast eine Heilkrautpflanze ausgelegt."),
    ROSE("Rosenstrauch", "Rose", "Rose", 8, "x", "► Du hast einen Rosenstrauch angepflanzt."),
    STOFF("Stoffpflanze", "Stoff", "Stoffpflanze", 15, " Stück",
            "► Du hast eine Stoffpflanze ausgelegt.");

    private final String displayName;
    private final String substanceName;
    private final String paketType;
    private final int maxTime;
    private final String yieldUnit;
    private final String sowMessage;

    /**
     * This method takes in the display name sent to the player in the chat when mentioning the plant
     *
     * @param displayName the display name
     *
     * @return the associated {@link PlantType}
     */
    public static @NotNull Optional<PlantType> fromDisplayName(final String displayName) {
        for (PlantType plantType : PlantType.values()) {
            if (plantType.getDisplayName().equals(displayName)) {
                return Optional.of(plantType);
            }
        }
        return Optional.empty();
    }

    /**
     * This method takes in a given type which is parsed from the packets that are sent to the client.
     *
     * @param type the type that is mentioned in the packet
     *
     * @return the associated {@link PlantType}
     */
    public static @NotNull Optional<PlantType> fromPaketType(final String type) {
        for (PlantType plantType : PlantType.values()) {
            if (plantType.getPaketType().equals(type)) {
                return Optional.of(plantType);
            }
        }
        return Optional.empty();
    }


    public static @NotNull Optional<PlantType> fromSowMessage(final String message) {
        for (PlantType plantType : PlantType.values()) {
            if (plantType.getSowMessage().equals(message)) {
                return Optional.of(plantType);
            }
        }
        return Optional.empty();
    }
}
