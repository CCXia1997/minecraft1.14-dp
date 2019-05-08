package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.predicate.entity.DamageSourcePredicate;

public class DamageSourcePropertiesLootCondition implements LootCondition
{
    private final DamageSourcePredicate predicate;
    
    private DamageSourcePropertiesLootCondition(final DamageSourcePredicate predicate) {
        this.predicate = predicate;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.f, (LootContextParameter<BlockPos>)LootContextParameters.c);
    }
    
    public boolean a(final LootContext context) {
        final DamageSource damageSource2 = context.<DamageSource>get(LootContextParameters.c);
        final BlockPos blockPos3 = context.<BlockPos>get(LootContextParameters.f);
        return blockPos3 != null && damageSource2 != null && this.predicate.test(context.getWorld(), new Vec3d(blockPos3), damageSource2);
    }
    
    public static Builder builder(final DamageSourcePredicate.Builder builder) {
        return () -> new DamageSourcePropertiesLootCondition(builder.build());
    }
    
    public static class Factory extends LootCondition.Factory<DamageSourcePropertiesLootCondition>
    {
        protected Factory() {
            super(new Identifier("damage_source_properties"), DamageSourcePropertiesLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final DamageSourcePropertiesLootCondition condition, final JsonSerializationContext context) {
            json.add("predicate", condition.predicate.serialize());
        }
        
        @Override
        public DamageSourcePropertiesLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final DamageSourcePredicate damageSourcePredicate3 = DamageSourcePredicate.deserialize(json.get("predicate"));
            return new DamageSourcePropertiesLootCondition(damageSourcePredicate3, null);
        }
    }
}
