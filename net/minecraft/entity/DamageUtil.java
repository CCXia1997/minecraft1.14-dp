package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class DamageUtil
{
    public static float getDamageLeft(final float damage, final float armor, final float armorToughness) {
        final float float4 = 2.0f + armorToughness / 4.0f;
        final float float5 = MathHelper.clamp(armor - damage / float4, armor * 0.2f, 20.0f);
        return damage * (1.0f - float5 / 25.0f);
    }
    
    public static float a(final float float1, final float float2) {
        final float float3 = MathHelper.clamp(float2, 0.0f, 20.0f);
        return float1 * (1.0f - float3 / 25.0f);
    }
}
