package eu.germanrp.addon.core.listener;

import static eu.germanrp.addon.core.pattern.NameTagPattern.ADD_BOUNTY_PATTERN;
import static eu.germanrp.addon.core.pattern.NameTagPattern.ADD_DARKLIST_PATTERN;
import static eu.germanrp.addon.core.pattern.NameTagPattern.ADD_WANTEDS_PATTERN;
import static eu.germanrp.addon.core.pattern.NameTagPattern.REMOVE_DARKLIST_PATTERN;
import static eu.germanrp.addon.core.pattern.NameTagPattern.REMOVE_WANTEDS_PATTERN;

import eu.germanrp.addon.core.Enum.FactionName;
import eu.germanrp.addon.core.Enum.FactionName.FactionType;
import eu.germanrp.addon.core.Enum.NameTag;
import eu.germanrp.addon.core.GRUtilsAddon;
import eu.germanrp.addon.core.NameTagSubConfig;
import java.util.List;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;


public class NameTagListener {

  private final GRUtilsAddon addon;
  private final NameTagSubConfig nameTagSubConfig;
  private final FactionName factionName;
  private final ServerJoinListener serverJoinListener;

  public NameTagListener(GRUtilsAddon grUtilsAddon) {
    this.addon = grUtilsAddon;
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
        replace("literal{ }, literal{☣}[style={color=dark_gray}], ", "§8 ☣").
        replace("literal{ }, literal{☣}[style={color=dark_gray}]]]", "§8 ☣").
        replace("literal{ }, literal{◈}[style={color=aqua}]]]", "§b ◈").
        replace("literal{ }, literal{◈}[style={color=dark_gray}]]]", " ◈").
        replace("literal{ }, literal{◈}[style={color=gray}]]]", "§7 ◈").
        replace("literal{ }, literal{◈}[style={color=light_purple}]]]", "§d ◈").
        replace("literal{ }, literal{Ⓣ}[style={color=dark_purple}]]]", "§5 Ⓣ").
        replace("literal{ }, literal{◈}[style={color=red}]]]", "§c ◈");
    boolean gr = prefix.contains("GR");

    if (prefix.contains("red") || prefix.contains("dark_red") || prefix.contains("dark_aqua")
        || prefix.contains("aqua")) {
      return;
    }
    if (serverJoinListener.getMembers() != null) {
      List<String> memberlist = serverJoinListener.getMembers();
      NameTag factiontag = nameTagSubConfig.factionTag().get();

      if (memberlist.contains(playerName) && factiontag != NameTag.NONE) {
        String var17 = factiontag.getColor();
        prefix = var17 + (gr ? "[GR]" : "");
        event.setNameTag(Component.text(prefix + playerName + suffix));
        return;
      }
    }

    switch (factionName.getType()) {
      case BADFRAK -> {
        if (serverJoinListener.getBounties() != null) {
          List<String> bountylist = serverJoinListener.getBounties();
          NameTag bountytag = nameTagSubConfig.bountyTag().get();

          if (bountylist.contains(playerName) && bountytag != NameTag.NONE) {
            String color = bountytag.getColor();
            prefix = color + (gr ? "[GR]" : "");
            event.setNameTag(Component.text(prefix + playerName + suffix));
            return;
          }
        }

        if (serverJoinListener.getDarklist() != null) {
          List<String> darklist = serverJoinListener.getDarklist();
          NameTag darklisttag = nameTagSubConfig.darklistTag().get();

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

        List<String> darklist = serverJoinListener.getDarklist();
        NameTag darklistTag = nameTagSubConfig.darklistTag().get();

        if (!darklist.contains(playerName) || darklistTag == NameTag.NONE) {
          return;
        }

        String color = darklistTag.getColor();
        prefix = color + (gr ? "[GR]" : "");
        event.setNameTag(Component.text(prefix + playerName + suffix));
      }
    }
  }

  @Subscribe
  public void onChatReceive(ChatReceiveEvent event) {

    String message = event.chatMessage().getPlainText();
    switch (factionName.getType()) {
      case BADFRAK -> {
        if (REMOVE_DARKLIST_PATTERN.matcher(message).find()) {
          serverJoinListener.getDarklist().remove(
              REMOVE_DARKLIST_PATTERN.matcher(message).group(2));
        }
        if (ADD_DARKLIST_PATTERN.matcher(message).find()) {
          serverJoinListener.getDarklist()
              .remove(ADD_DARKLIST_PATTERN.matcher(message).group(2));
        }
        if (ADD_BOUNTY_PATTERN.matcher(message).find()) {
          serverJoinListener.getBounties()
              .remove(ADD_BOUNTY_PATTERN.matcher(message).group(1));
        }
      }
      case STAAT -> {
        if (REMOVE_WANTEDS_PATTERN.matcher(message).find()) {
          serverJoinListener.getWantedPlayers().remove(
              REMOVE_WANTEDS_PATTERN.matcher(message).group(2));
        }
        if (ADD_WANTEDS_PATTERN.matcher(message).find()) {
          serverJoinListener.getWantedPlayers()
              .remove(ADD_WANTEDS_PATTERN.matcher(message).group(1));
        }
      }
    }
  }
}