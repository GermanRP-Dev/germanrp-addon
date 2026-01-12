package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.model.ATMPacket;
import lombok.val;
import net.labymod.addons.waypoints.WaypointService;
import net.labymod.addons.waypoints.Waypoints;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.Color;

import java.util.List;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

public class POIService {

    public static final String ATM_ID_PREFIX = "ATM";

    private final WaypointService waypointService = Waypoints.references().waypointService();
    private final GermanRPAddon addon;

    public POIService(final GermanRPAddon addon) {
        this.addon = addon;
        val atmConfig = addon.configuration().atmConfig();
        atmConfig.showATMWaypoints().addChangeListener(this::showOrHideATMs);
        atmConfig.atmWaypointColor().addChangeListener(this::updateATMColor);
    }

    public void addOrUpdateATMs(final List<ATMPacket.ATM> atms) {
        for (val atm : atms) {
            waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
            atm.toWaypointMeta().ifPresent(waypointService::add);
        }

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

    private void updateATMColor(final Color color) {
        waypointService.getAll()
                .stream()
                .filter(waypoint -> waypoint.meta().getIdentifier().startsWith(ATM_ID_PREFIX))
                .forEach(waypoint -> waypoint.meta().setColor(color));
    }

}
