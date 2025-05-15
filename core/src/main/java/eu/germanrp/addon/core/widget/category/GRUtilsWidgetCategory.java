package eu.germanrp.addon.core.widget.category;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import org.jetbrains.annotations.NotNull;

public class GRUtilsWidgetCategory extends HudWidgetCategory {

    public GRUtilsWidgetCategory() {
        super("germanrputils");
    }

    @Override
    public @NotNull Component title() {
        return Component.translatable("germanrputils.widget.category.title");
    }

    @Override
    public @NotNull Component description() {
        return Component.translatable("germanrputils.widget.category.description");
    }
}
