package eu.germanrp.addon.core;

import eu.germanrp.addon.core.commands.graffiti.GraffitiCommand;
import eu.germanrp.addon.core.common.AddonPlayer;
import eu.germanrp.addon.core.common.AddonVariables;
import eu.germanrp.addon.core.common.DefaultAddonPlayer;
import eu.germanrp.addon.core.common.DefaultAddonVariables;
import eu.germanrp.addon.core.executor.HitResultExecutor;
import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import eu.germanrp.addon.core.generated.DefaultReferenceStorage;
import eu.germanrp.addon.core.listener.EventRegistrationListener;
import eu.germanrp.addon.core.listener.NameTagListener;
import eu.germanrp.addon.core.listener.ServerJoinListener;
import eu.germanrp.addon.core.listener.*;
import eu.germanrp.addon.core.services.NavigationService;
import eu.germanrp.addon.core.services.UtilService;
import eu.germanrp.addon.core.services.VehicleService;
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

    private NavigationService navigationService;
    private UtilService utilService;
    private VehicleService vehicleService;

    private AddonPlayer player;
    private AddonVariables variables;

    private HitResultExecutor hitResultExecutor;
    private PlaySoundExecutor playSoundExecutor;

    private ServerJoinListener serverJoinListener;

    private HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private RoseHudWidget roseHudWidget;
    private StoffHudWidget stoffHudWidget;
    private GraffitiHudWidget graffitiHudWidget;
    private MajorEventWidget majorEventWidget;
    private PlayerExperienceWidget playerExperienceWidget;
    private BlackMarketWidget blackMarketWidget;

    @Override
    protected void load() {
        this.player = new DefaultAddonPlayer(this);
        this.variables = new DefaultAddonVariables(this);

        this.navigationService = new NavigationService();
        this.utilService = new UtilService(this);
        this.vehicleService = new VehicleService(this);

        this.logger().info("Loaded germanrpaddon");
    }

    @Override
    protected void enable() {
        this.registerSettingCategory();

        registerVersionDependantExecutors();
        registerWidgets();
        registerListener();
        registerCommands();

        this.logger().info("Enabled germanrpaddon");
    }

    @Override
    protected Class<GermanRPAddonConfiguration> configurationClass() {
        return GermanRPAddonConfiguration.class;
    }

    private void registerCommands() {
        registerCommand(new GraffitiCommand(this, graffitiHudWidget.getConfig()));
    }

    private void registerVersionDependantExecutors() {
        hitResultExecutor = ((DefaultReferenceStorage) this.referenceStorageAccessor()).hitResultExecutor();
        playSoundExecutor = ((DefaultReferenceStorage) this.referenceStorageAccessor()).playSoundExecutor();
    }

    private void registerWidgets() {
        final HudWidgetRegistry widgetRegistry = this.labyAPI().hudWidgetRegistry();
        final HudWidgetCategory widgetCategory = new GermanRPAddonWidgetCategory();

        this.heilkrautpflanzeHudWidget = new HeilkrautpflanzeHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/heilkrautpflanze.png")),
                playSoundExecutor
        );
        this.roseHudWidget = new RoseHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/rose.png")),
                playSoundExecutor
        );
        this.stoffHudWidget = new StoffHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/stoffpflanze.png")),
                playSoundExecutor
        );
        this.graffitiHudWidget = new GraffitiHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/graffiti.png")),
                this
        );
        this.majorEventWidget = new MajorEventWidget(
                this,
                widgetCategory
        );
        this.playerExperienceWidget = new PlayerExperienceWidget(
                this,
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/experience_bottle.png"))
        );
        this.blackMarketWidget = new BlackMarketWidget(
                this,
                widgetCategory
        );

        widgetRegistry.categoryRegistry().register(widgetCategory);
        widgetRegistry.register(heilkrautpflanzeHudWidget);
        widgetRegistry.register(roseHudWidget);
        widgetRegistry.register(stoffHudWidget);
        widgetRegistry.register(graffitiHudWidget);
        widgetRegistry.register(majorEventWidget);
        widgetRegistry.register(playerExperienceWidget);
        widgetRegistry.register(blackMarketWidget);
    }

    private void registerListener() {
        this.serverJoinListener = new ServerJoinListener(this);

        registerListener(serverJoinListener);
        registerListener(new EventRegistrationListener(this));
        registerListener(new NameTagListener(this));
        registerListener(new ChatListener(this));
        registerListener(new VehicleHotkeyListener(this));
    }
}
