package eu.germanrp.addon.api.nametag;

import net.labymod.api.laby3d.renderer.snapshot.ExtraKey;

public final class CharacterNameExtraKeys {

    private CharacterNameExtraKeys() {
        // Hide public constructor
    }

    public static final ExtraKey<CharacterNameSnapshot> CHARACTER_NAME = ExtraKey.of("character_name", CharacterNameSnapshot.class);

}
