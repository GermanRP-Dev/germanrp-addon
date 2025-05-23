package eu.germanrp.addon.core.Enum;

import lombok.Getter;

@Getter
public enum FactionName {

    NONE(null, FactionType.NEUTRAL),
    POLIZEI("Polizei", FactionType.STAAT),
    RETTUNGSDIENST("Rettungsdienst", FactionType.MEDIC),
    SINALOAKARTELL("CDS", FactionType.BADFRAK),
    CAMORRA("Camorra", FactionType.BADFRAK),
    ROUSSEAU("Rousseau", FactionType.BADFRAK),
    PRESSE("Presseagentur", FactionType.BADFRAK),
    MTFASHION("MT-Fashion", FactionType.BADFRAK),
    ESTABLISHMENT("Establishment", FactionType.BADFRAK),
    KARTELL("Kartell", FactionType.BADFRAK);

    private final String memberInfoCommandArg;
    private final FactionType type;

    FactionName(String memberInfoCommandArg, FactionType type) {
        this.memberInfoCommandArg = memberInfoCommandArg;
        this.type = type;
    }

    public enum FactionType {

        STAAT,
        MEDIC,
        BADFRAK,
        NEUTRAL
    }
}
