package net.minecraft.world.loot.condition;

import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;

public class TableBonusLootCondition implements LootCondition
{
    private final Enchantment enchantment;
    private final float[] chances;
    
    private TableBonusLootCondition(final Enchantment enchantment, final float[] chances) {
        this.enchantment = enchantment;
        this.chances = chances;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.i);
    }
    
    public boolean a(final LootContext context) {
        final ItemStack itemStack2 = context.<ItemStack>get(LootContextParameters.i);
        final int integer3 = (itemStack2 != null) ? EnchantmentHelper.getLevel(this.enchantment, itemStack2) : 0;
        final float float4 = this.chances[Math.min(integer3, this.chances.length - 1)];
        return context.getRandom().nextFloat() < float4;
    }
    
    public static Builder builder(final Enchantment enchantment, final float... chances) {
        return () -> new TableBonusLootCondition(enchantment, chances);
    }
    
    public static class Factory extends LootCondition.Factory<TableBonusLootCondition>
    {
        public Factory() {
            super(new Identifier("table_bonus"), TableBonusLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final TableBonusLootCondition condition, final JsonSerializationContext context) {
            json.addProperty("enchantment", Registry.ENCHANTMENT.getId(condition.enchantment).toString());
            json.add("chances", context.serialize(condition.chances));
        }
        
        @Override
        public TableBonusLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final Identifier identifier3 = new Identifier(JsonHelper.getString(json, "enchantment"));
            final Object o;
            final Object o2;
            final Enchantment enchantment4 = Registry.ENCHANTMENT.getOrEmpty(identifier3).<Throwable>orElseThrow(() -> {
                new JsonParseException("Invalid enchantment id: " + o2);
                return o;
            });
            final float[] arr5 = JsonHelper.<float[]>deserialize(json, "chances", context, float[].class);
            return new TableBonusLootCondition(enchantment4, arr5, null);
        }
    }
}
