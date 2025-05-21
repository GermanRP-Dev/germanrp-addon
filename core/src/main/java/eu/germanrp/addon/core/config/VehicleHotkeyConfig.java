package eu.germanrp.addon.core.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@Getter
@Accessors(fluent = true)
public class VehicleHotkeyConfig extends Config {

    @SettingSection("car")
    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleEngine = new ConfigProperty<>(Key.NONE);

    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleTurnSignalLeft = new ConfigProperty<>(Key.NONE);

    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleTurnSignalRight = new ConfigProperty<>(Key.NONE);

    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleHazardWarnSignal = new ConfigProperty<>(Key.NONE);

    @SettingSection("cruiseControl")
    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleCruiseControl = new ConfigProperty<>(Key.NONE);

    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> increaseCruiseControlSpeed = new ConfigProperty<>(Key.NONE);

    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> decreaseCruiseControlSpeed = new ConfigProperty<>(Key.NONE);

    @SettingSection("emergencySignal")
    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleEmergencySignal = new ConfigProperty<>(Key.NONE);

    @KeybindWidget.KeyBindSetting
    private final ConfigProperty<Key> toggleEmergencySignalSound = new ConfigProperty<>(Key.NONE);

}
