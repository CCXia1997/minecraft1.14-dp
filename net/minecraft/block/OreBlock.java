package net.minecraft.block;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class OreBlock extends Block
{
    public OreBlock(final Settings settings) {
        super(settings);
    }
    
    protected int getExperienceWhenMined(final Random random) {
        if (this == Blocks.H) {
            return MathHelper.nextInt(random, 0, 2);
        }
        if (this == Blocks.bR) {
            return MathHelper.nextInt(random, 3, 7);
        }
        if (this == Blocks.eb) {
            return MathHelper.nextInt(random, 3, 7);
        }
        if (this == Blocks.ap) {
            return MathHelper.nextInt(random, 2, 5);
        }
        if (this == Blocks.fp) {
            return MathHelper.nextInt(random, 2, 5);
        }
        return 0;
    }
    
    @Override
    public void onStacksDropped(final BlockState state, final World world, final BlockPos pos, final ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        if (EnchantmentHelper.getLevel(Enchantments.t, stack) == 0) {
            final int integer5 = this.getExperienceWhenMined(world.random);
            if (integer5 > 0) {
                this.dropExperience(world, pos, integer5);
            }
        }
    }
}
