package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.api.events.DutyBadgeShownEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import static eu.germanrp.addon.core.GermanRPAddon.NAMESPACE;
import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static net.labymod.api.Laby.fireEvent;

@RequiredArgsConstructor
public final class DutyBadgeListener {

    private static final Component ID_ADDED_TO_CHAR_INFOS = Component.translatable(NAMESPACE + ".message.id.added");

    private final GermanRPAddon addon;

    /**
     * {@code true} if a badge is currently being processed, {@code false} otherwise
     */
    private boolean processingBadge;

    private @Nullable String readName;

    @Subscribe
    @SuppressWarnings("unused")
    public void onChatReceiveEvent(final ChatReceiveEvent event) {
        val message = event.chatMessage().getPlainText();

        var matcher = BADGE_START.getPattern().matcher(message);
        if (matcher.matches()) {
            this.processingBadge = true;
        }

        matcher = BADGE_NAME.getPattern().matcher(message);
        if (matcher.matches() && this.processingBadge) {
            this.readName = matcher.group(1);
        }

        matcher = BADGE_END.getPattern().matcher(message);
        if (matcher.matches() && this.processingBadge) {
            val name = new ServerPlayer(matcher.group(1)).name();
            Laby.references().labyNetController().loadUniqueIdByName(name, uuidResult -> {

                if (uuidResult.isEmpty()) {
                    this.readName = null;
                    this.processingBadge = false;
                    return;
                }

                val uuid = uuidResult.get();
                this.processingBadge = false;
                val charInfo = new CharacterInfo(uuid, name, this.readName);
                addon.getScheduledExecutorService().schedule(() -> fireEvent(new DutyBadgeShownEvent(charInfo)), 10, TimeUnit.MILLISECONDS);
            });
        }

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onDutyBadgeShownEvent(final DutyBadgeShownEvent event) {
        val map = addon.configuration().characterInfoMap();
        val charInfo = event.charInfo();
        val uuid = charInfo.uniqueId();
        if (!map.containsKey(uuid)) {
            addon.getPlayer().sendInfoMessage(ID_ADDED_TO_CHAR_INFOS);
            map.put(uuid, charInfo);
        }
    }

}
