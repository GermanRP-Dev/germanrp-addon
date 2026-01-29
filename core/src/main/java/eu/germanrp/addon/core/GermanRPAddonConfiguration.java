package eu.germanrp.addon.core;

import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.api.models.SkillXP;
import eu.germanrp.addon.core.activity.CharInfoActivity;
import eu.germanrp.addon.core.config.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.ActivitySettingWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static eu.germanrp.addon.api.models.SkillXP.NORMAL;

@Getter
@Accessors(fluent = true)
@ConfigName("settings")
@SpriteTexture("settings")
public class GermanRPAddonConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    private final NameTagSubConfig nameTagSubConfig = new NameTagSubConfig();

    private final PoppyWidgetSubConfig poppyWidgetSubConfig =  new PoppyWidgetSubConfig();

    private final VehicleHotkeyConfig vehicleHotkeyConfig = new VehicleHotkeyConfig();

    private final HydrationConfig hydrationConfig = new HydrationConfig();

    private final ATMConfig atmConfig = new ATMConfig();

    @DropdownSetting
    private final ConfigProperty<SkillXP> skillXP = new ConfigProperty<>(NORMAL);

    @SettingSection("debug")
    @SwitchSetting
    private final ConfigProperty<Boolean> debug = new ConfigProperty<>(false);

    @Exclude
    private final HashMap<UUID, CharacterInfo> characterInfoMap = new HashMap<>();

    @ActivitySettingWidget.ActivitySetting
    @MethodOrder(after = "skillXP")
    public Activity openCharInfo() {
        return new CharInfoActivity();
    }

}
