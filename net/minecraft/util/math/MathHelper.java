package net.minecraft.util.math;

import net.minecraft.util.SystemUtil;
import java.util.function.IntPredicate;
import java.util.UUID;
import org.apache.commons.lang3.math.NumberUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;

public class MathHelper
{
    public static final float SQUARE_ROOT_OF_TWO;
    private static final float[] SINE_TABLE;
    private static final Random RANDOM;
    private static final int[] MULTIPLY_DE_BRUJIN_BIT_POSITION;
    private static final double SMALLEST_FRACTION_FREE_DOUBLE;
    private static final double[] ARCSINE_TABLE;
    private static final double[] COSINE_TABLE;
    
    public static float sin(final float float1) {
        return MathHelper.SINE_TABLE[(int)(float1 * 10430.378f) & 0xFFFF];
    }
    
    public static float cos(final float float1) {
        return MathHelper.SINE_TABLE[(int)(float1 * 10430.378f + 16384.0f) & 0xFFFF];
    }
    
    public static float sqrt(final float float1) {
        return (float)Math.sqrt(float1);
    }
    
    public static float sqrt(final double double1) {
        return (float)Math.sqrt(double1);
    }
    
    public static int floor(final float float1) {
        final int integer2 = (int)float1;
        return (float1 < integer2) ? (integer2 - 1) : integer2;
    }
    
    @Environment(EnvType.CLIENT)
    public static int fastFloor(final double double1) {
        return (int)(double1 + 1024.0) - 1024;
    }
    
    public static int floor(final double double1) {
        final int integer3 = (int)double1;
        return (double1 < integer3) ? (integer3 - 1) : integer3;
    }
    
    public static long lfloor(final double double1) {
        final long long3 = (long)double1;
        return (double1 < long3) ? (long3 - 1L) : long3;
    }
    
    @Environment(EnvType.CLIENT)
    public static int absFloor(final double double1) {
        return (int)((double1 >= 0.0) ? double1 : (-double1 + 1.0));
    }
    
    public static float abs(final float float1) {
        return Math.abs(float1);
    }
    
    public static int abs(final int integer) {
        return Math.abs(integer);
    }
    
    public static int ceil(final float float1) {
        final int integer2 = (int)float1;
        return (float1 > integer2) ? (integer2 + 1) : integer2;
    }
    
    public static int ceil(final double double1) {
        final int integer3 = (int)double1;
        return (double1 > integer3) ? (integer3 + 1) : integer3;
    }
    
