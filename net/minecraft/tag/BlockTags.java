package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;

public class BlockTags
{
    private static TagContainer<Block> container;
    private static int containerChanges;
    public static final Tag<Block> a;
    public static final Tag<Block> b;
    public static final Tag<Block> c;
    public static final Tag<Block> d;
    public static final Tag<Block> e;
    public static final Tag<Block> f;
    public static final Tag<Block> g;
    public static final Tag<Block> h;
    public static final Tag<Block> i;
    public static final Tag<Block> j;
    public static final Tag<Block> k;
    public static final Tag<Block> l;
    public static final Tag<Block> m;
    public static final Tag<Block> n;
    public static final Tag<Block> o;
    public static final Tag<Block> p;
    public static final Tag<Block> q;
    public static final Tag<Block> r;
    public static final Tag<Block> s;
    public static final Tag<Block> t;
    public static final Tag<Block> u;
    public static final Tag<Block> v;
    public static final Tag<Block> w;
    public static final Tag<Block> x;
    public static final Tag<Block> y;
    public static final Tag<Block> z;
    public static final Tag<Block> A;
    public static final Tag<Block> B;
    public static final Tag<Block> C;
    public static final Tag<Block> D;
    public static final Tag<Block> E;
    public static final Tag<Block> F;
    public static final Tag<Block> G;
    public static final Tag<Block> H;
    public static final Tag<Block> I;
    public static final Tag<Block> J;
    public static final Tag<Block> K;
    public static final Tag<Block> L;
    public static final Tag<Block> M;
    public static final Tag<Block> N;
    public static final Tag<Block> O;
    public static final Tag<Block> P;
    public static final Tag<Block> Q;
    public static final Tag<Block> R;
    public static final Tag<Block> S;
    public static final Tag<Block> T;
    public static final Tag<Block> U;
    public static final Tag<Block> V;
    public static final Tag<Block> W;
    public static final Tag<Block> X;
    
    public static void setContainer(final TagContainer<Block> tagContainer) {
        BlockTags.container = tagContainer;
        ++BlockTags.containerChanges;
    }
    
    public static TagContainer<Block> getContainer() {
        return BlockTags.container;
    }
    
    private static Tag<Block> register(final String string) {
        return new a(new Identifier(string));
    }
    
    static {
        BlockTags.container = new TagContainer<Block>(identifier -> Optional.empty(), "", false, "");
        a = register("wool");
        b = register("planks");
        c = register("stone_bricks");
        d = register("wooden_buttons");
        e = register("buttons");
        f = register("carpets");
        g = register("wooden_doors");
        h = register("wooden_stairs");
        i = register("wooden_slabs");
        j = register("wooden_fences");
        k = register("wooden_pressure_plates");
        l = register("wooden_trapdoors");
        m = register("doors");
        n = register("saplings");
        o = register("logs");
        p = register("dark_oak_logs");
        q = register("oak_logs");
        r = register("birch_logs");
        s = register("acacia_logs");
        t = register("jungle_logs");
        u = register("spruce_logs");
        v = register("banners");
        w = register("sand");
        x = register("stairs");
        y = register("slabs");
        z = register("walls");
        A = register("anvil");
        B = register("rails");
        C = register("leaves");
        D = register("trapdoors");
        E = register("small_flowers");
        F = register("beds");
        G = register("fences");
        H = register("flower_pots");
        I = register("enderman_holdable");
        J = register("ice");
        K = register("valid_spawn");
        L = register("impermeable");
        M = register("underwater_bonemeals");
        N = register("coral_blocks");
        O = register("wall_corals");
        P = register("coral_plants");
        Q = register("corals");
        R = register("bamboo_plantable_on");
        S = register("dirt_like");
        T = register("standing_signs");
        U = register("wall_signs");
        V = register("signs");
        W = register("dragon_immune");
        X = register("wither_immune");
    }
    
    static class a extends Tag<Block>
    {
        private int a;
        private Tag<Block> b;
        
        public a(final Identifier identifier) {
            super(identifier);
            this.a = -1;
        }
        
        @Override
        public boolean contains(final Block block) {
            if (this.a != BlockTags.containerChanges) {
                this.b = BlockTags.container.getOrCreate(this.getId());
                this.a = BlockTags.containerChanges;
            }
            return this.b.contains(block);
        }
        
        @Override
        public Collection<Block> values() {
            if (this.a != BlockTags.containerChanges) {
                this.b = BlockTags.container.getOrCreate(this.getId());
                this.a = BlockTags.containerChanges;
            }
            return this.b.values();
        }
        
        @Override
        public Collection<Entry<Block>> entries() {
            if (this.a != BlockTags.containerChanges) {
                this.b = BlockTags.container.getOrCreate(this.getId());
                this.a = BlockTags.containerChanges;
            }
            return this.b.entries();
        }
    }
}
