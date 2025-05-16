package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import eu.germanrp.addon.core.common.events.GraffitiUpdateEvent;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.germanrp.addon.core.GermanRPAddon.navigationService;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND_3;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND_30;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.SECOND_5;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.TICK;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.TICK_5;
import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
import static net.labymod.api.Laby.fireEvent;
import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.event.Phase.POST;

public class EventRegistrationListener {

    private static final Pattern GRAFFITI_ADDED_PATTERN = Pattern.compile(
            "^► \\[✦] (?:\\[GR])?(?<player>\\w{3,16}) hat (?:ein|das) Graffiti(?: angebracht)? \\((\\S*\\s*\\S*)\\)(?:!| aufgefrischt!)$"
    );
    private static final Pattern GRAFFITI_TIME_PATTERN = Pattern.compile(
            "^► Diese Stelle kann noch nicht wieder besprüht werden \\((?:(?<minutes>\\d+)\\s+Minuten?)?(?:\\s*(?<seconds>\\d+)\\s+Sekunden?)?\\)$",
            Pattern.CANON_EQ
    );

    private final GermanRPAddon addon;

    private long currentTick = 0;

    public EventRegistrationListener(final @NotNull GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        String plainText = event.chatMessage().getPlainText();

        this.addon.logger().info("plain: " + plainText);

        Matcher graffitiAddedMatcher = GRAFFITI_ADDED_PATTERN.matcher(plainText);
        if (graffitiAddedMatcher.matches()) {
            String graffitiName = graffitiAddedMatcher.group(2);
            this.addon.logger().info("[{}] Graffiti {} remaining time: 15:00", getClass(), graffitiName);
            Graffiti.fromName(graffitiName).ifPresent(graffiti -> {
                Duration remainingTime = ofSeconds(15 * 60);
                fireEvent(new GraffitiUpdateEvent(graffiti, remainingTime));
            });
            return;
        }

        Matcher graffitiTimeMatcher = GRAFFITI_TIME_PATTERN.matcher(plainText);
        if (graffitiTimeMatcher.matches()) {
            long minutes = ofNullable(graffitiTimeMatcher.group("minutes")).map(Long::parseLong).orElse(0L);
            long seconds = ofNullable(graffitiTimeMatcher.group("seconds")).map(Long::parseLong).orElse(0L);

            Graffiti nearestGraffiti = navigationService.getNearest(null, List.of(Graffiti.values()));
            this.addon.logger().info("[{}] Graffiti {} remaining time: {}:{}", getClass(), nearestGraffiti.getName(), graffitiTimeMatcher.group("minutes"), graffitiTimeMatcher.group("seconds"));

            Duration remainingTime = ofSeconds(minutes * 60 + seconds);
            fireEvent(new GraffitiUpdateEvent(nearestGraffiti, remainingTime));
            return;
        }
    }

    @Subscribe
    public void onGameTick(GameTickEvent event) {
        if (event.phase().equals(POST)) {
            this.currentTick++;

            labyAPI().eventBus().fire(new GermanRPAddonTickEvent(TICK));

            // 0,25 SECONDS
            if (this.currentTick % 5 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(TICK_5));
            }

            // 1 SECOND
            if (this.currentTick % 20 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND));
            }

            // 3 SECONDS
            if (this.currentTick % 60 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND_3));
            }

            // 5 SECONDS
            if (this.currentTick % 100 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND_5));
            }

            // 30 SECONDS
            if (this.currentTick % 600 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(SECOND_30));
            }

            // 1 MINUTE
            if (this.currentTick % 1200 == 0) {
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(GermanRPAddonTickEvent.Phase.MINUTE));
            }
        }
    }
}

