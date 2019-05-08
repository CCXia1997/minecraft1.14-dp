package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;

public class RandomChanceLootCondition implements LootCondition
{
    private final float chance;
    
    private RandomChanceLootCondition(final float chance) {
        this.chance = chance;
    }
    
    public boolean a(final LootContext context) {
        return context.getRandom().nextFloat() < this.chance;
    }
    
    public static Builder builder(final float chance) {
        return () -> new RandomChanceLootCondition(chance);
    }
    
    public static class Factory extends LootCondition.Factory<RandomChanceLootCondition>
    {
        protected Factory() {
            super(new Identifier("random_chance"), RandomChanceLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final RandomChanceLootCondition condition, final JsonSerializationContext context) {
            json.addProperty("chance", condition.chance);
        }
        
        @Override
        public RandomChanceLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            return new RandomChanceLootCondition(JsonHelper.getFloat(json, "chance"), null);
        }
    }
}
