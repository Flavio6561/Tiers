package com.tiers.mixin.client;

import com.tiers.TiersClient;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DisplayEntityRenderer.TextDisplayEntityRenderer.class)
public abstract class ModifyTextDisplaysClientMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;"), method = "getLines")
    public StringVisitable modifyLines(StringVisitable original) {
        if (!TiersClient.toggleMod)
            return original;

        for (PlayerProfile playerProfile : TiersClient.playerProfiles)
            if (playerProfile.status == Status.READY && (original.getString().contains(playerProfile.name) || original.getString().contains(playerProfile.originalName)))
                return playerProfile.getFullName((Text) original);

        return original;
    }
}