package eu.germanrp.addon.core.common;

import eu.germanrp.addon.api.models.Faction;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.ExperienceUpdateEvent;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.resources.ResourceLocation;
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

    private final GermanRPAddon addon;

    private boolean shouting = false;
    private boolean whispering = false;
    private boolean playerGR = false;
    private int playerXP;
    private int playerTBonusTime;
    private int playerNeededXP;
    private int playerPayDayTime;
    private Faction playerFaction;

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
    public void sendErrorMessage(Component component) {
        this.addon.displayMessage(prefix()
                .append(text("Fehler! ", DARK_RED, BOLD))
                .append(component.color(RED)));
    }

    @Override
    public void sendErrorMessage(String message) {
        this.addon.displayMessage(prefix()
                .append(text("Fehler! ", DARK_RED, BOLD))
                .append(text(message, RED)));
    }

    @Override
    public void sendInfoMessage(Component component) {
        Laby.labyAPI().minecraft().chatExecutor().displayClientMessage(prefix()
                .append(text("Info! ", AQUA, BOLD))
                .append(component.color(WHITE)));
    }

    @Override
    public void sendInfoMessage(String string) {
        Laby.labyAPI().minecraft().chatExecutor().displayClientMessage(prefix()
                .append(text("Info! ", AQUA, BOLD))
                .append(text(string, WHITE)));
    }

    @Override
    public void sendSyntaxMessage(Component component) {
        sendErrorMessage("Syntax: " + component);
    }

    @Override
    public void sendSyntaxMessage(String message) {
        sendErrorMessage("Syntax: " + message);
    }

    @Override
    public void sendDebugMessage(String message) {
        Laby.labyAPI().minecraft().chatExecutor().displayClientMessage(prefix()
                .append(text("Debug! ", DARK_AQUA, BOLD))
                .append(text(message, WHITE)));
    }

    @Override
    public void sendDebugMessage(Component component) {
        Laby.labyAPI().minecraft().chatExecutor().displayClientMessage(prefix()
                .append(text("Debug! ", DARK_AQUA, BOLD))
                .append(component.color(WHITE)));
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

    @Override
    public void setPlayerXP(int i) {
        this.playerXP = i;
        Laby.fireEvent(new ExperienceUpdateEvent());
    }

    @Override
    public void addPlayerXP(int i) {
        this.playerXP = this.playerXP + i;
        Laby.fireEvent(new ExperienceUpdateEvent());
    }

    @Override
    public int getPlayerXP() {
        return this.playerXP;
    }

    @Override
    public void setPlayerNeededXP(int i) {
        this.playerNeededXP = i;
    }

    @Override
    public int getPlayerNeededXP() {
        return this.playerNeededXP;
    }

    @Override
    public void setPlayerPayDayTime(int i) {
        this.playerPayDayTime = i;
    }

    @Override
    public int getPlayerPayDayTime() {
        return this.playerPayDayTime;
    }

    @Override
    public void setPlayerTBonusTime(int i) {
        this.playerTBonusTime = i;
    }

    @Override
    public int getPlayerTBonusTime() {
        return this.playerTBonusTime;
    }

    @Override
    public void setPlayerGR(boolean b) {
        this.playerGR = b;
    }

    @Override
    public boolean isPlayerGR() {
        return this.playerGR;
    }

    @Override
    public Faction getPlayerFactionName() {
        return this.playerFaction;
    }

    @Override
    public void playSound(ResourceLocation location, float volume, float pitch) {
        this.addon.labyAPI().minecraft().sounds().playSound(location, volume, pitch);
    }

    @Override
    public void setPlayerFactionName(Faction playerFaction) {
        this.playerFaction = playerFaction;
    }

    private Component prefix() {
        return text(ADDON_PREFIX_SYMBOL, DARK_AQUA, BOLD).hoverEvent(showText(text("Nachricht vom GermanRP-Addon", DARK_AQUA)));
    }
}
