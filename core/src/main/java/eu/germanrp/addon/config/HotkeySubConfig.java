package eu.germanrp.addon.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingOrder;

import static net.labymod.api.client.gui.screen.key.Key.NONE;

@Getter
@Accessors(fluent = true)
public class HotkeySubConfig extends Config {

    @SettingOrder(0)
    @KeyBindSetting
    private final ConfigProperty<Key> jobKey = new ConfigProperty<>(NONE);

    @SettingOrder(1)
    @KeyBindSetting
    private final ConfigProperty<Key> cruiseControlKey = new ConfigProperty<>(NONE);

    @SettingOrder(2)
    @KeyBindSetting
    private final ConfigProperty<Key> engineOnOffKey = new ConfigProperty<>(NONE);

    @SettingOrder(3)
    @KeyBindSetting
    private final ConfigProperty<Key> signalLeftKey = new ConfigProperty<>(NONE);

    @SettingOrder(4)
    @KeyBindSetting
    private final ConfigProperty<Key> signalRightKey = new ConfigProperty<>(NONE);

    @SettingOrder(5)
    @KeyBindSetting
    private final ConfigProperty<Key> warnSignalKey = new ConfigProperty<>(NONE);

    @SettingOrder(6)
    @KeyBindSetting
    private final ConfigProperty<Key> sosiOnOffKey = new ConfigProperty<>(NONE);

    @SettingOrder(7)
    @KeyBindSetting
    private final ConfigProperty<Key> sosiMuteKey = new ConfigProperty<>(NONE);

    @SettingOrder(8)
    @KeyBindSetting
    private final ConfigProperty<Key> cruiseControlUp = new ConfigProperty<>(NONE);

    @SettingOrder(9)
    @KeyBindSetting
    private final ConfigProperty<Key> cruiseControlDown = new ConfigProperty<>(NONE);
}
