package eu.germanrp.addon.core;

import eu.germanrp.addon.core.config.HydrationConfig;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@Getter
@Accessors(fluent = true)
@ConfigName("settings")
@SpriteTexture("settings")
public class GermanRPAddonConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    private final NameTagSubConfig nameTagSubConfig = new NameTagSubConfig();

    private final VehicleHotkeyConfig vehicleHotkeyConfig = new VehicleHotkeyConfig();

    private final HydrationConfig hydrationConfig = new HydrationConfig();

    @SettingSection("debug")
    @SwitchSetting
    private final ConfigProperty<Boolean> debug = new ConfigProperty<>(false);
}
