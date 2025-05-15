package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import org.jetbrains.annotations.NotNull;

import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND_3;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND_30;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND_5;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.TICK;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.TICK_5;
import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.event.Phase.POST;

public class EventRegistrationListener {

    private final GermanRPAddon addon;

    private long currentTick = 0;

    public EventRegistrationListener(final @NotNull GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onGameTick(GameTickEvent event) {
        if (event.phase().equals(POST)) {
            this.currentTick++;

            labyAPI().eventBus().fire(new GermanRPAddonTickEvent(TICK));

            // 0,25 SECONDS
            if (this.currentTick % 5 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(TICK_5));
            }

            // 1 SECOND
            if (this.currentTick % 20 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND));
            }

            // 3 SECONDS
            if (this.currentTick % 60 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND_3));
            }

            // 5 SECONDS
            if (this.currentTick % 100 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND_5));
            }

            // 30 SECONDS
            if (this.currentTick % 600 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND_30));
            }

            // 1 MINUTE
            if (this.currentTick % 1200 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(GermanRPAddonTickEvent.Phase.MINUTE));
            }
        }
    }
}

