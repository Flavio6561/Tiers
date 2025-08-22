package com.tiers.profile.types;

import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;

public class SubtiersProfile extends SuperProfile {
    public SubtiersProfile(String uuid, String apiUrl) {
        super(uuid, apiUrl);
        addGamemodes();
    }

    public SubtiersProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.SUBTIERS_MINECART, "minecart"));
        gameModes.add(new GameMode(Modes.SUBTIERS_DIAMOND_SURVIVAL, "dia_crystal"));
        gameModes.add(new GameMode(Modes.SUBTIERS_DEBUFF, "debuff"));
        gameModes.add(new GameMode(Modes.SUBTIERS_ELYTRA, "elytra"));
        gameModes.add(new GameMode(Modes.SUBTIERS_SPEED, "speed"));
        gameModes.add(new GameMode(Modes.SUBTIERS_CREEPER, "creeper"));
        gameModes.add(new GameMode(Modes.SUBTIERS_MANHUNT, "manhunt"));
        gameModes.add(new GameMode(Modes.SUBTIERS_DIAMOND_SMP, "dia_smp"));
        gameModes.add(new GameMode(Modes.SUBTIERS_BOW, "bow"));
        gameModes.add(new GameMode(Modes.SUBTIERS_BED, "bed"));
        gameModes.add(new GameMode(Modes.SUBTIERS_OG_VANILLA, "og_vanilla"));
        gameModes.add(new GameMode(Modes.SUBTIERS_TRIDENT, "trident"));
    }
}