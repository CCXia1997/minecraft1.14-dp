package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.world.loot.LootTableRanges;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.LootTableRange;

public class EnchantWithLevelsLootFunction extends ConditionalLootFunction
{
    private final LootTableRange range;
    private final boolean treasureEnchantmentsAllowed;
    
    private EnchantWithLevelsLootFunction(final LootCondition[] conditions, final LootTableRange range, final boolean treasureEnchantmentsAllowed) {
        super(conditions);
        this.range = range;
        this.treasureEnchantmentsAllowed = treasureEnchantmentsAllowed;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final Random random3 = context.getRandom();
        return EnchantmentHelper.enchant(random3, stack, this.range.next(random3), this.treasureEnchantmentsAllowed);
    }
    
    public static Builder builder(final LootTableRange range) {
        return new Builder(range);
    }
    
    public static class Builder extends ConditionalLootFunction.Builder<Builder>
    {
        private final LootTableRange range;
        private boolean treasureEnchantmentsAllowed;
        
        public Builder(final LootTableRange range) {
            this.range = range;
        }
        
        @Override
        protected Builder getThisBuilder() {
            return this;
        }
        
        public Builder allowTreasureEnchantments() {
            this.treasureEnchantmentsAllowed = true;
            return this;
        }
        
        @Override
        public LootFunction build() {
            return new EnchantWithLevelsLootFunction(this.getConditions(), this.range, this.treasureEnchantmentsAllowed, null);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<EnchantWithLevelsLootFunction>
    {
        public Factory() {
            super(new Identifier("enchant_with_levels"), EnchantWithLevelsLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final EnchantWithLevelsLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("levels", LootTableRanges.serialize(function.range, context));
            json.addProperty("treasure", function.treasureEnchantmentsAllowed);
        }
        
        @Override
        public EnchantWithLevelsLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final LootTableRange lootTableRange4 = LootTableRanges.deserialize(json.get("levels"), context);
            final boolean boolean5 = JsonHelper.getBoolean(json, "treasure", false);
            return new EnchantWithLevelsLootFunction(conditions, lootTableRange4, boolean5, null);
        }
    }
}
