package eu.germanrp.addon.api.network;

import com.google.gson.JsonObject;
import eu.germanrp.addon.api.models.PlantType;
import lombok.val;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.io.PayloadReader;

import java.util.Optional;

public final class PaketFactory {

    private PaketFactory() {
        throw new UnsupportedOperationException("This class should not be instantiated");
    }

    public static Optional<GRPaket> createPaket(byte[] payload) {
        final PayloadReader payloadReader = new PayloadReader(payload);
        final String header = payloadReader.readString();

        if (!header.startsWith("GRAddon-")) {
            return Optional.empty();
        }

        return switch (header) {

            case "GRAddon-Plant" -> {
                val payloadContent = payloadReader.readString();

                val jsonObject = GsonUtil.DEFAULT_GSON.fromJson(payloadContent, JsonObject.class);

                val type = PlantType.fromPaketType(jsonObject.get("type").getAsString());

                if (type.isEmpty()) {
                    yield Optional.empty();
                }

                val time = jsonObject.getAsJsonObject("time");

                val plantPaket = new PlantPacket(
                        jsonObject.get("active").getAsBoolean(),
                        type.get(),
                        jsonObject.get("value").getAsInt(),
                        time.get("current").getAsInt(),
                        time.get("max").getAsInt()
                );

                yield Optional.of(plantPaket);
            }

            default -> Optional.empty();
        };
    }
}
