package com.tiers.profiles.types;

import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;

public class SubtiersNETProfile extends BaseProfile {
    public GameMode minecart = new GameMode(TiersClient.Modes.SUBTIERSNET_MINECART, "minecart");
    public GameMode diamond_crystal = new GameMode(TiersClient.Modes.SUBTIERSNET_DIAMOND_CRYSTAL, "dia_crystal"); // to confirm
    public GameMode iron_pot = new GameMode(TiersClient.Modes.SUBTIERSNET_IRON_POT, "iron_pot");
    public GameMode elytra = new GameMode(TiersClient.Modes.SUBTIERSNET_ELYTRA, "elytra");
    public GameMode speed = new GameMode(TiersClient.Modes.SUBTIERSNET_SPEED, "speed");
    public GameMode creeper = new GameMode(TiersClient.Modes.SUBTIERSNET_CREEPER, "creeper");
    public GameMode manhunt = new GameMode(TiersClient.Modes.SUBTIERSNET_MANHUNT, "manhunt");
    public GameMode diamond_smp = new GameMode(TiersClient.Modes.SUBTIERSNET_DIAMOND_SMP, "dia_smp");
    public GameMode bow = new GameMode(TiersClient.Modes.SUBTIERSNET_BOW, "bow");
    public GameMode bed = new GameMode(TiersClient.Modes.SUBTIERSNET_BED, "bed");
    public GameMode og_vanilla = new GameMode(TiersClient.Modes.SUBTIERSNET_OG_VANILLA, "og_vanilla");
    public GameMode trident = new GameMode(TiersClient.Modes.SUBTIERSNET_TRIDENT, "trident");

    public SubtiersNETProfile(String uuid) {
        super(uuid, "https://subtiers.net/api/profile/");

        gameModes.add(minecart);
        gameModes.add(diamond_crystal);
        gameModes.add(iron_pot);
        gameModes.add(elytra);
        gameModes.add(speed);
        gameModes.add(creeper);
        gameModes.add(manhunt);
        gameModes.add(diamond_smp);
        gameModes.add(bow);
        gameModes.add(bed);
        gameModes.add(og_vanilla);
        gameModes.add(trident);
    }
}