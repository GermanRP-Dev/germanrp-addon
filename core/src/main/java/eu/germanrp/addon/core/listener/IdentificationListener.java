package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.IdentificationShownEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static net.labymod.api.Laby.fireEvent;

@RequiredArgsConstructor
public final class IdentificationListener {

    private static final Component ID_ADDED_TO_CHAR_INFOS = Component.translatable(NAMESPACE + ".message.id.added");

    private final GermanRPAddon addon;

    /**
     * {@code true} if an id is currently being processed, {@code false} otherwise
     */
    private boolean processingId;

    private String readFirstname;
    private String readLastname;

    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveEvent(final ChatReceiveEvent event) {
        val message = event.chatMessage().getPlainText();

        var matcher = ID_START.getPattern().matcher(message);
        if (matcher.matches() && !processingId) {
            processingId = true;
        }

        matcher = ID_FIRSTNAME.getPattern().matcher(message);
        if (matcher.matches() && processingId) {
            this.readFirstname = matcher.group(1);
        }

        matcher = ID_LASTNAME.getPattern().matcher(message);
        if (matcher.matches() && processingId) {
            this.readLastname = matcher.group(1);
        }

        matcher = ID_END.getPattern().matcher(message);
        if (matcher.matches() && processingId) {
            val name = new ServerPlayer(matcher.group(1)).name();
            Laby.references().labyNetController().loadUniqueIdByName(name, uuidResult -> {

                if (!uuidResult.isPresent()) {
                    this.readFirstname = null;
                    this.readLastname = null;
                    processingId = false;
                }

                val uuid = uuidResult.get();
                this.processingId = false;
                val charName = "%s %s".formatted(readFirstname, readLastname);
                val charInfo = new CharacterInfo(uuid, name, charName);
                threadPool.schedule(() -> fireEvent(new IdentificationShownEvent(charInfo)), 20, TimeUnit.MILLISECONDS);
            });
        }

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPersonalausweisShownEvent(final IdentificationShownEvent event) {
        val map = addon.configuration().characterInfoMap();
        val charInfo = event.charInfo();
        val uuid = charInfo.uniqueId();
        if (!map.containsKey(uuid)) {
            addon.getPlayer().sendInfoMessage(ID_ADDED_TO_CHAR_INFOS);
            map.put(uuid, charInfo);
        }
    }

}
