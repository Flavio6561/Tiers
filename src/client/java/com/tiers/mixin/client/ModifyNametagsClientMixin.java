package com.tiers.mixin.client;

import com.tiers.TiersClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class ModifyNametagsClientMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> {
    @Inject(at = @At("TAIL"), method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V")
    public void modifyPlayerDisplayNameAtRender(AvatarlikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        playerEntityRenderState.displayName = TiersClient.toggleMod ? TiersClient.addGetPlayer(playerLikeEntity.getNameForScoreboard(), false).getFullName(playerEntityRenderState.displayName) : playerEntityRenderState.displayName;
    }
}