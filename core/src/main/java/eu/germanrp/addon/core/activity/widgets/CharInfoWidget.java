package eu.germanrp.addon.core.activity.widgets;

import eu.germanrp.addon.api.models.CharacterInfo;
import lombok.Getter;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;


@AutoWidget
@Link("char-info-widget.lss")
public class CharInfoWidget extends FlexibleContentWidget {

    @Getter
    protected final CharacterInfo charInfo;

    private IconWidget iconWidget;
    private ComponentWidget playerNameWidget;
    private ComponentWidget characterNameWidget;

    public CharInfoWidget(final CharacterInfo charInfo) {
        this.charInfo = charInfo;
        this.addId("char-info-widget");
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        val icon = this.createIcon();
        if (icon != null) {
            this.iconWidget = new IconWidget(icon);
            this.iconWidget.addId("char-info-icon");
            this.addContent(this.iconWidget);
        }

        this.playerNameWidget = ComponentWidget.text(this.safeText(this.charInfo.playerName()));
        this.playerNameWidget.addId("char-info-player-name");
        this.addContent(this.playerNameWidget);

        this.characterNameWidget = ComponentWidget.text(this.safeText(this.charInfo.name()));
        this.characterNameWidget.addId("char-info-character-name");
        this.addFlexibleContent(this.characterNameWidget);
    }

    public void updateTitle() {
        if (this.playerNameWidget != null) {
            this.playerNameWidget.setComponent(Component.text(this.safeText(this.charInfo.playerName())));
        }
        if (this.characterNameWidget != null) {
            this.characterNameWidget.setComponent(Component.text(this.safeText(this.charInfo.name())));
        }
    }

    public void updateIcon() {
        if (this.iconWidget != null) {
            this.iconWidget.icon().set(this.createIcon());
        }
    }

    private Icon createIcon() {
        val uuid = this.charInfo.uniqueId();
        if (uuid != null) {
            return Icon.head(uuid);
        }

        val playerName = this.charInfo.playerName();
        if (playerName != null && !playerName.isBlank()) {
            return Icon.head(playerName);
        }

        return null;
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }


}
