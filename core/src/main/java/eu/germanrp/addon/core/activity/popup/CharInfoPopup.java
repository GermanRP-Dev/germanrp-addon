package eu.germanrp.addon.core.activity.popup;

import eu.germanrp.addon.api.models.CharacterInfo;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.widget.widgets.popup.SimpleAdvancedPopup;
import net.labymod.api.client.component.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

@Link("char-info-popup.lss")
public class CharInfoPopup extends SimpleAdvancedPopup {

    private final Action action;
    private final SimplePopupButton saveButton;
    private final List<Condition> conditions = new ArrayList<>();

    private String playerName;
    private String characterName;
    private final java.util.UUID uniqueId;
    private java.util.UUID resolvedUniqueId;

    private Consumer<CharacterInfo> saveListener;

    public CharInfoPopup() {
        this(Action.ADD, null);
    }

    public CharInfoPopup(CharacterInfo charInfo) {
        this(Action.EDIT, charInfo);
    }

    private CharInfoPopup(Action action, CharacterInfo charInfo) {
        this.action = action;
        this.uniqueId = charInfo != null ? charInfo.uniqueId() : null;
        this.playerName = charInfo != null ? charInfo.playerName() : null;
        this.characterName = charInfo != null ? charInfo.name() : null;
        if ((this.playerName == null || this.playerName.isBlank()) && this.uniqueId != null) {
            this.playerName = resolvePlayerName(this.uniqueId);
        }

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
                    String text = safeText(this.playerName);
                    Condition condition = new Condition(this.isPlayerNameValid(text));
                    this.conditions.add(condition);

                    TextFieldWidget playerNameInput = new TextFieldWidget();
                    playerNameInput.addId("char-info-popup-input");
                    playerNameInput.clearButton().set(true);
                    playerNameInput.setText(text);
                    playerNameInput.placeholder(Component.translatable(NAMESPACE + ".gui.char-info.placeholder.player-display-name"));
                    playerNameInput.updateListener(newValue -> {
                        this.playerName = normalizeText(newValue);
                        this.updateCondition(condition, this.isPlayerNameValid(this.playerName));
                    });

                    return playerNameInput;
                }
        ));

        container.addChild(this.createLabeledWidget(
                Component.translatable(NAMESPACE + ".gui.char-info.field.character-name"),
                () -> {
                    String text = safeText(this.characterName);
                    Condition condition = new Condition(this.characterName != null && !this.characterName.isBlank());
                    this.conditions.add(condition);

                    TextFieldWidget characterNameInput = new TextFieldWidget();
                    characterNameInput.addId("char-info-popup-input");
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

    public CharInfoPopup onSave(Runnable runnable) {
        return this.onSave(saved -> runnable.run());
    }

    private Widget createLabeledWidget(Component label, Supplier<Widget> widgetSupplier) {
        FlexibleContentWidget labelContainer = new FlexibleContentWidget();
        labelContainer.addId("char-info-popup-field");
        labelContainer.addContent(ComponentWidget.component(label));
        labelContainer.addContent(widgetSupplier.get());
        return labelContainer;
    }

    private void save() {
        java.util.UUID uuid = this.resolveUniqueIdForSave();
        if (uuid == null) {
            return;
        }

        CharacterInfo saved = new CharacterInfo(uuid, this.playerName, this.characterName);
        if (this.saveListener != null) {
            this.saveListener.accept(saved);
        }
    }

    private void updateCondition(Condition condition, boolean fulfilled) {
        condition.fulfilled = fulfilled;
        this.saveButton.enabled(this.allConditionsMet());
    }

    private boolean allConditionsMet() {
        for (Condition condition : this.conditions) {
            if (!condition.fulfilled) {
                return false;
            }
        }
        return true;
    }

    private boolean isPlayerNameValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        if (this.action == Action.EDIT) {
            return true;
        }

        this.resolvedUniqueId = resolveUniqueId(value);
        return this.resolvedUniqueId != null;
    }

    private java.util.UUID resolveUniqueIdForSave() {
        if (this.action == Action.EDIT) {
            return this.uniqueId;
        }

        if (this.resolvedUniqueId != null) {
            return this.resolvedUniqueId;
        }

        this.resolvedUniqueId = resolveUniqueId(this.playerName);
        return this.resolvedUniqueId;
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static java.util.UUID resolveUniqueId(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return null;
        }

        String normalized = playerName.trim();
        if (normalized.equalsIgnoreCase(Laby.labyAPI().getName())) {
            return Laby.labyAPI().getUniqueId();
        }

        var clientPacketListener = Laby.labyAPI().minecraft().getClientPacketListener();
        if (clientPacketListener == null) {
            return null;
        }

        var direct = clientPacketListener.getNetworkPlayerInfo(normalized);
        if (direct != null) {
            return direct.profile().getUniqueId();
        }

        for (var info : clientPacketListener.getNetworkPlayerInfos()) {
            String username = info.profile().getUsername();
            if (username != null && username.equalsIgnoreCase(normalized)) {
                return info.profile().getUniqueId();
            }

            String displayName = PlainTextComponentSerializer.plainText().serialize(info.displayName());
            if (displayName != null && displayName.equalsIgnoreCase(normalized)) {
                return info.profile().getUniqueId();
            }
        }

        return null;
    }

    private static String resolvePlayerName(java.util.UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }

        if (uniqueId.equals(Laby.labyAPI().getUniqueId())) {
            return Laby.labyAPI().getName();
        }

        var clientPacketListener = Laby.labyAPI().minecraft().getClientPacketListener();
        if (clientPacketListener == null) {
            return null;
        }

        var info = clientPacketListener.getNetworkPlayerInfo(uniqueId);
        if (info == null) {
            return null;
        }

        String displayName = PlainTextComponentSerializer.plainText().serialize(info.displayName());
        if (displayName != null && !displayName.isBlank()) {
            return displayName;
        }

        return info.profile().getUsername();
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
