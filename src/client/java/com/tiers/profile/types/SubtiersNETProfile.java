package com.tiers.profile.types;

import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;

public class SubtiersNETProfile extends SuperProfile {
    public SubtiersNETProfile(String uuid, String apiUrl) {
        super(uuid, apiUrl);
        addGamemodes();
    }

    public SubtiersNETProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.SUBTIERSNET_MINECART, "minecart"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_DIAMOND_CRYSTAL, "dia_crystal"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_DEBUFF, "debuff"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_ELYTRA, "elytra"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_SPEED, "speed"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_CREEPER, "creeper"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_MANHUNT, "manhunt"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_DIAMOND_SMP, "dia_smp"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_BOW, "bow"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_BED, "bed"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_OG_VANILLA, "og_vanilla"));
        gameModes.add(new GameMode(Modes.SUBTIERSNET_TRIDENT, "trident"));
    }
}