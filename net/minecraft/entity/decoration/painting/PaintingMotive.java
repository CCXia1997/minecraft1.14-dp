package net.minecraft.entity.decoration.painting;

import net.minecraft.util.registry.Registry;

public class PaintingMotive
{
    public static final PaintingMotive a;
    public static final PaintingMotive b;
    public static final PaintingMotive c;
    public static final PaintingMotive d;
    public static final PaintingMotive e;
    public static final PaintingMotive f;
    public static final PaintingMotive g;
    public static final PaintingMotive h;
    public static final PaintingMotive i;
    public static final PaintingMotive j;
    public static final PaintingMotive k;
    public static final PaintingMotive l;
    public static final PaintingMotive m;
    public static final PaintingMotive n;
    public static final PaintingMotive o;
    public static final PaintingMotive p;
    public static final PaintingMotive q;
    public static final PaintingMotive r;
    public static final PaintingMotive s;
    public static final PaintingMotive t;
    public static final PaintingMotive u;
    public static final PaintingMotive v;
    public static final PaintingMotive w;
    public static final PaintingMotive x;
    public static final PaintingMotive y;
    public static final PaintingMotive z;
    private final int width;
    private final int height;
    
    private static PaintingMotive register(final String string, final int integer2, final int integer3) {
        return Registry.<PaintingMotive>register(Registry.MOTIVE, string, new PaintingMotive(integer2, integer3));
    }
    
    public PaintingMotive(final int integer1, final int integer2) {
        this.width = integer1;
        this.height = integer2;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    static {
        a = register("kebab", 16, 16);
        b = register("aztec", 16, 16);
        c = register("alban", 16, 16);
        d = register("aztec2", 16, 16);
        e = register("bomb", 16, 16);
        f = register("plant", 16, 16);
        g = register("wasteland", 16, 16);
        h = register("pool", 32, 16);
        i = register("courbet", 32, 16);
        j = register("sea", 32, 16);
        k = register("sunset", 32, 16);
        l = register("creebet", 32, 16);
        m = register("wanderer", 16, 32);
        n = register("graham", 16, 32);
        o = register("match", 32, 32);
        p = register("bust", 32, 32);
        q = register("stage", 32, 32);
        r = register("void", 32, 32);
        s = register("skull_and_roses", 32, 32);
        t = register("wither", 32, 32);
        u = register("fighters", 64, 32);
        v = register("pointer", 64, 64);
        w = register("pigscene", 64, 64);
        x = register("burning_skull", 64, 64);
        y = register("skeleton", 64, 48);
        z = register("donkey_kong", 64, 48);
    }
}
