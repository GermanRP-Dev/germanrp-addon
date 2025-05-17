package eu.germanrp.addon.core.common.events;

import com.google.gson.JsonObject;
import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class LegacyGermanRPUtilsPayloadEvent implements Event {

    private final String header;
    private final JsonObject payloadContent;

}
