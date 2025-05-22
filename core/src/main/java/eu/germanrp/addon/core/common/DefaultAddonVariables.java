package eu.germanrp.addon.core.common;

import eu.germanrp.addon.core.GermanRPAddon;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DefaultAddonVariables implements AddonVariables{

    @Getter private final List<String> members = new ArrayList<>();
    @Getter private final List<String> darklist = new ArrayList<>();
    @Getter  private final List<String> bounties = new ArrayList<>();
    @Getter private final List<String> wantedPlayers = new ArrayList<>();
    private final GermanRPAddon addon;
    private boolean justJoined;

    public DefaultAddonVariables(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
    }

}
