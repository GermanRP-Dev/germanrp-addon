package eu.germanrp.addon.core.listener;

import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.APOTHEKEN_RAUB;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.BOMBE_START;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.JUWELEN_RAUB;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.SHOP_RAUB;

public class ChatListener {
    @Subscribe
    public void onChatReceiveMajorEvent(ChatReceiveEvent e){
        String m = e.chatMessage().getPlainText();
        final Matcher ApothekenRaubMatcher = APOTHEKEN_RAUB.getPattern().matcher(m);
        final Matcher ShopRaubMatcher = SHOP_RAUB.getPattern().matcher(m);
        final Matcher BombeStartMatcher = BOMBE_START.getPattern().matcher(m);
        final Matcher JuwelenRaubMatcher = JUWELEN_RAUB.getPattern().matcher(m);

        if(ShopRaubMatcher.find()){

        }
        if (ApothekenRaubMatcher.find()){

        }
        if (JuwelenRaubMatcher.find()){

        }
        if (BombeStartMatcher.find()){

        }

        

    }

}
