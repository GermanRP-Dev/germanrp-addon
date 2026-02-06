package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantRose;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;

import static eu.germanrp.addon.api.models.PlantType.ROSE;

public class RoseHudWidget extends PlantHudWidget {

    private static final Plant DUMMY_PLANT = new PlantRose(true, 3, 3);

    public RoseHudWidget(HudWidgetCategory category, Icon icon, GermanRPAddon addon) {
        super("rose", category, icon, addon);
    }

    @Override
    public Plant getDummyPlant() {
        return DUMMY_PLANT;
    }

    @Override
    public PlantType getPlantType() {
        return ROSE;
    }
}
