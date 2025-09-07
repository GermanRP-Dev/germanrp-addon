package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

import static net.labymod.api.Laby.fireEvent;

public class ServerJoinListener {

    private final GermanRPAddon addon;


    public ServerJoinListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            fireEvent(new JustJoinedEvent(false));
            return;
        }
        this.addon.getChatListener().setEmptyMessages(0);
        this.addon.getPlayer().setPlayerFaction(null);

        fireEvent(new JustJoinedEvent(true));

        this.addon.getPlayer().sendServerMessage("/stats");
    }

    public void onFactionNameGet() {
        final Faction faction = this.addon.getPlayer().getPlayerFaction();
        if (faction == Faction.NONE) {
            return;
        }

        this.addon.getNameTagService().getMembers().clear();
        this.addon.getPlayer().sendServerMessage(String.format("/memberinfo %s", faction.getMemberInfoCommandArg()));

        final Faction.Type type = faction.getType();
        if (type == Faction.Type.CRIME) {
            this.addon.getNameTagService().getDarklist().clear();
            this.addon.getNameTagService().getBounties().clear();
            this.addon.getPlayer().sendServerMessage("/darklist");
            this.addon.getPlayer().sendServerMessage("/kopfgelder");
        } else if (type == Faction.Type.STAAT) {
            this.addon.getNameTagService().getWantedPlayers().clear();
            this.addon.getPlayer().sendServerMessage("/wanteds");
        }
    }
}
