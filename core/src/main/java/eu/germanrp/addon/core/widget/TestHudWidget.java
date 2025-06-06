package eu.germanrp.addon.core.widget;

import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.render.matrix.Stack;

public class TestHudWidget extends SimpleHudWidget<TextHudWidgetConfig> {

    protected TestHudWidget() {
        super("test", TextHudWidgetConfig.class);
    }

    @Override
    public void render(Stack stack, MutableMouse mouse, float partialTicks, boolean isEditorContext, HudSize size) {

    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

}
