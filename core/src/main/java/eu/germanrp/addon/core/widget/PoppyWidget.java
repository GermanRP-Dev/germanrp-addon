package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.AddonServerJoinEvent;
import eu.germanrp.addon.core.common.events.PoppyAddToPouchEvent;
import eu.germanrp.addon.core.common.events.PoppyReceiveEvent;
import eu.germanrp.addon.core.common.events.PoppyRemoveFromPouchEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static net.labymod.api.Laby.fireEvent;
import static net.labymod.api.client.component.Component.translatable;
import static net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State.*;

@Getter
@Setter
public class PoppyWidget extends TextHudWidget<PoppyWidget.PoppyHudWidgetConfig> {

    public static class PoppyHudWidgetConfig extends TextHudWidgetConfig {

        @Getter
        @Accessors(fluent = true)
        @SwitchWidget.SwitchSetting
        private final ConfigProperty<Boolean> showPoppyPerMinute = new ConfigProperty<>(true);

    }

    private static final int UNKNOWN_POPPY_COUNT = Integer.MIN_VALUE;

    private final GermanRPAddon addon;

    private final Component poppyKey = translatable("germanrpaddon.widget.poppy.key");
    private final Component poppyUnknown = translatable("germanrpaddon.widget.poppy.dummy");
    private final Component poppyPpmKey = translatable("germanrpaddon.widget.poppy.ppmKey");


    private @Nullable TextLine poppyLine;
    private @Nullable TextLine poppiesPerMinuteLine;

    /**
     * The count of poppies the player has.
     */
    private int poppyCount = UNKNOWN_POPPY_COUNT;

    private final Queue<PoppyEntry> poppyEntries = new LinkedList<>();

    private record PoppyEntry(long timestamp, int amount) {
    }

    public PoppyWidget(final GermanRPAddon addon) {
        super("poppy", PoppyHudWidgetConfig.class);
        this.addon = addon;
        bindCategory(addon.getWidgetCategory());
        this.setIcon(Icon.texture(ResourceLocation.create("minecraft", "textures/block/poppy.png")));
    }

    @Override
    public void load(final PoppyHudWidgetConfig config) {
        super.load(config);
        this.poppyLine = createLine(poppyKey, poppyUnknown);
        this.poppiesPerMinuteLine = createLine(poppyPpmKey, "0");

        if (this.poppyLine != null && this.poppiesPerMinuteLine != null) {
            val isGR = addon.getUtilService().isGermanRP();
            this.poppyLine.setState(isGR ? VISIBLE : HIDDEN);
            this.poppiesPerMinuteLine.setState(isGR ? (config.showPoppyPerMinute().get() ? VISIBLE : DISABLED) : DISABLED);
        }
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if (this.poppyLine == null || this.poppiesPerMinuteLine == null) {
            return;
        }

        if (isEditorContext) {
            this.poppyLine.updateAndFlush("%d".formatted(69420));
            this.poppyLine.setState(VISIBLE);

            this.poppiesPerMinuteLine.updateAndFlush("%d".formatted(420));
            this.poppiesPerMinuteLine.setState(this.config.showPoppyPerMinute().get() ? VISIBLE : DISABLED);
            return;
        }

        val isGR = this.addon.getUtilService().isGermanRP();
        if (!isGR) {
            if (this.poppyLine.state() != HIDDEN) {
                this.poppyLine.setState(HIDDEN);
            }
            if (this.poppiesPerMinuteLine.state() != DISABLED) {
                this.poppiesPerMinuteLine.setState(DISABLED);
            }
            return;
        }

        if (this.poppyCount == UNKNOWN_POPPY_COUNT) {
            this.poppyLine.updateAndFlush(poppyUnknown);
        } else {
            this.poppyLine.updateAndFlush("%d".formatted(this.poppyCount));
        }
        this.poppyLine.setState(VISIBLE);

        val showPpm = this.config.showPoppyPerMinute().get();
        this.poppiesPerMinuteLine.setState(showPpm ? VISIBLE : DISABLED);

        if (showPpm) {
            val currentTime = System.currentTimeMillis();
            while (!poppyEntries.isEmpty() && currentTime - poppyEntries.peek().timestamp() > 60_000) {
                poppyEntries.poll();
            }

            val totalAdded = poppyEntries.stream().mapToInt(PoppyEntry::amount).sum();
            this.poppiesPerMinuteLine.updateAndFlush("%d".formatted(totalAdded));
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatMessage(final ChatReceiveEvent event) {
        val message = event.chatMessage().getPlainText();

        val poppyPouchAddMatcher = POPPY_ADD_TO_POUCH.getPattern().matcher(message);
        if (poppyPouchAddMatcher.matches()) {
            fireEvent(new PoppyAddToPouchEvent(Integer.parseInt(poppyPouchAddMatcher.group(1))));
            return;
        }

        val poppyPouchRemoveMatcher = POPPY_REMOVE_FROM_POUCH.getPattern().matcher(message);
        if (poppyPouchRemoveMatcher.matches()) {
            fireEvent(new PoppyRemoveFromPouchEvent(Integer.parseInt(poppyPouchRemoveMatcher.group(1))));
            return;
        }

        val poppyMessageMatcher = POPPY_MESSAGE.getPattern().matcher(message);
        if (!poppyMessageMatcher.matches()) {
            return;
        }

        val addedAmount = Integer.parseInt(poppyMessageMatcher.group(1));
        val totalAmount = Integer.parseInt(poppyMessageMatcher.group(2));
        fireEvent(new PoppyReceiveEvent(addedAmount, totalAmount));

        val shouldShowPoppyMessage = addon.configuration().poppyWidgetSubConfig().getShowPoppyMessagesInChat();
        event.setCancelled(!shouldShowPoppyMessage.get());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onMohnReceive(final PoppyReceiveEvent event) {
        this.poppyCount = event.total();
        this.poppyEntries.add(new PoppyEntry(System.currentTimeMillis(), event.addedAmount()));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPoppyAddToPouch(final PoppyAddToPouchEvent event) {
        if(this.poppyCount == UNKNOWN_POPPY_COUNT) {
            this.poppyCount = 0;
        }

        val newAmount = this.poppyCount - event.amount();
        this.poppyCount = Math.max(0, newAmount);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPoppyRemoveFromPouch(final PoppyRemoveFromPouchEvent event) {
        if(this.poppyCount == UNKNOWN_POPPY_COUNT) {
            this.poppyCount = 0;
        }

        val newAmount = this.poppyCount + event.amount();
        this.poppyCount = Math.max(0, newAmount);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onServerJoin(final AddonServerJoinEvent event) {
        if (this.poppyLine != null) {
            this.poppyLine.setState(event.isGR() ? VISIBLE : HIDDEN);
        }
        if (this.poppiesPerMinuteLine != null) {
            this.poppiesPerMinuteLine.setState(event.isGR() ? (this.config.showPoppyPerMinute().get() ? VISIBLE : DISABLED) : DISABLED);
        }
    }

}
