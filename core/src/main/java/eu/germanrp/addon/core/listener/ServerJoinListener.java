package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.AddonServerJoinEvent;
import eu.germanrp.addon.core.common.events.JoinSequenceCompletedEvent;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.val;
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
            fireEvent(new AddonServerJoinEvent(false));
            return;
        }
        this.addon.getChatListener().setEmptyMessages(0);
        this.addon.getPlayer().setPlayerFaction(null);

        fireEvent(new JustJoinedEvent(true));
        fireEvent(new AddonServerJoinEvent(true));
        GermanRPAddon.getInstance().getPlayer().setPlayPanic(false);
        this.addon.getPlayer().sendServerMessage("/stats");
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onJoinSequenceCompleted(final JoinSequenceCompletedEvent event) {
        val addonPlayer = this.addon.getPlayer();
        val addonVersion = this.addon.addonInfo().getVersion();
        addonPlayer.sendInfoMessage("GermanRP-Addon Version %s".formatted(addonVersion));
    }

    public void onFactionNameGet() {
        final Faction faction = this.addon.getPlayer().getPlayerFaction();
        if (faction == null || faction == Faction.NONE) {
            return;
        }

        this.addon.getNameTagService().getMembers().clear();
        this.addon.getJoinWorkflowManager().startTask("memberinfo");
        this.addon.getPlayer().sendServerMessage(String.format("/memberinfo %s", faction.getMemberInfoCommandArg()));

        final Faction.Type type = faction.getType();

        switch (type) {
            case CRIME -> {
                this.addon.getNameTagService().getDarklist().clear();
                this.addon.getNameTagService().getBounties().clear();
                this.addon.getJoinWorkflowManager().startTask("darklist");
                this.addon.getJoinWorkflowManager().startTask("bounties");
                this.addon.getPlayer().sendServerMessage("/darklist");
                this.addon.getPlayer().sendServerMessage("/kopfgelder");
            }

            case STAAT -> {
                this.addon.getNameTagService().getWantedPlayers().clear();
                this.addon.getJoinWorkflowManager().startTask("wanteds");
                this.addon.getPlayer().sendServerMessage("/wanteds");
            }

            default -> {
                this.addon.getJoinWorkflowManager().finishTask("memberinfo");
            }

        }
    }
}
