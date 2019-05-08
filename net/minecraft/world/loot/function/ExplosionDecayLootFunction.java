package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import java.util.Random;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;

public class ExplosionDecayLootFunction extends ConditionalLootFunction
{
    private ExplosionDecayLootFunction(final LootCondition[] conditions) {
        super(conditions);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final Float float3 = context.<Float>get(LootContextParameters.j);
        if (float3 != null) {
            final Random random4 = context.getRandom();
            final float float4 = 1.0f / float3;
            final int integer6 = stack.getAmount();
            int integer7 = 0;
            for (int integer8 = 0; integer8 < integer6; ++integer8) {
                if (random4.nextFloat() <= float4) {
                    ++integer7;
                }
            }
            stack.setAmount(integer7);
        }
        return stack;
    }
    
    public static Builder<?> builder() {
        return ConditionalLootFunction.builder((Function<LootCondition[], LootFunction>)ExplosionDecayLootFunction::new);
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<ExplosionDecayLootFunction>
    {
        protected Factory() {
            super(new Identifier("explosion_decay"), ExplosionDecayLootFunction.class);
        }
        
        @Override
        public ExplosionDecayLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            return new ExplosionDecayLootFunction(conditions, null);
        }
    }
}
