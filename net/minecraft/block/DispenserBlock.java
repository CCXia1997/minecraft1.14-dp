package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.SystemUtil;
import net.minecraft.state.property.Properties;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.container.Container;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.math.Position;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.stat.Stats;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class DispenserBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty TRIGGERED;
    private static final Map<Item, DispenserBehavior> BEHAVIORS;
    
    public static void registerBehavior(final ItemProvider itemProvider, final DispenserBehavior dispenserBehavior) {
        DispenserBlock.BEHAVIORS.put(itemProvider.getItem(), dispenserBehavior);
    }
    
    protected DispenserBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)DispenserBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)DispenserBlock.TRIGGERED, false));
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 4;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof DispenserBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity7);
            if (blockEntity7 instanceof DropperBlockEntity) {
                player.incrementStat(Stats.aa);
            }
            else {
                player.incrementStat(Stats.ac);
            }
        }
        return true;
    }
    
    protected void dispense(final World world, final BlockPos pos) {
        final BlockPointerImpl blockPointerImpl3 = new BlockPointerImpl(world, pos);
        final DispenserBlockEntity dispenserBlockEntity4 = blockPointerImpl3.<DispenserBlockEntity>getBlockEntity();
        final int integer5 = dispenserBlockEntity4.chooseNonEmptySlot();
        if (integer5 < 0) {
            world.playLevelEvent(1001, pos, 0);
            return;
        }
        final ItemStack itemStack6 = dispenserBlockEntity4.getInvStack(integer5);
        final DispenserBehavior dispenserBehavior7 = this.getBehaviorForItem(itemStack6);
        if (dispenserBehavior7 != DispenserBehavior.NOOP) {
            dispenserBlockEntity4.setInvStack(integer5, dispenserBehavior7.dispense(blockPointerImpl3, itemStack6));
        }
    }
    
    protected DispenserBehavior getBehaviorForItem(final ItemStack itemStack) {
        return DispenserBlock.BEHAVIORS.get(itemStack.getItem());
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        final boolean boolean7 = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        final boolean boolean8 = state.<Boolean>get((Property<Boolean>)DispenserBlock.TRIGGERED);
        if (boolean7 && !boolean8) {
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)DispenserBlock.TRIGGERED, true), 4);
        }
        else if (!boolean7 && boolean8) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)DispenserBlock.TRIGGERED, false), 4);
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!world.isClient) {
            this.dispense(world, pos);
        }
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new DispenserBlockEntity();
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)DispenserBlock.FACING, ctx.getPlayerFacing().getOpposite());
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof DispenserBlockEntity) {
            ItemScatterer.spawn(world, pos, (Inventory)blockEntity6);
            world.updateHorizontalAdjacent(pos, this);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    public static Position getOutputLocation(final BlockPointer blockPointer) {
        final Direction direction2 = blockPointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
        final double double3 = blockPointer.getX() + 0.7 * direction2.getOffsetX();
        final double double4 = blockPointer.getY() + 0.7 * direction2.getOffsetY();
        final double double5 = blockPointer.getZ() + 0.7 * direction2.getOffsetZ();
        return new PositionImpl(double3, double4, double5);
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
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)DispenserBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)DispenserBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)DispenserBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(DispenserBlock.FACING, DispenserBlock.TRIGGERED);
    }
    
    static {
        FACING = FacingBlock.FACING;
        TRIGGERED = Properties.TRIGGERED;
        BEHAVIORS = SystemUtil.<Map<Item, DispenserBehavior>>consume((Map<Item, DispenserBehavior>)new Object2ObjectOpenHashMap(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new ItemDispenserBehavior()));
    }
}
