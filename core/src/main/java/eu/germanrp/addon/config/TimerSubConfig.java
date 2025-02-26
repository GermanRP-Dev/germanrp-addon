package eu.germanrp.addon.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@Getter
@Accessors(fluent = true)
public class TimerSubConfig extends Config {

    @SwitchSetting
    private final ConfigProperty<Boolean> businessrobtimer = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> pharmacyrobtimer = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> jewelryrobtimer = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> museumrobtimer = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> hacktimer = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> bombtimer = new ConfigProperty<>(true);
}
