package eu.germanrp.addon.core;

import eu.germanrp.addon.core.commands.graffiti.GraffitiCommand;
import eu.germanrp.addon.core.executor.HitResultExecutor;
import eu.germanrp.addon.core.generated.DefaultReferenceStorage;
import eu.germanrp.addon.core.listener.*;
//import eu.germanrp.addon.core.listener.GraffitiListener;
//import eu.germanrp.addon.core.services.GraffitiService;
import eu.germanrp.addon.core.services.NavigationService;
import eu.germanrp.addon.core.services.UtilService;
import eu.germanrp.addon.core.widget.*;
import eu.germanrp.addon.core.widget.category.GermanRPAddonWidgetCategory;
import lombok.Getter;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;

@Getter
@AddonMain
public class GermanRPAddon extends LabyAddon<GermanRPAddonConfiguration> {

    public static final String NAMESPACE = "germanrpaddon";

    public static NavigationService navigationService;
    public static UtilService utilService;

    private HitResultExecutor hitResultExecutor;

    private ServerJoinListener serverJoinListener;

    private HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private RoseHudWidget roseHudWidget;
    private StoffHudWidget stoffHudWidget;
    private GraffitiHudWidget graffitiHudWidget;
   // private GraffitiService graffitiService;
    private MajorEventWidget majorEventWidget;

    @Override
    protected void enable() {
        this.registerSettingCategory();

        registerVersionDependantExecutors();
        registerServices();
        registerWidgets();
        registerServices();
        registerListener();
        registerCommands();

        this.logger().info("Enabled germanrpaddon");
    }

    @Override
    protected Class<GermanRPAddonConfiguration> configurationClass() {
        return GermanRPAddonConfiguration.class;
    }

    private void registerCommands() {
        registerCommand(new GraffitiCommand(this));
    }

    private void registerServices() {
       // this.graffitiService = new GraffitiService();
        navigationService = new NavigationService();
        utilService = new UtilService(this);
    }

    private void registerVersionDependantExecutors() {
        hitResultExecutor = ((DefaultReferenceStorage) this.referenceStorageAccessor()).hitResultExecutor();
    }

    private void registerWidgets() {
        final HudWidgetRegistry widgetRegistry = this.labyAPI().hudWidgetRegistry();
        final HudWidgetCategory widgetCategory = new GermanRPAddonWidgetCategory();

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
        /*this.graffitiHudWidget = new GraffitiHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/graffiti.png")),
                this.graffitiService
        );*/
        this.majorEventWidget = new MajorEventWidget(
                this,
                widgetCategory
        );

        widgetRegistry.categoryRegistry().register(widgetCategory);
        widgetRegistry.register(heilkrautpflanzeHudWidget);
        widgetRegistry.register(roseHudWidget);
        widgetRegistry.register(stoffHudWidget);
       // widgetRegistry.register(graffitiHudWidget);
        widgetRegistry.register(majorEventWidget);
    }

    private void registerListener() {
        this.serverJoinListener = new ServerJoinListener(this);

        registerListener(serverJoinListener);
        registerListener(new EventRegistrationListener(this));
        registerListener(new NameTagListener(this));
        registerListener(new PlantListener(this));
        registerListener(new ChatListener(this));
        //registerListener(new GraffitiListener(this));
    }
}
