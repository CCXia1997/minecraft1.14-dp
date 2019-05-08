package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class DiveJumpingGoal extends Goal
{
    public DiveJumpingGoal() {
        this.setControls(EnumSet.<Control>of(Control.a, Control.c));
    }
    
    protected float updatePitch(final float previousPitch, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - previousPitch; float4 < -180.0f; float4 += 360.0f) {}
        while (float4 >= 180.0f) {
            float4 -= 360.0f;
        }
        return previousPitch + float3 * float4;
    }
}
