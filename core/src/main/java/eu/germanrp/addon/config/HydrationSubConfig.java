package eu.germanrp.addon.config;

import eu.germanrp.addon.enums.HydrationEnum;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

import static eu.germanrp.addon.enums.HydrationEnum.ALL;

@Getter
@Accessors(fluent = true)
public class HydrationSubConfig extends Config {

    @DropdownSetting
    private final ConfigProperty<HydrationEnum> notificationtype = new ConfigProperty<>(ALL);

    @SwitchSetting
    private final ConfigProperty<Boolean> actionbar = new ConfigProperty<>(false);
}
