package com.tiers.profiles;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.profiles.types.MCTiersCOMProfile;
import com.tiers.profiles.types.MCTiersIOProfile;
import com.tiers.profiles.types.SubtiersNETProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlayerProfile {
    public Status status = Status.SEARCHING;

    public String name;
    public String uuid;

    public MCTiersCOMProfile mcTiersCOMProfile;
    public MCTiersIOProfile mcTiersIOProfile;
    public SubtiersNETProfile subtiersNETProfile;

    public Text originalNameText;
    public boolean imageSaved = false;
    private int numberOfRequests = 0;
    public int numberOfImageRequests = 0;

    public PlayerProfile(String name) {
        this.name = name;
        originalNameText = Text.of(name);
    }

    public void buildRequest(String name) {
        if (numberOfRequests == 12 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }

        numberOfRequests++;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                    .header("User-Agent", "Tiers")
                    .GET()
                    .build();

            HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        int statusCode = response.statusCode();

                        if (statusCode != 200 && statusCode != 404) {
                            long delay = switch (numberOfRequests) {
                                case 1 -> 50;
                                case 2 -> 100;
                                case 3 -> 200;
                                case 4 -> 500;
                                case 5 -> 800;
                                case 6 -> 1200;
                                default -> 2000;
                            };
                            CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
                                    .execute(() -> buildRequest(name));
                            return;
                        }
                        if (statusCode == 404) {
                            status = Status.NOT_EXISTING;
                            return;
                        }
                        parseUUID(response.body());
                    })
                    .exceptionally(exception -> {
                        CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS)
                                .execute(() -> buildRequest(name));
                        return null;
                    });
        } catch (IllegalArgumentException ignored) {
            status = Status.NOT_EXISTING;
        }
    }

    private void parseUUID(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("id")) {
            name = jsonObject.get("name").getAsString();
            uuid = jsonObject.get("id").getAsString();
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        savePlayerImage();
        fetchProfiles();
    }

    private void savePlayerImage() {
        if (numberOfImageRequests == 5)
            return;
        numberOfImageRequests++;
        String imageUrl = "https://mc-heads.net/body/" + uuid;
        String savePath = FabricLoader.getInstance().getConfigDir() + "/tiers-cache/" + uuid + ".png";
        try {
            Files.createDirectories(Paths.get(FabricLoader.getInstance().getConfigDir() + "/tiers-cache/"));
            ImageIO.write(ImageIO.read(URI.create(imageUrl).toURL()), "png", new File(savePath));
            imageSaved = true;
        } catch (IOException ignored) {
            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS)
                    .execute(this::savePlayerImage);
        }
    }

    private void fetchProfiles() {
        mcTiersCOMProfile = new MCTiersCOMProfile(uuid);
        mcTiersIOProfile = new MCTiersIOProfile(uuid);
        subtiersNETProfile = new SubtiersNETProfile(uuid);

        status = Status.READY;
    }

    public void resetDrawnStatus() {
        if (mcTiersCOMProfile == null || mcTiersIOProfile == null || subtiersNETProfile == null)
            return;
        mcTiersCOMProfile.drawn = false;
        mcTiersIOProfile.drawn = false;
        subtiersNETProfile.drawn = false;
        for (GameMode mode : mcTiersCOMProfile.gameModes)
            mode.drawn = false;
        for (GameMode mode : mcTiersIOProfile.gameModes)
            mode.drawn = false;
        for (GameMode mode : subtiersNETProfile.gameModes)
            mode.drawn = false;
    }
}