package eu.germanrp.addon.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public enum Faction {

    UNKNOWN(null, null, Type.NEUTRAL),
    NONE(null, "Keine (Zivilist)", Type.NEUTRAL),
    POLIZEI("Polizei", "Polizei", Type.STAAT),
    RETTUNGSDIENST("Rettungsdienst", "Rettungsdienst", Type.MEDIC),
    CAMORRA("Camorra", "Camorra", Type.CRIME),
    BRATVA_GANG("Bratva Gang", "Bratva Gang", Type.CRIME),
    PRESSE("Presseagentur", "Presseagentur", Type.NEUTRAL),
    ESTABLISHMENT("Establishment", "The Estalishment", Type.CRIME),
    VCI("VCI", "VanceCity Investment", Type.NEUTRAL),
    LA_COSA_NOSTRA("lcn", "La Cosa Nostra", Type.CRIME),
    IRON_SERPENTS("Serpents", "Iron Serpents", Type.CRIME),
    YAKUZA("Yakuza", "Yakuza", Type.CRIME);

    private final String memberInfoCommandArg;
    private final String displayName;
    private final @NotNull Type type;

    public enum Type {
        STAAT,
        MEDIC,
        CRIME,
        NEUTRAL
    }

    public static Faction fromDisplayName(String displayName) {
        for (Faction faction : values()) {
            val factionDisplayName = faction.getDisplayName();

            if (factionDisplayName == null) continue;

            if (factionDisplayName.equals(displayName)) return faction;
        }
        return UNKNOWN;
    }

}
