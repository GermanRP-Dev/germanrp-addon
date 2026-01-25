package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.events.network.HydrationUpdateEvent;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.event.Subscribe;

public class HydrationListener {

    private final GermanRPAddon addon;

    public HydrationListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onHydrationUpdate(HydrationUpdateEvent event) {
        this.addon.getPlayer().setHydration(event.getAmount());
    }

}
