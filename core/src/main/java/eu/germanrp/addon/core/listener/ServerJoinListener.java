package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.core.Enum.FactionName;
import eu.germanrp.addon.core.GRUtilsAddon;
import lombok.Getter;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.pattern.NameTagPattern.*;


public class ServerJoinListener {


    private int emptyMessages = 0;

    @Getter
    private boolean justJoined = false;

    private boolean faction;
    private boolean bounty;
    private boolean wanted;

    @Getter
    private boolean isGR = false;
    @Getter
    private final List<String> members = new ArrayList<>();
    @Getter
    private final List<String> darklist = new ArrayList<>();
    @Getter
    private final List<String> bounties = new ArrayList<>();
    @Getter
    private final List<String> wantedPlayers = new ArrayList<>();

    private  FactionName factionName;
    private final GRUtilsAddon addon;

    public ServerJoinListener(GRUtilsAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        factionName = this.addon.configuration().NameTagSubConfig().factionName().get();
        String ip = String.valueOf(event.serverData().address());
        emptyMessages = 0;

        if (!ip.toLowerCase().contains("germanrp.eu") && !ip.contentEquals("91.218.66.124")) {
            this.justJoined = false;
            this.isGR = false;
            return;
        }

        this.isGR = true;
        this.justJoined = true;


        if (factionName.equals(FactionName.NONE)) {
            return;
        }

        this.members.clear();
        Laby.references().chatExecutor()
                .chat(String.format("/memberinfo %s", factionName.getMemberInfoCommandArg()));

        switch (factionName.getType()) {
            case BADFRAK -> {
                this.darklist.clear();
                this.bounties.clear();
                Laby.references().chatExecutor().chat("/darklist");
                Laby.references().chatExecutor().chat("/kopfgelder");
            }
            case STAAT -> {
                this.wantedPlayers.clear();
                Laby.references().chatExecutor().chat("/wanteds");
            }
        }

    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if (!this.isGR || !this.justJoined) {
            return;
        }

        String message = event.chatMessage().getPlainText();
        if (factionName.equals(FactionName.NONE)) {
            this.justJoined = false;
            return;
        }
        if (FRAKTIONSMITGLIEDER_TITLE.matcher(message).find()) {
            event.setCancelled(true);
            this.faction = true;
            return;
        }

        if (this.faction) {
            event.setCancelled(true);
            final Matcher matcher = BOUNTY_MEMBER_WANTEDS_PATTERN.matcher(message);
            if (!matcher.find()) {
                if (!message.startsWith("        (Insgesamt ") || !message.endsWith(" verfügbar)")) {
                    return;
                }
                this.faction = false;
                return;
            }
            this.members.add(matcher.group(1).replace("[GR]", ""));
        }

        switch (factionName.getType()) {
            case BADFRAK -> {
                if (message.startsWith("► [Darklist] ")) {
                    event.setCancelled(true);
                    final Matcher matcher = DARKLIST_PATTERN.matcher(message);
                    if (!matcher.find()) {
                        return;
                    }
                    this.darklist.add(matcher.group(1).replace("[GR]", ""));
                    return;
                }
                if (message.contentEquals("            KOPFGELDER")) {
                    event.setCancelled(true);
                    this.bounty = true;
                    return;
                }
                if (this.bounty) {
                    event.setCancelled(true);
                    final Matcher matcher = BOUNTY_MEMBER_WANTEDS_PATTERN.matcher(message);
                    if (!matcher.find()) {
                        this.bounty = false;
                        this.justJoined = false;
                        return;
                    }
                    this.bounties.add(matcher.group(1).replace("[GR]", ""));
                    return;
                }
            }

            case STAAT -> {
                if (FAHNDUNGSLISTE_TITLE.matcher(message).find()) {
                    event.setCancelled(true);
                    this.wanted = true;
                    return;
                }
                if (this.wanted) {
                    event.setCancelled(true);
                    final Matcher matcher = BOUNTY_MEMBER_WANTEDS_PATTERN.matcher(message);
                    if (!matcher.find()) {
                        this.wanted = false;
                        this.justJoined = false;
                    }
                    this.wantedPlayers.add(matcher.group(1).replace("[GR]", ""));

                }

            }
        }
        if (message.isEmpty()) {
            emptyMessages++;
            if (emptyMessages > 2) {
                event.setCancelled(true);
            }
        }

    }

}