package eu.germanrp.addon.core.widget.category;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import org.jetbrains.annotations.NotNull;

public class GermanRPAddonWidgetCategory extends HudWidgetCategory {

    public GermanRPAddonWidgetCategory() {
        super("germanrpaddon");
    }

    @Override
    public @NotNull Component title() {
        return Component.translatable("germanrpaddon.widget.category.title");
    }

    @Override
    public @NotNull Component description() {
        return Component.translatable("germanrpaddon.widget.category.description");
    }
}
