package net.minecraft.particle;

import net.minecraft.util.registry.Registry;

public class ParticleTypes
{
    public static final DefaultParticleType a;
    public static final DefaultParticleType b;
    public static final DefaultParticleType c;
    public static final ParticleType<BlockStateParticleParameters> d;
    public static final DefaultParticleType e;
    public static final DefaultParticleType f;
    public static final DefaultParticleType g;
    public static final DefaultParticleType h;
    public static final DefaultParticleType i;
    public static final DefaultParticleType j;
    public static final DefaultParticleType k;
    public static final DefaultParticleType l;
    public static final DefaultParticleType m;
    public static final DefaultParticleType n;
    public static final ParticleType<DustParticleParameters> o;
    public static final DefaultParticleType p;
    public static final DefaultParticleType q;
    public static final DefaultParticleType r;
    public static final DefaultParticleType s;
    public static final DefaultParticleType t;
    public static final DefaultParticleType u;
    public static final DefaultParticleType v;
    public static final DefaultParticleType w;
    public static final ParticleType<BlockStateParticleParameters> x;
    public static final DefaultParticleType y;
    public static final DefaultParticleType z;
    public static final DefaultParticleType A;
    public static final DefaultParticleType B;
    public static final DefaultParticleType C;
    public static final DefaultParticleType D;
    public static final DefaultParticleType E;
    public static final DefaultParticleType F;
    public static final ParticleType<ItemStackParticleParameters> G;
    public static final DefaultParticleType H;
    public static final DefaultParticleType I;
    public static final DefaultParticleType J;
    public static final DefaultParticleType K;
    public static final DefaultParticleType L;
    public static final DefaultParticleType M;
    public static final DefaultParticleType N;
    public static final DefaultParticleType O;
    public static final DefaultParticleType P;
    public static final DefaultParticleType Q;
    public static final DefaultParticleType R;
    public static final DefaultParticleType S;
    public static final DefaultParticleType T;
    public static final DefaultParticleType U;
    public static final DefaultParticleType V;
    public static final DefaultParticleType W;
    public static final DefaultParticleType X;
    public static final DefaultParticleType Y;
    public static final DefaultParticleType Z;
    public static final DefaultParticleType aa;
    public static final DefaultParticleType ab;
    public static final DefaultParticleType ac;
    public static final DefaultParticleType ad;
    public static final DefaultParticleType ae;
    public static final DefaultParticleType af;
    
    private static DefaultParticleType register(final String name, final boolean alwaysShow) {
        return Registry.<DefaultParticleType>register(Registry.PARTICLE_TYPE, name, new DefaultParticleType(alwaysShow));
    }
    
    private static <T extends ParticleParameters> ParticleType<T> register(final String name, final ParticleParameters.Factory<T> factory) {
        return Registry.<ParticleType<T>>register(Registry.PARTICLE_TYPE, name, new ParticleType<T>(false, factory));
    }
    
    static {
        a = register("ambient_entity_effect", false);
        b = register("angry_villager", false);
        c = register("barrier", false);
        d = ParticleTypes.<BlockStateParticleParameters>register("block", BlockStateParticleParameters.PARAMETERS_FACTORY);
        e = register("bubble", false);
        f = register("cloud", false);
        g = register("crit", false);
        h = register("damage_indicator", true);
        i = register("dragon_breath", false);
        j = register("dripping_lava", false);
        k = register("falling_lava", false);
        l = register("landing_lava", false);
        m = register("dripping_water", false);
        n = register("falling_water", false);
        o = ParticleTypes.<DustParticleParameters>register("dust", DustParticleParameters.PARAMETERS_FACTORY);
        p = register("effect", false);
        q = register("elder_guardian", true);
        r = register("enchanted_hit", false);
        s = register("enchant", false);
        t = register("end_rod", false);
        u = register("entity_effect", false);
        v = register("explosion_emitter", true);
        w = register("explosion", true);
        x = ParticleTypes.<BlockStateParticleParameters>register("falling_dust", BlockStateParticleParameters.PARAMETERS_FACTORY);
        y = register("firework", false);
        z = register("fishing", false);
        A = register("flame", false);
        B = register("flash", false);
        C = register("happy_villager", false);
        D = register("composter", false);
        E = register("heart", false);
        F = register("instant_effect", false);
        G = ParticleTypes.<ItemStackParticleParameters>register("item", ItemStackParticleParameters.PARAMETERS_FACTORY);
        H = register("item_slime", false);
        I = register("item_snowball", false);
        J = register("large_smoke", false);
        K = register("lava", false);
        L = register("mycelium", false);
        M = register("note", false);
        N = register("poof", true);
        O = register("portal", false);
        P = register("rain", false);
        Q = register("smoke", false);
        R = register("sneeze", false);
        S = register("spit", true);
        T = register("squid_ink", true);
        U = register("sweep_attack", true);
        V = register("totem_of_undying", false);
        W = register("underwater", false);
        X = register("splash", false);
        Y = register("witch", false);
        Z = register("bubble_pop", false);
        aa = register("current_down", false);
        ab = register("bubble_column_up", false);
        ac = register("nautilus", false);
        ad = register("dolphin", false);
        ae = register("campfire_cosy_smoke", true);
        af = register("campfire_signal_smoke", true);
    }
}
