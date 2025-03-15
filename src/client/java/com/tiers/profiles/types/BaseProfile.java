package com.tiers.profiles.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.profiles.GameMode;
import com.tiers.profiles.Status;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.*;

public class BaseProfile {
    public Status status = Status.SEARCHING;
    public GameMode highest;
    public Text displayedRegion = Text.of("N/A");
    public Text displayedOverall = Text.of("N/A");
    public Text overallTooltip = Text.of("N/A");
    public Text regionTooltip = Text.of("N/A");
    public String region = "N/A";
    public int overallPosition;
    public int points;
    public boolean combatMaster = false;
    public boolean drawn = false;
    public  List<GameMode> gameModes = new ArrayList<>();
    private int numberOfRequests = 0;

    protected BaseProfile(String uuid, String apiUrl) {
        buildRequest(uuid, apiUrl);
    }

    private void buildRequest(String uuid, String apiUrl) {
        if (numberOfRequests == 5) {
            status = Status.TIMEOUTED;
            return;
        }
        numberOfRequests++;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + uuid))
                .header("User-Agent", "TiersMod")
                .GET()
                .build();

        HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200)
                        status = Status.NOT_EXISTING;
                    else
                        parseInfo(response.body());
                })
                .exceptionally(exception -> {
                    buildRequest(uuid, apiUrl);
                    return null;
                });
    }

    private void parseInfo(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (!jsonObject.has("name")) {
            status = Status.NOT_EXISTING;
            return;
        }

        if (!jsonObject.get("region").isJsonNull()) region = jsonObject.get("region").getAsString();
        overallPosition = jsonObject.get("overall").getAsInt();
        points = jsonObject.get("points").getAsInt();
        displayedRegion = getRegionText(region);
        displayedOverall = getOverallText(overallPosition);
        overallTooltip = getOverallTooltip();
        regionTooltip = getRegionTooltip(region);
        parseRankings(jsonObject.getAsJsonObject("rankings"));
    }

    private void parseRankings(JsonObject jsonObject) {
        for (GameMode gameMode : gameModes) {
            if (jsonObject.has(gameMode.parsingName)) {
                gameMode.parseTiers(jsonObject.getAsJsonObject(gameMode.parsingName));
            }
        }
        if (this instanceof MCTiersIOProfile) {
            if (points > (8 * 16))
                combatMaster = true;
        } else if (this instanceof  SubtiersNETProfile) {
            if (points > (12 * 20))
                combatMaster = true;
        } else {
            if (points > (8 * 20))
                combatMaster = true;
        }
        highest = getHighestMode();
    }

    private GameMode getHighestMode() {
        GameMode highest = null;
        int highestPoints = 0;
        for (GameMode gameMode : gameModes) {
            if (highestPoints == 0)
                highest = gameMode;
            if (gameMode.getTierPoints() > highestPoints) {
                highest = gameMode;
                highestPoints = gameMode.getTierPoints();
            }
        }
        status = Status.FOUND;
        return highest;
    }

    public GameMode getGameMode(String gamemodeName) {
        for (GameMode gameMode : gameModes) {
            if (gameMode.name.toString().equalsIgnoreCase(gamemodeName))
                return gameMode;
        }
        return null;
    }

    private Text getRegionText(String region) {
        if (region.equalsIgnoreCase("EU")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0x89f19c));
        else if (region.equalsIgnoreCase("NA")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xd95c6a));
        else if (region.equalsIgnoreCase("AS")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xaf7f91));
        else if (region.equalsIgnoreCase("AU")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0xd5ad80));
        else if (region.equalsIgnoreCase("SA")) return Text.literal(region).setStyle(Style.EMPTY.withColor(0x5bc9d9));
        return Text.literal("Unknown").setStyle(Style.EMPTY.withColor(0x222222));
    }

    private Text getOverallText(int overallPosition) {
        if (combatMaster) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xfde047));
        else if (points >= 100) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xfda4af));
        else if (points >= 50) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xd8b4fe));
        else if (points >= 20) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xc4b5fd));
        else if (points >= 10) return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xc4b5fd));
        return Text.literal("#" + overallPosition).setStyle(Style.EMPTY.withColor(0xd1d5db));
    }

    private Text getRegionTooltip(String region) {
        if (region.equalsIgnoreCase("EU")) return Text.literal("Europe").setStyle(Style.EMPTY.withColor(0x89f19c));
        else if (region.equalsIgnoreCase("NA")) return Text.literal("North America").setStyle(Style.EMPTY.withColor(0xd95c6a));
        else if (region.equalsIgnoreCase("AS")) return Text.literal("Asia").setStyle(Style.EMPTY.withColor(0xaf7f91));
        else if (region.equalsIgnoreCase("AU")) return Text.literal("Australia").setStyle(Style.EMPTY.withColor(0xd5ad80));
        else if (region.equalsIgnoreCase("SA")) return Text.literal("South America").setStyle(Style.EMPTY.withColor(0x5bc9d9));
        return Text.literal("Unknown").setStyle(Style.EMPTY.withColor(0x222222));
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

        return Text.literal(overallTooltip).setStyle(Style.EMPTY.withColor(Objects.requireNonNull(displayedOverall.getStyle().getColor()).getRgb()));
    }
}