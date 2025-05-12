package eu.germanrp.addon.core;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@ConfigName("settings")
@SpriteTexture("settings")
public class GRUtilsConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);


  private final NameTagSubConfig NameTagSubConfig = new NameTagSubConfig();
  @SpriteSlot(
      size = 32,
      x = 1
  )  @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }

  public NameTagSubConfig NameTagSubConfig() {
    return this.NameTagSubConfig;
  }
}
