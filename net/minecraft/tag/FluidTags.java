package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.fluid.Fluid;

public class FluidTags
{
    private static TagContainer<Fluid> container;
    private static int containerChanges;
    public static final Tag<Fluid> a;
    public static final Tag<Fluid> b;
    
    public static void setContainer(final TagContainer<Fluid> container) {
        FluidTags.container = container;
        ++FluidTags.containerChanges;
    }
    
    private static Tag<Fluid> register(final String string) {
        return new a(new Identifier(string));
    }
    
    static {
        FluidTags.container = new TagContainer<Fluid>(identifier -> Optional.empty(), "", false, "");
        a = register("water");
        b = register("lava");
    }
    
    public static class a extends Tag<Fluid>
    {
        private int a;
        private Tag<Fluid> b;
        
        public a(final Identifier identifier) {
            super(identifier);
            this.a = -1;
        }
        
        @Override
        public boolean contains(final Fluid fluid) {
            if (this.a != FluidTags.containerChanges) {
                this.b = FluidTags.container.getOrCreate(this.getId());
                this.a = FluidTags.containerChanges;
            }
            return this.b.contains(fluid);
        }
        
        @Override
        public Collection<Fluid> values() {
            if (this.a != FluidTags.containerChanges) {
                this.b = FluidTags.container.getOrCreate(this.getId());
                this.a = FluidTags.containerChanges;
            }
            return this.b.values();
        }
        
        @Override
        public Collection<Entry<Fluid>> entries() {
            if (this.a != FluidTags.containerChanges) {
                this.b = FluidTags.container.getOrCreate(this.getId());
                this.a = FluidTags.containerChanges;
            }
            return this.b.entries();
        }
    }
}
