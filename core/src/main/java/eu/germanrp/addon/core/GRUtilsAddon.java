package eu.germanrp.addon.core;

import eu.germanrp.addon.core.executor.ExampleChatExecutor;
import eu.germanrp.addon.core.generated.DefaultReferenceStorage;
import eu.germanrp.addon.core.listener.NameTagListener;
import eu.germanrp.addon.core.listener.PlantListener;
import eu.germanrp.addon.core.listener.ServerJoinListener;
import eu.germanrp.addon.core.widget.HeilkrautpflanzeHudWidget;
import eu.germanrp.addon.core.widget.RoseHudWidget;
import eu.germanrp.addon.core.widget.StoffHudWidget;
import eu.germanrp.addon.core.widget.category.GRUtilsWidgetCategory;
import lombok.Getter;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;

@Getter
@AddonMain
public class GRUtilsAddon extends LabyAddon<GRUtilsConfiguration> {

  public static final String NAMESPACE = "germanrputils";

  private ExampleChatExecutor chatExecutor;

  private ServerJoinListener serverJoinListener;

  private HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
  private RoseHudWidget roseHudWidget;
  private StoffHudWidget stoffHudWidget;

  @Override
  protected void enable() {
    this.registerSettingCategory();

    registerVersionDependantExecutors();
    registerWidgets();
    registerListener();

    this.logger().info("Enabled GermanRP Utils!");
  }

  @Override
  protected Class<GRUtilsConfiguration> configurationClass() {
    return GRUtilsConfiguration.class;
  }

  private void registerVersionDependantExecutors() {
    chatExecutor = ((DefaultReferenceStorage) this.referenceStorageAccessor()).exampleChatExecutor();
  }

  private void registerWidgets() {
    final HudWidgetRegistry widgetRegistry = this.labyAPI().hudWidgetRegistry();
    final HudWidgetCategory widgetCategory = new GRUtilsWidgetCategory();

    this.heilkrautpflanzeHudWidget = new HeilkrautpflanzeHudWidget(
        widgetCategory,
        Icon.texture(ResourceLocation.create(NAMESPACE, "images/heilkrautpflanze.png"))
    );
    this.roseHudWidget = new RoseHudWidget(
        widgetCategory,
        Icon.texture(ResourceLocation.create(NAMESPACE, "images/rose.png"))
    );
    this.stoffHudWidget = new StoffHudWidget(
        widgetCategory,
        Icon.texture(ResourceLocation.create(NAMESPACE, "images/stoffpflanze.png"))
    );

    widgetRegistry.categoryRegistry().register(widgetCategory);
    widgetRegistry.register(heilkrautpflanzeHudWidget);
    widgetRegistry.register(roseHudWidget);
    widgetRegistry.register(stoffHudWidget);
  }

  private void registerListener() {
    this.serverJoinListener = new ServerJoinListener(this);

    registerListener(serverJoinListener);
    registerListener(new NameTagListener(this));
    registerListener(new PlantListener(this));
  }

}
