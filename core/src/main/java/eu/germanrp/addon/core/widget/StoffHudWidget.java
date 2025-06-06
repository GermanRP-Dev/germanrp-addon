package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantStoff;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;

import static eu.germanrp.addon.api.models.PlantType.STOFF;

@SuppressWarnings("java:S110")
public class StoffHudWidget extends PlantHudWidget {

    private static final Plant DUMMY_PLANT = new PlantStoff(true, 1, 5);

    public StoffHudWidget(
            HudWidgetCategory category,
            Icon icon,
            GermanRPAddon addon) {
        super("stoff", category, icon, addon);
    }

    @Override
    public Plant getDummyPlant() {
        return DUMMY_PLANT;
    }

    @Override
    public PlantType getPlantType() {
        return STOFF;
    }
}
