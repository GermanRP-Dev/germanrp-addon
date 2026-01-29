package eu.germanrp.addon.core.activity.popup;

import lombok.val;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.lss.property.DirectPropertyValueAccessor;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

final class PlayerNameField extends TextFieldWidget {
    private static final String TEXT_FIELD_TYPE_NAME = "TextField";
    private static final String TEXT_FIELD_QUALIFIED_NAME = TextFieldWidget.class.getName()
            .replace('.', '/');
    private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("^\\w{3,16}$");
    private int resolveToken;
    private UUID currentResolved;
    private String lastNormalized;

    PlayerNameField(String text, Component placeholder, UUID initialUuid, PlayerNameListener updateListener) {
        this.addId(CharInfoPopup.CHAR_INFO_POPUP_INPUT);
        this.clearButton().set(true);
        this.setText(text);
        this.placeholder(placeholder);
        this.currentResolved = initialUuid;
        this.lastNormalized = normalizeText(text);
        this.updateListener(newValue -> this.handleUpdate(newValue, updateListener));
        this.handleUpdate(text, updateListener);
    }

    static boolean isValid(String value) {
        return value != null && PLAYER_NAME_PATTERN.matcher(value).matches();
    }

    static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        val trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void handleUpdate(String value, PlayerNameListener updateListener) {
        val normalized = normalizeText(value);
        val nextToken = ++this.resolveToken;
        if (useCurrentResolution(normalized, updateListener)) {
            return;
        }
        if (!applyNameChange(normalized, updateListener)) {
            return;
        }
        resolveUniqueIdAsync(normalized, nextToken, updateListener);
    }

    private boolean useCurrentResolution(String normalized, PlayerNameListener updateListener) {
        if (!Objects.equals(normalized, this.lastNormalized) || this.currentResolved == null) {
            return false;
        }
        if (updateListener != null) {
            updateListener.accept(normalized, this.currentResolved);
        }
        return true;
    }

    private boolean applyNameChange(String normalized, PlayerNameListener updateListener) {
        if (!Objects.equals(normalized, this.lastNormalized)) {
            this.lastNormalized = normalized;
            this.currentResolved = null;
            if (updateListener != null) {
                updateListener.accept(normalized, null);
            }
        } else if (updateListener != null && this.currentResolved != null) {
            updateListener.accept(normalized, this.currentResolved);
        }
        if (!isValid(normalized)) {
            this.currentResolved = null;
            return false;
        }
        return true;
    }

    private void resolveUniqueIdAsync(String normalized, int token, PlayerNameListener updateListener) {
        Laby.labyAPI().minecraft().executeOnRenderThread(() ->
                Laby.references().labyNetController().loadUniqueIdByName(normalized, result -> {
                    if (token != this.resolveToken || updateListener == null) {
                        return;
                    }
                    if (result == null || result.hasException() || !result.isPresent()) {
                        this.currentResolved = null;
                        updateListener.accept(normalized, null);
                        return;
                    }
                    result.ifPresent(resolved -> {
                        this.currentResolved = resolved;
                        updateListener.accept(normalized, resolved);
                    });
                }));
    }

    @FunctionalInterface
    interface PlayerNameListener {
        void accept(String normalizedName, UUID resolvedUniqueId);
    }

    @Override
    public String getTypeName() {
        return TEXT_FIELD_TYPE_NAME;
    }

    @Override
    public String getQualifiedName() {
        return TEXT_FIELD_QUALIFIED_NAME;
    }

    @Override
    public DirectPropertyValueAccessor getDirectPropertyValueAccessor() {
        return Laby.references().propertyRegistry()
                .getDirectPropertyValueAccessor(TextFieldWidget.class);
    }
}
