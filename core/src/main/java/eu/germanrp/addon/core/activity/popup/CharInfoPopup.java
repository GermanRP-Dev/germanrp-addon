package eu.germanrp.addon.core.activity.popup;

import eu.germanrp.addon.api.models.CharacterInfo;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.popup.SimpleAdvancedPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

@Link("char-info-popup.lss")
public class CharInfoPopup extends SimpleAdvancedPopup {

    public static final String CHAR_INFO_POPUP_INPUT = "char-info-popup-input";
    private final SimplePopupButton saveButton;
    private final List<Condition> conditions = new ArrayList<>();

    private java.util.UUID uniqueId;
    private String playerName;
    private String characterName;

    private Consumer<CharacterInfo> saveListener;

    public CharInfoPopup() {
        this(Action.ADD, null);
    }

    public CharInfoPopup(CharacterInfo charInfo) {
        this(Action.EDIT, charInfo);
    }

    private CharInfoPopup(Action action, CharacterInfo charInfo) {
        this.uniqueId = charInfo != null ? charInfo.uniqueId() : null;
        this.playerName = charInfo != null ? charInfo.playerName() : null;
        this.characterName = charInfo != null ? charInfo.name() : null;

        this.title = Component.translatable(action == Action.ADD
                ? NAMESPACE + ".gui.char-info.add-title"
                : NAMESPACE + ".gui.char-info.edit-title");

        this.saveButton = SimplePopupButton.create(
                Component.translatable(NAMESPACE + ".ui.button.save"),
                button -> this.save()
        );

        this.buttons = new ArrayList<>();
        this.buttons.add(SimplePopupButton.create(
                "cancel",
                Component.translatable(NAMESPACE + ".ui.button.cancel"),
                null
        ));
        this.buttons.add(this.saveButton);
    }

    @Override
    protected void initializeCustomWidgets(VerticalListWidget<Widget> container) {
        this.conditions.clear();

        container.addChild(this.createLabeledWidget(
                Component.translatable(NAMESPACE + ".gui.char-info.field.player-display-name"),
                () -> {
                    val text = safeText(this.playerName);
                    val condition = new Condition(false);
                    this.conditions.add(condition);

                    return new PlayerNameField(
                            text,
                            Component.translatable(NAMESPACE + ".gui.char-info.placeholder.player-display-name"),
                            this.uniqueId,
                            (normalizedValue, resolvedUniqueId) -> {
                                this.playerName = normalizedValue;
                                this.uniqueId = resolvedUniqueId;
                                this.updateCondition(condition, resolvedUniqueId != null);
                            }
                    );
                }
        ));

        container.addChild(this.createLabeledWidget(
                Component.translatable(NAMESPACE + ".gui.char-info.field.character-name"),
                () -> {
                    val text = safeText(this.characterName);
                    val condition = new Condition(this.characterName != null && !this.characterName.isBlank());
                    this.conditions.add(condition);

                    val characterNameInput = new TextFieldWidget();
                    characterNameInput.addId(CHAR_INFO_POPUP_INPUT);
                    characterNameInput.clearButton().set(true);
                    characterNameInput.setText(text);
                    characterNameInput.placeholder(Component.translatable(NAMESPACE + ".gui.char-info.placeholder.character-name"));
                    characterNameInput.updateListener(newValue -> {
                        this.characterName = normalizeText(newValue);
                        this.updateCondition(condition, this.characterName != null);
                    });

                    return characterNameInput;
                }
        ));

        this.saveButton.enabled(this.allConditionsMet());
    }

    public CharInfoPopup onSave(Consumer<CharacterInfo> consumer) {
        this.saveListener = consumer;
        return this;
    }

    private Widget createLabeledWidget(Component label, Supplier<Widget> widgetSupplier) {
        val labelContainer = new FlexibleContentWidget();
        labelContainer.addId("char-info-popup-field");
        labelContainer.addContent(ComponentWidget.component(label));
        labelContainer.addContent(widgetSupplier.get());
        return labelContainer;
    }

    private void save() {
        if (this.uniqueId == null) {
            return;
        }

        val saved = new CharacterInfo(this.uniqueId, this.playerName, this.characterName);
        if (this.saveListener != null) {
            this.saveListener.accept(saved);
        }
    }

    private void updateCondition(Condition condition, boolean fulfilled) {
        condition.fulfilled = fulfilled;
        this.saveButton.enabled(this.allConditionsMet());
    }

    private boolean allConditionsMet() {
        for (val condition : this.conditions) {
            if (!condition.fulfilled) {
                return false;
            }
        }
        return true;
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static String normalizeText(String value) {
        return PlayerNameField.normalizeText(value);
    }

    public enum Action {
        ADD,
        EDIT
    }

    private static class Condition {

        boolean fulfilled;

        Condition(boolean defaultFulfilled) {
            this.fulfilled = defaultFulfilled;
        }
    }
}
