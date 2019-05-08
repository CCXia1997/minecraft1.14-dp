package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.EnumProperty;

public class TallPlantBlock extends PlantBlock
{
    public static final EnumProperty<DoubleBlockHalf> HALF;
    
    public TallPlantBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<DoubleBlockHalf, DoubleBlockHalf>with(TallPlantBlock.HALF, DoubleBlockHalf.b));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final DoubleBlockHalf doubleBlockHalf7 = state.<DoubleBlockHalf>get(TallPlantBlock.HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleBlockHalf7 == DoubleBlockHalf.b == (facing == Direction.UP) && (neighborState.getBlock() != this || neighborState.<DoubleBlockHalf>get(TallPlantBlock.HALF) == doubleBlockHalf7)) {
            return Blocks.AIR.getDefaultState();
        }
        if (doubleBlockHalf7 == DoubleBlockHalf.b && facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockPos blockPos2 = ctx.getBlockPos();
        if (blockPos2.getY() < 255 && ctx.getWorld().getBlockState(blockPos2.up()).canReplace(ctx)) {
            return super.getPlacementState(ctx);
        }
        return null;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        world.setBlockState(pos.up(), ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<DoubleBlockHalf, DoubleBlockHalf>with(TallPlantBlock.HALF, DoubleBlockHalf.a), 3);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        if (state.<DoubleBlockHalf>get(TallPlantBlock.HALF) == DoubleBlockHalf.a) {
            final BlockState blockState4 = world.getBlockState(pos.down());
            return blockState4.getBlock() == this && blockState4.<DoubleBlockHalf>get(TallPlantBlock.HALF) == DoubleBlockHalf.b;
        }
        return super.canPlaceAt(state, world, pos);
    }
    
    public void placeAt(final IWorld world, final BlockPos pos, final int flags) {
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<DoubleBlockHalf, DoubleBlockHalf>with(TallPlantBlock.HALF, DoubleBlockHalf.b), flags);
        world.setBlockState(pos.up(), ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<DoubleBlockHalf, DoubleBlockHalf>with(TallPlantBlock.HALF, DoubleBlockHalf.a), flags);
    }
    
    @Override
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, @Nullable final BlockEntity blockEntity, final ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        final DoubleBlockHalf doubleBlockHalf5 = state.<DoubleBlockHalf>get(TallPlantBlock.HALF);
        final BlockPos blockPos6 = (doubleBlockHalf5 == DoubleBlockHalf.b) ? pos.up() : pos.down();
        final BlockState blockState7 = world.getBlockState(blockPos6);
        if (blockState7.getBlock() == this && blockState7.<DoubleBlockHalf>get(TallPlantBlock.HALF) != doubleBlockHalf5) {
            world.setBlockState(blockPos6, Blocks.AIR.getDefaultState(), 35);
            world.playLevelEvent(player, 2001, blockPos6, Block.getRawIdFromState(blockState7));
            if (!world.isClient && !player.isCreative()) {
                Block.dropStacks(state, world, pos, null, player, player.getMainHandStack());
                Block.dropStacks(blockState7, world, blockPos6, null, player, player.getMainHandStack());
            }
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(TallPlantBlock.HALF);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public long getRenderingSeed(final BlockState state, final BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down((state.<DoubleBlockHalf>get(TallPlantBlock.HALF) != DoubleBlockHalf.b) ? 1 : 0).getY(), pos.getZ());
    }
    
    static {
        HALF = Properties.DOUBLE_BLOCK_HALF;
    }
}
