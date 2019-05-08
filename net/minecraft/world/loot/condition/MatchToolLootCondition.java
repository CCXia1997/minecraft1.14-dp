package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.predicate.item.ItemPredicate;

public class MatchToolLootCondition implements LootCondition
{
    private final ItemPredicate predicate;
    
    public MatchToolLootCondition(final ItemPredicate predicate) {
        this.predicate = predicate;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.i);
    }
    
    public boolean a(final LootContext context) {
        final ItemStack itemStack2 = context.<ItemStack>get(LootContextParameters.i);
        return itemStack2 != null && this.predicate.test(itemStack2);
    }
    
    public static Builder builder(final ItemPredicate.Builder predicate) {
        return () -> new MatchToolLootCondition(predicate.build());
    }
    
    public static class Factory extends LootCondition.Factory<MatchToolLootCondition>
    {
        protected Factory() {
            super(new Identifier("match_tool"), MatchToolLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final MatchToolLootCondition condition, final JsonSerializationContext context) {
            json.add("predicate", condition.predicate.serialize());
        }
        
        @Override
        public MatchToolLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final ItemPredicate itemPredicate3 = ItemPredicate.deserialize(json.get("predicate"));
            return new MatchToolLootCondition(itemPredicate3);
        }
    }
}
