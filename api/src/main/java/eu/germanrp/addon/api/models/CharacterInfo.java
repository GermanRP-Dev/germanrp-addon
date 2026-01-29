package eu.germanrp.addon.api.models;

import java.util.UUID;

/**
 * Character info
 *
 * @param uniqueId   the unique id of the player that this information belongs to
 * @param playerName the Minecraft Name of the player
 * @param name       the player's character name
 */
public record CharacterInfo(
        UUID uniqueId,
        String playerName,
        String name
) {
}
