package eu.germanrp.addon.listener;

import eu.germanrp.addon.GermanRPAddon;
import lombok.RequiredArgsConstructor;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class OutOfCharakterChatListener {

    private final GermanRPAddon addon;

    @Subscribe
    public void onMessageSend(@NotNull ChatMessageSendEvent event) {
        String message = event.getMessage();

        boolean antiChatOOC = addon.configuration().antichatooc().get();
        if (!antiChatOOC) {
            return;
        }

        String regex = "(?i)(\\s|^)oo[cs](\\s|$)";

        if (message.matches(regex)) {
            event.changeMessage("/ooc " + message.replaceAll(regex, ""));
        }
    }
}
