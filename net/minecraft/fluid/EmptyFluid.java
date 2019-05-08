package net.minecraft.fluid;

import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;

public class EmptyFluid extends Fluid
{
    @Environment(EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }
    
    @Override
    public Item getBucketItem() {
        return Items.AIR;
    }
    
    public boolean a(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos, final Fluid fluid, final Direction direction) {
        return true;
    }
    
    public Vec3d getVelocity(final BlockView world, final BlockPos pos, final FluidState state) {
        return Vec3d.ZERO;
    }
    
    @Override
    public int getTickRate(final ViewableWorld viewableWorld) {
        return 0;
    }
    
    @Override
    protected boolean isEmpty() {
        return true;
    }
    
    @Override
    protected float getBlastResistance() {
        return 0.0f;
    }
    
    @Override
    public float getHeight(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos) {
        return 0.0f;
    }
    
    @Override
    protected BlockState toBlockState(final FluidState fluidState) {
        return Blocks.AIR.getDefaultState();
    }
    
    @Override
    public boolean isStill(final FluidState fluidState) {
        return false;
    }
    
    @Override
    public int getLevel(final FluidState fluidState) {
        return 0;
    }
    
    @Override
    public VoxelShape getShape(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos) {
        return VoxelShapes.empty();
    }
}
