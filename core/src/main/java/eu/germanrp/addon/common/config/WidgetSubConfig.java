package eu.germanrp.addon.common.config;

import eu.germanrp.addon.common.enums.Gangwar;
import eu.germanrp.addon.common.enums.Level;
import eu.germanrp.addon.common.enums.PlantEnum;
import eu.germanrp.addon.common.enums.SalaryEnum;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@Getter
@Accessors(fluent = true)
public class WidgetSubConfig extends Config {

    @DropdownSetting
    private final ConfigProperty<SalaryEnum> salarySetting = new ConfigProperty<>(SalaryEnum.BOTH);

    @DropdownSetting
    private final ConfigProperty<Level> levelSetting = new ConfigProperty<>(Level.CURRENTANDMAX);

    @DropdownSetting
    private final ConfigProperty<Level> skillSetting = new ConfigProperty<>(Level.CURRENTANDMAX);

    @DropdownSetting
    private final ConfigProperty<Gangwar> gangwarSetting = new ConfigProperty<>(Gangwar.ALL);

    @DropdownSetting
    private final ConfigProperty<PlantEnum> plantSetting = new ConfigProperty<>(PlantEnum.BOTH);
}
