package com.tiers.profile.types;

import com.tiers.misc.Mode;
import com.tiers.profile.GameMode;
import net.minecraft.util.Identifier;

public class MCTiersProfile extends SuperProfile {
    public static final Identifier MCTIERS_IMAGE = Identifier.of("minecraft", "textures/mctiers_logo.png");

    public MCTiersProfile(String uuid, String apiUrl) {
        super();
        addGamemodes();
        buildRequest(uuid, apiUrl);
    }

    public MCTiersProfile(String json) {
        super();
        addGamemodes();
        parseJson(json);
    }

    private void addGamemodes() {
        gameModes.add(new GameMode(Mode.MCTIERS_VANILLA, "vanilla"));
        gameModes.add(new GameMode(Mode.MCTIERS_UHC, "uhc"));
        gameModes.add(new GameMode(Mode.MCTIERS_POT, "pot"));
        gameModes.add(new GameMode(Mode.MCTIERS_NETH_OP, "nethop"));
        gameModes.add(new GameMode(Mode.MCTIERS_SMP, "smp"));
        gameModes.add(new GameMode(Mode.MCTIERS_SWORD, "sword"));
        gameModes.add(new GameMode(Mode.MCTIERS_AXE, "axe"));
        gameModes.add(new GameMode(Mode.MCTIERS_MACE, "mace"));
    }
}