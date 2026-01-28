package net.labymod.api.client.gui.screen.widget.widgets.input;

import net.labymod.api.Laby;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.key.mapper.KeyMapper;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.action.Switchable;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.BoundsType;
import net.labymod.api.client.gui.screen.widget.cursor.CursorTypes;
import net.labymod.api.client.sound.SoundType;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.accessor.SettingAccessor;
import net.labymod.api.configuration.settings.annotation.SettingElement;
import net.labymod.api.configuration.settings.annotation.SettingFactory;
import net.labymod.api.configuration.settings.annotation.SettingWidget;
import net.labymod.api.configuration.settings.switchable.BooleanSwitchableHandler;
import net.labymod.api.configuration.settings.widget.WidgetFactory;
import net.labymod.api.util.I18n;
import net.labymod.api.util.PrimitiveHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AutoWidget
@SettingWidget
public class SwitchWidget extends SimpleWidget {

  private static final String DEFAULT_ENABLED = "labymod.ui.switch.enabled";
  private static final String DEFAULT_DISABLED = "labymod.ui.switch.disabled";

  private final Switchable switchable;
  private boolean value;

  private String enabledText = "";
  private String disabledText = "";

  private String enabledTranslatableKey = null;
  private String disabledTranslatableKey = null;

  private final LssProperty<Integer> textHoverColor = new LssProperty<>(
      NamedTextColor.YELLOW.getValue()
  );

  protected SwitchWidget(Switchable switchable) {
    this.switchable = switchable;
    this.setHoverCursor(CursorTypes.POINTING_HAND, true);
  }

  public static SwitchWidget create(Switchable switchable) {
    return translatable(DEFAULT_ENABLED, DEFAULT_DISABLED, switchable);
  }

  @Override
  public String getDefaultRendererName() {
    return "Switch";
  }

  public boolean getValue() {
    return this.value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  @Override
  public boolean onPress() {
    this.value = !this.value;
    if (this.switchable != null) {
      this.switchable.switchValue(this.value);
    }

    Laby.references().soundService().play(
        this.value ? SoundType.SWITCH_TOGGLE_ON : SoundType.SWITCH_TOGGLE_OFF
    );

    return true;
  }

  @Override
  public void tick() {
  }

  @Override
  public boolean mouseReleased(MutableMouse mouse, MouseButton mouseButton) {
    return true;
  }

  @Override
  public boolean mouseClicked(MutableMouse mouse, MouseButton mouseButton) {
    if (this.isHovered() && mouseButton == MouseButton.LEFT) {
      this.onPress();
      this.callActionListeners();
      return true;
    }

    return false;
  }

  @Override
  public boolean mouseScrolled(MutableMouse mouse, double scrollDelta) {
    return true;
  }

  public String getText() {
    return this.value ? this.enabledText : this.disabledText;
  }

  @Override
  public float getContentWidth(BoundsType type) {
    return 50;
  }

  @Override
  public float getContentHeight(BoundsType type) {
    return 20;
  }

  @Override
  public boolean isHoverComponentRendered() {
    // don't cancel the parents hover if the hover color is null
    if (this.textHoverColor.get() == null) {
      return false;
    }

    return this.hasHoverComponent() ? super.isHoverComponentRendered() : this.isHovered();
  }

  public static SwitchWidget translatable(
      String enabledTranslatableKey,
      String disabledTranslatableKey,
      Switchable switchable
  ) {
    SwitchWidget widget = new SwitchWidget(switchable);
    widget.enabledTranslatableKey = enabledTranslatableKey;
    widget.disabledTranslatableKey = disabledTranslatableKey;
    return widget;
  }

  public static SwitchWidget text(
      String enabledText,
      String disabledText,
      Switchable switchable
  ) {
    SwitchWidget widget = new SwitchWidget(switchable);
    widget.enabledText = enabledText;
    widget.disabledText = disabledText;
    return widget;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    // Important: Localization should always be done when initializing the widget
    if (this.enabledTranslatableKey != null) {
      String enabledTranslation = I18n.getTranslation(this.enabledTranslatableKey);
      if (enabledTranslation == null) {
        this.enabledText = I18n.translate(DEFAULT_ENABLED);
      } else {
        this.enabledText = enabledTranslation;
      }
    }

    if (this.disabledTranslatableKey != null) {
      String disabledTranslation = I18n.getTranslation(this.disabledTranslatableKey);
      if (disabledTranslation == null) {
        this.disabledText = I18n.translate(DEFAULT_DISABLED);
      } else {
        this.disabledText = disabledTranslation;
      }
    }
  }

  public LssProperty<Integer> textHoverColor() {
    return this.textHoverColor;
  }

  /**
   * The switch setting annotation. Will result in a {@link SwitchWidget} when added to a field in a
   * config; can be translated by adding "PATH.enabled" and "PATH.disabled" to the
   * internationalization file.
   */
  @SettingElement(switchable = BooleanSwitchableHandler.class)
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface SwitchSetting {

    /**
     * When set to {@code true} a keybind widget is displayed next to the switch which can be used
     * by the user to specify a custom (optional) keybind to toggle the switch. If set to
     * {@code false} this setting will be a simple switch.
     */
    boolean hotkey() default false;
  }

  @SettingFactory
  public static class Factory implements WidgetFactory<SwitchSetting, Widget> {

    @Override
    public Widget[] create(
        Setting setting, SwitchSetting annotation, SettingAccessor accessor) {
      if (annotation.hotkey()) {
        String metaKey = setting.getId() + ".hotkey";

        SwitchWidget switchWidget = this.createSwitch(setting, accessor);
        KeybindWidget keybindWidget = new KeybindWidget(
            key -> accessor.config().configMeta().put(metaKey, key.getActualName())
        );

        if (accessor.config().hasConfigMeta(metaKey)) {
          keybindWidget.setKeyUpdater(() -> KeyMapper.getKey(accessor.config().configMeta().get(metaKey)));
          Key key = KeyMapper.getKey(accessor.config().configMeta().get(metaKey));
          if (key != null) {
            keybindWidget.key(key);
          }
        }

        return new Widget[]{switchWidget, keybindWidget};
      } else {
        SwitchWidget switchWidget = this.createSwitch(setting, accessor);

        return new SwitchWidget[]{switchWidget};
      }
    }

    private SwitchWidget createSwitch(
        Setting setting,
        SettingAccessor accessor) {
      SwitchWidget widget = new SwitchWidget(accessor::set);

      widget.enabledTranslatableKey = setting.getTranslationKey() + ".enabled";
      widget.disabledTranslatableKey = setting.getTranslationKey() + ".disabled";
      widget.setValue(accessor.get());

      accessor.property().addChangeListener(
          (type, oldValue, newValue) ->
              widget.setValue(newValue instanceof Boolean && (Boolean) newValue)
      );

      return widget;
    }

    @Override
    public Class<?>[] types() {
      return PrimitiveHelper.BOOLEAN;
    }
  }
}
