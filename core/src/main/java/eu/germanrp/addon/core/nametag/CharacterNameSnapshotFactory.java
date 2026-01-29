package eu.germanrp.addon.core.nametag;

import eu.germanrp.addon.api.nametag.CharacterNameExtraKeys;
import eu.germanrp.addon.api.nametag.CharacterNameSnapshot;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.laby3d.renderer.snapshot.Extras;
import net.labymod.api.laby3d.renderer.snapshot.LabySnapshotFactory;
import net.labymod.api.service.annotation.AutoService;

@AutoService(LabySnapshotFactory.class)
public class CharacterNameSnapshotFactory extends LabySnapshotFactory<Player, CharacterNameSnapshot> {

    public CharacterNameSnapshotFactory() {
        super(CharacterNameExtraKeys.CHARACTER_NAME);
    }

    @Override
    protected CharacterNameSnapshot create(Player player, Extras extras) {
        return new DefaultCharacterNameSnapshot(player, extras);
    }

}
