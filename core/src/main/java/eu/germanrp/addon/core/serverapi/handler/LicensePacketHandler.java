package eu.germanrp.addon.core.serverapi.handler;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.serverapi.packet.LicensePacket;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public final class LicensePacketHandler implements PacketHandler<LicensePacket> {

    private final GermanRPAddon addon;

    @Override
    public void handle(@NotNull UUID sender, @NotNull LicensePacket packet) {
        val player = this.addon.getPlayer();
        player.sendDebugMessage("LicensePacketHandler, handle, packet = %s".formatted(packet));
        player.setLicenses(packet.licenses());
    }

}
