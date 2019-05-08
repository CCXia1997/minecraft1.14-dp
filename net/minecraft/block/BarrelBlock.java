package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.util.ItemScatterer;
import net.minecraft.inventory.Inventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class BarrelBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    
    public BarrelBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)BarrelBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)BarrelBlock.OPEN, false));
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof BarrelBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity7);
            player.incrementStat(Stats.ap);
        }
        return true;
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory)blockEntity6);
            world.updateHorizontalAdjacent(pos, this);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (blockEntity5 instanceof BarrelBlockEntity) {
            ((BarrelBlockEntity)blockEntity5).tick();
        }
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new BarrelBlockEntity();
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof BarrelBlockEntity) {
                ((BarrelBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return Container.calculateComparatorOutput(world.getBlockEntity(pos));
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)BarrelBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)BarrelBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)BarrelBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BarrelBlock.FACING, BarrelBlock.OPEN);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)BarrelBlock.FACING, ctx.getPlayerFacing().getOpposite());
    }
    
    static {
        FACING = Properties.FACING;
        OPEN = Properties.OPEN;
    }
}
