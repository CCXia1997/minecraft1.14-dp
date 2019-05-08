package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;

public class EntityPropertiesLootCondition implements LootCondition
{
    private final EntityPredicate predicate;
    private final LootContext.EntityTarget entity;
    
    private EntityPropertiesLootCondition(final EntityPredicate predicate, final LootContext.EntityTarget entity) {
        this.predicate = predicate;
        this.entity = entity;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.f, (LootContextParameter<BlockPos>)this.entity.getIdentifier());
    }
    
    public boolean a(final LootContext context) {
        final Entity entity2 = context.<Entity>get(this.entity.getIdentifier());
        final BlockPos blockPos3 = context.<BlockPos>get(LootContextParameters.f);
        return blockPos3 != null && this.predicate.test(context.getWorld(), new Vec3d(blockPos3), entity2);
    }
    
    public static Builder create(final LootContext.EntityTarget entity) {
        return builder(entity, EntityPredicate.Builder.create());
    }
    
    public static Builder builder(final LootContext.EntityTarget entity, final EntityPredicate.Builder predicateBuilder) {
        return () -> new EntityPropertiesLootCondition(predicateBuilder.build(), entity);
    }
    
    public static class Factory extends LootCondition.Factory<EntityPropertiesLootCondition>
    {
        protected Factory() {
            super(new Identifier("entity_properties"), EntityPropertiesLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final EntityPropertiesLootCondition condition, final JsonSerializationContext context) {
            json.add("predicate", condition.predicate.serialize());
            json.add("entity", context.serialize(condition.entity));
        }
        
        @Override
        public EntityPropertiesLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final EntityPredicate entityPredicate3 = EntityPredicate.deserialize(json.get("predicate"));
            return new EntityPropertiesLootCondition(entityPredicate3, JsonHelper.<LootContext.EntityTarget>deserialize(json, "entity", context, LootContext.EntityTarget.class), null);
        }
    }
}
