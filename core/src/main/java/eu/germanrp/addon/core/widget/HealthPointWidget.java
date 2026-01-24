package eu.germanrp.addon.core.widget;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gfx.pipeline.renderer.text.TextRenderingOptions;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.client.render.font.RenderableComponent;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.configuration.loader.property.ConfigProperty;

import static net.labymod.api.Laby.labyAPI;

public class HealthPointWidget extends SimpleHudWidget<HealthPointWidget.HealthPointWidgetConfig> {

    public static class HealthPointWidgetConfig extends TextHudWidgetConfig {

        @Getter
        @Accessors(fluent = true)
        @DropdownWidget.DropdownSetting
        private final ConfigProperty<HealthUnit> unit = new ConfigProperty<>(HealthUnit.HEARTS);

    }

    public enum HealthUnit {
        HP,
        HEARTS
    }

    private static final Icon fullHeart = Icon.texture(ResourceLocation.create("minecraft", "textures/gui/sprites/hud/heart/full.png"));
    private static final Icon heartContainer = Icon.texture(ResourceLocation.create("minecraft", "textures/gui/sprites/hud/heart/container.png"));

    public HealthPointWidget(final HudWidgetCategory category) {
        super("health", HealthPointWidgetConfig.class);
        this.bindCategory(category);
        this.setIcon(fullHeart);
    }

    @Override
    public boolean isVisibleInGame() {
        return labyAPI().minecraft().getClientPlayer() != null;
    }

    @Override
    public void render(
            final RenderPhase renderPhase,
            final ScreenContext screenContext,
            final boolean isEditorContext,
            final HudSize size
    ) {
        if(isEditorContext) {
            var health = 20.0f;

            if(this.config.unit().get() == HealthUnit.HEARTS) {
                health /= 2.0f;
            }

            renderHeartDisplay(renderPhase, screenContext, size, health);
            return;
        }

        val clientPlayer = labyAPI().minecraft().getClientPlayer();

        if(clientPlayer == null) {
            return;
        }

        var health = clientPlayer.getHealth();

        if(this.config.unit().get() == HealthUnit.HEARTS) {
            health /= 2.0f;
        }

        renderHeartDisplay(renderPhase, screenContext, size, health);
    }

    private static void renderHeartDisplay(
            final RenderPhase renderPhase,
            final ScreenContext screenContext,
            final HudSize size,
            final float health
    ) {
        val healthComponent = Component.text("%.0f".formatted(health));
        val renderableComponent = RenderableComponent.of(healthComponent);

        val iconSize = 8.0F;
        val margin = 2.0F;

        if (renderPhase.canRender()) {
            screenContext.canvas().submitIcon(heartContainer, 0, 0, iconSize, iconSize);
            screenContext.canvas().submitIcon(fullHeart, 0, 0, iconSize, iconSize);
            screenContext.canvas().submitRenderableComponent(
                    renderableComponent,
                    iconSize + margin,
                    (iconSize - renderableComponent.getHeight()) / 2,
                    -1,
                    TextRenderingOptions.SHADOW
            );
        }

        size.setWidth(iconSize + margin + renderableComponent.getWidth());
        size.setHeight(iconSize);
    }

}
