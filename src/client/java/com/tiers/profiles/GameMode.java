package com.tiers.profiles;

import com.google.gson.JsonObject;
import com.tiers.TiersClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class GameMode {
    public Status status = Status.SEARCHING;

    public String tier;
    public String pos;
    public String peakTier;
    public String peakPos;
    public String attained;
    public String retired;

    public Text displayedTier;
    public String displayedTierUnformatted;
    public Text displayedPeakTier;
    public String displayedPeakTierUnformatted;
    public Text tierTooltip;
    public Text peakTierTooltip;

    public TiersClient.Modes name;
    public String parsingName;
    public boolean hasPeak = false;
    public boolean drawn = false;

    public GameMode(TiersClient.Modes name, String parsingName) {
        this.name = name;
        this.parsingName = parsingName;
    }

    public void parseTiers(JsonObject jsonObject) {
        if (jsonObject.has("tier") && jsonObject.has("pos") && jsonObject.has("attained") && jsonObject.has("retired")) {
            tier = jsonObject.get("tier").getAsString();
            pos = jsonObject.get("pos").getAsString();

            if (jsonObject.get("peak_tier").isJsonNull())
                peakTier = tier;
            else peakTier = jsonObject.get("peak_tier").getAsString();

            if (jsonObject.get("peak_pos").isJsonNull())
                peakPos = pos;
            else peakPos = jsonObject.get("peak_pos").getAsString();

            attained = jsonObject.get("attained").getAsString();
            retired = jsonObject.get("retired").getAsString();
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        displayedTierUnformatted = "";
        if (retired.equalsIgnoreCase("true"))
            displayedTierUnformatted = "R";
        displayedTierUnformatted += pos.equalsIgnoreCase("0") ? "HT" : "LT";
        displayedTierUnformatted += tier;

        displayedTier = Text.literal(displayedTierUnformatted).setStyle(Style.EMPTY.withColor(getTierColor(displayedTierUnformatted)));
        tierTooltip = getTierTooltip();

        if (!tier.equalsIgnoreCase(peakTier) || !(pos.equalsIgnoreCase(peakPos))) {
            displayedPeakTierUnformatted = peakPos.equalsIgnoreCase("0") ? "HT" : "LT";
            displayedPeakTierUnformatted += peakTier;

            displayedPeakTier = Text.literal(displayedPeakTierUnformatted).setStyle(Style.EMPTY.withColor(getTierColor(displayedPeakTierUnformatted)));
            peakTierTooltip = getPeakTierTooltip();

            hasPeak = true;
        }

        status = Status.READY;
    }

    private Text getTierTooltip() {
        String tierTooltipString = "";
        if (displayedTierUnformatted.contains("R"))
            tierTooltipString += "Retired ";

        if (displayedTierUnformatted.contains("H"))
            tierTooltipString += "High ";
        else tierTooltipString += "Low ";

        tierTooltipString += "Tier " + tier + "\n\nPoints: " + getTierPoints(false) + "\nAttained: " + String.valueOf(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(attained)), ZoneId.systemDefault())).replace("T", " ");

        return Text.literal(tierTooltipString).setStyle(Style.EMPTY.withColor(getTierColor(displayedTierUnformatted)));
    }

    private Text getPeakTierTooltip() {
        String peakTierTooltipString = "Peak: ";
        if (displayedPeakTierUnformatted.contains("R"))
            peakTierTooltipString += "Retired ";

        if (displayedPeakTierUnformatted.contains("H"))
            peakTierTooltipString += "High ";
        else peakTierTooltipString += "Low ";

        peakTierTooltipString += "Tier " + peakTier + "\n\nPoints: " + getTierPoints(true);

        return Text.literal(peakTierTooltipString).setStyle(Style.EMPTY.withColor(getTierColor(displayedPeakTierUnformatted)));
    }

    public int getTierPoints(boolean peak) {
        if (status == Status.NOT_EXISTING) return 0;
        String tier = displayedTierUnformatted;
        if (peak)
            tier = displayedPeakTierUnformatted;
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