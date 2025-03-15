package com.tiers.profiles;

import com.google.gson.JsonObject;
import com.tiers.TiersClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class GameMode {
    public TiersClient.Modes name;
    public String parsingName;
    public Text displayedTier = Text.of("N/A");
    public String displayedTierUnformatted = "N/A";
    public Text displayedPeakTier = Text.of("N/A");
    public Text tierTooltip = Text.of("N/A");
    public Text peakTierTooltip = Text.of("N/A");
    public String tier = "N/A";
    public String pos = "N/A";
    public String peakTier = "N/A";
    public String peakPos = "N/A";
    public String attained = "N/A";
    public String retired = "N/A";
    public boolean drawn;
    public Status status = Status.SEARCHING;

    public GameMode(TiersClient.Modes name, String parsingName) {
        this.name = name;
        this.parsingName = parsingName;
        drawn = false;
    }

    public void parseTiers(JsonObject jsonObject) {
        tier = jsonObject.get("tier").getAsString();
        pos = jsonObject.get("pos").getAsString();

        if (jsonObject.get("peak_tier").isJsonNull())
            peakTier = tier;
        else
            peakTier = jsonObject.get("peak_tier").getAsString();

        if (jsonObject.get("peak_pos").isJsonNull())
            peakPos = pos;
        else
            peakPos = jsonObject.get("peak_pos").getAsString();

        attained = jsonObject.get("attained").getAsString();
        retired = jsonObject.get("retired").getAsString();

        String displayedTierString = "";
        if (retired.equalsIgnoreCase("true"))
            displayedTierString = "R";
        displayedTierString += pos.equalsIgnoreCase("0") ? "HT" : "LT";
        displayedTierString += tier;

        displayedTierUnformatted = displayedTierString;
        displayedTier = Text.literal(displayedTierString).setStyle(Style.EMPTY.withColor(getTierColor(displayedTierString)));
        tierTooltip = getTierTooltip(displayedTierString);

        if (!tier.equalsIgnoreCase(peakTier) || !(pos.equalsIgnoreCase(peakPos))) {
            String displayedPeakTierString = "";
            if (retired.equalsIgnoreCase("true"))
                displayedPeakTierString = "R";
            displayedPeakTierString += peakPos.equalsIgnoreCase("0") ? "HT" : "LT";
            displayedPeakTierString += peakTier;
            displayedPeakTier = Text.literal(displayedPeakTierString).setStyle(Style.EMPTY.withColor(getTierColor(displayedPeakTierString)));
            peakTierTooltip = getPeakTierTooltip(displayedPeakTierString);
        }

        status = Status.FOUND;
    }

    private Text getTierTooltip(String tierString) {
        String tierTooltipString = "";
        if (tierString.contains("R"))
            tierTooltipString += "Retired ";

        if (tierString.contains("H"))
            tierTooltipString += "High ";
        else
            tierTooltipString += "Low ";

        tierTooltipString += "Tier " + tier + "\n\nPoints: " + getTierPoints() + "\nAttained: " + String.valueOf(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(attained)), ZoneId.systemDefault())).replace("T", " ");

        return Text.literal(tierTooltipString).setStyle(Style.EMPTY.withColor(getTierColor(tierString)));
    }

    private Text getPeakTierTooltip(String tierString) {
        String peakTierTooltipString = "Peak: ";
        if (tierString.contains("R"))
            peakTierTooltipString += "Retired ";

        if (tierString.contains("H"))
            peakTierTooltipString += "High ";
        else
            peakTierTooltipString += "Low ";

        peakTierTooltipString += "Tier " + tier + "\n\nPoints: " + getTierPoints();

        return Text.literal(peakTierTooltipString).setStyle(Style.EMPTY.withColor(getTierColor(tierString)));
    }

    public int getTierPoints() {
        if (displayedTierUnformatted.equalsIgnoreCase("N/A"))
            return 0;
        String tier = displayedTierUnformatted;
        tier = tier.replace("R", "");
        if (tier.equalsIgnoreCase("HT1")) return 60;
        else if (tier.equalsIgnoreCase("LT1"))
            if (name.toString().contains("MCTIERSIO")) return 44;
            else return 45;
        else if (tier.equalsIgnoreCase("HT2"))
            if (name.toString().contains("MCTIERSIO")) return 28;
            else return 30;
        else if (tier.equalsIgnoreCase("LT2"))
            if (name.toString().contains("MCTIERSIO")) return 16;
            else return 20;
        else if (tier.equalsIgnoreCase("HT3")) return 10;
        else if (tier.equalsIgnoreCase("LT3")) return 6;
        else if (tier.equalsIgnoreCase("HT4")) return 4;
        else if (tier.equalsIgnoreCase("LT4")) return 3;
        else if (tier.equalsIgnoreCase("HT5")) return 2;
        else if (tier.equalsIgnoreCase("LT5")) return 1;
        return 0;
    }

    private int getTierColor(String tier) {
        if (tier.contains("R")) return 0x1e2634;
        else if (tier.equalsIgnoreCase("HT1")) return 0xffcf4a;
        else if (tier.equalsIgnoreCase("LT1")) return 0xd5b355;
        else if (tier.equalsIgnoreCase("HT2")) return 0xa4b3c7;
        else if (tier.equalsIgnoreCase("LT2")) return 0x888d95;
        else if (tier.equalsIgnoreCase("HT3")) return 0xb56326;
        else if (tier.equalsIgnoreCase("LT3")) return 0x8f5931;
        return 0x655b79;
    }
}