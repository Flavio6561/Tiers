package com.tiers.profiles.types;

import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;

public class MCTiersCOMProfile extends BaseProfile {
    public MCTiersCOMProfile(String uuid) {
        super(uuid, "https://mctiers.com/api/profile/");

        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_VANILLA, "vanilla"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_UHC, "uhc"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_POT,"pot"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_NETHERITE_OP, "nethop"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_SMP, "smp"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_SWORD, "sword"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_AXE, "axe"));
        gameModes.add(new GameMode(TiersClient.Modes.MCTIERSCOM_MACE, "mace"));
    }
}