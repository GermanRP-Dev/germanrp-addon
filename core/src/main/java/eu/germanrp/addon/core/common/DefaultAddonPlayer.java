package eu.germanrp.addon.core.common;

import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.world.ClientWorld;
import net.labymod.api.util.math.position.Position;
import net.labymod.api.util.math.vector.FloatVector3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.client.component.Component.text;
import static net.labymod.api.client.component.event.HoverEvent.showText;
import static net.labymod.api.client.component.format.NamedTextColor.AQUA;
import static net.labymod.api.client.component.format.NamedTextColor.DARK_AQUA;
import static net.labymod.api.client.component.format.NamedTextColor.DARK_RED;
import static net.labymod.api.client.component.format.NamedTextColor.RED;
import static net.labymod.api.client.component.format.NamedTextColor.WHITE;
import static net.labymod.api.client.component.format.TextDecoration.BOLD;

/**
 * @author RettichLP
 */
public class DefaultAddonPlayer implements AddonPlayer {

    private static final String ADDON_PREFIX_SYMBOL = "  ₪ ";

    private boolean shouting = false;
    private boolean whispering = false;

    private final GermanRPAddon addon;

    public DefaultAddonPlayer(GermanRPAddon addon) {
        this.addon = addon;
    }

    @Override
    public ClientPlayer getPlayer() {
        return labyAPI().minecraft().getClientPlayer();
    }

    @Override
    public String getName() {
        return labyAPI().getName();
    }

    @Override
    public UUID getUniqueId() {
        return labyAPI().getUniqueId();
    }

    @Override
    public Float getHealth() {
        return getPlayer() != null ? getPlayer().getHealth() : null;
    }

    @Override
    public Position getLocation() {
        return getPlayer() != null ? getPlayer().position() : null;
    }

    @Override
    public void sendMessage(String message) {
        this.addon.displayMessage(message);
    }

    @Override
    public void sendMessage(Component component) {
        this.addon.displayMessage(component);
    }

    @Override
    public void sendEmptyMessage() {
        sendMessage("");
    }

    @Override
    public void sendErrorMessage(String message) {
        this.addon.displayMessage(prefix()
                .append(text("Fehler! ", DARK_RED, BOLD))
                .append(text(message, RED)));
    }

    @Override
    public void sendInfoMessage(String message) {
        this.addon.displayMessage(prefix()
                .append(text("Info! ", AQUA, BOLD))
                .append(text(message, WHITE)));
    }

    @Override
    public void sendSyntaxMessage(String message) {
        sendErrorMessage("Syntax: " + message);
    }

    @Override
    public void sendServerMessage(String message) {
        this.addon.sendMessage(message);
        this.addon.logger().info("AddonPlayer sent chat message: " + message);
    }

    @Override
    public ClientWorld getWorld() {
        return labyAPI().minecraft().clientWorld();
    }

    @Override
    public Scoreboard getScoreboard() {
        return labyAPI().minecraft().getScoreboard();
    }

    @Override
    public void stopRoute() {
        sendServerMessage("/stopnavi");
    }

    @Override
    public void setNaviRoute(int x, int y, int z) {
        setNaviRoute(new FloatVector3(x, y, z));
    }

    @Override
    public void setNaviRoute(@NotNull FloatVector3 location) {
        stopRoute();
        sendServerMessage(String.format("/navi %.2f %.2f %.2f", location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public void copyToClipboard(String string) {
        labyAPI().minecraft().setClipboard(string);
    }

    @Override
    public boolean isShouting() {
        return shouting;
    }

    @Override
    public void setShouting(boolean shouting) {
        this.shouting = shouting;
    }

    @Override
    public boolean isWhispering() {
        return whispering;
    }

    @Override
    public void setWhispering(boolean whispering) {
        this.whispering = whispering;
    }

    private Component prefix() {
        return text(ADDON_PREFIX_SYMBOL, DARK_AQUA, BOLD).hoverEvent(showText(text("Nachricht vom GermanRP-Addon", DARK_AQUA)));
    }
}
