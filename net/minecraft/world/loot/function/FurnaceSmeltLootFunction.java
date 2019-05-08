package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.world.World;
import net.minecraft.recipe.cooking.SmeltingRecipe;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.logging.log4j.Logger;

public class FurnaceSmeltLootFunction extends ConditionalLootFunction
{
    private static final Logger LOGGER;
    
    private FurnaceSmeltLootFunction(final LootCondition[] conditions) {
        super(conditions);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        }
        final Optional<SmeltingRecipe> optional3 = context.getWorld().getRecipeManager().<BasicInventory, SmeltingRecipe>getFirstMatch(RecipeType.SMELTING, new BasicInventory(new ItemStack[] { stack }), context.getWorld());
        if (optional3.isPresent()) {
            final ItemStack itemStack4 = optional3.get().getOutput();
            if (!itemStack4.isEmpty()) {
                final ItemStack itemStack5 = itemStack4.copy();
                itemStack5.setAmount(stack.getAmount());
                return itemStack5;
            }
        }
        FurnaceSmeltLootFunction.LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", stack);
        return stack;
    }
    
    public static Builder<?> builder() {
        return ConditionalLootFunction.builder((Function<LootCondition[], LootFunction>)FurnaceSmeltLootFunction::new);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<FurnaceSmeltLootFunction>
    {
        protected Factory() {
            super(new Identifier("furnace_smelt"), FurnaceSmeltLootFunction.class);
        }
        
        @Override
        public FurnaceSmeltLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            return new FurnaceSmeltLootFunction(conditions, null);
        }
    }
}
