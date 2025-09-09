package com.tiers.profile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.misc.Mode;
import com.tiers.profile.types.MCTiersProfile;
import com.tiers.profile.types.PvPTiersProfile;
import com.tiers.profile.types.SubtiersProfile;
import com.tiers.profile.types.SuperProfile;
import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.tiers.TiersClient.*;

public class PlayerProfile {
    public Status status;
    public int imageSaved;
    public int numberOfImageRequests;
    public String originalName;
    public boolean nameChanged;

    public String name = "";
    public String uuid = "";

    public MCTiersProfile profileMCTiers;
    public PvPTiersProfile profilePvPTiers;
    public SubtiersProfile profileSubtiers;

    public Text toAppendLeft;
    public Text toAppendRight;

    private int numberOfRequests;
    private final boolean regular;

    public PlayerProfile(String name, boolean regular) {
        this.regular = regular;
        this.name = name;
        originalName = name;

        status = !name.matches("^[a-zA-Z0-9_]{3,16}$") || name.contains(".") || name.length() < 3 || name.length() > 16 ? Status.NOT_PLAYER : Status.SEARCHING;
    }

    public PlayerProfile(String mojangJson, String jsonMCTiers, String jsonPvPTiers, String jsonSubtiers) {
        regular = false;
        JsonObject jsonObject = JsonParser.parseString(mojangJson).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("id")) {
            this.name = jsonObject.get("name").getAsString();
            uuid = jsonObject.get("id").getAsString();
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        Path path = FabricLoader.getInstance().getGameDir().resolve("cache/tiers/3b653c04f2d9422a87e7ccf8b146c350.png");

        try (InputStream inputStream = MinecraftClient.getInstance().getResourceManager().getResource(Identifier.of("minecraft", "textures/default.png")).orElseThrow().getInputStream()) {
            if (inputStream == null) throw new IOException();

            Files.createDirectories(path.getParent());
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            LOGGER.warn("Error copying default skin");
        }

        profileMCTiers = new MCTiersProfile(jsonMCTiers);
        profilePvPTiers = new PvPTiersProfile(jsonPvPTiers);
        profileSubtiers = new SubtiersProfile(jsonSubtiers);

        updateAppendingText();

        status = Status.READY;
    }

