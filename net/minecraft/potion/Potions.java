package net.minecraft.potion;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;

public class Potions
{
    public static final Potion a;
    public static final Potion b;
    public static final Potion c;
    public static final Potion d;
    public static final Potion e;
    public static final Potion f;
    public static final Potion g;
    public static final Potion h;
    public static final Potion i;
    public static final Potion j;
    public static final Potion k;
    public static final Potion l;
    public static final Potion m;
    public static final Potion n;
    public static final Potion o;
    public static final Potion p;
    public static final Potion q;
    public static final Potion r;
    public static final Potion s;
    public static final Potion t;
    public static final Potion u;
    public static final Potion v;
    public static final Potion w;
    public static final Potion x;
    public static final Potion y;
    public static final Potion z;
    public static final Potion A;
    public static final Potion B;
    public static final Potion C;
    public static final Potion D;
    public static final Potion E;
    public static final Potion F;
    public static final Potion G;
    public static final Potion H;
    public static final Potion I;
    public static final Potion J;
    public static final Potion K;
    public static final Potion L;
    public static final Potion M;
    public static final Potion N;
    public static final Potion O;
    public static final Potion P;
    public static final Potion Q;
    
    private static Potion register(final String name, final Potion potion) {
        return Registry.<Potion>register(Registry.POTION, name, potion);
    }
    
    static {
        a = register("empty", new Potion(new StatusEffectInstance[0]));
        b = register("water", new Potion(new StatusEffectInstance[0]));
        c = register("mundane", new Potion(new StatusEffectInstance[0]));
        d = register("thick", new Potion(new StatusEffectInstance[0]));
        e = register("awkward", new Potion(new StatusEffectInstance[0]));
        f = register("night_vision", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.p, 3600) }));
        g = register("long_night_vision", new Potion("night_vision", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.p, 9600) }));
        h = register("invisibility", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.n, 3600) }));
        i = register("long_invisibility", new Potion("invisibility", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.n, 9600) }));
        j = register("leaping", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.h, 3600) }));
        k = register("long_leaping", new Potion("leaping", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.h, 9600) }));
        l = register("strong_leaping", new Potion("leaping", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.h, 1800, 1) }));
        m = register("fire_resistance", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.l, 3600) }));
        n = register("long_fire_resistance", new Potion("fire_resistance", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.l, 9600) }));
        o = register("swiftness", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.a, 3600) }));
        p = register("long_swiftness", new Potion("swiftness", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.a, 9600) }));
        q = register("strong_swiftness", new Potion("swiftness", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.a, 1800, 1) }));
        r = register("slowness", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.b, 1800) }));
        s = register("long_slowness", new Potion("slowness", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.b, 4800) }));
        t = register("strong_slowness", new Potion("slowness", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.b, 400, 3) }));
        u = register("turtle_master", new Potion("turtle_master", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.b, 400, 3), new StatusEffectInstance(StatusEffects.k, 400, 2) }));
        v = register("long_turtle_master", new Potion("turtle_master", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.b, 800, 3), new StatusEffectInstance(StatusEffects.k, 800, 2) }));
        w = register("strong_turtle_master", new Potion("turtle_master", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.b, 400, 5), new StatusEffectInstance(StatusEffects.k, 400, 3) }));
        x = register("water_breathing", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.m, 3600) }));
        y = register("long_water_breathing", new Potion("water_breathing", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.m, 9600) }));
        z = register("healing", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.f, 1) }));
        A = register("strong_healing", new Potion("healing", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.f, 1, 1) }));
        B = register("harming", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.g, 1) }));
        C = register("strong_harming", new Potion("harming", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.g, 1, 1) }));
        D = register("poison", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.s, 900) }));
        E = register("long_poison", new Potion("poison", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.s, 1800) }));
        F = register("strong_poison", new Potion("poison", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.s, 432, 1) }));
        G = register("regeneration", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.j, 900) }));
        H = register("long_regeneration", new Potion("regeneration", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.j, 1800) }));
        I = register("strong_regeneration", new Potion("regeneration", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.j, 450, 1) }));
        J = register("strength", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.e, 3600) }));
        K = register("long_strength", new Potion("strength", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.e, 9600) }));
        L = register("strong_strength", new Potion("strength", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.e, 1800, 1) }));
        M = register("weakness", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.r, 1800) }));
        N = register("long_weakness", new Potion("weakness", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.r, 4800) }));
        O = register("luck", new Potion("luck", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.z, 6000) }));
        P = register("slow_falling", new Potion(new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.B, 1800) }));
        Q = register("long_slow_falling", new Potion("slow_falling", new StatusEffectInstance[] { new StatusEffectInstance(StatusEffects.B, 4800) }));
    }
}
