package eu.germanrp.addon.core.activity;

import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.activity.popup.CharInfoPopup;
import eu.germanrp.addon.core.activity.widgets.CharInfoHeaderWidget;
import eu.germanrp.addon.core.activity.widgets.CharInfoListItemWidget;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;

import java.util.ArrayList;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

@AutoActivity
@Link("char-info.lss")
public class CharInfoActivity extends Activity {

    private static final Component HEADER_TEXT = Component.translatable(NAMESPACE + ".gui.char-info");

    private final VerticalListWidget<CharInfoListItemWidget> charInfoList;
    private ArrayList<CharInfoListItemWidget> charInfoWidgets;

    private ButtonWidget removeButton;
    private ButtonWidget editButton;
    private CharInfoListItemWidget selectedCharInfo;

    public CharInfoActivity() {
        this.charInfoList = new VerticalListWidget<>();
        this.charInfoWidgets = new ArrayList<>();
        this.charInfoList.addId("char-info-list");
        this.charInfoList.setSelectCallback(charInfoListItemWidget -> {
            val selectedWidget = this.charInfoList.listSession().getSelectedEntry();
            if (selectedWidget == null
                || selectedWidget.getCharacterInfo() != charInfoListItemWidget.getCharacterInfo()) {
                this.editButton.setEnabled(true);
                this.removeButton.setEnabled(true);
            }
        });

        this.charInfoList.setDoubleClickCallback(charInfoListItemWidget -> this.setAction(Action.EDIT));

        this.updateCharInfoContextList();
    }

    @Override
    public boolean mouseClicked(MutableMouse mouse, MouseButton mouseButton) {
        try {
            return super.mouseClicked(mouse, mouseButton);
        } finally {
            this.selectedCharInfo = this.charInfoList.listSession().getSelectedEntry();
            this.editButton.setEnabled(this.selectedCharInfo != null);
            this.removeButton.setEnabled(this.selectedCharInfo != null);
        }
    }

    private void updateCharInfoContextList() {
        this.reload();
    }

    private void handleAddAction() {
        val popup = new CharInfoPopup()
                .onSave(this::saveNewCharInfo);
        popup.displayInOverlay();
    }

    private void handleEditAction() {
        if (this.selectedCharInfo == null) {
            return;
        }

        val original = this.selectedCharInfo.getCharInfo();
        val popup = new CharInfoPopup(original)
                .onSave(updated -> this.saveEditedCharInfo(original, updated));
        popup.displayInOverlay();
    }

    private void handleRemoveAction() {
        if (this.selectedCharInfo == null) {
            return;
        }

        val charInfo = this.selectedCharInfo.getCharInfo();
        val config = GermanRPAddon.getInstance().configuration();
        var removed = false;

        if (charInfo.uniqueId() != null) {
            removed = config.characterInfoMap().remove(charInfo.uniqueId()) != null;
        }

        if (!removed) {
            val iterator = config.characterInfoMap().entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue().equals(charInfo)) {
                    iterator.remove();
                    break;
                }
            }
        }

        this.charInfoList.listSession().setSelectedEntry(null);
        this.selectedCharInfo = null;
        this.reload();
    }

    private void saveNewCharInfo(CharacterInfo charInfo) {
        if (charInfo == null || charInfo.uniqueId() == null) {
            return;
        }

        GermanRPAddon.getInstance().configuration().characterInfoMap()
                .put(charInfo.uniqueId(), charInfo);
        this.reload();
    }

    private void saveEditedCharInfo(CharacterInfo original, CharacterInfo updated) {
        if (updated == null || updated.uniqueId() == null) {
            return;
        }

        val config = GermanRPAddon.getInstance().configuration();
        val oldId = original != null ? original.uniqueId() : null;
        if (oldId != null && !oldId.equals(updated.uniqueId())) {
            config.characterInfoMap().remove(oldId);
        } else if (oldId == null && original != null) {
            val iterator = config.characterInfoMap().entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue().equals(original)) {
                    iterator.remove();
                    break;
                }
            }
        }

        config.characterInfoMap().put(updated.uniqueId(), updated);
        this.reload();
    }

    public void setAction(Action action) {
        switch (action) {
            case ADD:
                this.handleAddAction();
                break;
            case EDIT:
                this.handleEditAction();
                break;
            case REMOVE:
                this.handleRemoveAction();
                break;
        }
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        val container = new FlexibleContentWidget();
        container.addId("char-info-container");

        val headerWidget = new CharInfoHeaderWidget(HEADER_TEXT);
        container.addContent(headerWidget);

        val widgets = new VerticalListWidget<>();
        widgets.addId("char-info-overview-list");

        this.charInfoWidgets = new ArrayList<>();

        val charInfoMap = GermanRPAddon.getInstance().configuration().characterInfoMap();
        for (val charInfo : charInfoMap.values()) {
            val widget = new CharInfoListItemWidget(charInfo);
            widget.setPressable(() -> this.charInfoList.listSession().setSelectedEntry(widget));
            widgets.addChild(widget);
            this.charInfoWidgets.add(widget);
            this.charInfoList.addChild(widget);
        }

        val scrollWidget = new ScrollWidget(widgets);
        scrollWidget.addId("char-info-scroll");
        container.addFlexibleContent(scrollWidget);

        this.selectedCharInfo = this.charInfoList.listSession().getSelectedEntry();

        val menu = new HorizontalListWidget();
        menu.addId("overview-button-menu");

        val addButton = ButtonWidget.i18n(NAMESPACE + ".ui.button.add", () -> this.setAction(Action.ADD));
        menu.addEntry(addButton);

        this.editButton = ButtonWidget.i18n(NAMESPACE + ".ui.button.edit", () -> this.setAction(Action.EDIT));
        this.editButton.setEnabled(this.selectedCharInfo != null);
        menu.addEntry(this.editButton);

        this.removeButton = ButtonWidget.i18n(NAMESPACE + ".ui.button.remove", () -> this.setAction(Action.REMOVE));
        this.removeButton.setEnabled(this.selectedCharInfo != null);
        menu.addEntry(this.removeButton);

        container.addContent(menu);

        this.document().addChild(container);
    }

    public enum Action {
        ADD, EDIT, REMOVE
    }

}
