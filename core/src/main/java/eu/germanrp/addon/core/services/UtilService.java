package eu.germanrp.addon.core.services;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.services.util.UtilTextService;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.client.resources.ResourceLocation;

import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.client.gui.icon.Icon.texture;

/**
 * @author RettichLP
 */
@Getter
public class UtilService {

    private final Icon icon = texture(ResourceLocation.create("germanrpaddon", "themes/vanilla/textures/icon.png")).resolution(64, 64);

    @Accessors(fluent = true)
    private final UtilTextService text;

    private final GermanRPAddon addon;

    public UtilService(GermanRPAddon addon) {
        this.addon = addon;
        this.text = new UtilTextService();
    }

    public String version() {
        return "2.5.0";
    }

    public boolean isGermanRP() {
        if (labyAPI().minecraft().isIngame()) {
            ServerData serverData = labyAPI().serverController().getCurrentServerData();
            return serverData != null && serverData.address().matches("germanrp.eu", 25565, true);
        }
        return false;
    }

//    public void debug(String debugMessage) {
//        if (this.addon.configuration().debug().get()) {
//            this.addon.player().sendMessage(Message.getBuilder()
//                    .of("[").color(ColorCode.DARK_GRAY).advance()
//                    .of("DEBUG").color(ColorCode.YELLOW).advance()
//                    .of("]").color(ColorCode.DARK_GRAY).advance().space()
//                    .add(debugMessage)
//                    .createComponent());
//        }
//    }

//    public List<String> getOnlinePlayers() {
//        ClientPacketListener clientPacketListener = labyAPI().minecraft().getClientPacketListener();
//        if (clientPacketListener == null) {
//            return Collections.emptyList();
//        }
//
//        Collection<NetworkPlayerInfo> networkPlayerInfoCollection = clientPacketListener.getNetworkPlayerInfos();
//
//        return networkPlayerInfoCollection.stream()
//                .map(networkPlayerInfo -> networkPlayerInfo.profile().getUsername())
//                .map(this.text::stripColor)
//                .map(this.text::stripPrefix)
//                .sorted()
//                .collect(Collectors.toList());
//    }

//    /**
//     * Replaces the addon api token with "TOKEN"
//     *
//     * @param message Message which needs to be checked
//     *
//     * @return Message without addon api token
//     */
//    public String messageWithHiddenToken(String message) {
//        return Optional.ofNullable(this.addon.api().getToken())
//                .map(s -> message.replace(s, "TOKEN"))
//                .orElse("Message cannot be displayed because it contains a token.");
//    }
}
