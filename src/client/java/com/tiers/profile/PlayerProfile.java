package com.tiers.profile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.TiersClient;
import com.tiers.profile.types.MCTiersCOMProfile;
import com.tiers.profile.types.MCTiersIOProfile;
import com.tiers.profile.types.SubtiersNETProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.tiers.TiersClient.*;

public class PlayerProfile {
    public Status status = Status.SEARCHING;

    public String name = "";
    public String uuid = "";

    public MCTiersCOMProfile mcTiersCOMProfile;
    public MCTiersIOProfile mcTiersIOProfile;
    public SubtiersNETProfile subtiersNETProfile;

    public Text originalNameText;
    public boolean imageSaved = false;
    public int numberOfImageRequests = 0;
    private int numberOfRequests = 0;
    private final boolean regular;

    public PlayerProfile(String name, boolean regular) {
        this.regular = regular;
        this.name = name;
        originalNameText = Text.of(name);
    }

    public PlayerProfile(String mojangJson, String mcTiersCOMJson, String mcTiersIOJson, String subtiersNETJson) {
        regular = false;
        JsonObject jsonObject = JsonParser.parseString(mojangJson).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("id")) {
            this.name = jsonObject.get("name").getAsString();
            uuid = jsonObject.get("id").getAsString();
            originalNameText = Text.of(name);
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        Path targetFile = FabricLoader.getInstance().getGameDir().resolve("cache/tiers/3b653c04f2d9422a87e7ccf8b146c350.png");

        try (InputStream inputStream = TiersClient.class.getResourceAsStream("default.png")) {
            if (inputStream == null) throw new IOException();

            Files.createDirectories(targetFile.getParent());
            Files.copy(inputStream, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            LOGGER.warn("Error copying default image");
        }

        mcTiersCOMProfile = new MCTiersCOMProfile(mcTiersCOMJson);
        mcTiersIOProfile = new MCTiersIOProfile(mcTiersIOJson);
        subtiersNETProfile = new SubtiersNETProfile(subtiersNETJson);

        status = Status.READY;
    }

    public void buildRequest(String name) {
        if (!name.matches("^[a-zA-Z0-9_]{3,16}$") || name.contains(".")) {
            status = Status.NOT_EXISTING;
            return;
        }

        if (numberOfRequests > 4) {
            backupRequest("https://api.mojang.com/users/profiles/minecraft/", name);
            return;
        }

        numberOfRequests++;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://playerdb.co/api/player/minecraft/" + name))
                    .header("User-Agent", userAgent)
                    .GET()
                    .build();

            HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        int statusCode = response.statusCode();

                        if (statusCode == 400 || statusCode == 500) {
                            status = Status.NOT_EXISTING;
                            return;
                        } else if (response.statusCode() != 200) {
                            backupRequest("https://api.mojang.com/users/profiles/minecraft/", name);
                            return;
                        }

                        parseJson(response.body());
                    })
                    .exceptionally(exception -> {
                        CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> buildRequest(name));
                        return null;
                    });
        } catch (IllegalArgumentException ignored) {
            status = Status.NOT_EXISTING;
        }
    }

    public void backupRequest(String apiUrl, String name) {
        if (numberOfRequests > 11 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }

        numberOfRequests++;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + name))
                    .header("User-Agent", userAgent)
                    .GET()
                    .build();

            HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.body().contains("minecraft/profile/lookup")) {
                            status = Status.API_ISSUE;
                            return;
                        }

                        int statusCode = response.statusCode();

                        if (statusCode == 404 || statusCode == 400) {
                            status = Status.NOT_EXISTING;
                            return;
                        } else if (statusCode == 403) {
                            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(() -> backupRequest("https://api.minecraftservices.com/minecraft/profile/lookup/name/", name));
                            return;
                        } else if (statusCode != 200) {
                            long delay = switch (numberOfRequests) {
                                case 1 -> 50;
                                case 2, 3 -> 100;
                                case 4, 5 -> 400;
                                case 6, 7 -> 900;
                                default -> 1500;
                            };
                            CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS).execute(() -> backupRequest(apiUrl, name));
                            return;
                        }

                        parseJson(response.body());
                    })
                    .exceptionally(exception -> {
                        CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> backupRequest(apiUrl, name));
                        return null;
                    });
        } catch (IllegalArgumentException ignored) {
            status = Status.NOT_EXISTING;
        }
    }

    private void parseJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("code") && jsonObject.has("data") && jsonObject.has("success")) {
            if (!jsonObject.get("success").getAsString().contains("true")) {
                backupRequest("https://api.mojang.com/users/profiles/minecraft/", name);
                return;
            }
            JsonObject data = jsonObject.getAsJsonObject("data");
            if (data.has("player")) {
                JsonObject player = data.getAsJsonObject("player");
                if (player.has("username") && player.has("raw_id")) {
                    name = player.get("username").getAsString();
                    uuid = player.get("raw_id").getAsString();
                    originalNameText = Text.of(name);
                }
            }
        } else if (jsonObject.has("name") && jsonObject.has("id")) {
            name = jsonObject.get("name").getAsString();
            uuid = jsonObject.get("id").getAsString();
            originalNameText = Text.of(name);
        }

        if (uuid.isEmpty()) {
            status = Status.NOT_EXISTING;
            return;
        }

        if (!regular)
            savePlayerImage();

        mcTiersCOMProfile = new MCTiersCOMProfile(uuid, "https://mctiers.com/api/profile/");
        mcTiersIOProfile = new MCTiersIOProfile(uuid, "https://mctiers.io/api/profile/");
        subtiersNETProfile = new SubtiersNETProfile(uuid, "https://subtiers.net/api/profile/");

        status = Status.READY;
    }

    public void savePlayerImage() {
        if (numberOfImageRequests == 5)
            return;
        numberOfImageRequests++;
        String savePath = FabricLoader.getInstance().getGameDir() + "/cache/tiers/" + (regular ? "players/" : "");
        CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(Paths.get(savePath));
                ImageIO.write(ImageIO.read(URI.create("https://mc-heads.net/body/" + uuid).toURL()), "png", new File(savePath + uuid + ".png"));
                imageSaved = true;
            } catch (IOException ignored) {
                CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(this::savePlayerImage);
            }
        });
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