package eu.germanrp.addon.core.migration;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.GermanRPAddonConfiguration;
import eu.germanrp.addon.core.config.VehicleHotkeyConfig;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.config.ConfigurationVersionUpdateEvent;

public final class ConfigurationMigrator {

    @Subscribe
    @SuppressWarnings("unused")
    public void onConfigVersionUpdateEvent(ConfigurationVersionUpdateEvent event) {
        final Class<? extends Config> configClass = event.getConfigClass();


        if(configClass == VehicleHotkeyConfig.class) {
            migrateVehicleConfig(event);
        } else if (configClass == GermanRPAddonConfiguration.class) {
            migrateAddonConfig(event);
        }
    }

    private void migrateAddonConfig(ConfigurationVersionUpdateEvent event) {
        final int usedVersion = event.getUsedVersion();

        if(usedVersion == 1) {
            final JsonObject config = event.getJsonObject();

            final String oldNameTagSubConfig = "NameTagSubConfig";
            if(config.has(oldNameTagSubConfig)) {
                final String newNameTagSubConfig = "nameTagSubConfig";
                config.add(newNameTagSubConfig, config.get(oldNameTagSubConfig));
                config.remove(oldNameTagSubConfig);
            }

            event.setJsonObject(config);
        }
    }

    private void migrateVehicleConfig(final ConfigurationVersionUpdateEvent event) {
        final int usedVersion = event.getUsedVersion();

        if(usedVersion == 1) {
            final JsonObject config = event.getJsonObject();

            if(config.has("toggleCruiseControl")) {
                config.remove("toggleCruiseControl");
            }

            event.setJsonObject(config);
        }
    }

}
