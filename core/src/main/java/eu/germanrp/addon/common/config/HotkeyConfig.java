package eu.germanrp.addon.common.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

import static net.labymod.api.client.gui.screen.key.Key.NONE;

@Getter
@Accessors(fluent = true)
public class HotkeyConfig extends Config {

    @KeyBindSetting
    private final ConfigProperty<Key> job = new ConfigProperty<>(NONE);

    @SettingSection("car")
    @KeyBindSetting
    private final ConfigProperty<Key> engineToggle = new ConfigProperty<>(NONE);

    @KeyBindSetting
    private final ConfigProperty<Key> turnSignalToggleLeft = new ConfigProperty<>(NONE);

    @KeyBindSetting
    private final ConfigProperty<Key> turnSignalToggleRight = new ConfigProperty<>(NONE);

    @KeyBindSetting
    private final ConfigProperty<Key> hazardLightsToggle = new ConfigProperty<>(NONE);

    @SettingSection("emergencySignal")
    @KeyBindSetting
    private final ConfigProperty<Key> emergencySignalToggle = new ConfigProperty<>(NONE);

    @KeyBindSetting
    private final ConfigProperty<Key> emergencySignalMute = new ConfigProperty<>(NONE);

    @SettingSection("cruiseControl")
    @KeyBindSetting
    private final ConfigProperty<Key> cruiseControlToggle = new ConfigProperty<>(NONE);

    @KeyBindSetting
    private final ConfigProperty<Key> cruiseControlUp = new ConfigProperty<>(NONE);

    @KeyBindSetting
    private final ConfigProperty<Key> cruiseControlDown = new ConfigProperty<>(NONE);
}
