package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.PortalBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

public class FlintAndSteelItem extends Item
{
    public FlintAndSteelItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final PlayerEntity playerEntity2 = usageContext.getPlayer();
        final IWorld iWorld3 = usageContext.getWorld();
        final BlockPos blockPos4 = usageContext.getBlockPos();
        final BlockPos blockPos5 = blockPos4.offset(usageContext.getFacing());
        if (canSetOnFire(iWorld3.getBlockState(blockPos5), iWorld3, blockPos5)) {
            iWorld3.playSound(playerEntity2, blockPos5, SoundEvents.ds, SoundCategory.e, 1.0f, FlintAndSteelItem.random.nextFloat() * 0.4f + 0.8f);
            final BlockState blockState6 = ((FireBlock)Blocks.bM).getStateForPosition(iWorld3, blockPos5);
            iWorld3.setBlockState(blockPos5, blockState6, 11);
            final ItemStack itemStack7 = usageContext.getItemStack();
            if (playerEntity2 instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity2, blockPos5, itemStack7);
                itemStack7.<PlayerEntity>applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(usageContext.getHand()));
            }
            return ActionResult.a;
        }
        final BlockState blockState6 = iWorld3.getBlockState(blockPos4);
        if (canBeLit(blockState6)) {
            iWorld3.playSound(playerEntity2, blockPos4, SoundEvents.ds, SoundCategory.e, 1.0f, FlintAndSteelItem.random.nextFloat() * 0.4f + 0.8f);
            iWorld3.setBlockState(blockPos4, ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)Properties.LIT, true), 11);
            if (playerEntity2 != null) {
                usageContext.getItemStack().<PlayerEntity>applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(usageContext.getHand()));
            }
            return ActionResult.a;
        }
        return ActionResult.c;
    }
    
    public static boolean canBeLit(final BlockState state) {
        return state.getBlock() == Blocks.lV && !state.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED) && !state.<Boolean>get((Property<Boolean>)Properties.LIT);
    }
    
    public static boolean canSetOnFire(final BlockState block, final IWorld world, final BlockPos pos) {
        final BlockState blockState4 = ((FireBlock)Blocks.bM).getStateForPosition(world, pos);
        boolean boolean5 = false;
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            if (world.getBlockState(pos.offset(direction7)).getBlock() == Blocks.bJ && ((PortalBlock)Blocks.cM).b(world, pos) != null) {
                boolean5 = true;
            }
        }
        return block.isAir() && (blockState4.canPlaceAt(world, pos) || boolean5);
    }
}
