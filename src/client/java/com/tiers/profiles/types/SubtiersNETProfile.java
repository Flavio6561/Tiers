package com.tiers.profiles.types;

import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;

public class SubtiersNETProfile extends BaseProfile {
    public SubtiersNETProfile(String uuid) {
        super(uuid, "https://subtiers.net/api/profile/");

        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_MINECART, "minecart"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_DIAMOND_CRYSTAL, "dia_crystal"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_DEBUFF, "debuff"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_ELYTRA, "elytra"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_SPEED, "speed"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_CREEPER, "creeper"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_MANHUNT, "manhunt"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_DIAMOND_SMP, "dia_smp"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_BOW, "bow"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_BED, "bed"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_OG_VANILLA, "og_vanilla"));
        gameModes.add(new GameMode(TiersClient.Modes.SUBTIERSNET_TRIDENT, "trident"));
    }
}