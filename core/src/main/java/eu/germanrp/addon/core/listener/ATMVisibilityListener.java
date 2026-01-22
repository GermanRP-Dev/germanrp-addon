package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import net.labymod.api.event.Subscribe;

public class ATMVisibilityListener {

    private final GermanRPAddon addon;

    public ATMVisibilityListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onTick(GermanRPAddonTickEvent event) {
        if (event.isPhase(GermanRPAddonTickEvent.Phase.SECOND)) {
            this.addon.getPoiService().checkCooldowns();
        }
    }

}
