package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.common.model.ATMPacket;
import net.labymod.addons.waypoints.WaypointService;
import net.labymod.addons.waypoints.Waypoints;

public class POIService {

    public static final String ATM_ID_PREFIX = "ATM";

    private final WaypointService waypointService = Waypoints.references().waypointService();

    public void addOrUpdateATM(final ATMPacket.ATM atm) {
        waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
        atm.toWaypointMeta().ifPresent(waypointService::add);
        waypointService.refresh();
    }

}
