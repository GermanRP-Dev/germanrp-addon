package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.MajorWidgetUpdateEvent;
import eu.germanrp.addon.core.widget.MajorEventWidget;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.time.ZonedDateTime;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.APOTHEKEN_RAUB;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.BOMBE_START;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.JUWELEN_RAUB;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.SHOP_RAUB;


public class ChatListener {
    private final GermanRPAddon addon;
    private final MajorEventWidget majorEventWidget;
    public ChatListener(GermanRPAddon addon) {
        this.addon = addon;
        this.majorEventWidget = this.addon.getMajorEventWidget();
    }
    @Subscribe
    public void onChatReceiveMajorEvent(ChatReceiveEvent e){
        if(this.majorEventWidget.isMajorEvent()){
            return;
        }
        String m = e.chatMessage().getPlainText();
        final Matcher ApothekenRaubMatcher = APOTHEKEN_RAUB.getPattern().matcher(m);
        final Matcher ShopRaubMatcher = SHOP_RAUB.getPattern().matcher(m);
        final Matcher BombeStartMatcher = BOMBE_START.getPattern().matcher(m);
        final Matcher JuwelenRaubMatcher = JUWELEN_RAUB.getPattern().matcher(m);

        if(ShopRaubMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Shopraub", 3));
            return;
        }

        if (ApothekenRaubMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Apothekenraub", 8));
            return;
        }

        if (JuwelenRaubMatcher.find()) {
            Laby.fireEvent(new MajorWidgetUpdateEvent("Juwelenraub", 3));
            return;
        }

        if (BombeStartMatcher.find()){
            Laby.fireEvent(new MajorWidgetUpdateEvent("Bombe", 10));
            return;
        }
    }


}
