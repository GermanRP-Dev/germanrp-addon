package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.executor.HitResultExecutor;
import eu.germanrp.addon.core.services.GraffitiService;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraffitiListener {

    private static final Pattern GRAFFITI_ADDED_PATTERN = Pattern.compile(
            "^► \\[✦] (?:\\[GR]|)(?<player>\\w{3,16}) hat (?:(das Graffiti \\((?<graffiti1>\\S* \\S*)\\) aufgefrischt)|(ein Graffiti angebracht \\((?<graffiti2>\\S*\\s*\\S*)\\)))!$");
    private static final Pattern GRAFFITI_TIME_PATTERN = Pattern.compile(
            "^► Diese Stelle kann noch nicht wieder besprüht werden \\((?:(?<minutes>\\d+)\\s+Minuten?)?(?:\\s*(?<seconds>\\d+)\\s+Sekunden?)?\\)$",
            Pattern.CANON_EQ
    );

    private final GermanRPAddon addon;
    private final HitResultExecutor hitResultExecutor;
    private final GraffitiService graffitiService;

    public GraffitiListener(final @NotNull GermanRPAddon addon) {
        this.addon = addon;
        this.hitResultExecutor = addon.getHitResultExecutor();
        this.graffitiService = addon.getGraffitiService();
    }

    @Subscribe
    public void onGraffitiUpdate(final @NotNull ChatReceiveEvent event) {
        final String plainText = event.chatMessage().getPlainText();
        final Matcher matcher = GRAFFITI_ADDED_PATTERN.matcher(plainText);

        if (!matcher.matches()) {
            return;
        }

        final String graffitiName =
                matcher.group("graffiti1") != null ? matcher.group("graffiti1") : matcher.group("graffiti2");

        Graffiti.getByName(graffitiName)
                .ifPresent(graffiti -> graffitiService.updateGraffiti(graffiti, Instant.now().plusSeconds(60L * 15)));
    }

    @Subscribe
    public void onGraffitiTimeUpdate(final @NotNull ChatReceiveEvent event) {
        final String plainText = event.chatMessage().getPlainText();
        final Matcher matcher = GRAFFITI_TIME_PATTERN.matcher(plainText);

        if (!matcher.matches()) {
            return;
        }

        final long minutes = matcher.group("minutes") != null ? Long.parseLong(matcher.group("minutes")) : 0;
        final long seconds = matcher.group("seconds") != null ? Long.parseLong(matcher.group("seconds")) : 0;

        hitResultExecutor.getBlockLookingAt().flatMap(Graffiti::getByBlockPosition).ifPresent(graffiti -> {
            addon.displayMessage(Component.text("Updated graffiti time: ").append(Component.text(graffiti.getName())));
            graffitiService.updateGraffiti(graffiti, Instant.now().plusSeconds((minutes * 60) + seconds));
        });
    }
}

