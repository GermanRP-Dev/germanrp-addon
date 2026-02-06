package eu.germanrp.addon.api.events;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the client receives a chat message
 * that is relevant to this Addon.
 */
@Getter
@Accessors(fluent = true)
public class GermanRPChatReceiveEvent extends ChatReceiveEvent {

    public GermanRPChatReceiveEvent(@NotNull ChatMessage message) {
        super(message);
    }

}
