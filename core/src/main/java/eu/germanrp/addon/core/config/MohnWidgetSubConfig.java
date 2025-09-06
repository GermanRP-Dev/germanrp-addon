package eu.germanrp.addon.core.config;

import lombok.Getter;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@Getter
public class MohnWidgetSubConfig extends Config {
    @SwitchWidget.SwitchSetting
    private final ConfigProperty<Boolean> showMohnMessages = new ConfigProperty<>(true);

}
