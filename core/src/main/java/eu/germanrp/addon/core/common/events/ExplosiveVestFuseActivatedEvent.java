package eu.germanrp.addon.core.common.events;

import net.labymod.api.event.Event;

/**
 * Fired when an explosive vest fuse starts.
 *
 * @param seconds the fuse duration in seconds
 */
public record ExplosiveVestFuseActivatedEvent(int seconds) implements Event {

    private static final int FUSE_DURATION = 5;

    public ExplosiveVestFuseActivatedEvent() {
        this(FUSE_DURATION);
    }

}
