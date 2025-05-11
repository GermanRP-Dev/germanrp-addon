package eu.germanrp.addon.common.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@Getter
@Accessors(fluent = true)
public class TimerConfig extends Config {

    @SwitchSetting
    private final ConfigProperty<Boolean> robBusiness = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> robPharmacy = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> robJewelry = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> robMuseum = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> hack = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> bomb = new ConfigProperty<>(true);
}
