package eu.germanrp.addon.core.widget;

import eu.germanrp.addon.api.models.Plant;
import eu.germanrp.addon.api.models.PlantFactory;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.core.executor.PlaySoundExecutor;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;

@SuppressWarnings("java:S110")
public class HeilkrautpflanzeHudWidget extends PlantHudWidget {

    private static final Plant DUMMY_PLANT = PlantFactory.createPlant(
            PlantType.HEILKRAUTPFLANZE,
            true,
            20,
            5
    );

    public HeilkrautpflanzeHudWidget(HudWidgetCategory category, Icon icon, PlaySoundExecutor playSoundExecutor) {
        super("heilkrautpflanze", category, icon, playSoundExecutor);
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
