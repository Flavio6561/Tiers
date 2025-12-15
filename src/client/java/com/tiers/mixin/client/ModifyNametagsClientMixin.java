package com.tiers.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tiers.TiersClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class ModifyNametagsClientMixin {
    @Shadow
    public abstract String getNameForScoreboard();

    @ModifyReturnValue(at = @At("RETURN"), method = "getDisplayName")
    private Text modifyDisplayName(Text original) {
        return TiersClient.toggleMod ? TiersClient.addGetPlayer(getNameForScoreboard(), false).getFullName(original) : original;
    }
}