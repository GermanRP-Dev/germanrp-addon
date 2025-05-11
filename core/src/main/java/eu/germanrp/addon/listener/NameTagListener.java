package eu.germanrp.addon.listener;

import eu.germanrp.addon.GermanRPAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

import java.util.ArrayList;
import java.util.List;

import static net.labymod.api.client.component.Component.empty;
import static net.labymod.api.client.component.Component.text;
import static net.labymod.api.client.component.format.NamedTextColor.WHITE;

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
    public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {

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
            Component prefix = team.getPrefix();
            Component suffix = team.getSuffix();
            TextColor factionTag = addon.configuration().nametags().faction().get();
            TextColor darkListTag = addon.configuration().nametags().darkList().get();
            TextColor bountyTag = addon.configuration().nametags().bounty().get();
            TextColor policeTag = addon.configuration().nametags().police().get();
            boolean gr = false; // TODO prefix.contains("[GR]");

            if (factionTag != WHITE) {
                if (memberList.contains(playerName)) {
                    prefix = text(gr ? "[GR]" : "", factionTag);
                    event.setNameTag(empty()
                            .append(prefix)
                            .append(text(playerName))
                            .append(suffix));
                    return;
                }
            }
            if (bountyTag != WHITE) {
                if (bountyList.contains(playerName)) {
                    prefix = gr ? text("[GR]", bountyTag) : empty();
                    event.setNameTag(text(prefix + playerName + suffix));
                    return;
                }
            }
            if (darkListTag != WHITE) {
                if (darklistList.contains(playerName)) {
                    prefix = gr ? text("[GR]", darkListTag) : empty();
                    event.setNameTag(text(prefix + playerName + suffix));
                    return;
                }
            }
            if (policeTag != WHITE) {
//                if (prefix.startsWith("§3")) { TODO
                prefix = gr ? text("[GR]", policeTag) : empty();
                event.setNameTag(text(prefix + playerName + suffix));
//                }
            }
        }
    }
}
