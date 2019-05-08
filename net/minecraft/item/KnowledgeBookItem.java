package net.minecraft.item;

import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import net.minecraft.recipe.RecipeManager;
import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import java.util.Collection;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import com.google.common.collect.Lists;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item
{
    private static final Logger LOGGER;
    
    public KnowledgeBookItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final CompoundTag compoundTag5 = itemStack4.getTag();
        if (!player.abilities.creativeMode) {
            player.setStackInHand(hand, ItemStack.EMPTY);
        }
        if (compoundTag5 == null || !compoundTag5.containsKey("Recipes", 9)) {
            KnowledgeBookItem.LOGGER.error("Tag not valid: {}", compoundTag5);
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        if (!world.isClient) {
            final ListTag listTag6 = compoundTag5.getList("Recipes", 8);
            final List<Recipe<?>> list7 = Lists.newArrayList();
            final RecipeManager recipeManager8 = world.getServer().getRecipeManager();
            for (int integer9 = 0; integer9 < listTag6.size(); ++integer9) {
                final String string10 = listTag6.getString(integer9);
                final Optional<? extends Recipe<?>> optional11 = recipeManager8.get(new Identifier(string10));
                if (!optional11.isPresent()) {
                    KnowledgeBookItem.LOGGER.error("Invalid recipe: {}", string10);
                    return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
                }
                list7.add(optional11.get());
            }
            player.unlockRecipes(list7);
            player.incrementStat(Stats.c.getOrCreateStat(this));
        }
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
