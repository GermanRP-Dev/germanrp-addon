package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.network.ATMPacket;
import eu.germanrp.addon.core.GermanRPAddon;
import lombok.val;
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
        val message = "Received ATM packet: %s".formatted(packet);
        this.addon.logger().debug(message);
        this.addon.getPlayer().sendDebugMessage(message);
    }

}
