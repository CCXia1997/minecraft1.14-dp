package net.minecraft.fluid;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;

public abstract class WaterFluid extends BaseFluid
{
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_WATER;
    }
    
    @Override
    public Fluid getStill() {
        return Fluids.WATER;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public Item getBucketItem() {
        return Items.ky;
    }
    
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(final World world, final BlockPos blockPos, final FluidState fluidState, final Random random) {
        if (!fluidState.isStill() && !fluidState.<Boolean>get((Property<Boolean>)WaterFluid.FALLING)) {
            if (random.nextInt(64) == 0) {
                world.playSound(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, SoundEvents.nd, SoundCategory.e, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
            }
        }
        else if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.W, blockPos.getX() + random.nextFloat(), blockPos.getY() + random.nextFloat(), blockPos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public ParticleParameters getParticle() {
        return ParticleTypes.m;
    }
    
    @Override
    protected boolean isInfinite() {
        return true;
    }
    
    @Override
    protected void beforeBreakingBlock(final IWorld world, final BlockPos pos, final BlockState state) {
        final BlockEntity blockEntity4 = state.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world.getWorld(), pos, blockEntity4);
    }
    
    public int b(final ViewableWorld viewableWorld) {
        return 4;
    }
    
    public BlockState toBlockState(final FluidState fluidState) {
        return ((AbstractPropertyContainer<O, BlockState>)Blocks.A.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)FluidBlock.LEVEL, BaseFluid.d(fluidState));
    }
    
    @Override
    public boolean matchesType(final Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }
    
    public int getLevelDecreasePerBlock(final ViewableWorld world) {
        return 1;
    }
    
    @Override
    public int getTickRate(final ViewableWorld viewableWorld) {
        return 5;
    }
    
    public boolean a(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos, final Fluid fluid, final Direction direction) {
        return direction == Direction.DOWN && !fluid.matches(FluidTags.a);
    }
    
    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }
    
    public static class Still extends WaterFluid
    {
        @Override
        public int getLevel(final FluidState fluidState) {
            return 8;
        }
        
        @Override
        public boolean isStill(final FluidState fluidState) {
            return true;
        }
    }
    
    public static class Flowing extends WaterFluid
    {
        @Override
        protected void appendProperties(final StateFactory.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(Flowing.LEVEL);
        }
        
        @Override
        public int getLevel(final FluidState fluidState) {
            return fluidState.<Integer>get((Property<Integer>)Flowing.LEVEL);
        }
        
        @Override
        public boolean isStill(final FluidState fluidState) {
            return false;
        }
    }
}
