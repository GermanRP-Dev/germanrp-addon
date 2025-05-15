package eu.germanrp.addon.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.labymod.api.util.math.vector.IntVector3;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Graffiti {

    CAMORRA_HQ("Autobahnbrücke (Reichenviertel)", new IntVector3(458, 71, 262)),
    GARAGE_POLICE_HQ("Tiefgarage (Polizeipräsidium)", new IntVector3(498, 58, -781)),
    GARAGE_HOLDEN_TOWER("Tiefgarage (Holden-Tower)", new IntVector3(508, 61, -850)),
    APOTHEKE_1("Apotheke-1 (Downtown)", new IntVector3(646, 68, -943)),
    GARAGE_PLATTENBAU("Tiefgarage (Plattenbau)", new IntVector3(699, 57, -1088)),
    SCHOOL_DOWNTOWN("Schule (Downtown)", new IntVector3(768, 68, -1061)),
    POST("Postzentrale (Downtown)", new IntVector3(499, 68, -1118)),
    TRAM_ALTSTADT("Tram-Unterführung (Altstadt)", new IntVector3(-635, 70, -944)),
    U_BAHN("U-Bahn (Altstadt)", new IntVector3(-474, 52, -888)),
    U_BAHN_STRANDPASSAGE("U-Bahn (Strandpassage)", new IntVector3(552, 52, 795)),
    U_BAHN_ASIA("U-Bahn (Asia-Viertel)", new IntVector3(-464, 70, 935)),
    JVA_ALTSTADT("JVA (Altstadt)", new IntVector3(-798, 70, -754));

    private final String name;
    private final IntVector3 position;

    public static Optional<Graffiti> getByBlockPosition(IntVector3 blockPosition) {
        for (Graffiti graffiti : Graffiti.values()) {
            if (graffiti.getPosition().equals(blockPosition)) {
                return Optional.of(graffiti);
            }
        }
        return Optional.empty();
    }

    public static Optional<Graffiti> getByName(String name) {
        for (Graffiti graffiti : Graffiti.values()) {
            if (graffiti.getName().equals(name)) {
                return Optional.of(graffiti);
            }
        }
        return Optional.empty();
    }
}
