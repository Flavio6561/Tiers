package com.tiers.profiles.types;

import com.tiers.TiersClient;
import com.tiers.profiles.GameMode;

public class MCTiersCOMProfile extends BaseProfile {
    public GameMode vanilla = new GameMode(TiersClient.Modes.MCTIERSCOM_VANILLA, "vanilla");
    public GameMode uhc = new GameMode(TiersClient.Modes.MCTIERSCOM_UHC, "uhc");
    public GameMode pot = new GameMode(TiersClient.Modes.MCTIERSCOM_POT,"pot");
    public GameMode netherite_op = new GameMode(TiersClient.Modes.MCTIERSCOM_NETHERITE_OP, "nethop");
    public GameMode smp = new GameMode(TiersClient.Modes.MCTIERSCOM_SMP, "smp");
    public GameMode sword = new GameMode(TiersClient.Modes.MCTIERSCOM_SWORD, "sword");
    public GameMode axe = new GameMode(TiersClient.Modes.MCTIERSCOM_AXE, "axe");
    public GameMode mace = new GameMode(TiersClient.Modes.MCTIERSCOM_MACE, "mace");

    public MCTiersCOMProfile(String uuid) {
        super(uuid, "https://mctiers.com/api/profile/");

        gameModes.add(vanilla);
        gameModes.add(uhc);
        gameModes.add(pot);
        gameModes.add(netherite_op);
        gameModes.add(smp);
        gameModes.add(sword);
        gameModes.add(axe);
        gameModes.add(mace);
    }
}