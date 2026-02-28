package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.events.DutyBadgeShownEvent;
import eu.germanrp.addon.api.events.GermanRPChatReceiveEvent;
import eu.germanrp.addon.api.models.CharacterInfo;
import eu.germanrp.addon.api.models.ServerPlayer;
import eu.germanrp.addon.core.GermanRPAddon;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
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
    public void onChatReceiveEvent(final GermanRPChatReceiveEvent event) {
        val message = event.chatMessage().getPlainText();

        var matcher = BADGE_START.getPattern().matcher(message);
        if (matcher.matches()) {
            addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent: BADGE_START matched");
            this.processingBadge = true;
        }

        matcher = BADGE_NAME.getPattern().matcher(message);
        if (matcher.matches() && this.processingBadge) {
            this.readName = matcher.group(1);
            addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent: BADGE_NAME matched \"%s\"".formatted(this.readName));
        }

        matcher = BADGE_END.getPattern().matcher(message);
        if (!matcher.matches() || !this.processingBadge) {
            return;
        }

        val matchedName = matcher.group(1);
        addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent: BADGE_END matched \"%s\"".formatted(matchedName));
        val name = new ServerPlayer(matchedName).name();
        addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent, BADGE_END: looking up uuid for name: %s...".formatted(name));
        Laby.references().labyNetController().loadUniqueIdByName(name, uuidResult -> {

            if (uuidResult.isEmpty()) {
                addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent, BADGE_END: unable to fetch uuid for name %s".formatted(name));
                this.readName = null;
                this.processingBadge = false;
                return;
            }

            val uuid = uuidResult.get();
            addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent, BADGE_END: uuid for name \"%s\" is \"%s\"".formatted(name, uuid));
            this.processingBadge = false;
            val charInfo = new CharacterInfo(uuid, name, this.readName);
            val dutyBadgeShownEvent = new DutyBadgeShownEvent(charInfo);
            addon.getPlayer().sendDebugMessage("DutyBadgeListener, onChatReceiveEvent, BADGE_END: firing %s".formatted(dutyBadgeShownEvent));
            addon.getScheduledExecutorService().schedule(() -> fireEvent(dutyBadgeShownEvent), 10, TimeUnit.MILLISECONDS);
        });

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onDutyBadgeShownEvent(final DutyBadgeShownEvent event) {
        addon.getPlayer().sendDebugMessage("DutyBadgeListener, onDutyBadgeShownEvent: Received Event \"%s\"".formatted(event));
        val map = addon.configuration().characterInfoMap();
        val charInfo = event.charInfo();
        val uuid = charInfo.uniqueId();

        if (map.containsKey(uuid)) {
            addon.getPlayer().sendDebugMessage("DutyBadgeListener, onDutyBadgeShownEvent: Skipping character info \"%s\"".formatted(charInfo));
            return;
        }

        addon.getPlayer().sendDebugMessage("DutyBadgeListener, onDutyBadgeShownEvent: Adding character info \"%s\"".formatted(charInfo));
        addon.getPlayer().sendInfoMessage(ID_ADDED_TO_CHAR_INFOS);
        map.put(uuid, charInfo);
    }

}
