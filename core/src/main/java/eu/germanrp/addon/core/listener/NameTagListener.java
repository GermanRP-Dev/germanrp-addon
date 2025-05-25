package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.Enum.FactionName;
import eu.germanrp.addon.core.Enum.FactionName.FactionType;
import eu.germanrp.addon.core.Enum.NameTag;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.NameTagSubConfig;
import eu.germanrp.addon.core.common.AddonVariables;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

import java.util.List;

public class NameTagListener {

    private final GermanRPAddon addon;

    private final NameTagSubConfig nameTagSubConfig;
    private final AddonVariables addonVariables;

    public NameTagListener(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
        this.addonVariables = this.addon.getVariables();
        this.nameTagSubConfig = addon.configuration().NameTagSubConfig();
    }
    @Subscribe
    public void onNameTag(PlayerNameTagRenderEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            return;
        }
        FactionName factionName = this.addon.getPlayer().getPlayerFactionName();

        NetworkPlayerInfo playerInfo = event.getPlayerInfo();
        if (playerInfo == null) {
            return;
        }
        if (factionName == null){
            return;
        }
        if (factionName.getType() == FactionType.NEUTRAL) {
            return;
        }

        String playerName = playerInfo.profile().getUsername();
        ScoreboardTeam team = playerInfo.getTeam();

        if (team == null) {
            return;
        }

        String prefix = team.getPrefix().toString();
        if (prefix.contains("red") || prefix.contains("dark_red") || prefix.contains("dark_aqua")
                || prefix.contains("✝")) {
            return;
        }

        if (this.addonVariables.getMembers()!= null) {
            List<String> memberlist = this.addonVariables.getMembers();
            NameTag factionTag = nameTagSubConfig.factionColor().get();

            if (memberlist.contains(playerName) && factionTag != NameTag.NONE) {
                event.setNameTag(team.getPrefix().append(Component.text(playerInfo.profile().getUsername())).append(team.getSuffix()).color(factionTag.getTextColor()));
                return;
            }
        }

        switch (factionName.getType()) {
            case BADFRAK -> {
                if (this.addonVariables.getBounties() != null) {
                    List<String> bountylist = this.addonVariables.getBounties();
                    NameTag bountyTag = nameTagSubConfig.bountyColor().get();

                    if (bountylist.contains(playerName) && bountyTag != NameTag.NONE) {
                        event.setNameTag(team.getPrefix().append(Component.text(playerInfo.profile().getUsername())).append(team.getSuffix()).color(bountyTag.getTextColor()));

                        return;
                    }
                }

                if (this.addonVariables.getDarklist()!= null) {
                    List<String> darklist = this.addonVariables.getDarklist();
                    NameTag darklisttag = nameTagSubConfig.darklistColor().get();

                    if (darklist.contains(playerName) && darklisttag != NameTag.NONE) {
                        event.setNameTag(team.getPrefix().append(Component.text(playerInfo.profile().getUsername())).append(team.getSuffix()).color(darklisttag.getTextColor()));
                    }
                }
            }

            case STAAT -> {
                if (this.addonVariables.getWantedPlayers() == null) {
                    return;
                }
                List<String> wantedList = this.addonVariables.getWantedPlayers();
                NameTag wantedColor = nameTagSubConfig.wantedColor().get();

                if (wantedList.contains(playerName) && wantedColor != NameTag.NONE) {
                    event.setNameTag(team.getPrefix().append(Component.text(playerInfo.profile().getUsername())).append(team.getSuffix()).color(wantedColor.getTextColor()));
                }
            }
        }
    }
}
