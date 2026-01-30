package com.tiers.mixin.client;

import com.mojang.authlib.GameProfile;
import com.tiers.TiersClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class AddRenderedPlayersClientMixin {
    @Inject(at = @At(value = "TAIL"), method = "<init>")
    private void onConstruct(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        if (TiersClient.toggleMod)
            TiersClient.addGetPlayer(gameProfile.getName(), false);
    }
}