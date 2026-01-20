package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.POIService;
import eu.germanrp.addon.serverapi.packet.RegisteredATMsPacket;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ATMPacketHandler implements PacketHandler<RegisteredATMsPacket> {

    private final GermanRPAddon addon;
    private final POIService poiService;

    public ATMPacketHandler(GermanRPAddon addon) {
        this.addon = addon;
        this.poiService = addon.getPoiService();
    }

    @Override
    public void handle(@NotNull UUID sender, @NotNull RegisteredATMsPacket packet) {
        this.addon.getPlayer().sendDebugMessage("ATMPacketHandler, handle, packet = %s".formatted(packet));
        this.poiService.addOrUpdateATMs(packet.atms());
    }

}
