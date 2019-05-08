package net.minecraft.client.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Matrix4f
{
    private final float[] components;
    
    public Matrix4f() {
        this.components = new float[16];
    }
    
    public Matrix4f(final Quaternion quaternion) {
        this();
        final float float2 = quaternion.a();
        final float float3 = quaternion.b();
        final float float4 = quaternion.c();
        final float float5 = quaternion.d();
        final float float6 = 2.0f * float2 * float2;
        final float float7 = 2.0f * float3 * float3;
        final float float8 = 2.0f * float4 * float4;
        this.components[0] = 1.0f - float7 - float8;
        this.components[5] = 1.0f - float8 - float6;
        this.components[10] = 1.0f - float6 - float7;
        this.components[15] = 1.0f;
        final float float9 = float2 * float3;
        final float float10 = float3 * float4;
        final float float11 = float4 * float2;
        final float float12 = float2 * float5;
        final float float13 = float3 * float5;
        final float float14 = float4 * float5;
        this.components[1] = 2.0f * (float9 + float14);
        this.components[4] = 2.0f * (float9 - float14);
        this.components[2] = 2.0f * (float11 - float13);
        this.components[8] = 2.0f * (float11 + float13);
        this.components[6] = 2.0f * (float10 + float12);
        this.components[9] = 2.0f * (float10 - float12);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Matrix4f matrix4f2 = (Matrix4f)o;
        return Arrays.equals(this.components, matrix4f2.components);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
    
    public void setFromBuffer(final FloatBuffer buffer) {
        this.setFromBuffer(buffer, false);
    }
    
    public void setFromBuffer(final FloatBuffer buffer, final boolean transpose) {
        if (transpose) {
            for (int integer3 = 0; integer3 < 4; ++integer3) {
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.components[integer3 * 4 + integer4] = buffer.get(integer4 * 4 + integer3);
                }
            }
        }
        else {
            buffer.get(this.components);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Matrix4f:\n");
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            for (int integer3 = 0; integer3 < 4; ++integer3) {
                stringBuilder1.append(this.components[integer2 + integer3 * 4]);
                if (integer3 != 3) {
                    stringBuilder1.append(" ");
                }
            }
            stringBuilder1.append("\n");
        }
        return stringBuilder1.toString();
    }
    
    public void putIntoBuffer(final FloatBuffer buffer) {
        this.putIntoBuffer(buffer, false);
    }
    
    public void putIntoBuffer(final FloatBuffer buffer, final boolean transpose) {
        if (transpose) {
            for (int integer3 = 0; integer3 < 4; ++integer3) {
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    buffer.put(integer4 * 4 + integer3, this.components[integer3 * 4 + integer4]);
                }
            }
        }
        else {
            buffer.put(this.components);
        }
    }
    
    public void set(final int row, final int col, final float value) {
        this.components[row + 4 * col] = value;
    }
    
    public static Matrix4f a(final double double1, final float float3, final float float4, final float float5) {
        final float float6 = (float)(1.0 / Math.tan(double1 * 0.01745329238474369 / 2.0));
        final Matrix4f matrix4f7 = new Matrix4f();
        matrix4f7.set(0, 0, float6 / float3);
        matrix4f7.set(1, 1, float6);
        matrix4f7.set(2, 2, (float5 + float4) / (float4 - float5));
        matrix4f7.set(3, 2, -1.0f);
        matrix4f7.set(2, 3, 2.0f * float5 * float4 / (float4 - float5));
        return matrix4f7;
    }
    
    public static Matrix4f projectionMatrix(final float viewportWidth, final float viewportHeight, final float nearPlane, final float farPlane) {
        final Matrix4f matrix4f5 = new Matrix4f();
        matrix4f5.set(0, 0, 2.0f / viewportWidth);
        matrix4f5.set(1, 1, 2.0f / viewportHeight);
        final float float6 = farPlane - nearPlane;
        matrix4f5.set(2, 2, -2.0f / float6);
        matrix4f5.set(3, 3, 1.0f);
        matrix4f5.set(0, 3, -1.0f);
        matrix4f5.set(1, 3, -1.0f);
        matrix4f5.set(2, 3, -(farPlane + nearPlane) / float6);
        return matrix4f5;
    }
}
