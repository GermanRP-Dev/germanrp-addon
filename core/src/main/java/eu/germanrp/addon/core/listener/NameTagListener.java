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
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;

public class NameTagListener {

    private final GermanRPAddon addon;

    private final NameTagSubConfig nameTagSubConfig;
    private  FactionName factionName;
    private final ServerJoinListener serverJoinListener;
    private final AddonVariables addonVariables;

    public NameTagListener(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
        this.addonVariables = this.addon.getVariables();
        this.nameTagSubConfig = addon.configuration().NameTagSubConfig();
        this.serverJoinListener = addon.getServerJoinListener();
    }

    @Subscribe
    public void onNameTag(PlayerNameTagRenderEvent event) {
        if (!this.addon.getUtilService().isGermanRP()) {
            return;
        }
        this.factionName = this.addon.getPlayer().getPlayerFactionName();

        NetworkPlayerInfo playerInfo = event.getPlayerInfo();
        if (playerInfo == null) {
            return;
        }
        if (this.factionName == null){
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

        String suffix = team.getSuffix().toString().
                replace("empty[siblings=[", "").
                replace("literal{ }, literal{Ⓑ}[style={color=aqua}], ", "§b Ⓑ").
                replace("literal{ }, literal{Ⓑ}[style={color=aqua}]]]", "§b Ⓑ").
                replace("literal{ }, literal{☣}[style={color=dark_gray}], ", "§8 ☣").
                replace("literal{ }, literal{☣}[style={color=dark_gray}]]]", "§8 ☣").
                replace("literal{ }, literal{◈}[style={color=aqua}]]]", "§b ◈").
                replace("literal{ }, literal{◈}[style={color=dark_gray}]]]", " ◈").
                replace("literal{ }, literal{◈}[style={color=gray}]]]", "§7 ◈").
                replace("literal{ }, literal{◈}[style={color=light_purple}]]]", "§d ◈").
                replace("literal{ }, literal{Ⓣ}[style={color=dark_purple}], ", "§5 Ⓣ").
                replace("literal{ }, literal{Ⓣ}[style={color=dark_purple}]]]", "§5 Ⓣ").
                replace("literal{ }, literal{◈}[style={color=red}]]]", "§c ◈").
                replace("empty", "");

        boolean gr = prefix.contains("GR");
        boolean afk = event.nameTag().toString().contains("italic");
        if(playerName.toLowerCase().equals("etwaseckiges")){
            gr = true;
        }

        if (prefix.contains("red") || prefix.contains("dark_red") || prefix.contains("dark_aqua")
                || prefix.contains("✝")) {
            return;
        }
        if (this.addonVariables.getMembers()!= null) {
            List<String> memberlist = this.addonVariables.getMembers();
            NameTag factionTag = nameTagSubConfig.factionColor().get();

            if (memberlist.contains(playerName) && factionTag != NameTag.NONE) {
                String color = factionTag.getColor();
                prefix = color + (afk ? "§o":"") + (gr ? "[GR]" : "");
                event.setNameTag(Component.text(prefix + playerName + suffix));
                return;
            }
        }

        switch (factionName.getType()) {
            case BADFRAK -> {
                if (this.addonVariables.getBounties() != null) {
                    List<String> bountylist = this.addonVariables.getBounties();
                    NameTag bountyTag = nameTagSubConfig.bountyColor().get();

                    if (bountylist.contains(playerName) && bountyTag != NameTag.NONE) {
                        String color = bountyTag.getColor();
                        prefix = color + (afk ? "§o":"") + (gr ? "[GR]" : "");
                        event.setNameTag(Component.text(prefix + playerName + suffix));
                        return;
                    }
                }

                if (this.addonVariables.getDarklist()!= null) {
                    List<String> darklist = this.addonVariables.getDarklist();
                    NameTag darklisttag = nameTagSubConfig.darklistColor().get();

                    if (darklist.contains(playerName) && darklisttag != NameTag.NONE) {
                        String color = darklisttag.getColor();
                        prefix = color + (afk ? "§o":"") + (gr ? "[GR]" : "");
                        event.setNameTag(Component.text(prefix + playerName + suffix));
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
                    String color = wantedColor.getColor();
                    prefix = color + (afk ? "§o":"") + (gr ? "[GR]" : "");
                    event.setNameTag(Component.text(prefix + playerName + suffix));
                }
            }
        }
    }
}
