package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.entity.EntityType;

public class EntityTags
{
    private static TagContainer<EntityType<?>> container;
    private static int d;
    public static final Tag<EntityType<?>> a;
    public static final Tag<EntityType<?>> b;
    
    public static void setContainer(final TagContainer<EntityType<?>> tagContainer) {
        EntityTags.container = tagContainer;
        ++EntityTags.d;
    }
    
    public static TagContainer<EntityType<?>> getContainer() {
        return EntityTags.container;
    }
    
    private static Tag<EntityType<?>> register(final String string) {
        return new a(new Identifier(string));
    }
    
    static {
        EntityTags.container = new TagContainer<EntityType<?>>(identifier -> Optional.empty(), "", false, "");
        a = register("skeletons");
        b = register("raiders");
    }
    
    public static class a extends Tag<EntityType<?>>
    {
        private int a;
        private Tag<EntityType<?>> b;
        
        public a(final Identifier identifier) {
            super(identifier);
            this.a = -1;
        }
        
        @Override
        public boolean contains(final EntityType<?> entityType) {
            if (this.a != EntityTags.d) {
                this.b = EntityTags.container.getOrCreate(this.getId());
                this.a = EntityTags.d;
            }
            return this.b.contains(entityType);
        }
        
        @Override
        public Collection<EntityType<?>> values() {
            if (this.a != EntityTags.d) {
                this.b = EntityTags.container.getOrCreate(this.getId());
                this.a = EntityTags.d;
            }
            return this.b.values();
        }
        
        @Override
        public Collection<Entry<EntityType<?>>> entries() {
            if (this.a != EntityTags.d) {
                this.b = EntityTags.container.getOrCreate(this.getId());
                this.a = EntityTags.d;
            }
            return this.b.entries();
        }
    }
}
