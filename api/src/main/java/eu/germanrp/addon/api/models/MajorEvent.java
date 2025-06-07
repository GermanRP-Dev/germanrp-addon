package eu.germanrp.addon.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum MajorEvent {

    CLEAR("-", 0),
    NONE("Keiner", 0),
    BOMB("Bombe", 10*60),
    ROB("Raub", 3*60),
    ROB_ABOUT_TO_END("Raub (10s)", 10),
    PHARMACY_ROB("Apothekenraub", 8*60),
    HACKER("Hackangriff", 8*60),
    JEWELLERY_ROB("Juwelier", 3*60),
    MUSEUM_ROB("Museum", 3*60);

    private final String name;
    private final int seconds;

    public static Optional<MajorEvent> fromName(String name) {
        return stream(MajorEvent.values())
                .filter(majorEvent -> majorEvent.getName().equals(name))
                .findFirst();
    }

}
