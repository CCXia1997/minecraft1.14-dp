package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.UniformLootTableRange;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction extends ConditionalLootFunction
{
    private static final Logger LOGGER;
    private final UniformLootTableRange durabilityRange;
    
    private SetDamageLootFunction(final LootCondition[] contents, final UniformLootTableRange durabilityRange) {
        super(contents);
        this.durabilityRange = durabilityRange;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.hasDurability()) {
            final float float3 = 1.0f - this.durabilityRange.nextFloat(context.getRandom());
            stack.setDamage(MathHelper.floor(float3 * stack.getDurability()));
        }
        else {
            SetDamageLootFunction.LOGGER.warn("Couldn't set damage of loot item {}", stack);
        }
        return stack;
    }
    
    public static Builder<?> builder(final UniformLootTableRange durabilityRange) {
        return ConditionalLootFunction.builder(conditions -> new SetDamageLootFunction(conditions, durabilityRange));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetDamageLootFunction>
    {
        protected Factory() {
            super(new Identifier("set_damage"), SetDamageLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetDamageLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("damage", context.serialize(function.durabilityRange));
        }
        
        @Override
        public SetDamageLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            return new SetDamageLootFunction(conditions, JsonHelper.<UniformLootTableRange>deserialize(json, "damage", context, UniformLootTableRange.class), null);
        }
    }
}
