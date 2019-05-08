package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.util.BoundedIntUnaryOperator;

public class LimitCountLootFunction extends ConditionalLootFunction
{
    private final BoundedIntUnaryOperator limit;
    
    private LimitCountLootFunction(final LootCondition[] conditions, final BoundedIntUnaryOperator limit) {
        super(conditions);
        this.limit = limit;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final int integer3 = this.limit.applyAsInt(stack.getAmount());
        stack.setAmount(integer3);
        return stack;
    }
    
    public static Builder<?> builder(final BoundedIntUnaryOperator limit) {
        return ConditionalLootFunction.builder(conditions -> new LimitCountLootFunction(conditions, limit));
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<LimitCountLootFunction>
    {
        protected Factory() {
            super(new Identifier("limit_count"), LimitCountLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final LimitCountLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("limit", context.serialize(function.limit));
        }
        
        @Override
        public LimitCountLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final BoundedIntUnaryOperator boundedIntUnaryOperator4 = JsonHelper.<BoundedIntUnaryOperator>deserialize(json, "limit", context, BoundedIntUnaryOperator.class);
            return new LimitCountLootFunction(conditions, boundedIntUnaryOperator4, null);
        }
    }
}
