package eu.germanrp.addon.core.roseWidget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.GlobalRegexRegistry;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import eu.germanrp.addon.core.config.MohnWidgetSubConfig;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.util.List;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.MOHN_MESSAGE;
import static net.labymod.api.client.component.Component.translatable;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.*;

@Getter
@Setter
public class MohnWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final List<Component> translatableList = List.of(
            translatable("germanrpaddon.widget.mohn.key")
    );

    private List<TextLine> listTextLine = List.of();
    public MohnWidget(HudWidgetCategory widgetCategory) {
        super("MohnWidget");
        super.bindCategory(widgetCategory);

    }
    @Override
    public void load(TextHudWidgetConfig config) {
        for (Component comp : translatableList){
            this.listTextLine.add(this.createLine(comp));
        }
        for (TextLine textLine : listTextLine){
            textLine.setState(HIDDEN);
        }

    }
    @Subscribe
    public void onChatMessage(ChatReceiveEvent event) {
        String message = event.chatMessage().getPlainText();
        MohnWidgetSubConfig mohnWidgetSubConfig = GermanRPAddon.getInstance().configuration().mohnWidgetSubConfig();
        if(mohnWidgetSubConfig.getShowMohnMessages().get().equals(false)) {
            Matcher matcher = MOHN_MESSAGE.getPattern().matcher(message);
            if (matcher.find()) {
                event.setCancelled(true);
            }
        }

    }

    @Subscribe
    public void onServerJoin(JustJoinedEvent e) {
        TextLine.State state = e.isJustJoined() ? VISIBLE : HIDDEN;
        if(state == HIDDEN) {
            for (TextLine textLine : listTextLine) {
                textLine.setState(HIDDEN);
            }
        }
    }

}
