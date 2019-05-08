package net.minecraft.realms;

import org.apache.commons.lang3.StringUtils;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsMth
{
    public static float sin(final float float1) {
        return MathHelper.sin(float1);
    }
    
    public static double nextDouble(final Random random, final double double2, final double double4) {
        return MathHelper.nextDouble(random, double2, double4);
    }
    
    public static int ceil(final float float1) {
        return MathHelper.ceil(float1);
    }
    
    public static int floor(final double double1) {
        return MathHelper.floor(double1);
    }
    
    public static int intFloorDiv(final int integer1, final int integer2) {
        return MathHelper.floorDiv(integer1, integer2);
    }
    
    public static float abs(final float float1) {
        return MathHelper.abs(float1);
    }
    
    public static int clamp(final int integer1, final int integer2, final int integer3) {
        return MathHelper.clamp(integer1, integer2, integer3);
    }
    
    public static double clampedLerp(final double double1, final double double3, final double double5) {
        return MathHelper.clampedLerp(double1, double3, double5);
    }
    
    public static int ceil(final double double1) {
        return MathHelper.ceil(double1);
    }
    
    public static boolean isEmpty(final String string) {
        return StringUtils.isEmpty((CharSequence)string);
    }
    
    public static long lfloor(final double double1) {
        return MathHelper.lfloor(double1);
    }
    
    public static float sqrt(final double double1) {
        return MathHelper.sqrt(double1);
    }
    
    public static double clamp(final double double1, final double double3, final double double5) {
        return MathHelper.clamp(double1, double3, double5);
    }
    
    public static int getInt(final String string, final int integer) {
        return MathHelper.parseInt(string, integer);
    }
    
    public static double getDouble(final String string, final double double2) {
        return MathHelper.parseDouble(string, double2);
    }
    
    public static int log2(final int integer) {
        return MathHelper.log2(integer);
    }
    
    public static int absFloor(final double double1) {
        return MathHelper.absFloor(double1);
    }
    
    public static int smallestEncompassingPowerOfTwo(final int integer) {
        return MathHelper.smallestEncompassingPowerOfTwo(integer);
    }
    
    public static float sqrt(final float float1) {
        return MathHelper.sqrt(float1);
    }
    
    public static float cos(final float float1) {
        return MathHelper.cos(float1);
    }
    
    public static int getInt(final String string, final int integer2, final int integer3) {
        return MathHelper.parseInt(string, integer2, integer3);
    }
    
    public static int fastFloor(final double double1) {
        return MathHelper.fastFloor(double1);
    }
    
    public static double absMax(final double double1, final double double3) {
        return MathHelper.absMax(double1, double3);
    }
    
    public static float nextFloat(final Random random, final float float2, final float float3) {
        return MathHelper.nextFloat(random, float2, float3);
    }
    
    public static double wrapDegrees(final double double1) {
        return MathHelper.wrapDegrees(double1);
    }
    
    public static float wrapDegrees(final float float1) {
        return MathHelper.wrapDegrees(float1);
    }
    
    public static float clamp(final float float1, final float float2, final float float3) {
        return MathHelper.clamp(float1, float2, float3);
    }
    
    public static double getDouble(final String string, final double double2, final double double4) {
        return MathHelper.parseDouble(string, double2, double4);
    }
    
    public static int roundUp(final int integer1, final int integer2) {
        return MathHelper.roundUp(integer1, integer2);
    }
    
    public static double average(final long[] arr) {
        return MathHelper.average(arr);
    }
    
    public static int floor(final float float1) {
        return MathHelper.floor(float1);
    }
    
    public static int abs(final int integer) {
        return MathHelper.abs(integer);
    }
    
    public static int nextInt(final Random random, final int integer2, final int integer3) {
        return MathHelper.nextInt(random, integer2, integer3);
    }
}
