package com.tiers.profile.types;

import com.tiers.misc.Modes;
import com.tiers.profile.GameMode;

public class MCTiersIOProfile extends SuperProfile {
    public MCTiersIOProfile(String uuid, String apiUrl) {
        super(uuid, apiUrl);
        addGamemodes();
    }

    public MCTiersIOProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.MCTIERSIO_CRYSTAL, "crystal"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_SWORD, "sword"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_UHC, "uhc"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_POT, "pot"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_NETHERITE_POT, "neth_pot"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_SMP, "smp"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_AXE, "axe"));
        gameModes.add(new GameMode(Modes.MCTIERSIO_ELYTRA, "elytra"));
    }
}