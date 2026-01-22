package eu.germanrp.addon.core.widget;

import lombok.val;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gfx.pipeline.renderer.text.TextRenderingOptions;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.font.RenderableComponent;
import net.labymod.api.client.resources.ResourceLocation;

import static net.labymod.api.Laby.labyAPI;

public class HealthPointWidget extends SimpleHudWidget<HudWidgetConfig> {

    private static final Icon fullHeart = Icon.texture(ResourceLocation.create("minecraft", "textures/gui/sprites/hud/heart/full.png"));
    private static final Icon heartContainer = Icon.texture(ResourceLocation.create("minecraft", "textures/gui/sprites/hud/heart/container.png"));

    public HealthPointWidget(final HudWidgetCategory category) {
        super("health", HudWidgetConfig.class);
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
            renderHeartDisplay(renderPhase, screenContext, size, 20.0f);
            return;
        }

        val clientPlayer = labyAPI().minecraft().getClientPlayer();

        if(clientPlayer == null) {
            return;
        }

        renderHeartDisplay(renderPhase, screenContext, size, clientPlayer.getHealth());
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
