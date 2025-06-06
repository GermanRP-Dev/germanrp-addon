package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantHeilkraut;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;

public class HeilkrautpflanzeHudWidget extends PlantHudWidget {

    private static final Plant DUMMY_PLANT = new PlantHeilkraut(true, 20, 5);

    public HeilkrautpflanzeHudWidget(HudWidgetCategory category, Icon icon, PlaySoundExecutor playSoundExecutor, GermanRPAddon addon) {
        super("heilkrautpflanze", category, icon, playSoundExecutor, addon);
    }

    @Override
    public Plant getDummyPlant() {
        return DUMMY_PLANT;
    }

    @Override
    public PlantType getPlantType() {
        return PlantType.HEILKRAUTPFLANZE;
    }
}
