package com.tiers.profiles.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;
import com.tiers.profiles.Status;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class BaseProfile {
    public Status status = Status.SEARCHING;

    public String region;
    public int points;
    public int overallPosition;
    public boolean combatMaster;

    public Text displayedRegion;
    public Text displayedOverall;
    public Text overallTooltip;
    public Text regionTooltip;

    public ArrayList<GameMode> gameModes = new ArrayList<>();

    public GameMode highest;
    public boolean drawn = false;
    private int numberOfRequests = 0;

    protected BaseProfile(String uuid, String apiUrl) {
        buildRequest(uuid, apiUrl);
    }

    private void buildRequest(String uuid, String apiUrl) {
        if (numberOfRequests == 5 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }
        numberOfRequests++;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + uuid))
                .header("User-Agent", "Tiers")
                .GET()
                .build();

        HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200)
                        status = Status.NOT_EXISTING;
                    else parseInfo(response.body());
                })
                .exceptionally(exception -> {
                    buildRequest(uuid, apiUrl);
                    return null;
                });
    }

    private void parseInfo(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("region") && jsonObject.has("points") && jsonObject.has("overall")) {
            if (!jsonObject.get("region").isJsonNull())
                region = jsonObject.get("region").getAsString();
            else region = "Unknown";
            points = jsonObject.get("points").getAsInt();
            combatMaster = points >= 250;
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

    public GameMode getGameMode(TiersClient.Modes gamemode) {
        for (GameMode gameMode : gameModes) {
            if (gameMode.name.toString().equalsIgnoreCase(gamemode.toString()))
                return gameMode;
        }
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
        if (region.equalsIgnoreCase("EU")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0x89f19c));
        else if (region.equalsIgnoreCase("NA")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xd95c6a));
        else if (region.equalsIgnoreCase("AS")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xaf7f91));
        else if (region.equalsIgnoreCase("AU")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xd5ad80));
        else if (region.equalsIgnoreCase("SA")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0x5bc9d9));
        else if (region.equalsIgnoreCase("ME")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xffda58));
        return Text.literal("Unknown").setStyle(Style.EMPTY.withColor(0x222222));
    }

    private Text getRegionTooltip() {
        if (region.equalsIgnoreCase("EU")) return Text.literal("Europe").setStyle(Style.EMPTY.withColor(0x89f19c));
        else if (region.equalsIgnoreCase("NA")) return Text.literal("North America").setStyle(Style.EMPTY.withColor(0xd95c6a));
        else if (region.equalsIgnoreCase("AS")) return Text.literal("Asia").setStyle(Style.EMPTY.withColor(0xaf7f91));
        else if (region.equalsIgnoreCase("AU")) return Text.literal("Australia").setStyle(Style.EMPTY.withColor(0xd5ad80));
        else if (region.equalsIgnoreCase("SA")) return Text.literal("South America").setStyle(Style.EMPTY.withColor(0x5bc9d9));
        else if (region.equalsIgnoreCase("ME")) return Text.literal("Middle East").setStyle(Style.EMPTY.withColor(0xffda58));
        return Text.literal("Unknown").setStyle(Style.EMPTY.withColor(0x222222));
    }

    private Text getOverallText() {
        if (combatMaster) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xfde047));
        else if (points >= 100) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xfda4af));
        else if (points >= 50) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xd8b4fe));
        else if (points >= 20) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xc4b5fd));
        else if (points >= 10) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xc4b5fd));
        return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xd1d5db));
    }

    private Text getOverallTooltip() {
        String overallTooltip = "Combat ";
        if (this instanceof SubtiersNETProfile)
            overallTooltip = "Subtiers ";
        if (combatMaster) overallTooltip += "Master";
        else if (points >= 100) overallTooltip += "Ace";
        else if (points >= 50) overallTooltip += "Specialist";
        else if (points >= 20) overallTooltip += "Cadet";
        else if (points >= 10) overallTooltip += "Novice";
        else overallTooltip = "Rookie";
        overallTooltip += "\n\nPoints: " + points;

        return Text.literal(overallTooltip).setStyle(displayedOverall.getStyle());
    }
}