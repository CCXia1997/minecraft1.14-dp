package net.minecraft.block;

import net.minecraft.state.property.Properties;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.DyeItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public abstract class AbstractSignBlock extends BlockWithEntity implements Waterloggable
{
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    
    protected AbstractSignBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)AbstractSignBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return AbstractSignBlock.SHAPE;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasBlockEntityBreakingRender(final BlockState state) {
        return true;
    }
    
    @Override
    public boolean canMobSpawnInside() {
        return true;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new SignBlockEntity();
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof SignBlockEntity) {
            final SignBlockEntity signBlockEntity8 = (SignBlockEntity)blockEntity7;
            final ItemStack itemStack9 = player.getStackInHand(hand);
            if (itemStack9.getItem() instanceof DyeItem && player.abilities.allowModifyWorld) {
                final boolean boolean10 = signBlockEntity8.setTextColor(((DyeItem)itemStack9.getItem()).getColor());
                if (boolean10 && !player.isCreative()) {
                    itemStack9.subtractAmount(1);
                }
            }
            return signBlockEntity8.onActivate(player);
        }
        return false;
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)AbstractSignBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    }
}
