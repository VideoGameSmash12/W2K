package me.videogamesm12.w2k.val.v26_1_x.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class MainInjector
{
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Ljava/lang/System;setProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
    private static String disableHeadlessMode(String key, String value, Operation<String> original)
    {
        return "false";
    }
}
