package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.util.UtilTextService;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.client.gui.icon.Icon.texture;

/**
 * @author RettichLP
 */
@Getter
public class UtilService {

    private final Icon icon = texture(ResourceLocation.create(GermanRPAddon.NAMESPACE, "themes/vanilla/textures/icon.png")).resolution(64, 64);

    @Accessors(fluent = true)
    private final UtilTextService text;

    private final GermanRPAddon addon;

    public UtilService(GermanRPAddon addon) {
        this.addon = addon;
        this.text = new UtilTextService();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isGermanRP() {
        if (labyAPI().minecraft().isIngame()) {
            val serverData = labyAPI().serverController().getCurrentServerData();

            if(serverData == null) {
                return false;
            }

            val address = serverData.address();

            return address.matches("germanrp.de", 25565, true)
                    || address.matches("germanrp.eu", 25565, true)
                    || address.matches("91.218.66.124", 25565, true)
                    || address.matches("dev.germanrp.eu", 25554, true)
                    || address.matches("91.218.66.124", 25554, true);

        }
        return false;
    }

    public boolean isLegacyAddonPacket(ResourceLocation eventIdentifier) {
        return eventIdentifier.getNamespace().equals("labymod3")
                && eventIdentifier.getPath().equals("main");
    }
}
