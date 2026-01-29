package eu.germanrp.addon.core.common.events;

import eu.germanrp.addon.api.models.CharacterInfo;
import net.labymod.api.event.Event;

public record DutyBadgeShownEvent(CharacterInfo charInfo) implements Event {
}
