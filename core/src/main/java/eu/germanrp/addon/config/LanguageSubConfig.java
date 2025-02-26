package eu.germanrp.addon.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@Getter
@Accessors(fluent = true)
public class LanguageSubConfig extends Config {

    @SwitchSetting
    @SpriteSlot(size = 32, y = 1)
    private final ConfigProperty<Boolean> ocallaghan = new ConfigProperty<>(false);

    @SwitchSetting
    @SpriteSlot(size = 32)
    private final ConfigProperty<Boolean> medellin = new ConfigProperty<>(false);

    @SwitchSetting
    @SpriteSlot(size = 32, x = 3)
    private final ConfigProperty<Boolean> camorra = new ConfigProperty<>(false);

    @SwitchSetting
    @SpriteSlot(size = 32, y = 2)
    private final ConfigProperty<Boolean> establishment = new ConfigProperty<>(false);

    /*@SwitchSetting
    private final ConfigProperty<Boolean> sinaloa = new ConfigProperty<>(false);*///Sinaloa

    @SwitchSetting
    @SpriteSlot(size = 32, x = 2)
    private final ConfigProperty<Boolean> yakuza = new ConfigProperty<>(false);
}
