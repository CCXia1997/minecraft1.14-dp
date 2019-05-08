package net.minecraft.predicate.entity;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import net.minecraft.tag.Tag;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.tag.EntityTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import net.minecraft.entity.EntityType;
import com.google.common.base.Joiner;

public abstract class EntityTypePredicate
{
    public static final EntityTypePredicate ANY;
    private static final Joiner COMMA_JOINER;
    
    public abstract boolean matches(final EntityType<?> arg1);
    
    public abstract JsonElement toJson();
    
    public static EntityTypePredicate deserialize(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return EntityTypePredicate.ANY;
        }
        final String string2 = JsonHelper.asString(element, "type");
        if (string2.startsWith("#")) {
            final Identifier identifier3 = new Identifier(string2.substring(1));
            final Tag<EntityType<?>> tag4 = EntityTags.getContainer().getOrCreate(identifier3);
            return new Tagged(tag4);
        }
        final Identifier identifier3 = new Identifier(string2);
        final Object o;
        final Object o2;
        final EntityType<?> entityType4 = Registry.ENTITY_TYPE.getOrEmpty(identifier3).<Throwable>orElseThrow(() -> {
            new JsonSyntaxException("Unknown entity type '" + o2 + "', valid types are: " + EntityTypePredicate.COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds()));
            return o;
        });
        return new Single(entityType4);
    }
    
    public static EntityTypePredicate create(final EntityType<?> type) {
        return new Single(type);
    }
    
    public static EntityTypePredicate create(final Tag<EntityType<?>> tag) {
        return new Tagged(tag);
    }
    
    static {
        ANY = new EntityTypePredicate() {
            @Override
            public boolean matches(final EntityType<?> entityType) {
                return true;
            }
            
            @Override
            public JsonElement toJson() {
                return JsonNull.INSTANCE;
            }
        };
        COMMA_JOINER = Joiner.on(", ");
    }
    
    static class Single extends EntityTypePredicate
    {
        private final EntityType<?> type;
        
        public Single(final EntityType<?> entityType) {
            this.type = entityType;
        }
        
        @Override
        public boolean matches(final EntityType<?> entityType) {
            return this.type == entityType;
        }
        
        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Registry.ENTITY_TYPE.getId(this.type).toString());
        }
    }
    
    static class Tagged extends EntityTypePredicate
    {
        private final Tag<EntityType<?>> tag;
        
        public Tagged(final Tag<EntityType<?>> tag) {
            this.tag = tag;
        }
        
        @Override
        public boolean matches(final EntityType<?> entityType) {
            return this.tag.contains(entityType);
        }
        
        @Override
        public JsonElement toJson() {
            return new JsonPrimitive("#" + this.tag.getId().toString());
        }
    }
}
