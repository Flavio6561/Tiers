package com.tiers.mixin.client;

import com.tiers.TiersClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class ModifyNameTagsClientMixin {
	@Shadow public abstract String getNameForScoreboard();

	@Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
	private void getDisplayName(CallbackInfoReturnable<Text> cir) {
		if (TiersClient.toggleMod)
			cir.setReturnValue(TiersClient.getFullName(this.getNameForScoreboard(), cir.getReturnValue()));
		else
			cir.setReturnValue(cir.getReturnValue());
	}
}