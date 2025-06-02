package eu.germanrp.addon.core.common.events;

import lombok.Data;
import net.labymod.api.event.Event;

@Data
public class JustJoinedEvent implements Event {

    private final boolean justJoined;
}
