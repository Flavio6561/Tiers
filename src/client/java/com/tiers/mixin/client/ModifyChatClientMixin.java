package com.tiers.mixin.client;

import com.tiers.TiersClient;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public class ModifyChatClientMixin {
    @ModifyVariable(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", argsOnly = true)
    private Text addMessage(Text original) {
        if (!TiersClient.toggleMod || !TiersClient.toggleChat)
            return original;

        Text text = original;
        String textString = text.getString();

        for (PlayerProfile playerProfile : TiersClient.playerProfiles) {
            if (playerProfile.status != Status.READY)
                continue;

            String targetName = playerProfile.nameChanged ? playerProfile.inGameName : playerProfile.name;

            if (!textString.contains(targetName))
                continue;

            text = deepReplace(text, targetName, playerProfile);
        }

        return text;
    }

    @Unique
    private Text deepReplace(Text original, String targetName, PlayerProfile playerProfile) {
        Style originalStyle = original.getStyle();
        MutableText newText;
        TextContent content = original.getContent();

        if (content instanceof PlainTextContent plain) {
            String string = plain.string();

            if (string.contains(targetName)) {
                newText = Text.empty();
                int lastIndex = 0;
                int index;

                while ((index = string.indexOf(targetName, lastIndex)) != -1) {
                    if (index > lastIndex)
                        newText.append(Text.literal(string.substring(lastIndex, index)).setStyle(originalStyle));

                    MutableText namePart = Text.literal(targetName).setStyle(originalStyle);
                    newText.append(playerProfile.getFullName(namePart));

                    lastIndex = index + targetName.length();
                }

                if (lastIndex < string.length())
                    newText.append(Text.literal(string.substring(lastIndex)).setStyle(originalStyle));
            } else {
                newText = Text.literal(string).setStyle(originalStyle);
            }
        } else if (content instanceof TranslatableTextContent translatableTextContent) {
            Object[] args = translatableTextContent.getArgs();
            Object[] newArgs = new Object[args.length];

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Text text)
                    newArgs[i] = deepReplace(text, targetName, playerProfile);
                else if (args[i] instanceof String string)
                    newArgs[i] = deepReplace(Text.literal(string).setStyle(originalStyle), targetName, playerProfile);
                else
                    newArgs[i] = args[i];
            }
            newText = Text.translatable(translatableTextContent.getKey(), newArgs).setStyle(originalStyle);
        } else {
            newText = original.copyContentOnly().setStyle(originalStyle);
        }

        for (Text sibling : original.getSiblings())
            newText.append(deepReplace(sibling, targetName, playerProfile));

        return newText;
    }
}