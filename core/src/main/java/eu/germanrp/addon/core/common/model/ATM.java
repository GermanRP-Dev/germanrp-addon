package eu.germanrp.addon.core.common.model;

import lombok.Builder;
import lombok.val;
import net.labymod.addons.waypoints.waypoint.WaypointBuilder;
import net.labymod.addons.waypoints.waypoint.WaypointMeta;
import net.labymod.addons.waypoints.waypoint.WaypointType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.math.vector.DoubleVector3;

import java.util.Optional;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;
import static eu.germanrp.addon.core.services.POIService.ATM_ID_PREFIX;

@Builder
public record ATM(
        String displayName,
        String id,
        double x,
        double y,
        double z
) {

    private static final Component WAYPOINT_PREFIX = Component.translatable(NAMESPACE + ".waypoint.atmPrefix");

    public Optional<WaypointMeta> toWaypointMeta() {
        val currentServerData = Laby.references().serverController().getCurrentServerData();

        if (currentServerData == null) {
            return Optional.empty();
        }

        return Optional.of(WaypointBuilder.create()
                .identifier(ATM_ID_PREFIX + this.id())
                .title(
                        Component.text()
                                .append("[")
                                .append(WAYPOINT_PREFIX)
                                .append("]")
                                .append(Component.space())
                                .append(this.displayName())
                                .build()
                )
                .location(new DoubleVector3(this.x(), this.y(), this.z()))
                .type(WaypointType.ADDON_MANAGED)
                .server(currentServerData.address())
                .currentDimension()
                .build()
        );
    }

}
