package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.Enum.FactionName;
import eu.germanrp.addon.core.Enum.FactionName.FactionType;
import eu.germanrp.addon.core.Enum.NameTag;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.NameTagSubConfig;
import eu.germanrp.addon.core.common.AddonVariables;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextDecoration;
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
    @SuppressWarnings("unused")
    public void onNameTag(PlayerNameTagRenderEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            return;
        }

        FactionName factionName = this.addon.getPlayer().getPlayerFactionName();

        NetworkPlayerInfo playerInfo = event.getPlayerInfo();

        if (playerInfo == null || factionName == null || factionName.getType() == FactionType.NEUTRAL) {
            return;
        }

        String playerName = playerInfo.profile().getUsername();
        ScoreboardTeam team = playerInfo.getTeam();

        if (team == null) {
            return;
        }

        Component prefix = team.getPrefix().copy();

        if (prefix.toString().contains("red") || prefix.toString().contains("dark_red") || prefix.toString()
                .contains("dark_aqua") || prefix.toString().contains("✝")) {
            return;
        }

        boolean isAFK = event.nameTag().toString().contains("italic");

        if (this.addonVariables.getMembers() != null) {
            List<String> memberlist = this.addonVariables.getMembers();
            NameTag factionTag = nameTagSubConfig.factionColor().get();

            if (changeNameTag(event, playerName, team, prefix, isAFK, memberlist, factionTag)) return;
        }

        switch (factionName.getType()) {
            case BADFRAK -> {
                if (this.addonVariables.getBounties() != null) {
                    List<String> bountylist = this.addonVariables.getBounties();
                    NameTag bountyTag = nameTagSubConfig.bountyColor().get();

                    if (changeNameTag(event, playerName, team, prefix, isAFK, bountylist, bountyTag)) {
                        return;
                    }
                }

                if (this.addonVariables.getDarklist() != null) {
                    List<String> darklist = this.addonVariables.getDarklist();
                    NameTag darklistTag = nameTagSubConfig.darklistColor().get();

                    changeNameTag(event, playerName, team, prefix, isAFK, darklist, darklistTag);
                }

            }

            case STAAT -> {
                if (this.addonVariables.getWantedPlayers() == null) {
                    return;
                }

                List<String> wantedList = this.addonVariables.getWantedPlayers();
                NameTag wantedColor = nameTagSubConfig.wantedColor().get();

                changeNameTag(event, playerName, team, prefix, isAFK, wantedList, wantedColor);
            }

            default -> {
                // Don't do anything for the other types
            }
        }
    }

    private boolean changeNameTag(
            PlayerNameTagRenderEvent event,
            String playerName,
            ScoreboardTeam team,
            Component prefix,
            boolean isAFK,
            List<String> list,
            NameTag tag
    ) {
        if (!list.contains(playerName) || tag == NameTag.NONE) {
            return false;
        }

        Component nameTag =
                prefix.append(Component.text(playerName)).append(team.getSuffix()).color(tag.getTextColor());
        if (isAFK) {
            nameTag = nameTag.decorate(TextDecoration.ITALIC);
        }

        event.setNameTag(nameTag);
        return true;
    }
}