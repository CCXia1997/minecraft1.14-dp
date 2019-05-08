package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PumpkinBlock extends GourdBlock
{
    protected PumpkinBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final ItemStack itemStack7 = player.getStackInHand(hand);
        if (itemStack7.getItem() == Items.lW) {
            if (!world.isClient) {
                final Direction direction8 = blockHitResult.getSide();
                final Direction direction9 = (direction8.getAxis() == Direction.Axis.Y) ? player.getHorizontalFacing().getOpposite() : direction8;
                world.playSound(null, pos, SoundEvents.jh, SoundCategory.e, 1.0f, 1.0f);
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.cN.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)CarvedPumpkinBlock.FACING, direction9), 11);
                final ItemEntity itemEntity10 = new ItemEntity(world, pos.getX() + 0.5 + direction9.getOffsetX() * 0.65, pos.getY() + 0.1, pos.getZ() + 0.5 + direction9.getOffsetZ() * 0.65, new ItemStack(Items.lZ, 4));
                itemEntity10.setVelocity(0.05 * direction9.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * direction9.getOffsetZ() + world.random.nextDouble() * 0.02);
                world.spawnEntity(itemEntity10);
                itemStack7.<PlayerEntity>applyDamage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }
            return true;
        }
        return super.activate(state, world, pos, player, hand, blockHitResult);
    }
    
    @Override
    public StemBlock getStem() {
        return (StemBlock)Blocks.dF;
    }
    
    @Override
    public AttachedStemBlock getAttachedStem() {
        return (AttachedStemBlock)Blocks.dD;
    }
}
