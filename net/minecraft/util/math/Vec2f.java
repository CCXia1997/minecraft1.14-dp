package net.minecraft.util.math;

public class Vec2f
{
    public static final Vec2f ZERO;
    public static final Vec2f b;
    public static final Vec2f c;
    public static final Vec2f d;
    public static final Vec2f e;
    public static final Vec2f f;
    public static final Vec2f g;
    public static final Vec2f h;
    public final float x;
    public final float y;
    
    public Vec2f(final float x, final float float2) {
        this.x = x;
        this.y = float2;
    }
    
    public boolean equals(final Vec2f vec2f) {
        return this.x == vec2f.x && this.y == vec2f.y;
    }
    
    static {
        ZERO = new Vec2f(0.0f, 0.0f);
        b = new Vec2f(1.0f, 1.0f);
        c = new Vec2f(1.0f, 0.0f);
        d = new Vec2f(-1.0f, 0.0f);
        e = new Vec2f(0.0f, 1.0f);
        f = new Vec2f(0.0f, -1.0f);
        g = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
        h = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);
    }
}
