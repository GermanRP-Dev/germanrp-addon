package eu.germanrp.addon.api.network;

import net.labymod.serverapi.api.packet.Packet;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import net.labymod.serverapi.api.payload.io.PayloadWriter;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;


public class ATMPacket implements Packet {

    private String displayName;
    private String id;
    private int x;
    private int y;
    private int z;

    @Override
    public void write(@NotNull PayloadWriter writer) {
        writer.writeString(displayName);
        writer.writeString(id);
        writer.writeVarInt(x);
        writer.writeVarInt(y);
        writer.writeVarInt(z);
    }

    @Override
    public void read(@NotNull PayloadReader reader) {
        this.displayName = reader.readString();
        this.id = reader.readString();
        this.x = reader.readVarInt();
        this.y = reader.readVarInt();
        this.z = reader.readVarInt();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ATMPacket.class.getSimpleName() + "[", "]")
                .add("displayName='" + displayName + "'")
                .add("id=" + id)
                .add("x=" + x)
                .add("y=" + y)
                .add("z=" + z)
                .toString();
    }
}
