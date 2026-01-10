package eu.germanrp.addon.core.common.model;

import lombok.*;
import lombok.experimental.Accessors;
import net.labymod.addons.waypoints.waypoint.WaypointBuilder;
import net.labymod.addons.waypoints.waypoint.WaypointMeta;
import net.labymod.addons.waypoints.waypoint.WaypointType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.math.vector.DoubleVector3;
import net.labymod.serverapi.api.packet.Packet;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import net.labymod.serverapi.api.payload.io.PayloadWriter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;
import static eu.germanrp.addon.core.services.POIService.ATM_ID_PREFIX;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ATMPacket implements Packet {

    @Accessors(fluent = true)
    private List<ATM> atms;

    @Override
    public void write(@NotNull PayloadWriter writer) {
        writer.writeCollection(
                this.atms,
                (atm) -> {
                    writer.writeString(atm.displayName);
                    writer.writeString(atm.id);
                    writer.writeDouble(atm.x);
                    writer.writeDouble(atm.y);
                    writer.writeDouble(atm.z);
                }
        );
    }

    @Override
    public void read(@NotNull PayloadReader reader) {
        this.atms = reader.readList(() -> ATM.builder()
                .displayName(reader.readString())
                .id(reader.readString())
                .x(reader.readDouble())
                .y(reader.readDouble())
                .z(reader.readDouble())
                .build());
    }

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

}
