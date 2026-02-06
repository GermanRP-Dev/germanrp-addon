package eu.germanrp.addon.core.serverapi.handler;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.POIService;
import eu.germanrp.addon.serverapi.packet.atm.AddATMPacket;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AddATMPacketHandler implements PacketHandler<AddATMPacket> {

    private final GermanRPAddon addon;
    private final POIService poiService;

    public AddATMPacketHandler(final GermanRPAddon addon) {
        this.addon = addon;
        this.poiService = addon.getPoiService();
    }

    @Override
    public void handle(@NotNull UUID sender, @NotNull AddATMPacket packet) {
        this.addon.getPlayer().sendDebugMessage("AddATMPacketHandler, handle, packet = %s".formatted(packet));
        this.poiService.addATM(packet.atm());
    }

}
