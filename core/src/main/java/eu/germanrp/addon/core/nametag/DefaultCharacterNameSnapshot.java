package eu.germanrp.addon.core.nametag;

import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.api.nametag.CharacterNameSnapshot;
import eu.germanrp.addon.core.GermanRPAddon;
import lombok.val;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.laby3d.renderer.snapshot.AbstractLabySnapshot;
import net.labymod.api.laby3d.renderer.snapshot.Extras;

public class DefaultCharacterNameSnapshot extends AbstractLabySnapshot implements CharacterNameSnapshot {

    private final String name;

    protected DefaultCharacterNameSnapshot(Player player, Extras extras) {
        super(extras);

        val uuid = player.getUniqueId();

        val characterInfoMap = GermanRPAddon.getInstance().configuration().characterInfoMap();

        this.name = characterInfoMap.getOrDefault(uuid, new CharacterInfo(null, null, "Unbekannt")).name();
    }

    @Override
    public String name() {
        return this.name;
    }

}
