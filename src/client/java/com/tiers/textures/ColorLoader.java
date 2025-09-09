package com.tiers.textures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tiers.TiersClient;
import com.tiers.profile.PlayerProfile;
import com.tiers.screens.ConfigScreen;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.tiers.TiersClient.LOGGER;

public class ColorLoader implements SimpleSynchronousResourceReloadListener {
    public static Identifier identifier = Identifier.of("minecraft", "colors/pvptiers.json");

    @Override
    public Identifier getFabricId() {
        return Identifier.of("tiers", "color_loader");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        if (resourceManager.getResource(identifier).isPresent()) {
            try {
                ColorControl.updateColors(JsonHelper.deserialize(new Gson(), new InputStreamReader(resourceManager.getResource(identifier).get().getInputStream(), StandardCharsets.UTF_8), JsonObject.class));
                TiersClient.restyleAllTexts(TiersClient.playerProfiles);
                TiersClient.updateAllTags();
            } catch (IOException ignored) {
                LOGGER.warn("Error loading colors info");
            }
        }

        if (ConfigScreen.defaultProfile == null) {
            ConfigScreen.ownProfile = new PlayerProfile(MinecraftClient.getInstance().getGameProfile().getName(), false);
            ConfigScreen.ownProfile.buildRequest();

            ConfigScreen.defaultProfile = new PlayerProfile("{\"id\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\"}",
                    "{\"uuid\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\",\"rankings\":{\"axe\":{\"tier\":1,\"pos\":0,\"peak_tier\":1,\"peak_pos\":0,\"attained\":1701927844,\"retired\":true},\"smp\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1712888458,\"retired\":false},\"sword\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1733880489,\"retired\":false},\"uhc\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1709746421,\"retired\":false},\"vanilla\":{\"tier\":4,\"pos\":0,\"peak_tier\":4,\"peak_pos\":0,\"attained\":1713570014,\"retired\":false},\"nethop\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1710301241,\"retired\":false},\"pot\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1723349754,\"retired\":false},\"mace\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1737168704,\"retired\":false}},\"region\":\"NA\",\"points\":116,\"overall\":28,\"badges\":[{\"title\":\"Axe Champion\",\"desc\":\"Attained T1+ in Axe for any amount of time\"},{\"title\":\"Axe Expert\",\"desc\":\"Attained T2+ in Axe for any amount of time\"},{\"title\":\"Adventurous\",\"desc\":\"Attained a tier on every present current gamemode\"},{\"title\":\"Holding The Crown\",\"desc\":\"T1 category of 1 gamemode for 30 days or more\"}],\"combat_master\":false}",
                    "{\"uuid\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\",\"rankings\":{\"pot\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1723349754,\"retired\":false},\"crystal\":{\"tier\":4,\"pos\":0,\"peak_tier\":4,\"peak_pos\":0,\"attained\":1713570014,\"retired\":false},\"uhc\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1709746421,\"retired\":false},\"sword\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1733880489,\"retired\":false},\"smp\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1712888458,\"retired\":false},\"neth_pot\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1710301241,\"retired\":false},\"axe\":{\"tier\":1,\"pos\":1,\"peak_tier\":1,\"peak_pos\":1,\"attained\":1744884400,\"retired\":true}},\"region\":\"EU\",\"points\":90,\"overall\":44,\"badges\":[{\"title\":\"Axe Champion\",\"desc\":\"Attained T1+ in Axe for any amount of time\"},{\"title\":\"Axe Expert\",\"desc\":\"Attained T2+ in Axe for any amount of time\"},{\"title\":\"Adventurous\",\"desc\":\"Attained a tier on every present current gamemode\"},{\"title\":\"Holding The Crown\",\"desc\":\"T1 category of 1 gamemode for 30 days or more\"}]}",
                    "{\"uuid\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\",\"rankings\":{\"bed\":{\"tier\":4,\"pos\":0,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1748753398,\"retired\":false},\"speed\":{\"tier\":1,\"pos\":1,\"peak_tier\":1,\"peak_pos\":1,\"attained\":1747584669,\"retired\":false},\"dia_smp\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1734383100,\"retired\":false},\"og_vanilla\":{\"tier\":2,\"pos\":0,\"peak_tier\":2,\"peak_pos\":0,\"attained\":1746316718,\"retired\":true},\"bow\":{\"tier\":4,\"pos\":1,\"peak_tier\":4,\"peak_pos\":1,\"attained\":1747630846,\"retired\":false},\"debuff\":{\"tier\":2,\"pos\":1,\"peak_tier\":2,\"peak_pos\":1,\"attained\":1746668477,\"retired\":false},\"manhunt\":{\"tier\":2,\"pos\":1,\"peak_tier\":2,\"peak_pos\":1,\"attained\":1736014926,\"retired\":true},\"trident\":{\"tier\":3,\"pos\":0,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1741136796,\"retired\":false},\"elytra\":{\"tier\":2,\"pos\":1,\"peak_tier\":2,\"peak_pos\":1,\"attained\":1747112500,\"retired\":false},\"dia_crystal\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1747589669,\"retired\":false},\"minecart\":{\"tier\":3,\"pos\":0,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1747032612,\"retired\":false},\"creeper\":{\"tier\":1,\"pos\":0,\"peak_tier\":1,\"peak_pos\":0,\"attained\":1748398520,\"retired\":false}},\"region\":\"NA\",\"points\":236,\"overall\":1,\"badges\":[{\"title\":\"Speed Expert\",\"desc\":\"Attained T2+ in Speed for any amount of time\"},{\"title\":\"Creeper Expert\",\"desc\":\"Attained T2+ in Creeper for any amount of time\"},{\"title\":\"OG Vanilla Expert\",\"desc\":\"Attained T2+ in OG Vanilla for any amount of time\"},{\"title\":\"Manhunt Expert\",\"desc\":\"Attained T2+ in Manhunt for any amount of time\"},{\"title\":\"Adventurous\",\"desc\":\"Attained a tier on every present current gamemode\"},{\"title\":\"Creeper Champion\",\"desc\":\"Attained T1+ in Creeper for any amount of time\"},{\"title\":\"DeBuff Expert\",\"desc\":\"Attained T2+ in DeBuff for any amount of time\"},{\"title\":\"Holding The Crown\",\"desc\":\"T1 category of 1 gamemode for 30 days or more\"},{\"title\":\"Elytra Expert\",\"desc\":\"Attained T2+ in Elytra for any amount of time\"},{\"title\":\"Speed Champion\",\"desc\":\"Attained T1+ in Speed for any amount of time\"}],\"combat_master\":false}");
        } else {
            ArrayList<PlayerProfile> configProfiles = new ArrayList<>();
            configProfiles.add(ConfigScreen.defaultProfile);
            configProfiles.add(ConfigScreen.ownProfile);
            TiersClient.restyleAllTexts(configProfiles);
        }
    }
}