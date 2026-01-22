package eu.germanrp.addon.core.services;

import eu.germanrp.addon.api.events.atm.ATMOffCooldownEvent;
import eu.germanrp.addon.core.ATMHelper;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.serverapi.model.ATM;
import lombok.val;
import net.labymod.addons.waypoints.WaypointService;
import net.labymod.addons.waypoints.Waypoints;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.Color;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;
import static net.labymod.api.Laby.fireEvent;

public class POIService {

    public static final String ATM_ID_PREFIX = "ATM";

    private final WaypointService waypointService = Waypoints.references().waypointService();
    private final GermanRPAddon addon;
    private final Map<String, ATM> atmMap = new HashMap<>();
    private final Map<String, Instant> cooldownAtms = new HashMap<>();
    private final PriorityBlockingQueue<CooldownEntry> cooldownQueue = new PriorityBlockingQueue<>(11, Comparator.comparing(CooldownEntry::expiry));

    public POIService(final GermanRPAddon addon) {
        this.addon = addon;
        val atmConfig = addon.configuration().atmConfig();
        atmConfig.showATMWaypoints().addChangeListener(this::showOrHideATMs);
        atmConfig.atmWaypointColor().addChangeListener(this::updateATMColor);
        atmConfig.hideDamagedATMs().addChangeListener(show -> this.refreshATMs());
    }

    public synchronized void addOrUpdateATMs(final Set<ATM> atms) {
        val now = Instant.now();
        for (val atm : atms) {
            this.internalAddOrUpdateATM(atm, now);
        }

        waypointService.refresh();
    }

    public synchronized void addATM(final ATM atm) {
        this.internalAddOrUpdateATM(atm, Instant.now());
        waypointService.refresh();
    }

    public synchronized void updateATM(final String id, final ATM atm) {
        if (!this.atmMap.containsKey(id)) {
            return;
        }

        this.atmMap.remove(id);
        this.internalAddOrUpdateATM(atm, Instant.now());
        waypointService.refresh();
    }

    public synchronized void removeATM(final String id) {
        this.atmMap.remove(id);
        this.cooldownAtms.remove(id);
        this.cooldownQueue.removeIf(entry -> entry.id().equals(id));
        waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + id));
        waypointService.refresh();
    }

    private void internalAddOrUpdateATM(final ATM atm, final Instant now) {
        this.atmMap.put(atm.id(), atm);
        val cooldown = Instant.ofEpochMilli(atm.cooldownTimestamp());
        if (cooldown.isAfter(now)) {
            this.cooldownAtms.put(atm.id(), cooldown);
            this.cooldownQueue.add(new CooldownEntry(atm.id(), cooldown));
        } else {
            this.cooldownAtms.remove(atm.id());
        }
        waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
        ATMHelper.toWaypointMeta(atm).ifPresent(waypointService::add);
    }

    public synchronized void checkCooldowns() {
        if (this.cooldownQueue.isEmpty()) {
            return;
        }

        val now = Instant.now();
        while (!this.cooldownQueue.isEmpty()) {
            val entry = this.cooldownQueue.peek();
            val currentCooldown = this.cooldownAtms.get(entry.id());

            // Check if the entry is stale
            if (currentCooldown == null || entry.expiry().isBefore(currentCooldown)) {
                this.cooldownQueue.poll();
            } else if (now.isAfter(entry.expiry())) {
                // If it's not stale and expired, fire the event
                this.cooldownQueue.poll();
                this.cooldownAtms.remove(entry.id());
                fireEvent(new ATMOffCooldownEvent(entry.id()));
            } else {
                // Entry is not stale and not expired, so we can stop
                break;
            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public synchronized void onATMCooldown(final ATMOffCooldownEvent event) {
        val atm = this.atmMap.get(event.atmId());
        if (atm != null) {
            waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
            ATMHelper.toWaypointMeta(atm).ifPresent(waypointService::add);
            waypointService.refresh();
        }
    }

    public synchronized void refreshATMs() {
        for (val atm : this.atmMap.values()) {
            waypointService.remove(waypoint -> waypoint.meta().getIdentifier().equals(ATM_ID_PREFIX + atm.id()));
            ATMHelper.toWaypointMeta(atm).ifPresent(waypointService::add);
        }
        waypointService.refresh();
    }

    private synchronized void showOrHideATMs(final boolean show) {
        val message = show
                ? Component.translatable(NAMESPACE + ".message.atm.showWaypoints")
                : Component.translatable(NAMESPACE + ".message.atm.hideWaypoints");

        this.addon.getPlayer().sendInfoMessage(message);

        this.refreshATMs();
    }

    private synchronized void updateATMColor(final Color color) {
        for (val waypoint : waypointService.getAll()) {
            val meta = waypoint.meta();
            if (meta.getIdentifier().startsWith(ATM_ID_PREFIX)) {
                meta.setColor(color);
            }
        }
    }

    private record CooldownEntry(String id, Instant expiry) {
    }

}
