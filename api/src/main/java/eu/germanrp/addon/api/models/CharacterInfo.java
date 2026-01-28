package eu.germanrp.addon.api.models;

import java.util.UUID;

public record CharacterInfo(
        UUID uniqueId,
        String playerName,
        String name
) {
}
