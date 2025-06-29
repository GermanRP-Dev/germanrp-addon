package eu.germanrp.addon.api.models;

import lombok.Getter;

@Getter
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
    KARTELL("Kartell", Type.CRIME);
    VCI("VC Investment", Type.NEUTRAL),

    private final String memberInfoCommandArg;
    private final Type type;

    Faction(String memberInfoCommandArg, Type type) {
        this.memberInfoCommandArg = memberInfoCommandArg;
        this.type = type;
    }

    public enum Type {
        STAAT,
        MEDIC,
        CRIME,
        NEUTRAL
    }
}
