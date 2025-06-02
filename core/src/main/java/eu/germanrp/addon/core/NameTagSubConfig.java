package eu.germanrp.addon.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;
import net.labymod.api.util.Color;

@Getter
@Accessors(fluent = true)
@ConfigName("Nametags")
public class NameTagSubConfig extends Config {

    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> factionColorEnabled = new ConfigProperty<>(false);

    @ColorPickerWidget.ColorPickerSetting
    @SettingRequires("factionColorEnabled")
    private final ConfigProperty<Color> factionColor = new ConfigProperty<>(Color.of(0xFFAAAAAA));

    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> bountyColorEnabled = new ConfigProperty<>(false);

    @ColorPickerWidget.ColorPickerSetting
    @SettingRequires("bountyColorEnabled")
    private final ConfigProperty<Color> bountyColor = new ConfigProperty<>(Color.of(0xFFAAAAAA));

    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> darklistColorEnabled = new ConfigProperty<>(false);

    @ColorPickerWidget.ColorPickerSetting
    @SettingRequires("darklistColorEnabled")
    private final ConfigProperty<Color> darklistColor = new ConfigProperty<>(Color.of(0xFFAAAAAA));

    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> wantedColorEnabled = new ConfigProperty<>(false);

    @ColorPickerWidget.ColorPickerSetting
    @SettingRequires("wantedColorEnabled")
    private final ConfigProperty<Color> wantedColor = new ConfigProperty<>(Color.of(0xFFAAAAAA));

    @Override
    public int getConfigVersion() {
        return 2;
    }
}
