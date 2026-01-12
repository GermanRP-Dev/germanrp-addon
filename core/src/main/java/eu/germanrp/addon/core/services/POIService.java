package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.model.ATMPacket;
import lombok.val;
import net.labymod.addons.waypoints.WaypointService;
import net.labymod.addons.waypoints.Waypoints;
import net.labymod.api.client.component.Component;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

public class POIService {

    public static final String ATM_ID_PREFIX = "ATM";

    private final WaypointService waypointService = Waypoints.references().waypointService();
    private final GermanRPAddon addon;

    public POIService(final GermanRPAddon addon) {
        this.addon = addon;
        addon.configuration().atmConfig().showATMWaypoints().addChangeListener(this::showOrHideATMs);
    }

    public void addOrUpdateATM(final ATMPacket.ATM atm) {
        waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
        atm.toWaypointMeta().ifPresent(waypointService::add);
        waypointService.refresh();
    }

    private void showOrHideATMs(final boolean show) {
        val message = show
                ? Component.translatable(NAMESPACE + ".message.atm.showWaypoints")
                : Component.translatable(NAMESPACE + ".message.atm.hideWaypoints");

        this.addon.getPlayer().sendInfoMessage(message);

        waypointService.getAll()
                .stream()
                .filter(waypoint -> waypoint.meta().getIdentifier().startsWith(ATM_ID_PREFIX))
                .forEach(waypoint -> waypoint.meta().setVisible(show));
    }

}
