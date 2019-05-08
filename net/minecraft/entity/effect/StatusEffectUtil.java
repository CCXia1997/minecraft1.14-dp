package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.MathHelper;

public final class StatusEffectUtil
{
    @Environment(EnvType.CLIENT)
    public static String durationToString(final StatusEffectInstance effect, final float multiplier) {
        if (effect.isPermanent()) {
            return "**:**";
        }
        final int integer3 = MathHelper.floor(effect.getDuration() * multiplier);
        return ChatUtil.ticksToString(integer3);
    }
    
    public static boolean hasHaste(final LivingEntity livingEntity) {
        return livingEntity.hasStatusEffect(StatusEffects.c) || livingEntity.hasStatusEffect(StatusEffects.C);
    }
    
    public static int getHasteAmplifier(final LivingEntity livingEntity) {
        int integer2 = 0;
        int integer3 = 0;
        if (livingEntity.hasStatusEffect(StatusEffects.c)) {
            integer2 = livingEntity.getStatusEffect(StatusEffects.c).getAmplifier();
        }
        if (livingEntity.hasStatusEffect(StatusEffects.C)) {
            integer3 = livingEntity.getStatusEffect(StatusEffects.C).getAmplifier();
        }
        return Math.max(integer2, integer3);
    }
    
    public static boolean hasWaterBreathing(final LivingEntity livingEntity) {
        return livingEntity.hasStatusEffect(StatusEffects.m) || livingEntity.hasStatusEffect(StatusEffects.C);
    }
}
