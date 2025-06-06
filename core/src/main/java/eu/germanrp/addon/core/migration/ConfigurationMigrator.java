package eu.germanrp.addon.core.migration;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.NameTagSubConfig;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import net.labymod.api.client.component.ComponentService;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.config.ConfigurationVersionUpdateEvent;
import net.labymod.api.util.Color;

import java.util.HashMap;
import java.util.Map;

public final class ConfigurationMigrator {

    @Subscribe
    @SuppressWarnings("unused")
    public void onConfigVersionUpdateEvent(ConfigurationVersionUpdateEvent event) {
        final Class<? extends Config> configClass = event.getConfigClass();
        // TODO: Add config classes here and migrate them
    }

    private void migrateNameTagConfig(ConfigurationVersionUpdateEvent event) {
        final int usedVersion = event.getUsedVersion();

        if (usedVersion == 1) {
            final JsonObject config = event.getJsonObject();

            final String factionColorKey = "factionColor";
            final String bountyColorKey = "bountyColor";
            final String darklistColorKey = "darklistColor";
            final String wantedColorKey = "wantedColor";

            if (config.has(factionColorKey)) {
                migrateConfig(config, factionColorKey);
            } else if (config.has(bountyColorKey)) {
                migrateConfig(config, bountyColorKey);
            } else if (config.has(darklistColorKey)) {
                migrateConfig(config, darklistColorKey);
            } else if (config.has(wantedColorKey)) {
                migrateConfig(config, wantedColorKey);
            }

            event.setJsonObject(config);
        }
    }

    private static void migrateConfig(JsonObject config, String colorKey) {
        final Map<String, Color> migrationMap = new HashMap<>();
        migrationMap.put("NONE", ComponentService.parseTextColor("black").color());
        migrationMap.put("BLACK", ComponentService.parseTextColor("black").color());
        migrationMap.put("DARKBLUE", ComponentService.parseTextColor("dark_blue").color());
        migrationMap.put("DARKGREEN", ComponentService.parseTextColor("dark_green").color());
        migrationMap.put("DARKPURPLE", ComponentService.parseTextColor("dark_purple").color());
        migrationMap.put("GOLD", ComponentService.parseTextColor("gold").color());
        migrationMap.put("GRAY", ComponentService.parseTextColor("gray").color());
        migrationMap.put("DARKGRAY", ComponentService.parseTextColor("dark_gray").color());
        migrationMap.put("BLUE", ComponentService.parseTextColor("blue").color());
        migrationMap.put("GREEN", ComponentService.parseTextColor("green").color());
        migrationMap.put("AQUA", ComponentService.parseTextColor("aqua").color());
        migrationMap.put("LIGHTPURPLE", ComponentService.parseTextColor("light_purple").color());
        migrationMap.put("YELLOW", ComponentService.parseTextColor("yellow").color());
        migrationMap.put("WHITE", ComponentService.parseTextColor("white").color());
        final String factionColor = config.get(colorKey).getAsString();
        config.addProperty(colorKey, migrationMap.get(factionColor).getValue());
        config.addProperty(colorKey + "Enabled", !factionColor.equals("NONE"));
    }

    private void migrateVehicleConfig(final ConfigurationVersionUpdateEvent event) {
        final int usedVersion = event.getUsedVersion();

        if (usedVersion == 1) {
            final JsonObject config = event.getJsonObject();

            if (config.has("toggleCruiseControl")) {
                config.remove("toggleCruiseControl");
            }

            event.setJsonObject(config);
        }
    }
}
