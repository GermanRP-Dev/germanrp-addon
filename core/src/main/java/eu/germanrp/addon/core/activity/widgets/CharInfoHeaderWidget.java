package eu.germanrp.addon.core.activity.widgets;

import eu.germanrp.addon.core.nametag.CharacterNameTag;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget.State;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;

@AutoWidget
public class CharInfoHeaderWidget extends HorizontalListWidget {

    private final CheckBoxWidget checkbox;
    private final ComponentWidget title;

    public CharInfoHeaderWidget(Component titleText) {
        this.checkbox = new CheckBoxWidget();
        this.title = ComponentWidget.component(titleText);

        this.addId("char-info-header");
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        this.checkbox.setState(CharacterNameTag.isEnabled() ? State.CHECKED : State.UNCHECKED);
        this.checkbox.setPressable(this::handleNameTagToggle);
        this.checkbox.addId("checkbox");
        this.title.addId("title");

        this.addEntry(this.checkbox);
        this.addEntry(this.title);
    }

    private void handleNameTagToggle() {
        CharacterNameTag.setEnabled(this.checkbox.state() == State.CHECKED);
    }
}
