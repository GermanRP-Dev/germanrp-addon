package eu.germanrp.addon.api.models;

import eu.germanrp.addon.api.network.PlantPaket;

public interface PlantPaketReceiver {

    void onPaketReceive(final PlantPaket paket);

    void reset();

    void updatePlant(final Plant plant);
}
