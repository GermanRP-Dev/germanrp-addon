package eu.germanrp.addon.api.models;

/**
 * Represents a server player with a name, this class strips the [GR] prefix from the player's name.
 *
 * @param name the player's name
 */
public record ServerPlayer(String name) {

    public static final String GR_NAME_PREFIX = "[GR]";

    public ServerPlayer(String name) {
        this.name = name.replace(GR_NAME_PREFIX, "");
    }

}
