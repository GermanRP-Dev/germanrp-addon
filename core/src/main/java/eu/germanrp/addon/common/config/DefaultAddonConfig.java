package eu.germanrp.addon.common.config;

import eu.germanrp.addon.common.enums.Faction;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

import static eu.germanrp.addon.common.enums.Faction.NONE;

@Getter
@Accessors(fluent = true)
@ConfigName("settings")
@SpriteTexture("settings")
public class DefaultAddonConfig extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SettingSection("onlygr")
    @SwitchSetting
    private final ConfigProperty<Boolean> antichatooc = new ConfigProperty<>(true);

    private final WidgetSubConfig widgets = new WidgetSubConfig();

    @SpriteSlot(size = 32, x = 3, y = 1)
    private final HotkeyConfig hotkeys = new HotkeyConfig();

    @SpriteSlot(size = 32, x = 1, y = 2)
    private final HydrationConfig hydration = new HydrationConfig();

    @SpriteSlot(size = 32, x = 2, y = 1)
    private final TimerConfig timer = new TimerConfig();

    @SpriteSlot(size = 32, x = 1, y = 1)
    private final NameTagConfig nametags = new NameTagConfig();

    @SpriteSlot(size = 32, x = 1)
    private final ConfigProperty<Faction> language = new ConfigProperty<>(NONE);
}


