package net.minecraft.client.render.model;

import net.minecraft.util.math.Vec3i;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.util.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BakedQuadFactory
{
    private static final float a;
    private static final float b;
    private static final a[] c;
    private static final a d;
    private static final a e;
    private static final a f;
    private static final a g;
    
    public BakedQuad bake(final Vector3f from, final Vector3f to, final ModelElementFace face, final Sprite texture, final Direction side, final ModelBakeSettings settings, @Nullable final ModelRotation rotation, final boolean shade) {
        ModelElementTexture modelElementTexture9 = face.textureData;
        if (settings.isUvLocked()) {
            modelElementTexture9 = this.uvLock(face.textureData, side, settings.getRotation());
        }
        final float[] arr10 = new float[modelElementTexture9.uvs.length];
        System.arraycopy(modelElementTexture9.uvs, 0, arr10, 0, arr10.length);
        final float float11 = texture.getWidth() / (texture.getMaxU() - texture.getMinU());
        final float float12 = texture.getHeight() / (texture.getMaxV() - texture.getMinV());
        final float float13 = 4.0f / Math.max(float12, float11);
        final float float14 = (modelElementTexture9.uvs[0] + modelElementTexture9.uvs[0] + modelElementTexture9.uvs[2] + modelElementTexture9.uvs[2]) / 4.0f;
        final float float15 = (modelElementTexture9.uvs[1] + modelElementTexture9.uvs[1] + modelElementTexture9.uvs[3] + modelElementTexture9.uvs[3]) / 4.0f;
        modelElementTexture9.uvs[0] = MathHelper.lerp(float13, modelElementTexture9.uvs[0], float14);
        modelElementTexture9.uvs[2] = MathHelper.lerp(float13, modelElementTexture9.uvs[2], float14);
        modelElementTexture9.uvs[1] = MathHelper.lerp(float13, modelElementTexture9.uvs[1], float15);
        modelElementTexture9.uvs[3] = MathHelper.lerp(float13, modelElementTexture9.uvs[3], float15);
        final int[] arr11 = this.a(modelElementTexture9, texture, side, this.a(from, to), settings.getRotation(), rotation, shade);
        final Direction direction17 = a(arr11);
        System.arraycopy(arr10, 0, modelElementTexture9.uvs, 0, arr10.length);
        if (rotation == null) {
            this.a(arr11, direction17);
        }
        return new BakedQuad(arr11, face.tintIndex, direction17, texture);
    }
    
    private ModelElementTexture uvLock(final ModelElementTexture modelElementTexture, final Direction direction, final net.minecraft.client.render.model.ModelRotation modelRotation) {
        return BakedQuadFactory.c[a(modelRotation, direction)].a(modelElementTexture);
    }
    
    private int[] a(final ModelElementTexture modelElementTexture, final Sprite sprite, final Direction direction, final float[] arr, final net.minecraft.client.render.model.ModelRotation modelRotation, @Nullable final ModelRotation modelRotation, final boolean boolean7) {
        final int[] arr2 = new int[28];
        for (int integer9 = 0; integer9 < 4; ++integer9) {
            this.a(arr2, integer9, direction, modelElementTexture, arr, sprite, modelRotation, modelRotation, boolean7);
        }
        return arr2;
    }
    
    private int a(final Direction direction) {
        final float float2 = this.b(direction);
        final int integer3 = MathHelper.clamp((int)(float2 * 255.0f), 0, 255);
        return 0xFF000000 | integer3 << 16 | integer3 << 8 | integer3;
    }
    
    private float b(final Direction direction) {
        switch (direction) {
            case DOWN: {
                return 0.5f;
            }
            case UP: {
                return 1.0f;
            }
            case NORTH:
            case SOUTH: {
                return 0.8f;
            }
            case WEST:
            case EAST: {
                return 0.6f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    private float[] a(final Vector3f vector3f1, final Vector3f vector3f2) {
        final float[] arr3 = new float[Direction.values().length];
        arr3[CubeFace.DirectionIds.WEST] = vector3f1.x() / 16.0f;
        arr3[CubeFace.DirectionIds.DOWN] = vector3f1.y() / 16.0f;
        arr3[CubeFace.DirectionIds.NORTH] = vector3f1.z() / 16.0f;
        arr3[CubeFace.DirectionIds.EAST] = vector3f2.x() / 16.0f;
        arr3[CubeFace.DirectionIds.UP] = vector3f2.y() / 16.0f;
        arr3[CubeFace.DirectionIds.SOUTH] = vector3f2.z() / 16.0f;
        return arr3;
    }
    
    private void a(final int[] arr, final int integer, final Direction direction, final ModelElementTexture modelElementTexture, final float[] arr, final Sprite sprite, final net.minecraft.client.render.model.ModelRotation modelRotation, @Nullable final ModelRotation modelRotation, final boolean boolean9) {
        final Direction direction2 = modelRotation.apply(direction);
        final int integer2 = boolean9 ? this.a(direction2) : -1;
        final CubeFace.Corner corner12 = CubeFace.a(direction).getCorner(integer);
        final Vector3f vector3f13 = new Vector3f(arr[corner12.xSide], arr[corner12.ySide], arr[corner12.zSide]);
        this.a(vector3f13, modelRotation);
        final int integer3 = this.a(vector3f13, direction, integer, modelRotation);
        this.a(arr, integer3, integer, vector3f13, integer2, sprite, modelElementTexture);
    }
    
    private void a(final int[] arr, final int integer2, final int integer3, final Vector3f vector3f, final int integer5, final Sprite sprite, final ModelElementTexture modelElementTexture) {
        final int integer6 = integer2 * 7;
        arr[integer6] = Float.floatToRawIntBits(vector3f.x());
        arr[integer6 + 1] = Float.floatToRawIntBits(vector3f.y());
        arr[integer6 + 2] = Float.floatToRawIntBits(vector3f.z());
        arr[integer6 + 3] = integer5;
        arr[integer6 + 4] = Float.floatToRawIntBits(sprite.getU(modelElementTexture.getU(integer3)));
        arr[integer6 + 4 + 1] = Float.floatToRawIntBits(sprite.getV(modelElementTexture.getV(integer3)));
    }
    
    private void a(final Vector3f vector3f, @Nullable final ModelRotation modelRotation) {
        if (modelRotation == null) {
            return;
        }
        Vector3f vector3f2 = null;
        Vector3f vector3f3 = null;
        switch (modelRotation.axis) {
            case X: {
                vector3f2 = new Vector3f(1.0f, 0.0f, 0.0f);
                vector3f3 = new Vector3f(0.0f, 1.0f, 1.0f);
                break;
            }
            case Y: {
                vector3f2 = new Vector3f(0.0f, 1.0f, 0.0f);
                vector3f3 = new Vector3f(1.0f, 0.0f, 1.0f);
                break;
            }
            case Z: {
                vector3f2 = new Vector3f(0.0f, 0.0f, 1.0f);
                vector3f3 = new Vector3f(1.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                throw new IllegalArgumentException("There are only 3 axes");
            }
        }
        final Quaternion quaternion5 = new Quaternion(vector3f2, modelRotation.angle, true);
        if (modelRotation.rescale) {
            if (Math.abs(modelRotation.angle) == 22.5f) {
                vector3f3.scale(BakedQuadFactory.a);
            }
            else {
                vector3f3.scale(BakedQuadFactory.b);
            }
            vector3f3.add(1.0f, 1.0f, 1.0f);
        }
        else {
            vector3f3.set(1.0f, 1.0f, 1.0f);
        }
        this.a(vector3f, new Vector3f(modelRotation.origin), quaternion5, vector3f3);
    }
    
    public int a(final Vector3f vector3f, final Direction direction, final int integer, final net.minecraft.client.render.model.ModelRotation modelRotation) {
        if (modelRotation == net.minecraft.client.render.model.ModelRotation.X0_Y0) {
            return integer;
        }
        this.a(vector3f, new Vector3f(0.5f, 0.5f, 0.5f), modelRotation.getQuaternion(), new Vector3f(1.0f, 1.0f, 1.0f));
        return modelRotation.a(direction, integer);
    }
    
    private void a(final Vector3f vector3f1, final Vector3f vector3f2, final Quaternion quaternion, final Vector3f vector3f4) {
        final Vector4f vector4f5 = new Vector4f(vector3f1.x() - vector3f2.x(), vector3f1.y() - vector3f2.y(), vector3f1.z() - vector3f2.z(), 1.0f);
        vector4f5.a(quaternion);
        vector4f5.multiply(vector3f4);
        vector3f1.set(vector4f5.x() + vector3f2.x(), vector4f5.y() + vector3f2.y(), vector4f5.z() + vector3f2.z());
    }
    
    public static Direction a(final int[] arr) {
        final Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(arr[0]), Float.intBitsToFloat(arr[1]), Float.intBitsToFloat(arr[2]));
        final Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(arr[7]), Float.intBitsToFloat(arr[8]), Float.intBitsToFloat(arr[9]));
        final Vector3f vector3f4 = new Vector3f(Float.intBitsToFloat(arr[14]), Float.intBitsToFloat(arr[15]), Float.intBitsToFloat(arr[16]));
        final Vector3f vector3f5 = new Vector3f(vector3f2);
        vector3f5.subtract(vector3f3);
        final Vector3f vector3f6 = new Vector3f(vector3f4);
        vector3f6.subtract(vector3f3);
        final Vector3f vector3f7 = new Vector3f(vector3f6);
        vector3f7.cross(vector3f5);
        vector3f7.reciprocal();
        Direction direction8 = null;
        float float9 = 0.0f;
        for (final Direction direction9 : Direction.values()) {
            final Vec3i vec3i14 = direction9.getVector();
            final Vector3f vector3f8 = new Vector3f((float)vec3i14.getX(), (float)vec3i14.getY(), (float)vec3i14.getZ());
            final float float10 = vector3f7.dot(vector3f8);
            if (float10 >= 0.0f && float10 > float9) {
                float9 = float10;
                direction8 = direction9;
            }
        }
        if (direction8 == null) {
            return Direction.UP;
        }
        return direction8;
    }
    
    private void a(final int[] arr, final Direction direction) {
        final int[] arr2 = new int[arr.length];
        System.arraycopy(arr, 0, arr2, 0, arr.length);
        final float[] arr3 = new float[Direction.values().length];
        arr3[CubeFace.DirectionIds.WEST] = 999.0f;
        arr3[CubeFace.DirectionIds.DOWN] = 999.0f;
        arr3[CubeFace.DirectionIds.NORTH] = 999.0f;
        arr3[CubeFace.DirectionIds.EAST] = -999.0f;
        arr3[CubeFace.DirectionIds.UP] = -999.0f;
        arr3[CubeFace.DirectionIds.SOUTH] = -999.0f;
        for (int integer5 = 0; integer5 < 4; ++integer5) {
            final int integer6 = 7 * integer5;
            final float float7 = Float.intBitsToFloat(arr2[integer6]);
            final float float8 = Float.intBitsToFloat(arr2[integer6 + 1]);
            final float float9 = Float.intBitsToFloat(arr2[integer6 + 2]);
            if (float7 < arr3[CubeFace.DirectionIds.WEST]) {
                arr3[CubeFace.DirectionIds.WEST] = float7;
            }
            if (float8 < arr3[CubeFace.DirectionIds.DOWN]) {
                arr3[CubeFace.DirectionIds.DOWN] = float8;
            }
            if (float9 < arr3[CubeFace.DirectionIds.NORTH]) {
                arr3[CubeFace.DirectionIds.NORTH] = float9;
            }
            if (float7 > arr3[CubeFace.DirectionIds.EAST]) {
                arr3[CubeFace.DirectionIds.EAST] = float7;
            }
            if (float8 > arr3[CubeFace.DirectionIds.UP]) {
                arr3[CubeFace.DirectionIds.UP] = float8;
            }
            if (float9 > arr3[CubeFace.DirectionIds.SOUTH]) {
                arr3[CubeFace.DirectionIds.SOUTH] = float9;
            }
        }
        final CubeFace cubeFace5 = CubeFace.a(direction);
        for (int integer6 = 0; integer6 < 4; ++integer6) {
            final int integer7 = 7 * integer6;
            final CubeFace.Corner corner8 = cubeFace5.getCorner(integer6);
            final float float9 = arr3[corner8.xSide];
            final float float10 = arr3[corner8.ySide];
            final float float11 = arr3[corner8.zSide];
            arr[integer7] = Float.floatToRawIntBits(float9);
            arr[integer7 + 1] = Float.floatToRawIntBits(float10);
            arr[integer7 + 2] = Float.floatToRawIntBits(float11);
            for (int integer8 = 0; integer8 < 4; ++integer8) {
                final int integer9 = 7 * integer8;
                final float float12 = Float.intBitsToFloat(arr2[integer9]);
                final float float13 = Float.intBitsToFloat(arr2[integer9 + 1]);
                final float float14 = Float.intBitsToFloat(arr2[integer9 + 2]);
                if (MathHelper.equalsApproximate(float9, float12) && MathHelper.equalsApproximate(float10, float13) && MathHelper.equalsApproximate(float11, float14)) {
                    arr[integer7 + 4] = arr2[integer9 + 4];
                    arr[integer7 + 4 + 1] = arr2[integer9 + 4 + 1];
                }
            }
        }
    }
    
    private static void a(final net.minecraft.client.render.model.ModelRotation modelRotation, final Direction direction, final a a) {
        BakedQuadFactory.c[a(modelRotation, direction)] = a;
    }
    
    private static int a(final net.minecraft.client.render.model.ModelRotation modelRotation, final Direction direction) {
        return net.minecraft.client.render.model.ModelRotation.values().length * direction.ordinal() + modelRotation.ordinal();
    }
    
    static {
        a = 1.0f / (float)Math.cos(0.39269909262657166) - 1.0f;
        b = 1.0f / (float)Math.cos(0.7853981852531433) - 1.0f;
        c = new a[net.minecraft.client.render.model.ModelRotation.values().length * Direction.values().length];
        d = new a() {
            @Override
            ModelElementTexture a(final float float1, final float float2, final float float3, final float float4) {
                return new ModelElementTexture(new float[] { float1, float2, float3, float4 }, 0);
            }
        };
        e = new a() {
            @Override
            ModelElementTexture a(final float float1, final float float2, final float float3, final float float4) {
                return new ModelElementTexture(new float[] { float4, 16.0f - float1, float2, 16.0f - float3 }, 270);
            }
        };
        f = new a() {
            @Override
            ModelElementTexture a(final float float1, final float float2, final float float3, final float float4) {
                return new ModelElementTexture(new float[] { 16.0f - float1, 16.0f - float2, 16.0f - float3, 16.0f - float4 }, 0);
            }
        };
        g = new a() {
            @Override
            ModelElementTexture a(final float float1, final float float2, final float float3, final float float4) {
                return new ModelElementTexture(new float[] { 16.0f - float2, float3, 16.0f - float4, float1 }, 90);
            }
        };
        a(net.minecraft.client.render.model.ModelRotation.X0_Y0, Direction.DOWN, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y0, Direction.EAST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y0, Direction.NORTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y0, Direction.SOUTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y0, Direction.UP, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y0, Direction.WEST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y90, Direction.EAST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y90, Direction.NORTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y90, Direction.SOUTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y90, Direction.WEST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y180, Direction.EAST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y180, Direction.NORTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y180, Direction.SOUTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y180, Direction.WEST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y270, Direction.EAST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y270, Direction.NORTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y270, Direction.SOUTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y270, Direction.WEST, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y0, Direction.DOWN, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y0, Direction.SOUTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y90, Direction.DOWN, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y180, Direction.DOWN, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y180, Direction.NORTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y270, Direction.DOWN, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y0, Direction.DOWN, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y0, Direction.UP, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y0, Direction.SOUTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y0, Direction.UP, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y90, Direction.UP, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y180, Direction.NORTH, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y180, Direction.UP, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y270, Direction.UP, BakedQuadFactory.d);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y270, Direction.UP, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y90, Direction.DOWN, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y0, Direction.WEST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y90, Direction.WEST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y180, Direction.WEST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y270, Direction.NORTH, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y270, Direction.SOUTH, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y270, Direction.WEST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y90, Direction.UP, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y270, Direction.DOWN, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y0, Direction.EAST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y90, Direction.EAST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y90, Direction.NORTH, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y90, Direction.SOUTH, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y180, Direction.EAST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y270, Direction.EAST, BakedQuadFactory.e);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y180, Direction.DOWN, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y180, Direction.UP, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y0, Direction.NORTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y0, Direction.UP, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y90, Direction.UP, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y180, Direction.SOUTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y180, Direction.UP, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y270, Direction.UP, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y0, Direction.EAST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y0, Direction.NORTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y0, Direction.SOUTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y0, Direction.WEST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y90, Direction.EAST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y90, Direction.NORTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y90, Direction.SOUTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y90, Direction.WEST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y180, Direction.DOWN, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y180, Direction.EAST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y180, Direction.NORTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y180, Direction.SOUTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y180, Direction.UP, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y180, Direction.WEST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y270, Direction.EAST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y270, Direction.NORTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y270, Direction.SOUTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y270, Direction.WEST, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y0, Direction.DOWN, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y0, Direction.NORTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y90, Direction.DOWN, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y180, Direction.DOWN, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y180, Direction.SOUTH, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y270, Direction.DOWN, BakedQuadFactory.f);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y90, Direction.UP, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X0_Y270, Direction.DOWN, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y0, Direction.EAST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y90, Direction.EAST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y90, Direction.NORTH, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y90, Direction.SOUTH, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y180, Direction.EAST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X90_Y270, Direction.EAST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y0, Direction.WEST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y90, Direction.DOWN, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X180_Y270, Direction.UP, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y90, Direction.WEST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y180, Direction.WEST, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y270, Direction.NORTH, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y270, Direction.SOUTH, BakedQuadFactory.g);
        a(net.minecraft.client.render.model.ModelRotation.X270_Y270, Direction.WEST, BakedQuadFactory.g);
    }
    
    @Environment(EnvType.CLIENT)
    abstract static class a
    {
        private a() {
        }
        
        public ModelElementTexture a(final ModelElementTexture modelElementTexture) {
            final float float2 = modelElementTexture.getU(modelElementTexture.c(0));
            final float float3 = modelElementTexture.getV(modelElementTexture.c(0));
            final float float4 = modelElementTexture.getU(modelElementTexture.c(2));
            final float float5 = modelElementTexture.getV(modelElementTexture.c(2));
            return this.a(float2, float3, float4, float5);
        }
        
        abstract ModelElementTexture a(final float arg1, final float arg2, final float arg3, final float arg4);
    }
}
