package eu.germanrp.addon.core.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

@Getter
@Accessors(fluent = true)
public class HydrationConfig extends Config {

    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> hideHydrationChatMessage = new ConfigProperty<>(false);

    @SettingRequires(value = "hideHydrationChatMessage", invert = true)
    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> displayHydrationMessageInActionBar = new ConfigProperty<>(false);

}
