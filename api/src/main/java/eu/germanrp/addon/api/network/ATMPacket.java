package eu.germanrp.addon.api.network;

import lombok.Builder;
import lombok.Getter;
import net.labymod.serverapi.api.packet.Packet;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import net.labymod.serverapi.api.payload.io.PayloadWriter;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;


@Builder
@Getter
public class ATMPacket implements Packet {

    private @NotNull String displayName;
    private @NotNull String id;
    private double x;
    private double y;
    private double z;

    @Override
    public void write(@NotNull PayloadWriter writer) {
        writer.writeString(displayName);
        writer.writeString(id);
        writer.writeDouble(x);
        writer.writeDouble(y);
        writer.writeDouble(z);
    }

    @Override
    public void read(@NotNull PayloadReader reader) {
        this.displayName = reader.readString();
        this.id = reader.readString();
        this.x = reader.readDouble();
        this.y = reader.readDouble();
        this.z = reader.readDouble();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ATMPacket.class.getSimpleName() + "[", "]")
                .add("displayName='" + displayName + "'")
                .add("id='" + id + "'")
                .add("x=" + x)
                .add("y=" + y)
                .add("z=" + z)
                .toString();
    }

}
