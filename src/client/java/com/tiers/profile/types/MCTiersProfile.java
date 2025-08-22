package com.tiers.profile.types;

import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;

public class MCTiersProfile extends SuperProfile {
    public MCTiersProfile(String uuid, String apiUrl) {
        super(uuid, apiUrl);
        addGamemodes();
    }

    public MCTiersProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.MCTIERS_VANILLA, "vanilla"));
        gameModes.add(new GameMode(Modes.MCTIERS_UHC, "uhc"));
        gameModes.add(new GameMode(Modes.MCTIERS_POT, "pot"));
        gameModes.add(new GameMode(Modes.MCTIERS_NETH_OP, "nethop"));
        gameModes.add(new GameMode(Modes.MCTIERS_SMP, "smp"));
        gameModes.add(new GameMode(Modes.MCTIERS_SWORD, "sword"));
        gameModes.add(new GameMode(Modes.MCTIERS_AXE, "axe"));
        gameModes.add(new GameMode(Modes.MCTIERS_MACE, "mace"));
    }
}