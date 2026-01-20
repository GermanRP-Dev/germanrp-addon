package eu.germanrp.addon.core.serverapi.handler;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.POIService;
import eu.germanrp.addon.serverapi.packet.RemoveATMPacket;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoveATMPacketHandler implements PacketHandler<RemoveATMPacket> {

    private final GermanRPAddon addon;
    private final POIService poiService;

    public RemoveATMPacketHandler(final GermanRPAddon addon) {
        this.addon = addon;
        this.poiService = addon.getPoiService();
    }

    @Override
    public void handle(@NotNull UUID sender, @NotNull RemoveATMPacket packet) {
        this.addon.getPlayer().sendDebugMessage("RemoveATMPacketHandler, handle, packet = %s".formatted(packet));
        this.poiService.removeATM(packet.id());
    }

}
