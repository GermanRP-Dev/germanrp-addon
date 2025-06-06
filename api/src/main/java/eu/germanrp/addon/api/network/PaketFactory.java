package eu.germanrp.addon.api.network;

import com.google.gson.JsonObject;
import eu.germanrp.addon.api.models.PlantType;
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

        return switch (header) {
            case "GRAddon-Plant" -> {
                final String payloadContent = payloadReader.readString();

                final JsonObject jsonObject = GsonUtil.DEFAULT_GSON.fromJson(payloadContent, JsonObject.class);

                final Optional<PlantType> type = PlantType.fromPaketType(jsonObject.get("type").getAsString());

                if (type.isEmpty()) {
                    yield Optional.empty();
                }

                final JsonObject time = jsonObject.getAsJsonObject("time");

                final PlantPacket plantPaket = new PlantPacket(
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
