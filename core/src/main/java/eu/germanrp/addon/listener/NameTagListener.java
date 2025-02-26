package eu.germanrp.addon.listener;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.enums.NameTagColor;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

import java.util.ArrayList;
import java.util.List;

public class NameTagListener {

    private final GermanRPAddon addon;
    private final List<String> memberList = new ArrayList<>();
    private final List<String> darklistList = new ArrayList<>();
    private final List<String> bountyList = new ArrayList<>();
    private boolean members = false;
    private boolean darklists = false;
    private boolean bounties = false;
    private int spaceCounter = 0;

    public NameTagListener(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onMessageReceive(ChatReceiveEvent event) {

        String message = event.chatMessage().getOriginalPlainText();

        if (message.contains("► Fraktionsmember online ◄")) {
            members = true;
        }
        if (message.startsWith("    ► ") && members) {
            String messageTwo = message.replace("    ► ", "");
            for (String s : messageTwo.split(" ")) {
                if (!s.equals(" ")) {
                    memberList.add(s.replace("[GR]", ""));
                    memberList.add(s);
                    break;
                }
            }
        } else if (members) {
            spaceCounter++;
            if (spaceCounter > 2) {
                spaceCounter = 0;
                members = false;
            }
        }

        if (message.equals("► [Darklist] Darklist deiner Fraktion:")) {
            darklists = true;
        }

        if (message.startsWith("► [Darklist] - ") && darklists) {
            String messageTwo = message.replace("► [Darklist] - ", "");
            for (String s : messageTwo.split(" ")) {
                if (!s.equals(" ")) {
                    darklistList.add(s.replace("[GR]", ""));
                    darklistList.add(s);
                    break;
                }
            }
        } else if (darklists) {
            spaceCounter++;
            if (spaceCounter > 1) {
                spaceCounter = 0;
                darklists = false;
            }
        }

        if (message.contains("KOPFGELDER")) {
            bounties = true;
        }
        if (message.startsWith("    » ") && bounties) {
            String messageTwo = message.replace("    » ", "");
            for (String s : messageTwo.split(" ")) {
                if (!s.equals(" ")) {
                    bountyList.add(s.replace("[GR]", ""));
                    bountyList.add(s);
                    break;
                }
            }
        } else if (bounties) {
            spaceCounter++;
            if (message.equals("    » Derzeit hat niemand Kopfgeld")) {
                bounties = false;
                return;
            }
            if (spaceCounter > 2) {
                spaceCounter = 0;
                bounties = false;
            }
        }
        if (message.equals("► Du bist in keiner Fraktion.") && !memberList.isEmpty()) {
            memberList.clear();
            darklistList.clear();
            bountyList.clear();
        }
    }

    @Subscribe
    public void onNameTag(PlayerNameTagRenderEvent event) {

        PlayerNameTagRenderEvent.Context context = event.context();
        NetworkPlayerInfo networkPlayerInfo = event.getPlayerInfo();

        if (networkPlayerInfo == null) {
            return;
        }

        String playerName = networkPlayerInfo.profile().getUsername();
        if (context.equals(PlayerNameTagRenderEvent.Context.PLAYER_RENDER)) {
            ScoreboardTeam team = networkPlayerInfo.getTeam();
            if (team == null) {
                return;
            }
            String prefix = team.getPrefix().toString().replace("literal{", "").replace("}", "");
            if (prefix.equals("§c")) {
                return;
            }
            if (prefix.equals("§8")) {
                return;
            }
            String suffix = team.getSuffix().toString().replace("literal{", "").replace("}", "");
            NameTagColor factionTag = addon.configuration().nametags().factiontag().get();
            NameTagColor darkListTag = addon.configuration().nametags().darklisttag().get();
            NameTagColor bountyTag = addon.configuration().nametags().bountytag().get();
            NameTagColor policeTag = addon.configuration().nametags().policetag().get();
            boolean gr = prefix.contains("[GR]");
            if (suffix.equals("empty")) {
                suffix = "";
            }

            if (factionTag != NameTagColor.NONE) {
                if (memberList.contains(playerName)) {
                    prefix = enumToColor(factionTag.toString()) + (gr ? "[GR]" : "");
                    event.setNameTag(Component.text(prefix + playerName + suffix));
                    return;
                }
            }
            if (bountyTag != NameTagColor.NONE) {
                if (bountyList.contains(playerName)) {
                    prefix = enumToColor(bountyTag.toString()) + (gr ? "[GR]" : "");
                    event.setNameTag(Component.text(prefix + playerName + suffix));
                    return;
                }
            }
            if (darkListTag != NameTagColor.NONE) {
                if (darklistList.contains(playerName)) {
                    prefix = enumToColor(darkListTag.toString()) + (gr ? "[GR]" : "");
                    event.setNameTag(Component.text(prefix + playerName + suffix));
                    return;
                }
            }
            if (policeTag != NameTagColor.NONE) {
                if (prefix.startsWith("§3")) {
                    prefix = enumToColor(policeTag.toString()) + (gr ? "[GR]" : "");
                    event.setNameTag(Component.text(prefix + playerName + suffix));
                }
            }
        }
    }

    private String enumToColor(String color) {
        return switch (color) {
            case "GRAY" -> "§7";
            case "DARKGRAY" -> "§8";
            case "BLACK" -> "§0";
            case "GREEN" -> "§a";
            case "DARKGREEN" -> "§2";
            case "LIGHTBLUE" -> "§b";
            case "BLUE" -> "§9";
            case "DARKBLUE" -> "§1";
            case "CYAN" -> "§3";
            case "YELLOW" -> "§e";
            case "ORANGE" -> "§6";
            case "PINK" -> "§d";
            case "PURPLE" -> "§5";
            default -> "§f";
        };
    }
}
