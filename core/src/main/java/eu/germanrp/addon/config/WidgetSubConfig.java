package eu.germanrp.addon.config;

import eu.germanrp.addon.enums.GangwarEnum;
import eu.germanrp.addon.enums.LevelEnum;
import eu.germanrp.addon.enums.PlantEnum;
import eu.germanrp.addon.enums.SalaryEnum;
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
    private final ConfigProperty<LevelEnum> levelSetting = new ConfigProperty<>(LevelEnum.CURRENTANDMAX);

    @DropdownSetting
    private final ConfigProperty<LevelEnum> skillSetting = new ConfigProperty<>(LevelEnum.CURRENTANDMAX);

    @DropdownSetting
    private final ConfigProperty<GangwarEnum> gangwarSetting = new ConfigProperty<>(GangwarEnum.ALL);

    @DropdownSetting
    private final ConfigProperty<PlantEnum> plantSetting = new ConfigProperty<>(PlantEnum.BOTH);
}