    public void buildRequest() {
        if (status != Status.SEARCHING)
            return;

        if (numberOfRequests == 3) {
            buildRequest("https://api.mojang.com/users/profiles/minecraft/");
            return;
        }

        numberOfRequests++;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://playerdb.co/api/player/minecraft/" + name))
                .header("User-Agent", userAgent)
                .GET()
                .build();

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
                int statusCode = response.statusCode();

                if (statusCode == 400 || statusCode == 500) {
                    status = Status.NOT_EXISTING;
                    return;
                } else if (statusCode != 200) {
                    buildRequest("https://api.mojang.com/users/profiles/minecraft/");
                    return;
                }
                parseJson(response.body());
            }).exceptionally(ignored -> {
                CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(this::buildRequest);
                return null;
            });
        }
    }

    public void buildRequest(String apiUrl) {
        if (numberOfRequests == 12 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }

        numberOfRequests++;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + name))
                .header("User-Agent", userAgent)
                .GET()
                .build();

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
                if (response.body().contains("minecraft/profile/lookup")) {
                    status = Status.API_ISSUE;
                    return;
                }

                int statusCode = response.statusCode();

                if (statusCode == 404 || statusCode == 400) {
                    status = Status.NOT_EXISTING;
                    return;
                } else if (statusCode == 403) {
                    CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(() -> buildRequest("https://api.minecraftservices.com/minecraft/profile/lookup/name/"));
                    return;
                } else if (statusCode != 200) {
                    long delay = switch (numberOfRequests) {
                        case 1 -> 50;
                        case 2, 3 -> 100;
                        case 4, 5 -> 400;
                        case 6, 7 -> 900;
                        default -> 1500;
                    };
                    CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS).execute(() -> buildRequest(apiUrl));
                    return;
                }
                parseJson(response.body());
            }).exceptionally(ignored -> {
                CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> buildRequest(apiUrl));
                return null;
            });
        }
    }

    public void savePlayerImage() {
        String apiUrl = "https://mc-heads.net/body/";

        if (numberOfImageRequests == 2)
            apiUrl = "https://visage.surgeplay.com/full/432/";
        else if (numberOfImageRequests == 4)
            apiUrl = "https://render.crafty.gg/3d/full/";
        else if (numberOfImageRequests == 6)
            return;

        final String finalApiUrl = apiUrl + uuid;

        numberOfImageRequests++;

        String path = FabricLoader.getInstance().getGameDir() + "/cache/tiers/" + (regular ? "players/" : "");
        CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(Paths.get(path));
                URL uri = new URI(finalApiUrl).toURL();
                HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", userAgent);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);

                try (InputStream inputStream = httpURLConnection.getInputStream()) {
                    ImageIO.write(ImageIO.read(inputStream), "png", new File(path + uuid + ".png"));
                    imageSaved = numberOfImageRequests;
                }
            } catch (IOException | URISyntaxException ignored) {
                CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(this::savePlayerImage);
            }
        });
    }

    private void parseJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("code") && jsonObject.has("data") && jsonObject.has("success")) {
            if (!jsonObject.get("success").getAsString().contains("true")) {
                buildRequest("https://api.mojang.com/users/profiles/minecraft/");
                return;
            }
            JsonObject data = jsonObject.getAsJsonObject("data");
            if (data.has("player")) {
                JsonObject player = data.getAsJsonObject("player");
                if (player.has("username") && player.has("raw_id") && player.has("id")) {
                    name = player.get("username").getAsString();
                    uuid = player.get("raw_id").getAsString();
                }
            }
        } else if (jsonObject.has("name") && jsonObject.has("id")) {
            name = jsonObject.get("name").getAsString();
            uuid = jsonObject.get("id").getAsString();
        }

        if (uuid.isEmpty()) {
            status = Status.NOT_EXISTING;
            return;
        }

        if (!regular)
            savePlayerImage();

        profileMCTiers = new MCTiersProfile(uuid, "https://mctiers.com/api/profile/");
        profilePvPTiers = new PvPTiersProfile(uuid, "https://pvptiers.com/api/profile/");
        profileSubtiers = new SubtiersProfile(uuid, "https://subtiers.net/api/profile/");

        updateAppendingText();

        if (!originalName.equalsIgnoreCase(name))
            nameChanged = true;

        status = Status.READY;
    }

    public void updateAppendingText() {
        toAppendRight = Text.empty();
        toAppendLeft = Text.empty();

        if (positionMCTiers == DisplayStatus.RIGHT)
            toAppendRight = updateProfileNametagRight(profileMCTiers, activeMCTiersMode);
        else if (positionMCTiers == DisplayStatus.LEFT)
            toAppendLeft = updateProfileNametagLeft(profileMCTiers, activeMCTiersMode);

        if (positionPvPTiers == DisplayStatus.RIGHT)
            toAppendRight = updateProfileNametagRight(profilePvPTiers, activePvPTiersMode);
        else if (positionPvPTiers == DisplayStatus.LEFT)
            toAppendLeft = updateProfileNametagLeft(profilePvPTiers, activePvPTiersMode);

        if (positionSubtiers == DisplayStatus.RIGHT)
            toAppendRight = updateProfileNametagRight(profileSubtiers, activeSubtiersMode);
        else if (positionSubtiers == DisplayStatus.LEFT)
            toAppendLeft = updateProfileNametagLeft(profileSubtiers, activeSubtiersMode);
    }

    public Text getFullNametag() {
        Text playerText = nameChanged ? Text.of(originalName + " (AKA " + name + ")") : Text.of(name);

        if (!toggleMod)
            return playerText;

        updateAppendingText();
        return Text.empty()
                .append(toAppendLeft)
                .append(playerText)
                .append(toAppendRight);
    }

    public Text getFullNametag(Text original) {
        if (status != Status.READY)
            return original;

        return Text.empty()
                .append(toAppendLeft)
                .append(original)
                .append(toAppendRight);
    }

    private Text updateProfileNametagRight(SuperProfile superProfile, Mode activeMode) {
        MutableText returnValue = Text.empty();

        if (superProfile != null && superProfile.status == Status.READY) {
            GameMode shown = superProfile.getGameMode(activeMode);

            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED))
                return returnValue;

            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && superProfile.highest != null)
                shown = superProfile.highest;

            if (displayMode == ModesTierDisplay.HIGHEST && superProfile.highest != null && superProfile.highest.getTierPoints(false) > shown.getTierPoints(false))
                shown = superProfile.highest;

            if (shown == null || shown.status != Status.READY)
                return returnValue;

            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));
            returnValue.append(Text.empty().append(separator).append(shown.displayedTier));

            if (showIcons)
                returnValue.append(Text.literal(" ").append(shown.gamemode.getIconTag()));
        }
        return returnValue;
    }

    private Text updateProfileNametagLeft(SuperProfile superProfile, Mode activeMode) {
        MutableText returnValue = Text.empty();

        if (superProfile != null && superProfile.status == Status.READY) {
            GameMode shown = superProfile.getGameMode(activeMode);

            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED))
                return returnValue;

            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && superProfile.highest != null)
                shown = superProfile.highest;

            if (displayMode == ModesTierDisplay.HIGHEST && superProfile.highest != null && superProfile.highest.getTierPoints(false) > shown.getTierPoints(false))
                shown = superProfile.highest;

            if (shown == null || shown.status != Status.READY)
                return returnValue;

            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));

            if (showIcons)
                returnValue = Text.empty().append(shown.gamemode.getIconTag()).append(" ");
            returnValue.append(Text.empty().append(shown.displayedTier).append(separator));
        }
        return returnValue;
    }

    public void resetDrawnStatus() {
        if (profileMCTiers == null || profilePvPTiers == null || profileSubtiers == null)
            return;
        profileMCTiers.drawn = false;
        profilePvPTiers.drawn = false;
        profileSubtiers.drawn = false;
        for (GameMode mode : profileMCTiers.gameModes)
            mode.drawn = false;
        for (GameMode mode : profilePvPTiers.gameModes)
            mode.drawn = false;
        for (GameMode mode : profileSubtiers.gameModes)
            mode.drawn = false;
    }

    public boolean isPlayerValid() {
        if (status == Status.NOT_EXISTING) {
            sendMessageToPlayer(Icons.colorText(name + " was not found or isn't a premium account", "red"), false);
            return false;
        } else if (status == Status.NOT_PLAYER) {
            sendMessageToPlayer(Icons.colorText("Not a valid player name", "red"), false);
            return false;
        } else if (status == Status.TIMEOUTED) {
            sendMessageToPlayer(Icons.colorText(name + "'s search was timeouted. Clear cache and retry", "red"), false);
            return false;
        } else if (status == Status.API_ISSUE) {
            sendMessageToPlayer(Icons.colorText(name + "'s search failed: API Issue. Update Tiers or retry in a while", "red"), false);
            return false;
        }
        return true;
    }
}