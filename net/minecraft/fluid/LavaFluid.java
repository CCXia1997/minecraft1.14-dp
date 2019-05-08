package net.minecraft.fluid;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.state.property.Property;
import net.minecraft.block.FluidBlock;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;

public abstract class LavaFluid extends BaseFluid
{
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_LAVA;
    }
    
    @Override
    public Fluid getStill() {
        return Fluids.LAVA;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }
    
    @Override
    public Item getBucketItem() {
        return Items.kz;
    }
    
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(final World world, final BlockPos blockPos, final FluidState fluidState, final Random random) {
        final BlockPos blockPos2 = blockPos.up();
        if (world.getBlockState(blockPos2).isAir() && !world.getBlockState(blockPos2).isFullOpaque(world, blockPos2)) {
            if (random.nextInt(100) == 0) {
                final double double6 = blockPos.getX() + random.nextFloat();
                final double double7 = blockPos.getY() + 1;
                final double double8 = blockPos.getZ() + random.nextFloat();
                world.addParticle(ParticleTypes.K, double6, double7, double8, 0.0, 0.0, 0.0);
                world.playSound(double6, double7, double8, SoundEvents.fS, SoundCategory.e, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                world.playSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.fQ, SoundCategory.e, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }
    
    public void onRandomTick(final World world, final BlockPos blockPos, final FluidState fluidState, final Random random) {
        if (!world.getGameRules().getBoolean("doFireTick")) {
            return;
        }
        final int integer5 = random.nextInt(3);
        if (integer5 > 0) {
            BlockPos blockPos2 = blockPos;
            for (int integer6 = 0; integer6 < integer5; ++integer6) {
                blockPos2 = blockPos2.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                if (!world.isHeightValidAndBlockLoaded(blockPos2)) {
                    return;
                }
                final BlockState blockState8 = world.getBlockState(blockPos2);
                if (blockState8.isAir()) {
                    if (this.a((ViewableWorld)world, blockPos2)) {
                        world.setBlockState(blockPos2, Blocks.bM.getDefaultState());
                        return;
                    }
                }
                else if (blockState8.getMaterial().blocksMovement()) {
                    return;
                }
            }
        }
        else {
            for (int integer7 = 0; integer7 < 3; ++integer7) {
                final BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!world.isHeightValidAndBlockLoaded(blockPos3)) {
                    return;
                }
                if (world.isAir(blockPos3.up()) && this.b(world, blockPos3)) {
                    world.setBlockState(blockPos3.up(), Blocks.bM.getDefaultState());
                }
            }
        }
    }
    
    private boolean a(final ViewableWorld viewableWorld, final BlockPos blockPos) {
        for (final Direction direction6 : Direction.values()) {
            if (this.b(viewableWorld, blockPos.offset(direction6))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean b(final ViewableWorld viewableWorld, final BlockPos blockPos) {
        return (blockPos.getY() < 0 || blockPos.getY() >= 256 || viewableWorld.isBlockLoaded(blockPos)) && viewableWorld.getBlockState(blockPos).getMaterial().isBurnable();
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public ParticleParameters getParticle() {
        return ParticleTypes.j;
    }
    
    @Override
    protected void beforeBreakingBlock(final IWorld world, final BlockPos pos, final BlockState state) {
        this.a(world, pos);
    }
    
    public int b(final ViewableWorld viewableWorld) {
        return viewableWorld.getDimension().doesWaterVaporize() ? 4 : 2;
    }
    
    public BlockState toBlockState(final FluidState fluidState) {
        return ((AbstractPropertyContainer<O, BlockState>)Blocks.B.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)FluidBlock.LEVEL, BaseFluid.d(fluidState));
    }
    
    @Override
    public boolean matchesType(final Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
    }
    
    public int getLevelDecreasePerBlock(final ViewableWorld world) {
        return world.getDimension().doesWaterVaporize() ? 1 : 2;
    }
    
    public boolean a(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos, final Fluid fluid, final Direction direction) {
        return fluidState.getHeight(blockView, blockPos) >= 0.44444445f && fluid.matches(FluidTags.a);
    }
    
    @Override
    public int getTickRate(final ViewableWorld viewableWorld) {
        return viewableWorld.getDimension().isNether() ? 10 : 30;
    }
    
    public int getNextTickDelay(final World world, final BlockPos pos, final FluidState oldState, final FluidState newState) {
        int integer5 = this.getTickRate(world);
        if (!oldState.isEmpty() && !newState.isEmpty() && !oldState.<Boolean>get((Property<Boolean>)LavaFluid.FALLING) && !newState.<Boolean>get((Property<Boolean>)LavaFluid.FALLING) && newState.getHeight(world, pos) > oldState.getHeight(world, pos) && world.getRandom().nextInt(4) != 0) {
            integer5 *= 4;
        }
        return integer5;
    }
    
    private void a(final IWorld iWorld, final BlockPos blockPos) {
        iWorld.playLevelEvent(1501, blockPos, 0);
    }
    
    @Override
    protected boolean isInfinite() {
        return false;
    }
    
    @Override
    protected void flow(final IWorld world, final BlockPos pos, final BlockState state, final Direction direction, final FluidState fluidState) {
        if (direction == Direction.DOWN) {
            final FluidState fluidState2 = world.getFluidState(pos);
            if (this.matches(FluidTags.b) && fluidState2.matches(FluidTags.a)) {
                if (state.getBlock() instanceof FluidBlock) {
                    world.setBlockState(pos, Blocks.b.getDefaultState(), 3);
                }
                this.a(world, pos);
                return;
            }
        }
        super.flow(world, pos, state, direction, fluidState);
    }
    
    @Override
    protected boolean hasRandomTicks() {
        return true;
    }
    
    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }
    
    public static class Still extends LavaFluid
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
    
    public static class Flowing extends LavaFluid
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
