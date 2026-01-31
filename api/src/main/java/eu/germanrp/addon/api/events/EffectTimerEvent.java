package eu.germanrp.addon.api.events;

import eu.germanrp.addon.serverapi.packet.EffectPacket;
import net.labymod.api.event.Event;

import java.time.Instant;

public record EffectTimerEvent(
        EffectPacket.EffectType type,
        String displayName,
        Instant end
) implements Event {

    public static EffectTimerEvent of(EffectPacket packet) {
        return new EffectTimerEvent(packet.getType(), packet.getDisplayName(), packet.getEnd());
    }

}
