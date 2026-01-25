package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.events.network.HydrationUpdateEvent;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.AddonServerJoinEvent;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.util.List;

import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.client.component.Component.translatable;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.HIDDEN;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.VISIBLE;

public class HydrationWidget extends TextHudWidget<TextHudWidgetConfig> {

    private static final Component hydrationKey = translatable(GermanRPAddon.NAMESPACE + ".widget.hydration.key");
    private static final Component hydrationDummyValue = translatable(GermanRPAddon.NAMESPACE + ".widget.hydration.dummy");

    private final GermanRPAddon addon;
    private final List<String> hydrationMessages = List.of("► Du bist durstig.","► Du bist sehr durstig. (Trinke etwas, um nicht zu dehydrieren!)","► Du bist sehr durstig.","► Du bist stark dehydriert und fühlst dich schwach.");

    private TextLine textLine;

    public HydrationWidget(GermanRPAddon addon, HudWidgetCategory widgetCategory) {
        super("hydration");
        this.bindCategory(widgetCategory);
        this.addon = addon;
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        this.textLine = this.createLine(hydrationKey, hydrationDummyValue);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onAddonServerJoinEvent(final AddonServerJoinEvent e) {
        this.textLine.setState(e.isGR() ? VISIBLE : HIDDEN);
    }

    @Subscribe
    public void onHydrationUpdate(HydrationUpdateEvent event) {
        val amount = event.getAmount();

        if(Double.isNaN(amount)) {
            this.textLine.updateAndFlush(hydrationDummyValue);
            return;
        }

        this.textLine.updateAndFlush(format(amount));
    }

    @Subscribe
    public void onChatMessage(ChatReceiveEvent event) {
        final String plainText = event.chatMessage().getPlainText();

        // Ignore unknown messages
        if (!hydrationMessages.contains(plainText)) {
            return;
        }

        if (this.addon.configuration().hydrationConfig().hideHydrationChatMessage().get().equals(Boolean.TRUE)) {
            event.setCancelled(true);
            return;
        }

        if (this.addon.configuration().hydrationConfig().displayHydrationMessageInActionBar().get()
                .equals(Boolean.FALSE)) {
            return; // If we end up here, the message will just go through to the client
        }

        labyAPI().minecraft().chatExecutor().displayActionBar(event.chatMessage().component());
        event.setCancelled(true);
    }

    private String format(double hydration) {
        return String.format("%.2f%%", hydration);
    }
}
