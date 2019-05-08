package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.block.FireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;

public class FireChargeItem extends Item
{
    public FireChargeItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        if (world2.isClient) {
            return ActionResult.a;
        }
        BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (blockState4.getBlock() == Blocks.lV) {
            if (!blockState4.<Boolean>get((Property<Boolean>)CampfireBlock.LIT) && !blockState4.<Boolean>get((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
                this.playUseSound(world2, blockPos3);
                world2.setBlockState(blockPos3, ((AbstractPropertyContainer<O, BlockState>)blockState4).<Comparable, Boolean>with((Property<Comparable>)CampfireBlock.LIT, true));
            }
        }
        else {
            blockPos3 = blockPos3.offset(usageContext.getFacing());
            if (world2.getBlockState(blockPos3).isAir()) {
                this.playUseSound(world2, blockPos3);
                world2.setBlockState(blockPos3, ((FireBlock)Blocks.bM).getStateForPosition(world2, blockPos3));
            }
        }
        usageContext.getItemStack().subtractAmount(1);
        return ActionResult.a;
    }
    
    private void playUseSound(final World world, final BlockPos pos) {
        world.playSound(null, pos, SoundEvents.dg, SoundCategory.e, 1.0f, (FireChargeItem.random.nextFloat() - FireChargeItem.random.nextFloat()) * 0.2f + 1.0f);
    }
}
