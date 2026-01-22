package eu.germanrp.addon.core.serverapi.handler;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.POIService;
import eu.germanrp.addon.serverapi.packet.UpdateATMPacket;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UpdateATMPacketHandler implements PacketHandler<UpdateATMPacket> {

    private final GermanRPAddon addon;
    private final POIService poiService;

    public UpdateATMPacketHandler(final GermanRPAddon addon) {
        this.addon = addon;
        this.poiService = addon.getPoiService();
    }

    @Override
    public void handle(@NotNull UUID sender, @NotNull UpdateATMPacket packet) {
        this.addon.getPlayer().sendDebugMessage("UpdateATMPacketHandler, handle, packet = %s".formatted(packet));
        this.poiService.updateATM(packet.id(), packet.atm());
    }

}
