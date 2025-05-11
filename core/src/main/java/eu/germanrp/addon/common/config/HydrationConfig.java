package eu.germanrp.addon.common.config;

import eu.germanrp.addon.common.enums.HydrationNotification;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

import static eu.germanrp.addon.common.enums.HydrationNotification.ALL;

@Getter
@Accessors(fluent = true)
public class HydrationConfig extends Config {

    @DropdownSetting
    private final ConfigProperty<HydrationNotification> notificationtype = new ConfigProperty<>(ALL);

    @SwitchSetting
    private final ConfigProperty<Boolean> actionbar = new ConfigProperty<>(false);
}
