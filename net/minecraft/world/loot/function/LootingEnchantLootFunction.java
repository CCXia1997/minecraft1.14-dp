package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.UniformLootTableRange;

public class LootingEnchantLootFunction extends ConditionalLootFunction
{
    private final UniformLootTableRange countRange;
    private final int limit;
    
    private LootingEnchantLootFunction(final LootCondition[] conditions, final UniformLootTableRange countRange, final int limit) {
        super(conditions);
        this.countRange = countRange;
        this.limit = limit;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.d);
    }
    
    private boolean hasLimit() {
        return this.limit > 0;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final Entity entity3 = context.<Entity>get(LootContextParameters.d);
        if (entity3 instanceof LivingEntity) {
            final int integer4 = EnchantmentHelper.getLooting((LivingEntity)entity3);
            if (integer4 == 0) {
                return stack;
            }
            final float float5 = integer4 * this.countRange.nextFloat(context.getRandom());
            stack.addAmount(Math.round(float5));
            if (this.hasLimit() && stack.getAmount() > this.limit) {
                stack.setAmount(this.limit);
            }
        }
        return stack;
    }
    
    public static Builder builder(final UniformLootTableRange countRange) {
        return new Builder(countRange);
    }
    
    public static class Builder extends ConditionalLootFunction.Builder<Builder>
    {
        private final UniformLootTableRange countRange;
        private int limit;
        
        public Builder(final UniformLootTableRange countRange) {
            this.limit = 0;
            this.countRange = countRange;
        }
        
        @Override
        protected Builder getThisBuilder() {
            return this;
        }
        
        public Builder withLimit(final int limit) {
            this.limit = limit;
            return this;
        }
        
        @Override
        public LootFunction build() {
            return new LootingEnchantLootFunction(this.getConditions(), this.countRange, this.limit, null);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<LootingEnchantLootFunction>
    {
        protected Factory() {
            super(new Identifier("looting_enchant"), LootingEnchantLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final LootingEnchantLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("count", context.serialize(function.countRange));
            if (function.hasLimit()) {
                json.add("limit", context.serialize(function.limit));
            }
        }
        
        @Override
        public LootingEnchantLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final int integer4 = JsonHelper.getInt(json, "limit", 0);
            return new LootingEnchantLootFunction(conditions, JsonHelper.<UniformLootTableRange>deserialize(json, "count", context, UniformLootTableRange.class), integer4, null);
        }
    }
}
