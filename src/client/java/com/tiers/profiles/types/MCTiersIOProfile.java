package com.tiers.profiles.types;

import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;

public class MCTiersIOProfile extends BaseProfile {
    public GameMode crystal = new GameMode(TiersClient.Modes.MCTIERSIO_CRYSTAL, "crystal");
    public GameMode uhc = new GameMode(TiersClient.Modes.MCTIERSIO_UHC, "uhc");
    public GameMode pot = new GameMode(TiersClient.Modes.MCTIERSIO_POT, "pot");
    public GameMode netherite_pot = new GameMode(TiersClient.Modes.MCTIERSIO_NETHERITE_POT, "neth_pot");
    public GameMode smp = new GameMode(TiersClient.Modes.MCTIERSIO_SMP, "smp");
    public GameMode sword = new GameMode(TiersClient.Modes.MCTIERSIO_SWORD, "sword");
    public GameMode axe = new GameMode(TiersClient.Modes.MCTIERSIO_AXE, "axe");
    public GameMode elytra = new GameMode(TiersClient.Modes.MCTIERSIO_ELYTRA, "elytra");

    public MCTiersIOProfile(String uuid) {
        super(uuid, "https://mctiers.io/api/profile/");

        gameModes.add(crystal);
        gameModes.add(uhc);
        gameModes.add(pot);
        gameModes.add(netherite_pot);
        gameModes.add(smp);
        gameModes.add(sword);
        gameModes.add(axe);
        gameModes.add(elytra);
    }
}