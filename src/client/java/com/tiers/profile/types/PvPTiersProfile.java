package com.tiers.profile.types;

import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;

public class PvPTiersProfile extends SuperProfile {
    public PvPTiersProfile(String uuid, String apiUrl) {
        super(uuid, apiUrl);
        addGamemodes();
    }

    public PvPTiersProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.PVPTIERS_CRYSTAL, "crystal"));
        gameModes.add(new GameMode(Modes.PVPTIERS_SWORD, "sword"));
        gameModes.add(new GameMode(Modes.PVPTIERS_UHC, "uhc"));
        gameModes.add(new GameMode(Modes.PVPTIERS_POT, "pot"));
        gameModes.add(new GameMode(Modes.PVPTIERS_NETH_POT, "neth_pot"));
        gameModes.add(new GameMode(Modes.PVPTIERS_SMP, "smp"));
        gameModes.add(new GameMode(Modes.PVPTIERS_AXE, "axe"));
        gameModes.add(new GameMode(Modes.PVPTIERS_MACE, "mace"));
    }
}