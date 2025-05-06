package com.tiers.profiles.types;

import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;

public class MCTiersIOProfile extends BaseProfile {
    public MCTiersIOProfile(String uuid) {
        super(uuid, "https://mctiers.io/api/profile/");

        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_CRYSTAL, "crystal"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_SWORD, "sword"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_UHC, "uhc"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_POT, "pot"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_NETHERITE_POT, "neth_pot"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_SMP, "smp"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_AXE, "axe"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSIO_ELYTRA, "elytra"));
    }
}