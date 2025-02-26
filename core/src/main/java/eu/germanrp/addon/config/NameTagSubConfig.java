package eu.germanrp.addon.config;

import eu.germanrp.addon.enums.NameTagColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

import static eu.germanrp.addon.enums.NameTagColor.NONE;

@Getter
@Accessors(fluent = true)
public class NameTagSubConfig extends Config {

    @DropdownSetting
    private final ConfigProperty<NameTagColor> factiontag = new ConfigProperty<>(NONE);

    @DropdownSetting
    private final ConfigProperty<NameTagColor> darklisttag = new ConfigProperty<>(NONE);

    @DropdownSetting
    private final ConfigProperty<NameTagColor> bountytag = new ConfigProperty<>(NONE);

    @DropdownSetting
    private final ConfigProperty<NameTagColor> policetag = new ConfigProperty<>(NONE);
}
