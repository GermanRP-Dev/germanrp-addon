package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.FactionName;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.AddonPlayer;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

public class ServerJoinListener {

    private final GermanRPAddon addon;
    private final AddonPlayer player;
    private boolean forward;

    public ServerJoinListener(GermanRPAddon addon) {
        this.addon = addon;
        this.player = this.addon.getPlayer();
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            Laby.fireEvent(new JustJoinedEvent(false));
            return;
        }
        this.addon.getChatListener().setEmptyMessages(0);
        this.player.setPlayerFactionName(null);
        Laby.fireEvent(new JustJoinedEvent(true));

        this.addon.getPlayer().sendServerMessage("/stats");
    }

    public void onFactionNameGet() {
        FactionName factionName = player.getPlayerFactionName();
        if (factionName.equals(FactionName.NONE)) {
            return;
        }

        this.addon.getNameTagService().getMembers().clear();
        this.addon.getPlayer().sendServerMessage(String.format("/memberinfo %s", factionName.getMemberInfoCommandArg()));

        switch (factionName.getType()) {
            case BADFRAK -> {
                this.addon.getNameTagService().getDarklist().clear();
                this.addon.getNameTagService().getBounties().clear();
                this.addon.getPlayer().sendServerMessage("/darklist");
                this.addon.getPlayer().sendServerMessage("/kopfgelder");
            }
            case STAAT -> {
                this.addon.getNameTagService().getWantedPlayers().clear();
                this.addon.getPlayer().sendServerMessage("/wanteds");
            }
        }
    }
}
