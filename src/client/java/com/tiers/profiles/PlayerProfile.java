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
        buildRequest(name);
    }

    private void buildRequest(String name) {
        if (numberOfRequests == 4 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }
        numberOfRequests++;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                .header("User-Agent", "Tiers")
                .GET()
                .build();

        HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200)
                        status = Status.NOT_EXISTING;
                    else parseUUID(response.body());
                })
                .exceptionally(exception -> {
                    buildRequest(name);
                    return null;
                });
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
            savePlayerImage();
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