package com.tiers.profile.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;
import com.tiers.textures.ColorControl;
import com.tiers.profile.Status;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.tiers.TiersClient.userAgent;

public class SuperProfile {
    public Status status = Status.SEARCHING;

    public String region;
    public int points;
    public int overallPosition;

    public Text displayedRegion;
    public Text displayedOverall;
    public Text overallTooltip;
    public Text regionTooltip;

    public final ArrayList<GameMode> gameModes = new ArrayList<>();

    public GameMode highest;
    public String originalJson;
    public boolean drawn = false;
    private int numberOfRequests = 0;

    protected SuperProfile(String uuid, String apiUrl) {
        CompletableFuture.delayedExecutor(20, TimeUnit.MILLISECONDS).execute(() -> buildRequest(uuid, apiUrl));
    }

    protected SuperProfile(String json) {
        CompletableFuture.delayedExecutor(20, TimeUnit.MILLISECONDS).execute(() -> parseJson(json));
    }

    private void buildRequest(String uuid, String apiUrl) {
        if (numberOfRequests == 5 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }

        numberOfRequests++;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + uuid))
                .header("User-Agent", userAgent)
                .GET()
                .build();

        HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 404) {
                        status = Status.NOT_EXISTING;
                        return;
                    } else if (response.statusCode() != 200) {
                        status = Status.API_ISSUE;
                        return;
                    }

                    parseJson(response.body());
                })
                .exceptionally(exception -> {
                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> buildRequest(uuid, apiUrl));
                    return null;
                });
    }

    public void parseJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("region") &&
                jsonObject.has("points") && jsonObject.has("overall")) {
            if (!jsonObject.get("region").isJsonNull())
                region = jsonObject.get("region").getAsString();
            else
                region = "Unknown";
            points = jsonObject.get("points").getAsInt();
            overallPosition = jsonObject.get("overall").getAsInt();
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        displayedRegion = getRegionText();
        regionTooltip = getRegionTooltip();
        displayedOverall = getOverallText();
        overallTooltip = getOverallTooltip();

        parseRankings(jsonObject.getAsJsonObject("rankings"));

        status = Status.READY;
        originalJson = json;
    }

    private void parseRankings(JsonObject jsonObject) {
        for (GameMode gameMode : gameModes) {
            if (jsonObject.has(gameMode.parsingName))
                gameMode.parseTiers(jsonObject.getAsJsonObject(gameMode.parsingName));
            else
                gameMode.status = Status.NOT_EXISTING;
        }
        highest = getHighestMode();
    }

    public GameMode getGameMode(Modes gamemode) {
        for (GameMode gameMode : gameModes)
            if (gameMode.name.toString().equalsIgnoreCase(gamemode.toString()))
                return gameMode;

        status = Status.NOT_EXISTING;
        return null;
    }

    private GameMode getHighestMode() {
        GameMode highest = null;
        int highestPoints = 0;
        for (GameMode gameMode : gameModes) {
            if (gameMode.status == Status.READY && gameMode.getTierPoints(false) > highestPoints) {
                highest = gameMode;
                highestPoints = gameMode.getTierPoints(false);
            }
        }
        return highest;
    }

    private Text getRegionText() {
        if (region.equalsIgnoreCase("EU"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("eu")));
        else if (region.equalsIgnoreCase("NA"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("na")));
        else if (region.equalsIgnoreCase("AS"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("as")));
        else if (region.equalsIgnoreCase("AU"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("au")));
        else if (region.equalsIgnoreCase("SA"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("sa")));
        else if (region.equalsIgnoreCase("ME"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("me")));
        else if (region.equalsIgnoreCase("AF"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("af")));
        else if (region.equalsIgnoreCase("OC"))
            return Text.literal(region).setStyle(Style.EMPTY.withColor(ColorControl.getColor("oc")));
        return Text.literal("Unknown").setStyle(Style.EMPTY.withColor(ColorControl.getColor("unknown")));
    }

    private Text getRegionTooltip() {
        if (region.equalsIgnoreCase("EU"))
            return Text.literal("Europe").setStyle(Style.EMPTY.withColor(ColorControl.getColor("eu")));
        else if (region.equalsIgnoreCase("NA"))
            return Text.literal("North America").setStyle(Style.EMPTY.withColor(ColorControl.getColor("na")));
        else if (region.equalsIgnoreCase("AS"))
            return Text.literal("Asia").setStyle(Style.EMPTY.withColor(ColorControl.getColor("as")));
        else if (region.equalsIgnoreCase("AU"))
            return Text.literal("Australia").setStyle(Style.EMPTY.withColor(ColorControl.getColor("au")));
        else if (region.equalsIgnoreCase("SA"))
            return Text.literal("South America").setStyle(Style.EMPTY.withColor(ColorControl.getColor("sa")));
        else if (region.equalsIgnoreCase("ME"))
            return Text.literal("Middle East").setStyle(Style.EMPTY.withColor(ColorControl.getColor("me")));
        else if (region.equalsIgnoreCase("AF"))
            return Text.literal("Africa").setStyle(Style.EMPTY.withColor(ColorControl.getColor("af")));
        else if (region.equalsIgnoreCase("OC"))
            return Text.literal("Oceania").setStyle(Style.EMPTY.withColor(ColorControl.getColor("oc")));
        return Text.literal("Unknown").setStyle(Style.EMPTY.withColor(ColorControl.getColor("unknown")));
    }

    private Text getOverallText() {
        if (!(this instanceof PvPTiersProfile) && points >= 250) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("master")));
        else if (this instanceof PvPTiersProfile && points >= 200) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("master")));
        else if (points >= 100) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("ace")));
        else if (points >= 50) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("specialist")));
        else if (points >= 20) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("cadet")));
        else if (points >= 10) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("novice")));

        return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(ColorControl.getColor("rookie")));
    }

    private Text getOverallTooltip() {
        String overallTooltip = "Combat ";

        if (this instanceof SubtiersProfile)
            overallTooltip = "Subtiers ";
        if (!(this instanceof PvPTiersProfile) && points >= 400) overallTooltip += "Grandmaster";
        else if (!(this instanceof PvPTiersProfile) && points >= 250) overallTooltip += "Master";
        else if (this instanceof PvPTiersProfile && points >= 200) overallTooltip += "Master";
        else if (points >= 100) overallTooltip += "Ace";
        else if (points >= 50) overallTooltip += "Specialist";
        else if (points >= 20) overallTooltip += "Cadet";
        else if (points >= 10) overallTooltip += "Novice";
        else overallTooltip = "Rookie";
        overallTooltip += "\n\nPoints: " + points;

        return Text.literal(overallTooltip).setStyle(displayedOverall.getStyle());
    }
}