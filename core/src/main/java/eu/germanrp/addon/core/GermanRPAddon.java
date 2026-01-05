package eu.germanrp.addon.core;

import eu.germanrp.addon.core.commands.TogglePanicCommand;
import eu.germanrp.addon.core.commands.graffiti.GraffitiCommand;
import eu.germanrp.addon.core.common.AddonPlayer;
import eu.germanrp.addon.core.common.DefaultAddonPlayer;
import eu.germanrp.addon.core.listener.*;
import eu.germanrp.addon.core.services.NameTagService;
import eu.germanrp.addon.core.services.NavigationService;
import eu.germanrp.addon.core.services.UtilService;
import eu.germanrp.addon.core.services.VehicleService;
import eu.germanrp.addon.core.workflow.JoinWorkflowManager;
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

    public static final String NAMESPACE = "germanrpeuaddon";

    @Getter
    private static GermanRPAddon instance;

    private NameTagService nameTagService;
    private NavigationService navigationService;
    private UtilService utilService;
    private VehicleService vehicleService;

    private AddonPlayer player;

    private ServerJoinListener serverJoinListener;
    private JoinWorkflowManager joinWorkflowManager;

    private HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private RoseHudWidget roseHudWidget;
    private StoffHudWidget stoffHudWidget;
    private GraffitiHudWidget graffitiHudWidget;
    private MajorEventWidget majorEventWidget;
    private PlayerExperienceWidget playerExperienceWidget;
    private BlackMarketWidget blackMarketWidget;
    private HydrationWidget hydrationWidget;
    private PayDayWidget paydayWidget;
    private ChatListener chatListener;

    @Override
    protected void load() {
        instance = this;
        this.player = new DefaultAddonPlayer(this);

        instantiateServices();

        this.logger().info("Loaded germanrpaddon");
    }

    @Override
    protected void enable() {
        this.registerSettingCategory();

        registerWidgets();
        registerListener();
        registerCommands();

        this.logger().info("Enabled germanrpaddon");
    }

    @Override
    protected Class<GermanRPAddonConfiguration> configurationClass() {
        return GermanRPAddonConfiguration.class;
    }

    private void instantiateServices() {
        this.nameTagService = new NameTagService();
        this.navigationService = new NavigationService();
        this.utilService = new UtilService(this);
        this.vehicleService = new VehicleService(this);
        this.joinWorkflowManager = new JoinWorkflowManager(this);
    }

    private void registerCommands() {
        registerCommand(new GraffitiCommand(this, this.graffitiHudWidget.getConfig()));
        registerCommand(new TogglePanicCommand());
    }

    private void registerWidgets() {
        final HudWidgetRegistry widgetRegistry = labyAPI().hudWidgetRegistry();
        final HudWidgetCategory widgetCategory = new GermanRPAddonWidgetCategory();

        this.heilkrautpflanzeHudWidget = new HeilkrautpflanzeHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/heilkrautpflanze.png")),
                this
        );
        this.roseHudWidget = new RoseHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/rose.png")),
                this
        );
        this.stoffHudWidget = new StoffHudWidget(
                widgetCategory,
                Icon.texture(ResourceLocation.create(NAMESPACE, "images/stoffpflanze.png")),
                this
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
        this.hydrationWidget = new HydrationWidget(
                this,
                widgetCategory
        );
        this.paydayWidget = new PayDayWidget(
                this,
                widgetCategory
        );

        widgetRegistry.categoryRegistry().register(widgetCategory);
        widgetRegistry.register(this.heilkrautpflanzeHudWidget);
        widgetRegistry.register(this.roseHudWidget);
        widgetRegistry.register(this.stoffHudWidget);
        widgetRegistry.register(this.graffitiHudWidget);
        widgetRegistry.register(this.majorEventWidget);
        widgetRegistry.register(this.playerExperienceWidget);
        widgetRegistry.register(this.blackMarketWidget);
        widgetRegistry.register(this.hydrationWidget);
        widgetRegistry.register(this.paydayWidget);
    }

    private void registerListener() {
        this.serverJoinListener = new ServerJoinListener(this);
        this.chatListener = new ChatListener(this);

        registerListener(this.serverJoinListener);
        registerListener(this.chatListener);

        registerListener(new SkillXPListener());
        registerListener(new EventRegistrationListener(this));
        registerListener(new NameTagListener(this));
        registerListener(new VehicleHotkeyListener(this));
        registerListener(new DarklistEventListener(this));
        registerListener(new BountyEventListener(this));
        registerListener(new WantedEventListener(this));
        registerListener(new MemberInfoEventListener(this));
    }
}
