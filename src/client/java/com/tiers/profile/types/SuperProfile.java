package com.tiers.profile.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tiers.misc.Mode;
import com.tiers.profile.GameMode;
import com.tiers.profile.Status;
import com.tiers.textures.Icons;
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
    private int numberOfRequests;

    private String region;
    private int points;
    private int overallPosition;

    public Text displayedRegion;
    public Text displayedOverall;
    public Text overallTooltip;
    public Text regionTooltip;

    public final ArrayList<GameMode> gameModes = new ArrayList<>();
    public GameMode highest;

    public String originalJson;
    public boolean drawn;

    protected SuperProfile() {
    }

    protected void buildRequest(String uuid, String apiUrl) {
        if (numberOfRequests == 5 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }

        numberOfRequests++;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + uuid))
                .header("User-Agent", userAgent)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() == 404) {
                status = Status.NOT_EXISTING;
                return;
            } else if (response.statusCode() != 200) {
                status = Status.API_ISSUE;
                return;
            }

            parseJson(response.body());
        }).exceptionally(ignored -> {
            CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> buildRequest(uuid, apiUrl));
            return null;
        });
    }

    public void parseJson(String json) {
        if (JsonParser.parseString(json).isJsonNull()) {
            status = Status.API_ISSUE;
            return;
        }

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("region") &&
                jsonObject.has("points") && jsonObject.has("overall") && jsonObject.has("rankings")) {
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

    public GameMode getGameMode(Mode gamemode) {
        for (GameMode gameMode : gameModes)
            if (gameMode.gamemode.toString().equalsIgnoreCase(gamemode.toString()))
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
            return Icons.colorText(region, "eu");
        else if (region.equalsIgnoreCase("NA"))
            return Icons.colorText(region, "na");
        else if (region.equalsIgnoreCase("AS"))
            return Icons.colorText(region, "as");
        else if (region.equalsIgnoreCase("AU"))
            return Icons.colorText(region, "au");
        else if (region.equalsIgnoreCase("SA"))
            return Icons.colorText(region, "sa");
        else if (region.equalsIgnoreCase("ME"))
            return Icons.colorText(region, "me");
        else if (region.equalsIgnoreCase("AF"))
            return Icons.colorText(region, "af");
        else if (region.equalsIgnoreCase("OC"))
            return Icons.colorText(region, "oc");
        return Icons.colorText("Unknown", "unknown");
    }

    private Text getRegionTooltip() {
        if (region.equalsIgnoreCase("EU"))
            return Icons.colorText("Europe", "eu");
        else if (region.equalsIgnoreCase("NA"))
            return Icons.colorText("North America", "na");
        else if (region.equalsIgnoreCase("AS"))
            return Icons.colorText("Asia", "as");
        else if (region.equalsIgnoreCase("AU"))
            return Icons.colorText("Australia", "au");
        else if (region.equalsIgnoreCase("SA"))
            return Icons.colorText("South America", "sa");
        else if (region.equalsIgnoreCase("ME"))
            return Icons.colorText("Middle East", "me");
        else if (region.equalsIgnoreCase("AF"))
            return Icons.colorText("Africa", "af");
        else if (region.equalsIgnoreCase("OC"))
            return Icons.colorText("Oceania", "oc");
        return Icons.colorText("Unknown", "unknown");
    }

    private Text getOverallText() {
        String positionString = "#" + overallPosition;
        if (!(this instanceof PvPTiersProfile) && points >= 250) return Icons.colorText(positionString, "master");
        else if (this instanceof PvPTiersProfile && points >= 200) return Icons.colorText(positionString, "master");
        else if (points >= 100) return Icons.colorText(positionString, "ace");
        else if (points >= 50) return Icons.colorText(positionString, "specialist");
        else if (points >= 20) return Icons.colorText(positionString, "cadet");
        else if (points >= 10) return Icons.colorText(positionString, "novice");

        return Icons.colorText(positionString, "rookie");
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