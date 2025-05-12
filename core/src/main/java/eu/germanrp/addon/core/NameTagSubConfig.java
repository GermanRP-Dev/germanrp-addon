package eu.germanrp.addon.core;

import eu.germanrp.addon.core.Enum.FactionName;
import eu.germanrp.addon.core.Enum.NameTag;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@ConfigName("Nametags")
public class NameTagSubConfig extends Config {

  @DropdownSetting
  private final ConfigProperty<NameTag> factionColor;
  @DropdownSetting
  private final ConfigProperty<NameTag> darklistColor;
  @DropdownSetting
  private final ConfigProperty<NameTag> bountyColor;
  @DropdownSetting
  private final ConfigProperty<NameTag> wantedColor;
  @DropdownSetting
  private final ConfigProperty<FactionName> factionName;

  public NameTagSubConfig() {
    this.wantedColor = new ConfigProperty<>(NameTag.NONE);
    this.factionColor = new ConfigProperty<>(NameTag.NONE);
    this.darklistColor = new ConfigProperty<>(NameTag.NONE);
    this.bountyColor = new ConfigProperty<>(NameTag.NONE);
    this.factionName = new ConfigProperty<>(FactionName.NONE);
  }

  public ConfigProperty<NameTag> factionTag() {
    return this.factionColor;
  }

  public ConfigProperty<NameTag> darklistTag() {
    return this.darklistColor;
  }

  public ConfigProperty<NameTag> bountyTag() {
    return this.bountyColor;
  }

  public ConfigProperty<NameTag> wantedColor() {
    return wantedColor;
  }

  public ConfigProperty<FactionName> factionName() {
    return factionName;
  }

}
