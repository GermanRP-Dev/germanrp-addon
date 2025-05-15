package eu.germanrp.addon.core.common.events;

import net.labymod.api.event.Event;

import static eu.germanrp.addon.core.GermanRPAddon.utilService;
import static net.labymod.api.Laby.labyAPI;

/**
 * @author RettichLP
 */
public class GermanRPAddonTickEvent implements Event {

    private final Phase phase;

    public GermanRPAddonTickEvent(Phase phase) {
        this.phase = phase;
    }

    public boolean isIngame() {
        return labyAPI().minecraft().isIngame();
    }

    public boolean isGermanRP() {
        return utilService.isGermanRP();
    }

    public boolean isPhase(Phase phase) {
        return this.phase.equals(phase);
    }

    public enum Phase {

        TICK,
        TICK_5,
        MINUTE,
        SECOND,
        SECOND_3,
        SECOND_5,
        SECOND_30
    }
}
