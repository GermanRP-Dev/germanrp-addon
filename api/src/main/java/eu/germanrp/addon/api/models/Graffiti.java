package eu.germanrp.addon.api.models;

import eu.germanrp.addon.api.Nearest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.labymod.api.util.math.position.DefaultPosition;
import net.labymod.api.util.math.position.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Graffiti implements Nearest {

    REICHENBRUECKE("Autobahnbrücke (Reichenviertel)", new DefaultPosition(458, 71, 263)),
    JVA("JVA (Altstadt)", new DefaultPosition(-798, 70, -754)),
    POST("Postzentrale (Downtown)", new DefaultPosition(496, 68, -1096)),
    SCHULE("Schule (Downtown)", new DefaultPosition(768, 68, -1061)),
    TIEFGARAGE_ALTENHEIM("Tiefgarage (Polizeipräsidium)", new DefaultPosition(498, 58, -781)),
    TIEFGARAGE_PLATTENBAU("Tiefgarage (Plattenbau)", new DefaultPosition(699, 57, -1111)),
    TIEFGARAGE_HOLDEN("Tiefgarage (Holden-Tower)", new DefaultPosition(509, 61, -850)),
    APO1("Apotheke-1 (Downtown)", new DefaultPosition(646, 68, -943)),
    U_BAHN_ALTSTADT("U-Bahn (Altstadt)", new DefaultPosition(-474, 52, -888)),
    TRAM("Tram-Unterführung (Altstadt)", new DefaultPosition(-635, 70, -944)),
    U_BAHN_ASIA("U-Bahn (Asia-Viertel)", new DefaultPosition(-464, 70, 935)),
    U_BAHN_STRANDPASSAGE("U-Bahn (Strandpassage)", new DefaultPosition(552, 52, 795));

    private final String name;
    private final Position position;

    @Override
    public Position getPosition() {
        return this.position;
    }

    public static @NotNull Optional<Graffiti> fromName(String name) {
        for (Graffiti graffiti : Graffiti.values()) {
            if (graffiti.getName().equals(name)) {
                return Optional.of(graffiti);
            }
        }
        return Optional.empty();
    }
}
