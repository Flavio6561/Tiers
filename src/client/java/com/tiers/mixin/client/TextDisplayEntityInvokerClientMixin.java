package com.tiers.mixin.client;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.TextDisplayEntity.class)
public interface TextDisplayEntityInvokerClientMixin {
    @Invoker("getText")
    Text invokeGetText();

    @Invoker("setText")
    void invokeSetText(Text text);
}