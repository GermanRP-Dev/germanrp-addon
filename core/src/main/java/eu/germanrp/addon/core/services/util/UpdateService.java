package eu.germanrp.addon.core.services.util;

import com.google.gson.JsonObject;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.util.GsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class UpdateService {

    public static final int HASH_LENGTH = 40;
    private final GermanRPAddon addon;

    public UpdateService(final GermanRPAddon addon) {
        this.addon = addon;
    }

    private static final URI GITHUB_API_NIGHTLY_TAG_URL =
            URI.create("https://api.github.com/repos/GermanRP-Dev/germanrp-addon/git/ref/tags/nightly");

    private static final HttpRequest NIGHTLY_TAG_REQUEST =
            HttpRequest.newBuilder().uri(GITHUB_API_NIGHTLY_TAG_URL).header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28").build();

    public boolean isUpdateAvailable() {
        final Optional<String> currentAddonHash = getCurrentAddonHash();

        final Optional<String> latestTagHash = getLatestTagHash();
        if (latestTagHash.isEmpty()) {
            return false;
        }

        final String latestHashOfNightly = latestTagHash.get();

        return currentAddonHash.filter(s -> !latestHashOfNightly.equals(s)).isPresent();
    }

    private Optional<String> getCurrentAddonHash() {
        // nightly release version form: nightly/ffac537e6cbbf934b08745a378932722df287a53
        final String currentAddonVersion = this.addon.addonInfo().getVersion();

        if(!(currentAddonVersion.startsWith("nightly/") && currentAddonVersion.length() == "nightly/".length() + HASH_LENGTH)) {
            return Optional.empty();
        }

        return Optional.of(currentAddonVersion.substring(
                currentAddonVersion.length() - HASH_LENGTH,
                currentAddonVersion.length() - 1
        ));
    }

    private static Optional<String> getLatestTagHash() {
        final HttpClient client = HttpClient.newHttpClient();
        try (client) {
            final HttpResponse<String> response =
                    client.send(NIGHTLY_TAG_REQUEST, HttpResponse.BodyHandlers.ofString());

            final JsonObject jsonResponse =
                    requireNonNull(GsonUtil.DEFAULT_GSON.fromJson(response.body(), JsonObject.class));

            final JsonObject jsonObject = requireNonNull(jsonResponse.getAsJsonObject("object"));

            final String hash = requireNonNull(jsonObject.get("sha").getAsString());

            return Optional.of(hash);

        } catch (IOException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }

}
