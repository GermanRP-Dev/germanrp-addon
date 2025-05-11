package eu.germanrp.addon.common.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

import static net.labymod.api.client.component.format.NamedTextColor.WHITE;

@Getter
@Accessors(fluent = true)
public class NameTagConfig extends Config {

    @DropdownSetting
    private final ConfigProperty<TextColor> faction = new ConfigProperty<>(WHITE);

    @DropdownSetting
    private final ConfigProperty<TextColor> darkList = new ConfigProperty<>(WHITE);

    @DropdownSetting
    private final ConfigProperty<TextColor> bounty = new ConfigProperty<>(WHITE);

    @DropdownSetting
    private final ConfigProperty<TextColor> police = new ConfigProperty<>(WHITE);
}
