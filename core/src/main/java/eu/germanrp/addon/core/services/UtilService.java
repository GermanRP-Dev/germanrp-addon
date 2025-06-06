package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.util.UtilTextService;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.client.resources.ResourceLocation;

import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.client.component.Component.empty;
import static net.labymod.api.client.component.format.NamedTextColor.DARK_GRAY;
import static net.labymod.api.client.component.format.NamedTextColor.GOLD;
import static net.labymod.api.client.gui.icon.Icon.texture;

/**
 * @author RettichLP
 */
@Getter
public class UtilService {

    private final Icon icon = texture(ResourceLocation.create("germanrpaddon", "themes/vanilla/textures/icon.png")).resolution(64, 64);

    @Accessors(fluent = true)
    private final UtilTextService text;

    private final GermanRPAddon addon;

    public UtilService(GermanRPAddon addon) {
        this.addon = addon;
        this.text = new UtilTextService();
    }

    @SuppressWarnings("SameReturnValue")
    public String version() {
        return "2.5.0";
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isGermanRP() {
        if (labyAPI().minecraft().isIngame()) {
            ServerData serverData = labyAPI().serverController().getCurrentServerData();
            return serverData != null && serverData.address().matches("germanrp.eu", 25565, true);
        }
        return false;
    }

    public boolean isLegacyAddonPacket(ResourceLocation eventIdentifier) {
        return eventIdentifier.getNamespace().equals("labymod3")
                && eventIdentifier.getPath().equals("main");
    }

    public void debug(String debugMessage) {
        if (this.addon.configuration().debug().get()) {
            Component component = empty()
                    .append(Component.text("[", DARK_GRAY))
                    .append(Component.text("DEBUG", GOLD))
                    .append(Component.text("] ", DARK_GRAY))
                    .append(Component.text(debugMessage));

            this.addon.getPlayer().sendMessage(component);
        }
    }
}
