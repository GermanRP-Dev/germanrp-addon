package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.network.ATMPacket;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ATMPacketHandler implements PacketHandler<ATMPacket> {

    private final GermanRPAddon addon;

    public ATMPacketHandler(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Override
    public void handle(@NotNull UUID sender, @NotNull ATMPacket packet) {
        this.addon.logger().debug("Received ATM packet: %s".formatted(packet));
    }

}
