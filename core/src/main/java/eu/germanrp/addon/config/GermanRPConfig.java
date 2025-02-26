package eu.germanrp.addon.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@Getter
@Accessors(fluent = true)
@ConfigName("settings")
@SpriteTexture("settings")
public class GermanRPConfig extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SettingSection("onlygr")
    @SwitchSetting
    private final ConfigProperty<Boolean> antichatooc = new ConfigProperty<>(true);

    private final WidgetSubConfig widgets = new WidgetSubConfig();

    @SpriteSlot(size = 32, x = 3, y = 1)
    private final HotkeySubConfig hotkeys = new HotkeySubConfig();

    @SpriteSlot(size = 32, x = 1, y = 2)
    private final HydrationSubConfig hydration = new HydrationSubConfig();

    @SpriteSlot(size = 32, x = 2, y = 1)
    private final TimerSubConfig timer = new TimerSubConfig();

    @SpriteSlot(size = 32, x = 1, y = 1)
    private final NameTagSubConfig nametags = new NameTagSubConfig();

    @SpriteSlot(size = 32, x = 1)
    private final LanguageSubConfig languages = new LanguageSubConfig();
}


