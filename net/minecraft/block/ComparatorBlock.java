package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import java.util.Random;
import net.minecraft.util.TaskPriority;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.state.property.EnumProperty;

public class ComparatorBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider
{
    public static final EnumProperty<ComparatorMode> MODE;
    
    public ComparatorBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)ComparatorBlock.FACING, Direction.NORTH)).with((Property<Comparable>)ComparatorBlock.POWERED, false)).<ComparatorMode, ComparatorMode>with(ComparatorBlock.MODE, ComparatorMode.a));
    }
    
    @Override
    protected int getUpdateDelayInternal(final BlockState blockState) {
        return 2;
    }
    
    @Override
    protected int getOutputLevel(final BlockView view, final BlockPos pos, final BlockState blockState) {
        final BlockEntity blockEntity4 = view.getBlockEntity(pos);
        if (blockEntity4 instanceof ComparatorBlockEntity) {
            return ((ComparatorBlockEntity)blockEntity4).getOutputSignal();
        }
        return 0;
    }
    
    private int calculateOutputSignal(final World world, final BlockPos pos, final BlockState state) {
        if (state.<ComparatorMode>get(ComparatorBlock.MODE) == ComparatorMode.b) {
            return Math.max(this.getPower(world, pos, state) - this.getMaxInputLevelSides(world, pos, state), 0);
        }
        return this.getPower(world, pos, state);
    }
    
    @Override
    protected boolean hasPower(final World world, final BlockPos pos, final BlockState state) {
        final int integer4 = this.getPower(world, pos, state);
        return integer4 >= 15 || (integer4 != 0 && integer4 >= this.getMaxInputLevelSides(world, pos, state));
    }
    
    @Override
    protected int getPower(final World world, final BlockPos pos, final BlockState state) {
        int integer4 = super.getPower(world, pos, state);
        final Direction direction5 = state.<Direction>get((Property<Direction>)ComparatorBlock.FACING);
        BlockPos blockPos6 = pos.offset(direction5);
        BlockState blockState7 = world.getBlockState(blockPos6);
        if (blockState7.hasComparatorOutput()) {
            integer4 = blockState7.getComparatorOutput(world, blockPos6);
        }
        else if (integer4 < 15 && blockState7.isSimpleFullBlock(world, blockPos6)) {
            blockPos6 = blockPos6.offset(direction5);
            blockState7 = world.getBlockState(blockPos6);
            if (blockState7.hasComparatorOutput()) {
                integer4 = blockState7.getComparatorOutput(world, blockPos6);
            }
            else if (blockState7.isAir()) {
                final ItemFrameEntity itemFrameEntity8 = this.getAttachedItemFrame(world, direction5, blockPos6);
                if (itemFrameEntity8 != null) {
                    integer4 = itemFrameEntity8.getComparatorPower();
                }
            }
        }
        return integer4;
    }
    
    @Nullable
    private ItemFrameEntity getAttachedItemFrame(final World world, final Direction facing, final BlockPos blockPos) {
        final List<ItemFrameEntity> list4 = world.<ItemFrameEntity>getEntities(ItemFrameEntity.class, new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), itemFrameEntity -> itemFrameEntity != null && itemFrameEntity.getHorizontalFacing() == facing);
        if (list4.size() == 1) {
            return list4.get(0);
        }
        return null;
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (!player.abilities.allowModifyWorld) {
            return false;
        }
        state = ((AbstractPropertyContainer<O, BlockState>)state).<ComparatorMode>cycle(ComparatorBlock.MODE);
        final float float7 = (state.<ComparatorMode>get(ComparatorBlock.MODE) == ComparatorMode.b) ? 0.55f : 0.5f;
        world.playSound(player, pos, SoundEvents.bg, SoundCategory.e, 0.3f, float7);
        world.setBlockState(pos, state, 2);
        this.update(world, pos, state);
        return true;
    }
    
    @Override
    protected void updatePowered(final World world, final BlockPos pos, final BlockState state) {
        if (world.getBlockTickScheduler().isTicking(pos, this)) {
            return;
        }
        final int integer4 = this.calculateOutputSignal(world, pos, state);
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        final int integer5 = (blockEntity5 instanceof ComparatorBlockEntity) ? ((ComparatorBlockEntity)blockEntity5).getOutputSignal() : 0;
        if (integer4 != integer5 || state.<Boolean>get((Property<Boolean>)ComparatorBlock.POWERED) != this.hasPower(world, pos, state)) {
            final TaskPriority taskPriority7 = this.isTargetNotAligned(world, pos, state) ? TaskPriority.c : TaskPriority.d;
            world.getBlockTickScheduler().schedule(pos, this, 2, taskPriority7);
        }
    }
    
    private void update(final World world, final BlockPos blockPos, final BlockState blockState) {
        final int integer4 = this.calculateOutputSignal(world, blockPos, blockState);
        final BlockEntity blockEntity5 = world.getBlockEntity(blockPos);
        int integer5 = 0;
        if (blockEntity5 instanceof ComparatorBlockEntity) {
            final ComparatorBlockEntity comparatorBlockEntity7 = (ComparatorBlockEntity)blockEntity5;
            integer5 = comparatorBlockEntity7.getOutputSignal();
            comparatorBlockEntity7.setOutputSignal(integer4);
        }
        if (integer5 != integer4 || blockState.<ComparatorMode>get(ComparatorBlock.MODE) == ComparatorMode.a) {
            final boolean boolean7 = this.hasPower(world, blockPos, blockState);
            final boolean boolean8 = blockState.<Boolean>get((Property<Boolean>)ComparatorBlock.POWERED);
            if (boolean8 && !boolean7) {
                world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Boolean>with((Property<Comparable>)ComparatorBlock.POWERED, false), 2);
            }
            else if (!boolean8 && boolean7) {
                world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Boolean>with((Property<Comparable>)ComparatorBlock.POWERED, true), 2);
            }
            this.updateTarget(world, blockPos, blockState);
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        this.update(world, pos, state);
    }
    
    @Override
    public boolean onBlockAction(final BlockState state, final World world, final BlockPos pos, final int type, final int data) {
        super.onBlockAction(state, world, pos, type, data);
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        return blockEntity6 != null && blockEntity6.onBlockAction(type, data);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new ComparatorBlockEntity();
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ComparatorBlock.FACING, ComparatorBlock.MODE, ComparatorBlock.POWERED);
    }
    
    static {
        MODE = Properties.COMPARATOR_MODE;
    }
}
