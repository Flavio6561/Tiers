package com.tiers.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tiers.TiersClient;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerListHud.class)
public class ModifyTabClientMixin {
    @ModifyReturnValue(at = @At("RETURN"), method = "getPlayerName")
    private Text modifyPlayerName(Text original) {
        if (!TiersClient.toggleMod || !TiersClient.toggleTab)
            return original;

        for (PlayerProfile playerProfile : TiersClient.playerProfiles)
            if (playerProfile.status == Status.READY && (original.getString().contains(playerProfile.name) || original.getString().contains(playerProfile.inGameName)))
                return playerProfile.getFullName(original);

        return original;
    }
}