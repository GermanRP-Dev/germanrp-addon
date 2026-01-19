package eu.germanrp.addon.api.events.atm;

import net.labymod.api.event.Event;

/**
 * Event fired when an ATM's cooldown has expired.
 * <p>
 * This event is used to trigger updates for ATM waypoints when they should become visible again
 * after their cooldown period ends.
 *
 * @param atmId the unique identifier of the ATM that is now off cooldown
 */
public record ATMOffCooldownEvent(String atmId) implements Event {

}
