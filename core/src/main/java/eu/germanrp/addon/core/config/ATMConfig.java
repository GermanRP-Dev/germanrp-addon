package eu.germanrp.addon.core.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;
import net.labymod.api.util.Color;

@Getter
@Accessors(fluent = true)
public class ATMConfig extends Config {

    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> silentATM = new ConfigProperty<>(true);

    @SwitchWidget.SwitchSetting(hotkey = true)
    private final ConfigProperty<Boolean> showATMWaypoints = new ConfigProperty<>(true);

    @ColorPickerWidget.ColorPickerSetting
    @SettingRequires("showATMWaypoints")
    private final ConfigProperty<Color> atmWaypointColor = new ConfigProperty<>(Color.of(0xFFAAAAAA));

}
