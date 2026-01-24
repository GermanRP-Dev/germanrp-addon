package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.core.common.events.ExplosiveVestFuseActivatedEvent;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.Color;
import net.labymod.api.util.bounds.Rectangle;

public class ExplosiveVestHudWidget extends SimpleHudWidget<ExplosiveVestHudWidget.ExplosiveVestHudWidgetConfig> {

    public static class ExplosiveVestHudWidgetConfig extends TextHudWidgetConfig {

        @Getter
        @Accessors(fluent = true)
        @ColorPickerWidget.ColorPickerSetting
        private final ConfigProperty<Color> fuseBackground = new ConfigProperty<>(Color.BLACK);

        @Getter
        @Accessors(fluent = true)
        @ColorPickerWidget.ColorPickerSetting
        private final ConfigProperty<Color> fuseForeground = new ConfigProperty<>(Color.RED);

    }

    private static final int FUSE_DURATION = 5000;
    private static final float FUSE_HEIGHT = 5f;
    private static final float FUSE_WIDTH = 100f;
    private static final long EDITOR_CYCLE_MS = 5000L;

    /**
     * The current progress of the fuse in milliseconds.
     * {@code -1} indicates that the fuse has not been activated yet,
     * or it has already exploded.
     */
    private int currentTime = -1;
    private int fuseDuration = FUSE_DURATION;

    public ExplosiveVestHudWidget(HudWidgetCategory category) {
        super("explosiveVest", ExplosiveVestHudWidgetConfig.class);
        this.bindCategory(category);
    }

    @Override
    public void render(RenderPhase phase, ScreenContext context, boolean isEditorContext, HudSize size) {
        val progress = progress(isEditorContext);

        if (isEditorContext) {
            renderFuse(phase, context, progress, size);
            return;
        }

        if (progress == 0) {
            // Hide the fuse if it hasn't been activated yet
            size.set(0f, 0f);
            return;
        }

        renderFuse(phase, context, progress, size);
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onAddonTick(final GermanRPAddonTickEvent event) {
        if (!event.isPhase(GermanRPAddonTickEvent.Phase.TICK)) {
            return;
        }

        if (currentTime == -1) {
            return;
        }

        this.currentTime -= 50;
        if (this.currentTime <= 0) {
            this.currentTime = -1;
        }
    }

    private void renderFuse(RenderPhase phase, ScreenContext context, float progress, HudSize size) {
        val canvas = context.canvas();
        val barWidth = FUSE_WIDTH * progress;

        if (phase.canRender()) {
            canvas.submitRect(Rectangle.relative(0, 0, FUSE_WIDTH, FUSE_HEIGHT), this.config.fuseBackground.get().get());
            canvas.submitRect(Rectangle.relative(0, 0, barWidth, FUSE_HEIGHT), this.config.fuseForeground.get().get());
        }

        size.set(FUSE_WIDTH, FUSE_HEIGHT);
    }

    private float progress(boolean isEditorContext) {
        if (isEditorContext) {
            return editorProgress();
        }

        if (fuseDuration <= 0) {
            return 0f;
        }

        return Math.clamp(currentTime / (float) fuseDuration, 0f, 1f);
    }

    private float editorProgress() {
        double phase = (System.currentTimeMillis() % EDITOR_CYCLE_MS) / (double) EDITOR_CYCLE_MS;
        return (float) (1.0 - phase);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onExplosiveVestFuseActivated(final ExplosiveVestFuseActivatedEvent event) {
        val seconds = Math.max(0, event.seconds());
        this.fuseDuration = seconds * 1000;
        this.currentTime = this.fuseDuration;
    }

}
