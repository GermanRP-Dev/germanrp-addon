package eu.germanrp.addon.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Faction {

    NONE(null, Type.NEUTRAL),
    POLIZEI("Polizei", Type.STAAT),
    RETTUNGSDIENST("Rettungsdienst", Type.MEDIC),
    SINALOAKARTELL("CDS", Type.CRIME),
    CAMORRA("Camorra", Type.CRIME),
    ROUSSEAU("Rousseau", Type.CRIME),
    PRESSE("Presseagentur", Type.NEUTRAL),
    MTFASHION("MT-Fashion", Type.NEUTRAL),
    ESTABLISHMENT("Establishment", Type.CRIME),
    VCI("VCI", Type.NEUTRAL),
    KARTELLCAYOPERICO("CDCP", Type.CRIME),
    KARTELL("Kartell", Type.CRIME),
    IRON_SERPENTS("Iron Serpent", Type.CRIME),
    BRATVA_GANG("Bratva", Type.CRIME);

    private final String memberInfoCommandArg;
    private final Type type;

    public enum Type {
        STAAT,
        MEDIC,
        CRIME,
        NEUTRAL
    }
}
