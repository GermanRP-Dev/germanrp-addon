package eu.germanrp.addon.core.common;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.world.ClientWorld;
import net.labymod.api.util.math.position.Position;
import net.labymod.api.util.math.vector.FloatVector3;

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

    void sendErrorMessage(String message);

    void sendInfoMessage(String message);

    void sendSyntaxMessage(String message);

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
}
