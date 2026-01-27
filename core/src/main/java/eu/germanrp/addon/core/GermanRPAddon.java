package eu.germanrp.addon.core;

import eu.germanrp.addon.core.commands.TogglePanicCommand;
import eu.germanrp.addon.core.commands.graffiti.GraffitiCommand;
import eu.germanrp.addon.core.common.AddonPlayer;
import eu.germanrp.addon.core.common.DefaultAddonPlayer;
import eu.germanrp.addon.core.common.sound.SoundSequence;
import eu.germanrp.addon.core.integration.labyswaypoints.WaypointsIntegration;
import eu.germanrp.addon.core.listener.*;
import eu.germanrp.addon.core.serverapi.handler.AddATMPacketHandler;
import eu.germanrp.addon.core.serverapi.handler.RegisteredATMsPacketHandler;
import eu.germanrp.addon.core.serverapi.handler.RemoveATMPacketHandler;
import eu.germanrp.addon.core.serverapi.handler.UpdateATMPacketHandler;
import eu.germanrp.addon.core.services.*;
import eu.germanrp.addon.core.widget.*;
import eu.germanrp.addon.core.widget.category.GermanRPAddonWidgetCategory;
import eu.germanrp.addon.core.workflow.JoinWorkflowManager;
import eu.germanrp.addon.serverapi.GermanRPAddonIntegration;
import eu.germanrp.addon.serverapi.packet.AddATMPacket;
import eu.germanrp.addon.serverapi.packet.RegisteredATMsPacket;
import eu.germanrp.addon.serverapi.packet.RemoveATMPacket;
import eu.germanrp.addon.serverapi.packet.UpdateATMPacket;
import lombok.Getter;
import lombok.val;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.generated.ReferenceStorage;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.serverapi.core.AddonProtocol;

@Getter
@AddonMain
public class GermanRPAddon extends LabyAddon<GermanRPAddonConfiguration> {

    public static final String NAMESPACE = "germanrpaddon";

    @Getter
    private static GermanRPAddon instance;

    private NameTagService nameTagService;
    private NavigationService navigationService;
    private UtilService utilService;
    private VehicleService vehicleService;
    private POIService poiService;
    private SoundSequence soundSequence;

    private AddonPlayer player;

    private ServerJoinListener serverJoinListener;
    private JoinWorkflowManager joinWorkflowManager;

    private GermanRPAddonWidgetCategory widgetCategory;

    private HeilkrautpflanzeHudWidget heilkrautpflanzeHudWidget;
    private RoseHudWidget roseHudWidget;
    private StoffHudWidget stoffHudWidget;
    private GraffitiHudWidget graffitiHudWidget;
    private MajorEventWidget majorEventWidget;
    private PlayerExperienceWidget playerExperienceWidget;
    private BlackMarketWidget blackMarketWidget;
    private HydrationWidget hydrationWidget;
    private PayDayWidget paydayWidget;
    private HealthPointWidget healthPointWidget;
    private ExplosiveVestHudWidget explosiveVestHudWidget;
    private ChatListener chatListener;
    private PoppyWidget poppyWidget;

    @Override
    protected void load() {
        instance = this;
        this.player = new DefaultAddonPlayer(this);

        instantiateServices();

        this.logger().info("Loaded %s".formatted(NAMESPACE));
    }

    @Override
    protected void enable() {
        this.registerSettingCategory();

        registerWidgets();
        registerListener();
        registerCommands();

        val references = Laby.references();
        registerIntegrations(references);

        val protocolService = references.labyModProtocolService();
        val integration = protocolService.getOrRegisterIntegration(GermanRPAddonIntegration.class, GermanRPAddonIntegration::new);
        val protocol = integration.addonProtocol();
        registerPackets(protocol);

        this.logger().info("Enabled %s".formatted(NAMESPACE));
    }

    private void registerPackets(AddonProtocol protocol) {
        protocol.registerHandler(RegisteredATMsPacket.class, new RegisteredATMsPacketHandler(this));
        protocol.registerHandler(UpdateATMPacket.class, new UpdateATMPacketHandler(this));
        protocol.registerHandler(AddATMPacket.class, new AddATMPacketHandler(this));
        protocol.registerHandler(RemoveATMPacket.class, new RemoveATMPacketHandler(this));
    }

    private static void registerIntegrations(ReferenceStorage references) {
        references.addonIntegrationService()
                .registerIntegration("labyswaypoints", WaypointsIntegration.class);
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
        this.poiService = new POIService(this);
        this.soundSequence = new SoundSequence(this);
    }

    private void registerCommands() {
        registerCommand(new GraffitiCommand(this, this.graffitiHudWidget.getConfig()));
        registerCommand(new TogglePanicCommand());
    }

    private void registerWidgets() {
        final HudWidgetRegistry widgetRegistry = labyAPI().hudWidgetRegistry();
        this.widgetCategory = new GermanRPAddonWidgetCategory();

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
        this.poppyWidget = new PoppyWidget(this);
        this.healthPointWidget = new HealthPointWidget(widgetCategory);
        this.explosiveVestHudWidget = new ExplosiveVestHudWidget(widgetCategory);

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
        widgetRegistry.register(this.poppyWidget);
        widgetRegistry.register(this.healthPointWidget);
        widgetRegistry.register(this.explosiveVestHudWidget);
    }

    private void registerListener() {
        this.serverJoinListener = new ServerJoinListener(this);
        this.chatListener = new ChatListener(this);

        registerListener(this.serverJoinListener);
        registerListener(this.chatListener);
        registerListener(this.poiService);
        registerListener(this.soundSequence);

        registerListener(new SkillXPListener());
        registerListener(new EventRegistrationListener(this));
        registerListener(new NameTagListener(this));
        registerListener(new VehicleHotkeyListener(this));
        registerListener(new DarklistEventListener(this));
        registerListener(new BountyEventListener(this));
        registerListener(new WantedEventListener(this));
        registerListener(new MemberInfoEventListener(this));
        registerListener(new PlayerStatsEventListener(this));
        registerListener(new ATMVisibilityListener(this));
    }

}
