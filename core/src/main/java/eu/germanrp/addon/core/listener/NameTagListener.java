package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.api.models.Faction.FactionType;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.NameTagSubConfig;
import eu.germanrp.addon.core.services.NameTagService;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

import java.util.Arrays;

public class NameTagListener {
    private static final String[] IGNORED_PREFIXES = {
            "red", "dark_red", "dark_aqua", "✝"
    };

    private final GermanRPAddon addon;

    private final NameTagSubConfig nameTagSubConfig;

    public NameTagListener(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
        this.nameTagSubConfig = addon.configuration().nameTagSubConfig();
    }

    @Subscribe
    public void onNameTag(PlayerNameTagRenderEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            return;
        }

        final Faction faction = this.addon.getPlayer().getPlayerFactionName();
        final NetworkPlayerInfo playerInfo = event.getPlayerInfo();

        if (playerInfo == null || faction == null || faction.getType() == FactionType.NEUTRAL) {
            return;
        }

        final String playerName = playerInfo.profile().getUsername();
        final ScoreboardTeam team = playerInfo.getTeam();

        if (team == null) {
            return;
        }

        final String prefix = team.getPrefix().toString();
        if(isIgnoredPrefix(prefix)) {
            return;
        }

        renderNameTag(event, playerName, faction);
    }

    private void renderNameTag(PlayerNameTagRenderEvent event, String playerName, Faction faction) {
        final NameTagService nameTagService = this.addon.getNameTagService();
        final boolean isAFK = event.nameTag().toString().contains("italic");

        if (Boolean.TRUE.equals(nameTagSubConfig.factionColorEnabled().get())
                && nameTagService.getMembers().contains(playerName)
        ) {
            final TextColor color = TextColor.color(nameTagSubConfig.factionColor().get().get());
            renderNameTag(event, isAFK, color);
            return;
        }

        if (faction.getType() == FactionType.BADFRAK) {

            if (Boolean.TRUE.equals(nameTagSubConfig.bountyColorEnabled().get())
                    && nameTagService.getBounties().contains(playerName)) {
                final TextColor color = TextColor.color(nameTagSubConfig.bountyColor().get().get());
                renderNameTag(event, isAFK, color);
                return;
            }

            if (Boolean.TRUE.equals(nameTagSubConfig.darklistColorEnabled().get())
                    && nameTagService.getBounties().contains(playerName)) {
                final TextColor color = TextColor.color(nameTagSubConfig.darklistColor().get().get());
                renderNameTag(event, isAFK, color);
            }

        } else if (faction.getType() == FactionType.STAAT
                && Boolean.TRUE.equals(nameTagSubConfig.wantedColorEnabled().get())
                && nameTagService.getBounties().contains(playerName)) {
            final TextColor color = TextColor.color(nameTagSubConfig.wantedColor().get().get());
            renderNameTag(event, isAFK, color);
        }
    }

    private void renderNameTag(final PlayerNameTagRenderEvent event, final boolean isAFK, final TextColor color) {
        final Component component = event.nameTag();

        component.color(color);
        component.getChildren().forEach(child -> child.color(color));

        if (isAFK) {
            component.decorate(TextDecoration.ITALIC);
        }

        event.setNameTag(component);
    }

    private boolean isIgnoredPrefix(final String prefix) {
        return Arrays.stream(IGNORED_PREFIXES).anyMatch(prefix::contains);
    }

}
