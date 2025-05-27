package eu.germanrp.addon.core;

import eu.germanrp.addon.api.models.NameTag;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@Getter
@Accessors(fluent = true)
@ConfigName("Nametags")
public class NameTagSubConfig extends Config {

    @DropdownSetting
    private final ConfigProperty<NameTag> factionColor = new ConfigProperty<>(NameTag.NONE);

    @DropdownSetting
    private final ConfigProperty<NameTag> bountyColor = new ConfigProperty<>(NameTag.NONE);

    @DropdownSetting
    private final ConfigProperty<NameTag> darklistColor = new ConfigProperty<>(NameTag.NONE);

    @DropdownSetting
    private final ConfigProperty<NameTag> wantedColor = new ConfigProperty<>(NameTag.NONE);

}
