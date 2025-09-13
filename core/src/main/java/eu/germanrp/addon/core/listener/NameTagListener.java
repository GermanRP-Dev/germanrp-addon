package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.api.models.Faction.Type;
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

public class NameTagListener {

    private static final String[] IGNORED_PREFIXES = {
            "red", "✝"
    };

    private final GermanRPAddon addon;

    private final NameTagSubConfig nameTagSubConfig;

    public NameTagListener(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
        this.nameTagSubConfig = this.addon.configuration().nameTagSubConfig();
    }

    @Subscribe
    public void onNameTag(PlayerNameTagRenderEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            return;
        }

        final Faction faction = this.addon.getPlayer().getPlayerFaction();
        final NetworkPlayerInfo playerInfo = event.getPlayerInfo();

        if (playerInfo == null || faction == null) {
            return;
        }

        final String playerName = playerInfo.profile().getUsername();
        final ScoreboardTeam team = playerInfo.getTeam();

        if (team == null) {
            return;
        }

        if (isIgnoredPrefix(event.nameTag().getChildren().getFirst().toString())) {
            return;
        }

        processNameTagRendering(event, playerName, faction);
    }

    private void processNameTagRendering(PlayerNameTagRenderEvent event, String playerName, Faction faction) {
        final NameTagService nameTagService = this.addon.getNameTagService();
        final boolean isAFK = event.nameTag().toString().contains("italic");

        if (this.nameTagSubConfig.factionColorEnabled().get() && nameTagService.getMembers().contains(playerName)) {
            final TextColor color = TextColor.color(this.nameTagSubConfig.factionColor().get().get());
            renderNameTag(event, isAFK, color);
            return;
        }

        if (faction.getType() == Type.CRIME) {
            if (this.nameTagSubConfig.bountyColorEnabled().get() && nameTagService.getBounties().contains(playerName)) {
                final TextColor color = TextColor.color(this.nameTagSubConfig.bountyColor().get().get());
                renderNameTag(event, isAFK, color);
                return;
            }

            if (this.nameTagSubConfig.darklistColorEnabled().get() && nameTagService.getDarklist().contains(playerName)) {
                final TextColor color = TextColor.color(this.nameTagSubConfig.darklistColor().get().get());
                renderNameTag(event, isAFK, color);
                return;
            }
        } else if (faction.getType() == Type.STAAT && this.nameTagSubConfig.wantedColorEnabled().get() && nameTagService.getWantedPlayers().contains(playerName)) {
            final TextColor color = TextColor.color(this.nameTagSubConfig.wantedColor().get().get());
            renderNameTag(event, isAFK, color);
            return;
        }

        assert event.getPlayerInfo() != null;
        if(event.getPlayerInfo().getTeam().getPrefix().toString().contains("GR")) {

            if (event.context().equals(PlayerNameTagRenderEvent.Context.PLAYER_RENDER)) {
                renderNameTag(event, isAFK, event.nameTag().getColor());
            }
        }

    }

    private static void renderNameTag(
            final PlayerNameTagRenderEvent event,
            final boolean isAFK,
            final TextColor color
    ) {
        final Component component = event.nameTag();

        component.color(color);
        component.getChildren().forEach(child -> child.color(color));

        if (isAFK) {
            component.decorate(TextDecoration.ITALIC);

        }


        event.setNameTag(component);
    }

    private static boolean isIgnoredPrefix(final String prefix) {
        for(String string : IGNORED_PREFIXES){
            if (prefix.contains(string)) return true;
        }
        return false;
    }
}
