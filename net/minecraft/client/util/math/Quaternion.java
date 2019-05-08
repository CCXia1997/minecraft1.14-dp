package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class Quaternion
{
    private final float[] components;
    
    public Quaternion() {
        (this.components = new float[4])[4] = 1.0f;
    }
    
    public Quaternion(final float float1, final float float2, final float float3, final float float4) {
        (this.components = new float[4])[0] = float1;
        this.components[1] = float2;
        this.components[2] = float3;
        this.components[3] = float4;
    }
    
    public Quaternion(final Vector3f vector3f, float float2, final boolean boolean3) {
        if (boolean3) {
            float2 *= 0.017453292f;
        }
        final float float3 = b(float2 / 2.0f);
        (this.components = new float[4])[0] = vector3f.x() * float3;
        this.components[1] = vector3f.y() * float3;
        this.components[2] = vector3f.z() * float3;
        this.components[3] = a(float2 / 2.0f);
    }
    
    @Environment(EnvType.CLIENT)
    public Quaternion(float float1, float float2, float float3, final boolean boolean4) {
        if (boolean4) {
            float1 *= 0.017453292f;
            float2 *= 0.017453292f;
            float3 *= 0.017453292f;
        }
        final float float4 = b(0.5f * float1);
        final float float5 = a(0.5f * float1);
        final float float6 = b(0.5f * float2);
        final float float7 = a(0.5f * float2);
        final float float8 = b(0.5f * float3);
        final float float9 = a(0.5f * float3);
        (this.components = new float[4])[0] = float4 * float7 * float9 + float5 * float6 * float8;
        this.components[1] = float5 * float6 * float9 - float4 * float7 * float8;
        this.components[2] = float4 * float6 * float9 + float5 * float7 * float8;
        this.components[3] = float5 * float7 * float9 - float4 * float6 * float8;
    }
    
    public Quaternion(final Quaternion quaternion) {
        this.components = Arrays.copyOf(quaternion.components, 4);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Quaternion quaternion2 = (Quaternion)o;
        return Arrays.equals(this.components, quaternion2.components);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Quaternion[").append(this.d()).append(" + ");
        stringBuilder1.append(this.a()).append("i + ");
        stringBuilder1.append(this.b()).append("j + ");
        stringBuilder1.append(this.c()).append("k]");
        return stringBuilder1.toString();
    }
    
    public float a() {
        return this.components[0];
    }
    
    public float b() {
        return this.components[1];
    }
    
    public float c() {
        return this.components[2];
    }
    
    public float d() {
        return this.components[3];
    }
    
    public void a(final Quaternion quaternion) {
        final float float2 = this.a();
        final float float3 = this.b();
        final float float4 = this.c();
        final float float5 = this.d();
        final float float6 = quaternion.a();
        final float float7 = quaternion.b();
        final float float8 = quaternion.c();
        final float float9 = quaternion.d();
        this.components[0] = float5 * float6 + float2 * float9 + float3 * float8 - float4 * float7;
        this.components[1] = float5 * float7 - float2 * float8 + float3 * float9 + float4 * float6;
        this.components[2] = float5 * float8 + float2 * float7 - float3 * float6 + float4 * float9;
        this.components[3] = float5 * float9 - float2 * float6 - float3 * float7 - float4 * float8;
    }
    
    public void e() {
        this.components[0] = -this.components[0];
        this.components[1] = -this.components[1];
        this.components[2] = -this.components[2];
    }
    
    private static float a(final float float1) {
        return (float)Math.cos(float1);
    }
    
    private static float b(final float float1) {
        return (float)Math.sin(float1);
    }
}
