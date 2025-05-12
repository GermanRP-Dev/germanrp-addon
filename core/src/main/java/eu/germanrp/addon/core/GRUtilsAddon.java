package eu.germanrp.addon.core;

import eu.germanrp.addon.core.commands.graffiti.GraffitiCommand;
import eu.germanrp.addon.core.executor.HitResultExecutor;
import eu.germanrp.addon.core.generated.DefaultReferenceStorage;
import eu.germanrp.addon.core.listener.GraffitiListener;
import eu.germanrp.addon.core.listener.NameTagListener;
import eu.germanrp.addon.core.listener.PlantListener;
import eu.germanrp.addon.core.listener.ServerJoinListener;
import eu.germanrp.addon.core.services.GraffitiService;
import eu.germanrp.addon.core.widget.GraffitiHudWidget;
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

    private HitResultExecutor hitResultExecutor;

    private ServerJoinListener serverJoinListener;

    private HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private RoseHudWidget roseHudWidget;
    private StoffHudWidget stoffHudWidget;
    private GraffitiHudWidget graffitiHudWidget;

    private GraffitiService graffitiService;

    private GraffitiService graffitiService;

    @Override
    protected void enable() {
        this.registerSettingCategory();

        registerVersionDependantExecutors();
        registerServices();registerWidgets();
        registerServices();
        registerListener();
        registerCommands();

        this.logger().info("Enabled GermanRP Utils!");
    }

    private void registerCommands() {
        registerCommand(new GraffitiCommand(this));
    }

    private void registerServices() {
        this.graffitiService = new GraffitiService();
    }

    @Override
    protected Class<GRUtilsConfiguration> configurationClass() {
        return GRUtilsConfiguration.class;
    }

    private void registerVersionDependantExecutors() {
        hitResultExecutor = ((DefaultReferenceStorage) this.referenceStorageAccessor()).hitResultExecutor();
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
        this.graffitiHudWidget = new GraffitiHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/graffiti.png")),
                this.graffitiService
        );

        widgetRegistry.categoryRegistry().register(widgetCategory);
        widgetRegistry.register(heilkrautpflanzeHudWidget);
        widgetRegistry.register(roseHudWidget);
        widgetRegistry.register(stoffHudWidget);
    widgetRegistry.register(graffitiHudWidget);
    }

    private void registerListener() {
        this.serverJoinListener = new ServerJoinListener(this);

        registerListener(serverJoinListener);
        registerListener(new NameTagListener(this));
        registerListener(new PlantListener(this));
        registerListener(new GraffitiListener(this));
    }

}
