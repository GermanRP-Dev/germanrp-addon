package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.model.ATMPacket;
import lombok.val;
import net.labymod.addons.waypoints.WaypointService;
import net.labymod.addons.waypoints.Waypoints;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;

public class POIService {

    public static final String ATM_ID_PREFIX = "ATM";

    private final WaypointService waypointService = Waypoints.references().waypointService();
    private final GermanRPAddon addon;
    private final Map<String, ATMPacket.ATM> atmMap = new HashMap<>();

    public POIService(final GermanRPAddon addon) {
        this.addon = addon;
        val atmConfig = addon.configuration().atmConfig();
        atmConfig.showATMWaypoints().addChangeListener(this::showOrHideATMs);
        atmConfig.atmWaypointColor().addChangeListener(this::updateATMColor);
        atmConfig.hideDamagedATMs().addChangeListener(show -> this.refreshATMs());
    }

    public void addOrUpdateATMs(final List<ATMPacket.ATM> atms) {
        for (val atm : atms) {
            this.atmMap.put(atm.id(), atm);
            waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
            atm.toWaypointMeta().ifPresent(waypointService::add);
        }

        waypointService.refresh();
    }

    public void refreshATMs() {
        for (val atm : this.atmMap.values()) {
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

        this.refreshATMs();
    }

    private void updateATMColor(final Color color) {
        for (val waypoint : waypointService.getAll()) {
            val meta = waypoint.meta();
            if (meta.getIdentifier().startsWith(ATM_ID_PREFIX)) {
                meta.setColor(color);
            }
        }
    }

}
