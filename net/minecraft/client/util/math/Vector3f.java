package net.minecraft.client.util.math;

import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Arrays;

public final class Vector3f
{
    private final float[] components;
    
    @Environment(EnvType.CLIENT)
    public Vector3f(final Vector3f vector3f) {
        this.components = Arrays.copyOf(vector3f.components, 3);
    }
    
    public Vector3f() {
        this.components = new float[3];
    }
    
    @Environment(EnvType.CLIENT)
    public Vector3f(final float x, final float y, final float float3) {
        this.components = new float[] { x, y, float3 };
    }
    
    public Vector3f(final Vec3d vec3d) {
        this.components = new float[] { (float)vec3d.x, (float)vec3d.y, (float)vec3d.z };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Vector3f vector3f2 = (Vector3f)o;
        return Arrays.equals(this.components, vector3f2.components);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
    
    public float x() {
        return this.components[0];
    }
    
    public float y() {
        return this.components[1];
    }
    
    public float z() {
        return this.components[2];
    }
    
    @Environment(EnvType.CLIENT)
    public void scale(final float float1) {
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            final float[] components = this.components;
            final int n = integer2;
            components[n] *= float1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static float clampFloat(final float v, final float min, final float max) {
        if (v < min) {
            return min;
        }
        if (v > max) {
            return max;
        }
        return v;
    }
    
    @Environment(EnvType.CLIENT)
    public void clamp(final float min, final float float2) {
        this.components[0] = clampFloat(this.components[0], min, float2);
        this.components[1] = clampFloat(this.components[1], min, float2);
        this.components[2] = clampFloat(this.components[2], min, float2);
    }
    
    public void set(final float x, final float y, final float float3) {
        this.components[0] = x;
        this.components[1] = y;
        this.components[2] = float3;
    }
    
    @Environment(EnvType.CLIENT)
    public void add(final float x, final float y, final float float3) {
        final float[] components = this.components;
        final int n = 0;
        components[n] += x;
        final float[] components2 = this.components;
        final int n2 = 1;
        components2[n2] += y;
        final float[] components3 = this.components;
        final int n3 = 2;
        components3[n3] += float3;
    }
    
    @Environment(EnvType.CLIENT)
    public void subtract(final Vector3f vector3f) {
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            final float[] components = this.components;
            final int n = integer2;
            components[n] -= vector3f.components[integer2];
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float dot(final Vector3f vector3f) {
        float float2 = 0.0f;
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            float2 += this.components[integer3] * vector3f.components[integer3];
        }
        return float2;
    }
    
    @Environment(EnvType.CLIENT)
    public void reciprocal() {
        float float1 = 0.0f;
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            float1 += this.components[integer2] * this.components[integer2];
        }
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            final float[] components = this.components;
            final int n = integer2;
            components[n] /= float1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void cross(final Vector3f vector3f) {
        final float float2 = this.components[0];
        final float float3 = this.components[1];
        final float float4 = this.components[2];
        final float float5 = vector3f.x();
        final float float6 = vector3f.y();
        final float float7 = vector3f.z();
        this.components[0] = float3 * float7 - float4 * float6;
        this.components[1] = float4 * float5 - float2 * float7;
        this.components[2] = float2 * float6 - float3 * float5;
    }
    
    public void a(final Quaternion quaternion) {
        final Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.a(new Quaternion(this.x(), this.y(), this.z(), 0.0f));
        final Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.e();
        quaternion2.a(quaternion3);
        this.set(quaternion2.a(), quaternion2.b(), quaternion2.c());
    }
}
