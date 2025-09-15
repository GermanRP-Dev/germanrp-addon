package eu.germanrp.addon.core.listener;

import com.google.gson.JsonObject;
import eu.germanrp.addon.api.events.network.HydrationUpdateEvent;
import eu.germanrp.addon.api.events.plant.PlantCreateEvent;
import eu.germanrp.addon.api.events.plant.PlantDestroyEvent;
import eu.germanrp.addon.api.events.plant.PlantPacketReceiveEvent;
import eu.germanrp.addon.api.models.Graffiti;
import eu.germanrp.addon.api.models.PlantType;
import eu.germanrp.addon.api.network.PlantPacket;
import eu.germanrp.addon.api.network.TimerPacket;
import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.*;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.util.GsonUtil;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.*;
import static eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent.Phase.*;
import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
import static net.labymod.api.Laby.fireEvent;
import static net.labymod.api.Laby.labyAPI;
import static net.labymod.api.event.Phase.POST;

public class EventRegistrationListener {

    private final GermanRPAddon addon;

    private long currentTick = 0;

    public EventRegistrationListener(final @NotNull GermanRPAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        String plainText = event.chatMessage().getPlainText();

        Matcher graffitiAddedMatcher = GRAFFITI_ADD.getPattern().matcher(plainText);
        if (graffitiAddedMatcher.matches()) {
            String graffitiName = graffitiAddedMatcher.group(2);
            this.addon.logger().info("[{}] Graffiti {} remaining time: 15:00", getClass(), graffitiName);
            Graffiti.fromName(graffitiName).ifPresent(graffiti -> {
                Duration remainingTime = ofSeconds(900); // 15 minutes
                fireEvent(new GraffitiUpdateEvent(graffiti, remainingTime));
            });
            return;
        }

        Matcher graffitiTimeMatcher = GRAFFITI_TIME.getPattern().matcher(plainText);
        if (graffitiTimeMatcher.matches()) {
            final long minutes = ofNullable(graffitiTimeMatcher.group("minutes")).map(Long::parseLong).orElse(0L);
            final long seconds = ofNullable(graffitiTimeMatcher.group("seconds")).map(Long::parseLong).orElse(0L);

            final ClientPlayer clientPlayer = labyAPI().minecraft().getClientPlayer();

            if (clientPlayer == null) {
                return;
            }

            final Optional<Graffiti> nearestGraffitiCandidate = this.addon.getNavigationService().getNearest(clientPlayer.position(), List.of(Graffiti.values()));

            if (nearestGraffitiCandidate.isEmpty()) {
                return;
            }

            final Graffiti nearestGraffiti = nearestGraffitiCandidate.get();
            this.addon.logger().info(
                    "[{}] Graffiti {} remaining time: {}:{}",
                    getClass(),
                    nearestGraffiti.getName(),
                    graffitiTimeMatcher.group("minutes"),
                    graffitiTimeMatcher.group("seconds")
            );

            final Duration remainingTime = ofSeconds(minutes * 60 + seconds);
            fireEvent(new GraffitiUpdateEvent(nearestGraffiti, remainingTime));
        }

        Matcher plantHarvestMatcher = PLANT_HARVEST.getPattern().matcher(plainText);
        if (plantHarvestMatcher.find()) {
            final String displayName = plantHarvestMatcher.group(1);
            PlantType.fromDisplayName(displayName).ifPresent(plantType -> fireEvent(new PlantDestroyEvent(plantType)));
            return;
        }

        PlantType.fromSowMessage(plainText).ifPresent(plantType -> fireEvent(new PlantCreateEvent(plantType)));
    }

    @Subscribe
    public void onNetworkPayloadEvent(final NetworkPayloadEvent event) {
        if (!this.addon.getUtilService().isLegacyAddonPacket(event.identifier())) {
            return;
        }

        final PayloadReader payloadReader = new PayloadReader(event.getPayload());
        final String header = payloadReader.readString();

        if (!header.startsWith("GRAddon-")) {
            return;
        }

        final String payload = payloadReader.readString();
        final JsonObject jsonObject = GsonUtil.DEFAULT_GSON.fromJson(payload, JsonObject.class);

        this.addon.logger().info("Legacy packet received: {} - {}", header, jsonObject);

        fireEvent(new LegacyGermanRPUtilsPayloadEvent(header, jsonObject));
    }

    @Subscribe
    public void onLegacyGermanRPUtilsPayloadEvent(final LegacyGermanRPUtilsPayloadEvent event) {
        switch (event.getHeader()) {
            case "GRAddon-Plant" -> {
                final JsonObject payloadContent = event.getPayloadContent();
                final Optional<PlantType> type = PlantType.fromPaketType(payloadContent.get("type").getAsString());

                if (type.isEmpty()) {
                    return;
                }

                final JsonObject time = payloadContent.getAsJsonObject("time");

                final PlantPacket plantPaket = new PlantPacket(
                        payloadContent.get("active").getAsBoolean(),
                        type.get(),
                        payloadContent.get("value").getAsInt(),
                        time.get("current").getAsInt(),
                        time.get("max").getAsInt()
                );

                // Ignore the first paket that is sent by the server
                // which would normally be used to create a plant
                // we use the chat message to create a plant
                // and not the first packet
                if (plantPaket.getCurrentTime() == 0) {
                    return;
                }

                fireEvent(new PlantPacketReceiveEvent(plantPaket));
            }
            case "GRAddon-Hydration" -> {
                final JsonObject payloadContent = event.getPayloadContent();
                final double hydration = payloadContent.get("value").getAsDouble();
                fireEvent(new HydrationUpdateEvent(hydration));
            }
            case "GRAddon-PayDay" -> {
                final JsonObject payloadContent = event.getPayloadContent();
                fireEvent(new PayDayPacketRecieveEvent(
                        payloadContent.get("time").getAsInt(),
                        payloadContent.get("salary").getAsJsonObject().get("faction").getAsFloat(),
                        payloadContent.get("salary").getAsJsonObject().get("job").getAsFloat()));
            }
            case "GRAddon-Timer" -> {
                final JsonObject payloadContent = event.getPayloadContent();

                final TimerPacket timerPacket = new TimerPacket(
                        payloadContent.get("active").getAsBoolean(),
                        payloadContent.get("name").getAsString(),
                        payloadContent.get("start").getAsLong()
                );

                fireEvent(new MajorWidgetUpdateEvent(timerPacket));
            }
            default -> {
                // Ignore unknown pakets
            }
        }
    }

    @Subscribe
    public void onServerDisconnectEvent(final ServerDisconnectEvent event) {
        Arrays.stream(PlantType.values()).forEach(plantType -> fireEvent(new PlantDestroyEvent(plantType)));
    }

    @Subscribe
    public void onGameTick(GameTickEvent event) {
        if (event.phase() == POST) {
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
                labyAPI().eventBus().fire(new GermanRPAddonTickEvent(MINUTE));
            }
        }
    }
}

