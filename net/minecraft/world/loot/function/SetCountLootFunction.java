package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import net.minecraft.world.loot.LootTableRanges;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.LootTableRange;

public class SetCountLootFunction extends ConditionalLootFunction
{
    private final LootTableRange countRange;
    
    private SetCountLootFunction(final LootCondition[] conditions, final LootTableRange countRange) {
        super(conditions);
        this.countRange = countRange;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        stack.setAmount(this.countRange.next(context.getRandom()));
        return stack;
    }
    
    public static Builder<?> builder(final LootTableRange countRange) {
        return ConditionalLootFunction.builder(conditions -> new SetCountLootFunction(conditions, countRange));
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetCountLootFunction>
    {
        protected Factory() {
            super(new Identifier("set_count"), SetCountLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetCountLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("count", LootTableRanges.serialize(function.countRange, context));
        }
        
        @Override
        public SetCountLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final LootTableRange lootTableRange4 = LootTableRanges.deserialize(json.get("count"), context);
            return new SetCountLootFunction(conditions, lootTableRange4, null);
        }
    }
}
