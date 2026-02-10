package eu.germanrp.addon.core.activity.widgets;

import eu.germanrp.addon.api.models.CharacterInfo;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;

@AutoWidget
public class CharInfoListItemWidget extends CharInfoWidget {

    public CharInfoListItemWidget(CharacterInfo charInfo) {
        super(charInfo);
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);
    }

    public CharacterInfo getCharacterInfo() {
        return this.charInfo;
    }
}
