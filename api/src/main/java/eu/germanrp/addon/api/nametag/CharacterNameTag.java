package eu.germanrp.addon.api.nametag;

import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.tag.tags.ComponentNameTag;
import net.labymod.api.client.render.state.entity.EntitySnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CharacterNameTag extends ComponentNameTag {

    @Override
    protected @NotNull List<Component> buildComponents(EntitySnapshot snapshot) {
        if (!snapshot.has(CharacterNameExtraKeys.CHARACTER_NAME)) {
            return super.buildComponents(snapshot);
        }

        val characterNameSnapshot = snapshot.get(CharacterNameExtraKeys.CHARACTER_NAME);
        val name = characterNameSnapshot.name();

        if (name == null) {
            return List.of();
        }

        return List.of(Component.text(name));
    }

    @Override
    public float getScale() {
        return 0.5f;
    }

}
