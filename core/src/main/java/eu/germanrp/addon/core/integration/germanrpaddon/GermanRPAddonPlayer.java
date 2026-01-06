package eu.germanrp.addon.core.integration.germanrpaddon;

import eu.germanrp.addon.api.network.ATMPacket;
import eu.germanrp.addon.core.integration.germanrpaddon.model.ATM;
import lombok.val;
import net.labymod.serverapi.core.AddonProtocol;
import net.labymod.serverapi.core.integration.LabyModIntegrationPlayer;

import java.util.UUID;

public class GermanRPAddonPlayer implements LabyModIntegrationPlayer {

    private final AddonProtocol addonProtocol;
    private final UUID uniqueID;

    public GermanRPAddonPlayer(AddonProtocol addonProtocol, UUID uniqueID) {
        this.addonProtocol = addonProtocol;
        this.uniqueID = uniqueID;
    }

    public void sendATM(ATM atm) {
        val atmPacket = ATMPacket.builder()
                .displayName(atm.name())
                .id(atm.id())
                .x(atm.x())
                .y(atm.y())
                .z(atm.z())
                .build();
        this.addonProtocol.sendPacket(this.uniqueID, atmPacket);
    }

}
