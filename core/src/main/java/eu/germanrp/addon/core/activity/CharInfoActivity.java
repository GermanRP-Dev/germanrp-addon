package eu.germanrp.addon.core.activity;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.core.activity.popup.CharInfoPopup;
import eu.germanrp.addon.core.activity.widgets.CharInfoHeaderWidget;
import eu.germanrp.addon.core.activity.widgets.CharInfoWidget;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.HrWidget;


import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

@AutoActivity
@Link("char-info.lss")
public class CharInfoActivity extends Activity {

    private static final Component HEADER_TEXT = Component.translatable(NAMESPACE + ".gui.char-info");

    private VerticalListWidget<CharInfoWidget> charInfoWidgetList;
    private CharInfoHeaderWidget headerWidget;
    private ButtonWidget removeButton;
    private ButtonWidget editButton;
    private CharInfoWidget selectedCharInfoWidget;

    public CharInfoActivity() {
        this.buildCharInfoList();
        this.updateCharInfoContextList();
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        val container = new FlexibleContentWidget();
        container.addId("char-info-container");

        this.headerWidget = new CharInfoHeaderWidget(HEADER_TEXT);
        container.addContent(this.headerWidget);

        this.buildCharInfoList();
        this.updateCharInfoContextList();

        val scrollWidget = new ScrollWidget(this.charInfoWidgetList);
        scrollWidget.addId("char-info-scroll");
        container.addFlexibleContent(scrollWidget);

        this.selectedCharInfoWidget = this.charInfoWidgetList.listSession().getSelectedEntry() instanceof CharInfoWidget
                ? (CharInfoWidget) this.charInfoWidgetList.listSession().getSelectedEntry()
                : null;

        val menu = new HorizontalListWidget();
        menu.addId("overview-button-menu");

        val addButton = ButtonWidget.i18n(NAMESPACE + ".ui.button.add", this::handleAddAction);
        menu.addEntry(addButton);

        this.editButton = ButtonWidget.i18n(NAMESPACE + ".ui.button.edit", this::handleEditAction);
        this.editButton.setEnabled(this.selectedCharInfoWidget != null);
        menu.addEntry(this.editButton);

        this.removeButton = ButtonWidget.i18n(NAMESPACE + ".ui.button.remove", this::handleRemoveAction);
        this.removeButton.setEnabled(this.selectedCharInfoWidget != null);
        menu.addEntry(this.removeButton);

        container.addContent(menu);

        this.document().addChild(container);
    }

    @Override
    public boolean mouseClicked(MutableMouse mouse, MouseButton mouseButton) {
        try {
            return super.mouseClicked(mouse, mouseButton);
        } finally {
            val clicked = this.findClickedCharInfoWidget(mouse);
            if (clicked != null) {
                this.selectCharInfoWidget(clicked);
            } else {
                this.clearCharInfoSelection();
            }
        }
    }

    private void updateCharInfoContextList() {
        this.charInfoWidgetList.removeChildIf(widget -> true);

        val charInfoMap = GermanRPAddon.getInstance().configuration().characterInfoMap();
        for (val charInfo : charInfoMap.values()) {
            val widget = new CharInfoWidget(charInfo);
            widget.setPressable(() -> this.selectCharInfoWidget(widget));
            this.charInfoWidgetList.addChild(widget);
        }

    }

    private CharInfoWidget findClickedCharInfoWidget(MutableMouse mouse) {
        val children = this.charInfoWidgetList.getChildrenAt(mouse.getX(), mouse.getY());
        for (val child : children) {
            Widget current = child;
            while (current != null) {
                if (current instanceof CharInfoWidget) {
                    return (CharInfoWidget) current;
                }
                val parent = current.getParent();
                current = parent instanceof Widget ? (Widget) parent : null;
            }
        }
        return null;
    }

    private void selectCharInfoWidget(CharInfoWidget widget) {
        this.charInfoWidgetList.listSession().setSelectedEntry(widget);
        this.selectedCharInfoWidget = widget;
        this.updateSelectedCharInfoStyles();
        if (this.editButton != null && this.removeButton != null) {
            this.editButton.setEnabled(true);
            this.removeButton.setEnabled(true);
        }
    }

    private void clearCharInfoSelection() {
        this.charInfoWidgetList.listSession().setSelectedEntry(null);
        this.selectedCharInfoWidget = null;
        this.updateSelectedCharInfoStyles();
        if (this.editButton != null && this.removeButton != null) {
            this.editButton.setEnabled(false);
            this.removeButton.setEnabled(false);
        }
    }

    private void updateSelectedCharInfoStyles() {
        for (val entry : this.charInfoWidgetList.getChildren()) {
            if (entry instanceof CharInfoWidget charInfoWidget) {
                charInfoWidget.setSelected(charInfoWidget == this.selectedCharInfoWidget);
            }
        }
    }

    private void buildCharInfoList() {
        this.charInfoWidgetList = new VerticalListWidget<>();
        this.charInfoWidgetList.addId("char-info-widget-list");
        this.charInfoWidgetList.selectable().set(true);
        this.charInfoWidgetList.setSelectCallback(charInfoWidget -> {
            this.selectedCharInfoWidget = charInfoWidget instanceof CharInfoWidget
                    ? (CharInfoWidget) charInfoWidget
                    : null;
            this.updateSelectedCharInfoStyles();
            if (this.editButton != null && this.removeButton != null) {
                this.editButton.setEnabled(this.selectedCharInfoWidget != null);
                this.removeButton.setEnabled(this.selectedCharInfoWidget != null);
            }
        });
        this.charInfoWidgetList.setDoubleClickCallback(widget -> this.handleEditAction());
    }

    private void handleAddAction() {
        val popup = new CharInfoPopup()
                .onSave(this::saveNewCharInfo);
        popup.displayInOverlay();
    }

    private void handleEditAction() {
        if (this.selectedCharInfoWidget == null) {
            return;
        }

        val original = this.selectedCharInfoWidget.getCharInfo();
        val popup = new CharInfoPopup(original)
                .onSave(updated -> this.saveEditedCharInfo(original, updated));
        popup.displayInOverlay();
    }

    private void handleRemoveAction() {
        if (this.selectedCharInfoWidget == null) {
            return;
        }

        val charInfo = this.selectedCharInfoWidget.getCharInfo();
        val config = GermanRPAddon.getInstance().configuration();
        boolean removed = false;

        if (charInfo.uniqueId() != null) {
            removed = config.characterInfoMap().remove(charInfo.uniqueId()) != null;
        }

        if (!removed) {
            val iterator = config.characterInfoMap().entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue().equals(charInfo)) {
                    iterator.remove();
                    removed = true;
                    break;
                }
            }
        }

        this.clearCharInfoSelection();
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

}
