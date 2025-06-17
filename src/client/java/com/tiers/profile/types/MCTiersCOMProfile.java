package com.tiers.profile.types;

import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;

public class MCTiersCOMProfile extends SuperProfile {
    public MCTiersCOMProfile(String uuid, String apiUrl) {
        super(uuid, apiUrl);
        addGamemodes();
    }

    public MCTiersCOMProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.MCTIERSCOM_VANILLA, "vanilla"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_UHC, "uhc"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_POT, "pot"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_NETHERITE_OP, "nethop"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_SMP, "smp"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_SWORD, "sword"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_AXE, "axe"));
        gameModes.add(new GameMode(Modes.MCTIERSCOM_MACE, "mace"));
    }
}