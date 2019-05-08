package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.item.SwordItem;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class BambooSaplingBlock extends Block implements Fertilizable
{
    protected static final VoxelShape SHAPE;
    
    public BambooSaplingBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final Vec3d vec3d5 = state.getOffsetPos(view, pos);
        return BambooSaplingBlock.SHAPE.offset(vec3d5.x, vec3d5.y, vec3d5.z);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (random.nextInt(3) == 0 && world.isAir(pos.up()) && world.getLightLevel(pos.up(), 0) >= 9) {
            this.grow(world, pos);
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.down()).matches(BlockTags.R);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (facing == Direction.UP && neighborState.getBlock() == Blocks.kQ) {
            world.setBlockState(pos, Blocks.kQ.getDefaultState(), 2);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(Items.BAMBOO);
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return world.getBlockState(pos.up()).isAir();
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        this.grow(world, pos);
    }
    
    @Override
    public float calcBlockBreakingDelta(final BlockState state, final PlayerEntity player, final BlockView world, final BlockPos pos) {
        if (player.getMainHandStack().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    protected void grow(final World world, final BlockPos pos) {
        world.setBlockState(pos.up(), ((AbstractPropertyContainer<O, BlockState>)Blocks.kQ.getDefaultState()).<BambooLeaves, BambooLeaves>with(BambooBlock.LEAVES, BambooLeaves.b), 3);
    }
    
    static {
        SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);
    }
}
