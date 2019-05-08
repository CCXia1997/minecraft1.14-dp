package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;

public class ItemTags
{
    private static TagContainer<Item> container;
    private static int containerChanges;
    public static final Tag<Item> a;
    public static final Tag<Item> b;
    public static final Tag<Item> c;
    public static final Tag<Item> d;
    public static final Tag<Item> e;
    public static final Tag<Item> f;
    public static final Tag<Item> g;
    public static final Tag<Item> h;
    public static final Tag<Item> i;
    public static final Tag<Item> j;
    public static final Tag<Item> k;
    public static final Tag<Item> l;
    public static final Tag<Item> m;
    public static final Tag<Item> n;
    public static final Tag<Item> o;
    public static final Tag<Item> p;
    public static final Tag<Item> q;
    public static final Tag<Item> r;
    public static final Tag<Item> s;
    public static final Tag<Item> t;
    public static final Tag<Item> u;
    public static final Tag<Item> v;
    public static final Tag<Item> w;
    public static final Tag<Item> x;
    public static final Tag<Item> y;
    public static final Tag<Item> z;
    public static final Tag<Item> A;
    public static final Tag<Item> B;
    public static final Tag<Item> C;
    public static final Tag<Item> D;
    public static final Tag<Item> E;
    public static final Tag<Item> F;
    public static final Tag<Item> G;
    public static final Tag<Item> H;
    public static final Tag<Item> I;
    public static final Tag<Item> J;
    public static final Tag<Item> K;
    public static final Tag<Item> L;
    public static final Tag<Item> M;
    
    public static void setContainer(final TagContainer<Item> tagContainer) {
        ItemTags.container = tagContainer;
        ++ItemTags.containerChanges;
    }
    
    public static TagContainer<Item> getContainer() {
        return ItemTags.container;
    }
    
    private static Tag<Item> register(final String string) {
        return new a(new Identifier(string));
    }
    
    static {
        ItemTags.container = new TagContainer<Item>(identifier -> Optional.empty(), "", false, "");
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
        H = register("boats");
        I = register("fishes");
        J = register("signs");
        K = register("music_discs");
        L = register("coals");
        M = register("arrows");
    }
    
    public static class a extends Tag<Item>
    {
        private int a;
        private Tag<Item> b;
        
        public a(final Identifier identifier) {
            super(identifier);
            this.a = -1;
        }
        
        @Override
        public boolean contains(final Item item) {
            if (this.a != ItemTags.containerChanges) {
                this.b = ItemTags.container.getOrCreate(this.getId());
                this.a = ItemTags.containerChanges;
            }
            return this.b.contains(item);
        }
        
        @Override
        public Collection<Item> values() {
            if (this.a != ItemTags.containerChanges) {
                this.b = ItemTags.container.getOrCreate(this.getId());
                this.a = ItemTags.containerChanges;
            }
            return this.b.values();
        }
        
        @Override
        public Collection<Entry<Item>> entries() {
            if (this.a != ItemTags.containerChanges) {
                this.b = ItemTags.container.getOrCreate(this.getId());
                this.a = ItemTags.containerChanges;
            }
            return this.b.entries();
        }
    }
}
