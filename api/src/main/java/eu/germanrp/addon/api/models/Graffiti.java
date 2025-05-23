package eu.germanrp.addon.api.models;

import eu.germanrp.addon.api.Nearest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.labymod.api.util.math.position.DefaultPosition;
import net.labymod.api.util.math.position.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum Graffiti implements Nearest {

    CAMORRA_HQ("Autobahnbrücke (Reichenviertel)", new DefaultPosition(458, 71, 262)),
    GARAGE_POLICE_HQ("Tiefgarage (Polizeipräsidium)", new DefaultPosition(498, 58, -781)),
    GARAGE_HOLDEN_TOWER("Tiefgarage (Holden-Tower)", new DefaultPosition(508, 61, -850)),
    APOTHEKE_1("Apotheke-1 (Downtown)", new DefaultPosition(646, 68, -943)),
    GARAGE_PLATTENBAU("Tiefgarage (Plattenbau)", new DefaultPosition(699, 57, -1088)),
    SCHOOL_DOWNTOWN("Schule (Downtown)", new DefaultPosition(768, 68, -1061)),
    POST("Postzentrale (Downtown)", new DefaultPosition(499, 68, -1118)),
    TRAM_ALTSTADT("Tram-Unterführung (Altstadt)", new DefaultPosition(-635, 70, -944)),
    U_BAHN("U-Bahn (Altstadt)", new DefaultPosition(-474, 52, -888)),
    U_BAHN_STRANDPASSAGE("U-Bahn (Strandpassage)", new DefaultPosition(552, 52, 795)),
    U_BAHN_ASIA("U-Bahn (Asia-Viertel)", new DefaultPosition(-464, 70, 935)),
    JVA_ALTSTADT("JVA (Altstadt)", new DefaultPosition(-798, 70, -754));

    private final String name;
    private final Position position;

    @Override
    public Position getPosition() {
        return this.position;
    }

    public static @NotNull Optional<Graffiti> fromName(String name) {
        return stream(Graffiti.values())
                .filter(graffiti -> graffiti.getName().equals(name))
                .findFirst();
    }
}
