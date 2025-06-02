package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.FactionName;
import eu.germanrp.addon.api.models.FactionName.FactionType;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.NameTagSubConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;
import net.labymod.api.util.Color;

import java.util.List;
import java.util.Optional;

public class NameTagListener {

    private final GermanRPAddon addon;

    private final NameTagSubConfig nameTagSubConfig;

    public NameTagListener(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
        this.nameTagSubConfig = addon.configuration().nameTagSubConfig();
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

        if (this.addon.getNameTagService().getMembers() != null) {
            List<String> memberlist = this.addon.getNameTagService().getMembers();
            Color factionTag = nameTagSubConfig.factionColor().get();
            boolean colorEnabled = nameTagSubConfig.factionColorEnabled().get();

            final Optional<Component> component =
                    changeNameTag(playerName, team, prefix, isAFK, memberlist, factionTag, colorEnabled);
            if (component.isPresent()) {
                event.setNameTag(component.get());
                return;
            }
        }

        switch (factionName.getType()) {
            case BADFRAK -> {
                if (this.addon.getNameTagService().getBounties() != null) {
                    List<String> bountylist = this.addon.getNameTagService().getBounties();
                    Color bountyTag = nameTagSubConfig.bountyColor().get();
                    boolean colorEnabled = nameTagSubConfig.bountyColorEnabled().get();

                    final Optional<Component> component =
                            changeNameTag(playerName, team, prefix, isAFK, bountylist, bountyTag, colorEnabled);
                    if (component.isPresent()) {
                        event.setNameTag(component.get());
                        return;
                    }
                }

                if (this.addon.getNameTagService().getDarklist() != null) {
                    List<String> darklist = this.addon.getNameTagService().getDarklist();
                    Color darklistTag = nameTagSubConfig.darklistColor().get();
                    boolean colorEnabled = nameTagSubConfig.darklistColorEnabled().get();

                    changeNameTag(
                            playerName,
                            team,
                            prefix,
                            isAFK,
                            darklist,
                            darklistTag,
                            colorEnabled
                    ).ifPresent(event::setNameTag);
                }
            }

            case STAAT -> {
                if (this.addon.getNameTagService().getWantedPlayers() == null) {
                    return;
                }

                List<String> wantedList = this.addon.getNameTagService().getWantedPlayers();
                Color wantedColor = nameTagSubConfig.wantedColor().get();
                boolean colorEnabled = nameTagSubConfig.wantedColorEnabled().get();

                changeNameTag(
                        playerName,
                        team,
                        prefix,
                        isAFK,
                        wantedList,
                        wantedColor,
                        colorEnabled
                ).ifPresent(event::setNameTag);
            }

            default -> {
                // Don't do anything for the other types
            }
        }
    }

    private Optional<Component> changeNameTag(
            String playerName,
            ScoreboardTeam team,
            Component prefix,
            boolean isAFK,
            List<String> list,
            Color color,
            boolean colorEnabled
    ) {
        if (!list.contains(playerName) || !colorEnabled) {
            return Optional.empty();
        }

        Component nameTag =
                prefix.append(Component.text(playerName)).append(team.getSuffix()).color(TextColor.color(color.get()));
        if (isAFK) {
            nameTag = nameTag.decorate(TextDecoration.ITALIC);
        }

        return Optional.of(nameTag);
    }
}
