package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.events.network.HydrationUpdateEvent;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.HIDDEN;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.VISIBLE;

public class HydrationWidget extends TextHudWidget<TextHudWidgetConfig> {

    private static final Component hydrationKey = Component.translatable("germanrpaddon.widget.hydration.key");

    private final GermanRPAddon addon;

    private TextLine textLine;

    public HydrationWidget(GermanRPAddon addon, HudWidgetCategory widgetCategory) {
        super("hydration");
        this.bindCategory(widgetCategory);
        this.addon = addon;
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        this.textLine = this.createLine(hydrationKey, format(0));
    }
    @Subscribe
    public void onServerJoin(JustJoinedEvent e){
        this.textLine.setState(e.isJustJoined() ? VISIBLE : HIDDEN);
    }

    @Subscribe
    public void onHydrationUpdate(HydrationUpdateEvent event) {
        this.textLine.updateAndFlush(format(event.getAmount()));
    }

    @Subscribe
    public void onChatMessage(ChatReceiveEvent event) {
        final String plainText = event.chatMessage().getPlainText();

        // Ignore unknown messages
        if (!plainText.equals("► Du bist durstig.") && !plainText.equals("► Du bist sehr durstig.") && !plainText.equals(
                "► Du bist sehr durstig. (Trinke etwas, um nicht zu dehydrieren!)")) {
            return;
        }

        if (addon.configuration().hydrationConfig().hideHydrationChatMessage().get().equals(Boolean.TRUE)) {
            event.setCancelled(true);
            return;
        }

        if (addon.configuration().hydrationConfig().displayHydrationMessageInActionBar().get()
                .equals(Boolean.FALSE)) {
            return; // If we end up here, the message will just go through to the client
        }

        addon.labyAPI().minecraft().chatExecutor().displayActionBar(event.chatMessage().component());
        event.setCancelled(true);
    }

    private String format(double hydration) {
        return String.format("%.2f%%", hydration);
    }
}
