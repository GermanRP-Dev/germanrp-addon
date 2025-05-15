package eu.germanrp.addon.core;

import net.labymod.api.client.resources.ResourceLocation;

public final class Utils {

    private Utils() {
        // Utility class
    }

    public static boolean isLegacyAddonPacket(ResourceLocation eventIdentifier) {
        return eventIdentifier.getNamespace().equals("labymod3")
                && eventIdentifier.getPath().equals("main");
    }
}
