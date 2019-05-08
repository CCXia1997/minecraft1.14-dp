package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import java.util.Collections;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.PistonBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.DirectionProperty;

public class PistonExtensionBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    public static final EnumProperty<PistonType> TYPE;
    
    public PistonExtensionBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)PistonExtensionBlock.FACING, Direction.NORTH)).<PistonType, PistonType>with(PistonExtensionBlock.TYPE, PistonType.a));
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return null;
    }
    
    public static BlockEntity createBlockEntityPiston(final BlockState pushedBlock, final Direction dir, final boolean extending, final boolean boolean4) {
        return new PistonBlockEntity(pushedBlock, dir, extending, boolean4);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof PistonBlockEntity) {
            ((PistonBlockEntity)blockEntity6).t();
        }
    }
    
    @Override
    public void onBroken(final IWorld world, final BlockPos pos, final BlockState state) {
        final BlockPos blockPos4 = pos.offset(state.<Direction>get((Property<Direction>)PistonExtensionBlock.FACING).getOpposite());
        final BlockState blockState5 = world.getBlockState(blockPos4);
        if (blockState5.getBlock() instanceof PistonBlock && blockState5.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED)) {
            world.clearBlockState(blockPos4, false);
        }
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return false;
    }
    
    @Override
    public boolean isSimpleFullBlock(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean canSuffocate(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (!world.isClient && world.getBlockEntity(pos) == null) {
            world.clearBlockState(pos, false);
            return true;
        }
        return false;
    }
    
    @Override
    public List<ItemStack> getDroppedStacks(final BlockState state, final LootContext.Builder builder) {
        final PistonBlockEntity pistonBlockEntity3 = this.getBlockEntityPiston(builder.getWorld(), builder.<BlockPos>get(LootContextParameters.f));
        if (pistonBlockEntity3 == null) {
            return Collections.<ItemStack>emptyList();
        }
        return pistonBlockEntity3.getPushedBlock().getDroppedStacks(builder);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.empty();
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        final PistonBlockEntity pistonBlockEntity5 = this.getBlockEntityPiston(view, pos);
        if (pistonBlockEntity5 != null) {
            return pistonBlockEntity5.a(view, pos);
        }
        return VoxelShapes.empty();
    }
    
    @Nullable
    private PistonBlockEntity getBlockEntityPiston(final BlockView world, final BlockPos blockPos) {
        final BlockEntity blockEntity3 = world.getBlockEntity(blockPos);
        if (blockEntity3 instanceof PistonBlockEntity) {
            return (PistonBlockEntity)blockEntity3;
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)PistonExtensionBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)PistonExtensionBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)PistonExtensionBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PistonExtensionBlock.FACING, PistonExtensionBlock.TYPE);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = PistonHeadBlock.FACING;
        TYPE = PistonHeadBlock.TYPE;
    }
}
