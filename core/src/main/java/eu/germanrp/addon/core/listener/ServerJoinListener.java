package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.FactionName;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.AddonPlayer;
import eu.germanrp.addon.core.common.AddonVariables;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerJoinEvent;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;

public class ServerJoinListener {

    private final GermanRPAddon addon;
    private final AddonPlayer player;
    private final AddonVariables addonVariables;
    private boolean forward;

    public ServerJoinListener(GermanRPAddon addon) {
        this.addon = addon;
        this.player = this.addon.getPlayer();
        this.addonVariables = this.addon.getVariables();
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) throws InterruptedException {
        if (!this.addon.getUtilService().isGermanRP()) {
            return;
        }

        Laby.fireEvent(new JustJoinedEvent(true));

        Laby.references().chatExecutor().chat("/stats", false);

    }
    public void onFactionNameGet(){
        FactionName factionName = player.getPlayerFactionName();
        if(factionName.equals(FactionName.NONE)){
            return;
        }
        this.addonVariables.getMembers().clear();
        Laby.references().chatExecutor()
                .chat(String.format("/memberinfo %s", factionName.getMemberInfoCommandArg()), false);

        switch (factionName.getType()) {
            case BADFRAK -> {
                this.addonVariables.getDarklist().clear();
                this.addonVariables.getBounties().clear();
                Laby.references().chatExecutor().chat("/darklist", false);
                Laby.references().chatExecutor().chat("/kopfgelder", false);
            }
            case STAAT -> {
                this.addonVariables.getWantedPlayers().clear();
                Laby.references().chatExecutor().chat("/wanteds", false);
            }
        }
    }
}
