package eu.germanrp.addon.core.integration.germanrpaddon;

import com.google.auto.service.AutoService;
import eu.germanrp.addon.api.network.ATMPacket;
import net.labymod.serverapi.api.packet.Direction;
import net.labymod.serverapi.core.AbstractLabyModPlayer;
import net.labymod.serverapi.core.AbstractLabyModProtocolService;
import net.labymod.serverapi.core.AddonProtocol;
import net.labymod.serverapi.core.integration.LabyModIntegrationPlayer;
import net.labymod.serverapi.core.integration.LabyModProtocolIntegration;
import org.jetbrains.annotations.NotNull;

@AutoService(LabyModProtocolIntegration.class)
public class GermanRPAddonIntegration implements LabyModProtocolIntegration {

    private AbstractLabyModProtocolService protocolService;
    private AddonProtocol addonProtocol;

    @Override
    public void initialize(AbstractLabyModProtocolService protocolService) {
        if (this.protocolService != null) {
            throw new IllegalStateException("GermanRPAddonIntegration is already initialized");
        }

        this.protocolService = protocolService;

        this.addonProtocol = new AddonProtocol(protocolService, "germanrpeuaddon");
        this.addonProtocol.registerPacket(0, ATMPacket.class, Direction.CLIENTBOUND);

        protocolService.registry().registerProtocol(this.addonProtocol);
    }

    @Override
    public LabyModIntegrationPlayer createIntegrationPlayer(AbstractLabyModPlayer<?> labyModPlayer) {
        return new GermanRPAddonPlayer(this.addonProtocol, labyModPlayer.getUniqueId());
    }

    public @NotNull AddonProtocol GermanRPAddonProtocol() {
        if (this.addonProtocol == null) {
            throw new IllegalStateException("GermanRPAddonIntegration is not initialized");
        }

        return this.addonProtocol;
    }

}
