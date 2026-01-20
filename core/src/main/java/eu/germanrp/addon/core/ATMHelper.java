package eu.germanrp.addon.core;

import eu.germanrp.addon.serverapi.model.ATM;
import lombok.val;
import net.labymod.addons.waypoints.waypoint.WaypointBuilder;
import net.labymod.addons.waypoints.waypoint.WaypointMeta;
import net.labymod.addons.waypoints.waypoint.WaypointType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.math.vector.DoubleVector3;

import java.time.Instant;
import java.util.Optional;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;
import static eu.germanrp.addon.core.services.POIService.ATM_ID_PREFIX;

public final class ATMHelper {

    private ATMHelper() {
        // Hide public constructor
    }

    private static final Component WAYPOINT_PREFIX = Component.translatable(NAMESPACE + ".waypoint.atmPrefix");

    public static Optional<WaypointMeta> toWaypointMeta(ATM atm) {
        val currentServerData = Laby.references().serverController().getCurrentServerData();

        if (currentServerData == null) {
            return Optional.empty();
        }

        val atmConfig = GermanRPAddon.getInstance().configuration().atmConfig();

        boolean visible = atmConfig.showATMWaypoints().get();
        if (visible && atmConfig.hideDamagedATMs().get() && atm.cooldownTimestamp() != -1) {
            visible = Instant.now().isAfter(Instant.ofEpochMilli(atm.cooldownTimestamp()));
        }

        return Optional.of(WaypointBuilder.create()
                .identifier(ATM_ID_PREFIX + atm.id())
                .title(
                        Component.text()
                                .append("[")
                                .append(WAYPOINT_PREFIX)
                                .append("]")
                                .append(Component.space())
                                .append(atm.displayName())
                                .build()
                )
                .location(new DoubleVector3(atm.x(), atm.y(), atm.z()))
                .type(WaypointType.ADDON_MANAGED)
                .server(currentServerData.address())
                .color(atmConfig.atmWaypointColor().get())
                .visible(visible)
                .currentDimension()
                .build()
        );
    }

}
