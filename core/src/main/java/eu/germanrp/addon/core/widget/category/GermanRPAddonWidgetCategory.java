package eu.germanrp.addon.core.widget.category;

import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import org.jetbrains.annotations.NotNull;

import static net.labymod.api.client.component.Component.translatable;

public class GermanRPAddonWidgetCategory extends HudWidgetCategory {

    public GermanRPAddonWidgetCategory() {
        super(GermanRPAddon.NAMESPACE);
    }

    @Override
    public @NotNull Component title() {
        return translatable(GermanRPAddon.NAMESPACE + ".widget.category.title");
    }

    @Override
    public @NotNull Component description() {
        return translatable(GermanRPAddon.NAMESPACE + ".widget.category.description");
    }
}
