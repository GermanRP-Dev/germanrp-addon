package eu.germanrp.addon.core.serverapi.handler;

import eu.germanrp.addon.api.events.EffectTimerEvent;
import eu.germanrp.addon.serverapi.packet.EffectPacket;
import net.labymod.serverapi.api.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.labymod.api.Laby.fireEvent;

public final class EffectPacketHandler implements PacketHandler<EffectPacket> {

    @Override
    public void handle(@NotNull UUID sender, @NotNull EffectPacket packet) {
        fireEvent(EffectTimerEvent.of(packet));
    }

}
