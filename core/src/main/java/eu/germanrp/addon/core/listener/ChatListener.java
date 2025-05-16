package eu.germanrp.addon.core.listener;

import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.pattern.MajorEventPattern.*;

public class ChatListener {
    @Subscribe
    public void onChatReceiveMajorEvent(ChatReceiveEvent e){
        String m = e.chatMessage().getPlainText();
        final Matcher ApothekenRaubPattern = APOTEKENRAUB_PATTERN.matcher(m);

        if(ApothekenRaubPattern.find()){

        }

    }

}