    public static int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static float clamp(final float value, final float min, final float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static double clamp(final double value, final double min, final double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static double clampedLerp(final double first, final double second, final double delta) {
        if (delta < 0.0) {
            return first;
        }
        if (delta > 1.0) {
            return second;
        }
        return lerp(delta, first, second);
    }
    
    public static double absMax(double double1, double double3) {
        if (double1 < 0.0) {
            double1 = -double1;
        }
        if (double3 < 0.0) {
            double3 = -double3;
        }
        return (double1 > double3) ? double1 : double3;
    }
    
    public static int floorDiv(final int integer1, final int integer2) {
        return Math.floorDiv(integer1, integer2);
    }
    
    public static int nextInt(final Random random, final int min, final int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }
    
    public static float nextFloat(final Random random, final float min, final float max) {
        if (min >= max) {
            return min;
        }
        return random.nextFloat() * (max - min) + min;
    }
    
    public static double nextDouble(final Random random, final double min, final double max) {
        if (min >= max) {
            return min;
        }
        return random.nextDouble() * (max - min) + min;
    }
    
    public static double average(final long[] array) {
        long long2 = 0L;
        for (final long long3 : array) {
            long2 += long3;
        }
        return long2 / (double)array.length;
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean equalsApproximate(final float float1, final float float2) {
        return Math.abs(float2 - float1) < 1.0E-5f;
    }
    
    public static boolean b(final double double1, final double double3) {
        return Math.abs(double3 - double1) < 9.999999747378752E-6;
    }
    
    public static int floorMod(final int integer1, final int integer2) {
        return Math.floorMod(integer1, integer2);
    }
    
    @Environment(EnvType.CLIENT)
    public static float floorMod(final float float1, final float float2) {
        return (float1 % float2 + float2) % float2;
    }
    
    @Environment(EnvType.CLIENT)
    public static double floorMod(final double double1, final double double3) {
        return (double1 % double3 + double3) % double3;
    }
    
    @Environment(EnvType.CLIENT)
    public static int wrapDegrees(final int integer) {
        int integer2 = integer % 360;
        if (integer2 >= 180) {
            integer2 -= 360;
        }
        if (integer2 < -180) {
            integer2 += 360;
        }
        return integer2;
    }
    
    public static float wrapDegrees(final float float1) {
        float float2 = float1 % 360.0f;
        if (float2 >= 180.0f) {
            float2 -= 360.0f;
        }
        if (float2 < -180.0f) {
            float2 += 360.0f;
        }
        return float2;
    }
    
    public static double wrapDegrees(final double double1) {
        double double2 = double1 % 360.0;
        if (double2 >= 180.0) {
            double2 -= 360.0;
        }
        if (double2 < -180.0) {
            double2 += 360.0;
        }
        return double2;
    }
    
    public static float subtractAngles(final float start, final float end) {
        return wrapDegrees(end - start);
    }
    
    public static float angleBetween(final float first, final float second) {
        return abs(subtractAngles(first, second));
    }
    
    public static float b(final float start, final float end, final float speed) {
        final float float4 = subtractAngles(start, end);
        final float float5 = clamp(float4, -speed, speed);
        return end - float5;
    }
    
    public static float c(final float float1, final float float2, float float3) {
        float3 = abs(float3);
        if (float1 < float2) {
            return clamp(float1 + float3, float1, float2);
        }
        return clamp(float1 - float3, float2, float1);
    }
    
    public static float d(final float float1, final float float2, final float float3) {
        final float float4 = subtractAngles(float1, float2);
        return c(float1, float1 + float4, float3);
    }
    
    @Environment(EnvType.CLIENT)
    public static int parseInt(final String string, final int fallback) {
        return NumberUtils.toInt(string, fallback);
    }
    
    @Environment(EnvType.CLIENT)
    public static int parseInt(final String string, final int fallback, final int minimum) {
        return Math.max(minimum, parseInt(string, fallback));
    }
    
    @Environment(EnvType.CLIENT)
    public static double parseDouble(final String string, final double fallback) {
        try {
            return Double.parseDouble(string);
        }
        catch (Throwable throwable4) {
            return fallback;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static double parseDouble(final String string, final double fallback, final double double4) {
        return Math.max(double4, parseDouble(string, fallback));
    }
    
    public static int smallestEncompassingPowerOfTwo(final int value) {
        int integer2 = value - 1;
        integer2 |= integer2 >> 1;
        integer2 |= integer2 >> 2;
        integer2 |= integer2 >> 4;
        integer2 |= integer2 >> 8;
        integer2 |= integer2 >> 16;
        return integer2 + 1;
    }
    
    private static boolean isPowerOfTwo(final int integer) {
        return integer != 0 && (integer & integer - 1) == 0x0;
    }
    
    public static int log2DeBrujin(int integer) {
        integer = (isPowerOfTwo(integer) ? integer : smallestEncompassingPowerOfTwo(integer));
        return MathHelper.MULTIPLY_DE_BRUJIN_BIT_POSITION[(int)(integer * 125613361L >> 27) & 0x1F];
    }
    
    public static int log2(final int integer) {
        return log2DeBrujin(integer) - (isPowerOfTwo(integer) ? 0 : 1);
    }
    
    public static int roundUp(final int integer1, int integer2) {
        if (integer2 == 0) {
            return 0;
        }
        if (integer1 == 0) {
            return integer2;
        }
        if (integer1 < 0) {
            integer2 *= -1;
        }
        final int integer3 = integer1 % integer2;
        if (integer3 == 0) {
            return integer1;
        }
        return integer1 + integer2 - integer3;
    }
    
    @Environment(EnvType.CLIENT)
    public static int packRgb(final float r, final float g, final float b) {
        return packRgb(floor(r * 255.0f), floor(g * 255.0f), floor(b * 255.0f));
    }
    
    @Environment(EnvType.CLIENT)
    public static int packRgb(final int r, final int g, final int b) {
        int integer4 = r;
        integer4 = (integer4 << 8) + g;
        integer4 = (integer4 << 8) + b;
        return integer4;
    }
    
    @Environment(EnvType.CLIENT)
    public static int multiplyColors(final int first, final int second) {
        final int integer3 = (first & 0xFF0000) >> 16;
        final int integer4 = (second & 0xFF0000) >> 16;
        final int integer5 = (first & 0xFF00) >> 8;
        final int integer6 = (second & 0xFF00) >> 8;
        final int integer7 = (first & 0xFF) >> 0;
        final int integer8 = (second & 0xFF) >> 0;
        final int integer9 = (int)(integer3 * (float)integer4 / 255.0f);
        final int integer10 = (int)(integer5 * (float)integer6 / 255.0f);
        final int integer11 = (int)(integer7 * (float)integer8 / 255.0f);
        return (first & 0xFF000000) | integer9 << 16 | integer10 << 8 | integer11;
    }
    
    public static double fractionalPart(final double value) {
        return value - lfloor(value);
    }
    
    public static long hashCode(final Vec3i vec) {
        return hashCode(vec.getX(), vec.getY(), vec.getZ());
    }
    
    public static long hashCode(final int x, final int y, final int z) {
        long long4 = (long)(x * 3129871) ^ z * 116129781L ^ (long)y;
        long4 = long4 * long4 * 42317861L + long4 * 11L;
        return long4 >> 16;
    }
    
    public static UUID randomUuid(final Random random) {
        final long long2 = (random.nextLong() & 0xFFFFFFFFFFFF0FFFL) | 0x4000L;
        final long long3 = (random.nextLong() & 0x3FFFFFFFFFFFFFFFL) | Long.MIN_VALUE;
        return new UUID(long2, long3);
    }
    
    public static UUID randomUUID() {
        return randomUuid(MathHelper.RANDOM);
    }
    
    public static double minusDiv(final double numerator, final double delta, final double denominator) {
        return (numerator - delta) / (denominator - delta);
    }
    
    public static double atan2(double double1, double double3) {
        final double double4 = double3 * double3 + double1 * double1;
        if (Double.isNaN(double4)) {
            return Double.NaN;
        }
        final boolean boolean7 = double1 < 0.0;
        if (boolean7) {
            double1 = -double1;
        }
        final boolean boolean8 = double3 < 0.0;
        if (boolean8) {
            double3 = -double3;
        }
        final boolean boolean9 = double1 > double3;
        if (boolean9) {
            final double double5 = double3;
            double3 = double1;
            double1 = double5;
        }
        final double double5 = fastInverseSqrt(double4);
        double3 *= double5;
        double1 *= double5;
        final double double6 = MathHelper.SMALLEST_FRACTION_FREE_DOUBLE + double1;
        final int integer14 = (int)Double.doubleToRawLongBits(double6);
        final double double7 = MathHelper.ARCSINE_TABLE[integer14];
        final double double8 = MathHelper.COSINE_TABLE[integer14];
        final double double9 = double6 - MathHelper.SMALLEST_FRACTION_FREE_DOUBLE;
        final double double10 = double1 * double8 - double3 * double9;
        final double double11 = (6.0 + double10 * double10) * double10 * 0.16666666666666666;
        double double12 = double7 + double11;
        if (boolean9) {
            double12 = 1.5707963267948966 - double12;
        }
        if (boolean8) {
            double12 = 3.141592653589793 - double12;
        }
        if (boolean7) {
            double12 = -double12;
        }
        return double12;
    }
    
    public static double fastInverseSqrt(double value) {
        final double double3 = 0.5 * value;
        long long5 = Double.doubleToRawLongBits(value);
        long5 = 6910469410427058090L - (long5 >> 1);
        value = Double.longBitsToDouble(long5);
        value *= 1.5 - double3 * value * value;
        return value;
    }
    
    @Environment(EnvType.CLIENT)
    public static int hsvToRgb(final float hue, final float saturation, final float value) {
        final int integer4 = (int)(hue * 6.0f) % 6;
        final float float5 = hue * 6.0f - integer4;
        final float float6 = value * (1.0f - saturation);
        final float float7 = value * (1.0f - float5 * saturation);
        final float float8 = value * (1.0f - (1.0f - float5) * saturation);
        float float9 = 0.0f;
        float float10 = 0.0f;
        float float11 = 0.0f;
        switch (integer4) {
            case 0: {
                float9 = value;
                float10 = float8;
                float11 = float6;
                break;
            }
            case 1: {
                float9 = float7;
                float10 = value;
                float11 = float6;
                break;
            }
            case 2: {
                float9 = float6;
                float10 = value;
                float11 = float8;
                break;
            }
            case 3: {
                float9 = float6;
                float10 = float7;
                float11 = value;
                break;
            }
            case 4: {
                float9 = float8;
                float10 = float6;
                float11 = value;
                break;
            }
            case 5: {
                float9 = value;
                float10 = float6;
                float11 = float7;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
            }
        }
        final int integer5 = clamp((int)(float9 * 255.0f), 0, 255);
        final int integer6 = clamp((int)(float10 * 255.0f), 0, 255);
        final int integer7 = clamp((int)(float11 * 255.0f), 0, 255);
        return integer5 << 16 | integer6 << 8 | integer7;
    }
    
    public static int f(int integer) {
        integer ^= integer >>> 16;
        integer *= -2048144789;
        integer ^= integer >>> 13;
        integer *= -1028477387;
        integer ^= integer >>> 16;
        return integer;
    }
    
    public static int binarySearch(int start, final int end, final IntPredicate leftPredicate) {
        int integer4 = end - start;
        while (integer4 > 0) {
            final int integer5 = integer4 / 2;
            final int integer6 = start + integer5;
            if (leftPredicate.test(integer6)) {
                integer4 = integer5;
            }
            else {
                start = integer6 + 1;
                integer4 -= integer5 + 1;
            }
        }
        return start;
    }
    
    public static float lerp(final float delta, final float first, final float second) {
        return first + delta * (second - first);
    }
    
    public static double lerp(final double delta, final double first, final double second) {
        return first + delta * (second - first);
    }
    
    public static double lerp2(final double deltaX, final double deltaY, final double double5, final double double7, final double double9, final double double11) {
        return lerp(deltaY, lerp(deltaX, double5, double7), lerp(deltaX, double9, double11));
    }
    
    public static double lerp3(final double deltaX, final double deltaY, final double deltaZ, final double double7, final double double9, final double double11, final double double13, final double double15, final double double17, final double double19, final double double21) {
        return lerp(deltaZ, lerp2(deltaX, deltaY, double7, double9, double11, double13), lerp2(deltaX, deltaY, double15, double17, double19, double21));
    }
    
    public static double perlinFade(final double double1) {
        return double1 * double1 * double1 * (double1 * (double1 * 6.0 - 15.0) + 10.0);
    }
    
    public static int sign(final double double1) {
        if (double1 == 0.0) {
            return 0;
        }
        return (double1 > 0.0) ? 1 : -1;
    }
    
    @Environment(EnvType.CLIENT)
    public static float lerpAngleDegrees(final float delta, final float first, final float second) {
        return first + delta * wrapDegrees(second - first);
    }
    
    static {
        SQUARE_ROOT_OF_TWO = sqrt(2.0f);
        int integer2;
        SINE_TABLE = SystemUtil.<float[]>consume(new float[65536], arr -> {
            for (integer2 = 0; integer2 < arr.length; ++integer2) {
                arr[integer2] = (float)Math.sin(integer2 * 3.141592653589793 * 2.0 / 65536.0);
            }
            return;
        });
        RANDOM = new Random();
        MULTIPLY_DE_BRUJIN_BIT_POSITION = new int[] { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };
        SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
        ARCSINE_TABLE = new double[257];
        COSINE_TABLE = new double[257];
        for (int integer3 = 0; integer3 < 257; ++integer3) {
            final double double2 = integer3 / 256.0;
            final double double3 = Math.asin(double2);
            MathHelper.COSINE_TABLE[integer3] = Math.cos(double3);
            MathHelper.ARCSINE_TABLE[integer3] = double3;
        }
    }
}
