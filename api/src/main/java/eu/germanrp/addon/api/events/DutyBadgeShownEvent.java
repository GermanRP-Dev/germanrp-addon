package eu.germanrp.addon.api.events;

import eu.germanrp.addon.api.models.CharacterInfo;
import net.labymod.api.event.Event;

public record DutyBadgeShownEvent(CharacterInfo charInfo) implements Event {
}
