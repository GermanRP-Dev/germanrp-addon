package eu.germanrp.addon.core.migration;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.config.ConfigurationVersionUpdateEvent;

public final class ConfigurationMigrator {

    @Subscribe
    @SuppressWarnings("unused")
    public void onConfigVersionUpdateEvent(ConfigurationVersionUpdateEvent event) {
        final Class<? extends Config> configClass = event.getConfigClass();


        if (configClass == VehicleHotkeyConfig.class) {
            migrateVehicleConfig(event);
        }

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
