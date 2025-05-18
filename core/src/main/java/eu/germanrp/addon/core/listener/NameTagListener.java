package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.Enum.FactionName;
import eu.germanrp.addon.core.Enum.FactionName.FactionType;
import eu.germanrp.addon.core.Enum.NameTag;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.NameTagSubConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

import java.util.List;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;

public class NameTagListener {

    private final GermanRPAddon addon;

    private final NameTagSubConfig nameTagSubConfig;
    private final FactionName factionName;
    private final ServerJoinListener serverJoinListener;

    public NameTagListener(GermanRPAddon germanRPAddon) {
        this.addon = germanRPAddon;
        this.nameTagSubConfig = addon.configuration().NameTagSubConfig();
        this.factionName = nameTagSubConfig.factionName().get();
        this.serverJoinListener = addon.getServerJoinListener();
    }

    @Subscribe
    public void onNameTag(PlayerNameTagRenderEvent event) {
        if (!serverJoinListener.isGR()) {
            return;
        }

        NetworkPlayerInfo playerInfo = event.getPlayerInfo();
        if (playerInfo == null) {
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

        if (prefix.contains("red") || prefix.contains("dark_red") || prefix.contains("dark_aqua")
                || prefix.contains("✝")) {
            return;
        }
        if (serverJoinListener.getMembers() != null) {
            List<String> memberlist = serverJoinListener.getMembers();
            NameTag factionTag = nameTagSubConfig.factionColor().get();

            if (memberlist.contains(playerName) && factionTag != NameTag.NONE) {
                String var17 = factionTag.getColor();
                prefix = var17 + (gr ? "[GR]" : "");
                event.setNameTag(Component.text(prefix + playerName + suffix));
                return;
            }
        }

        switch (factionName.getType()) {
            case BADFRAK -> {
                if (serverJoinListener.getBounties() != null) {
                    List<String> bountylist = serverJoinListener.getBounties();
                    NameTag bountyTag = nameTagSubConfig.bountyColor().get();

                    if (bountylist.contains(playerName) && bountyTag != NameTag.NONE) {
                        String color = bountyTag.getColor();
                        prefix = color + (gr ? "[GR]" : "");
                        event.setNameTag(Component.text(prefix + playerName + suffix));
                        return;
                    }
                }

                if (serverJoinListener.getDarklist() != null) {
                    List<String> darklist = serverJoinListener.getDarklist();
                    NameTag darklisttag = nameTagSubConfig.darklistColor().get();

                    if (darklist.contains(playerName) && darklisttag != NameTag.NONE) {
                        String color = darklisttag.getColor();
                        prefix = color + (gr ? "[GR]" : "");
                        event.setNameTag(Component.text(prefix + playerName + suffix));
                    }
                }
            }

            case STAAT -> {
                if (serverJoinListener.getWantedPlayers() == null) {
                    return;
                }

                List<String> wantedList = serverJoinListener.getWantedPlayers();
                NameTag wantedColor = nameTagSubConfig.wantedColor().get();

                if (wantedList.contains(playerName) && wantedColor != NameTag.NONE) {
                    String color = wantedColor.getColor();
                    prefix = color + (gr ? "[GR]" : "");
                    event.setNameTag(Component.text(prefix + playerName + suffix));
                }
            }
        }
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {

        String message = event.chatMessage().getPlainText();
        switch (factionName.getType()) {
            case BADFRAK -> {
                final Matcher nametagDarkListAddMatcher = DARK_LIST_ADD.getPattern().matcher(message);

                if (nametagDarkListAddMatcher.find()) {
                    serverJoinListener.getDarklist().add(nametagDarkListAddMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagDarkListRemoveMatcher = DARK_LIST_REMOVE.getPattern().matcher(message);
                if (nametagDarkListRemoveMatcher.find()) {
                    serverJoinListener.getDarklist().remove(nametagDarkListRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagBountyAddMatcher = BOUNTY_ADD.getPattern().matcher(message);
                if (nametagBountyAddMatcher.find()) {
                    serverJoinListener.getBounties().add(nametagBountyAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }

                final Matcher nametagBountyRemoveMatcher = BOUNTY_REMOVE.getPattern().matcher(message);
                if (nametagBountyRemoveMatcher.find()){
                    serverJoinListener.getBounties().remove(nametagBountyRemoveMatcher.group(1).replace("[GR]", ""));
                }
            }
            case STAAT -> {
                final Matcher nametagWantedRemoveMatcher = WANTED_REMOVE.getPattern().matcher(message);
                final Matcher nametagWantedAddMatcher = WANTED_ADD.getPattern().matcher(message);

                if (nametagWantedRemoveMatcher.find()) {
                    serverJoinListener.getWantedPlayers().
                            remove(nametagWantedRemoveMatcher.group(2).replace("[GR]", ""));
                    return;
                }
                if (nametagWantedAddMatcher.find()) {
                    serverJoinListener.getWantedPlayers().remove(nametagWantedAddMatcher.group(1).replace("[GR]", ""));
                    return;
                }
            }
        }
    }
}
