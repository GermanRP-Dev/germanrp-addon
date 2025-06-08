package eu.germanrp.addon.core.common;

import eu.germanrp.addon.api.models.FactionName;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.world.ClientWorld;
import net.labymod.api.util.math.position.Position;
import net.labymod.api.util.math.vector.FloatVector3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author RettichLP
 */
public interface AddonPlayer {

    ClientPlayer getPlayer();

    String getName();

    UUID getUniqueId();

    Float getHealth();

    Position getLocation();

    void sendMessage(String message);

    void sendMessage(Component component);

    void sendEmptyMessage();

    void sendErrorMessage(Component component);

    void sendErrorMessage(String message);

    void sendInfoMessage(Component component);

    void sendInfoMessage(String string);

    void sendSyntaxMessage(Component component);

    void sendSyntaxMessage(String message);

    void sendDebugMessage(String message);

    void sendDebugMessage(Component component);

    void sendServerMessage(String message);

    ClientWorld getWorld();

    Scoreboard getScoreboard();

    void stopRoute();

    void setNaviRoute(int x, int y, int z);

    void setNaviRoute(FloatVector3 location);

    void copyToClipboard(String string);

    boolean isShouting();

    void setShouting(boolean shouting);

    boolean isWhispering();

    void setWhispering(boolean whispering);

    void setPlayerXP(int i);

    void addPlayerXP(int i);

    int getPlayerXP();

    void setPlayerNeededXP(int i);

    int getPlayerNeededXP();

    void setPlayerPayDayTime(int i);

    int getPlayerPayDayTime();

    void setPlayerTBonusTime(int i);

    int getPlayerTBonusTime();

    void setPlayerGR(boolean b);

    boolean isPlayerGR();

    void setPlayerFactionName(@NotNull FactionName factionName);

    FactionName getPlayerFactionName();

    void playSound(ResourceLocation location, float volume, float pitch);
}
