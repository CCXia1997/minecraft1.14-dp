package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;

public class RandomChanceWithLootingLootCondition implements LootCondition
{
    private final float chance;
    private final float lootingMultiplier;
    
    private RandomChanceWithLootingLootCondition(final float chance, final float lootingMultiplier) {
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.d);
    }
    
    public boolean a(final LootContext context) {
        final Entity entity2 = context.<Entity>get(LootContextParameters.d);
        int integer3 = 0;
        if (entity2 instanceof LivingEntity) {
            integer3 = EnchantmentHelper.getLooting((LivingEntity)entity2);
        }
        return context.getRandom().nextFloat() < this.chance + integer3 * this.lootingMultiplier;
    }
    
    public static Builder builder(final float chance, final float lootingMultiplier) {
        return () -> new RandomChanceWithLootingLootCondition(chance, lootingMultiplier);
    }
    
    public static class Factory extends LootCondition.Factory<RandomChanceWithLootingLootCondition>
    {
        protected Factory() {
            super(new Identifier("random_chance_with_looting"), RandomChanceWithLootingLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final RandomChanceWithLootingLootCondition condition, final JsonSerializationContext context) {
            json.addProperty("chance", condition.chance);
            json.addProperty("looting_multiplier", condition.lootingMultiplier);
        }
        
        @Override
        public RandomChanceWithLootingLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            return new RandomChanceWithLootingLootCondition(JsonHelper.getFloat(json, "chance"), JsonHelper.getFloat(json, "looting_multiplier"), null);
        }
    }
}
