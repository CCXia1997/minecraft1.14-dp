package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.shape.VoxelShape;

public class TurtleEggBlock extends Block
{
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;
    public static final IntegerProperty HATCH;
    public static final IntegerProperty EGGS;
    
    public TurtleEggBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)TurtleEggBlock.HATCH, 0)).<Comparable, Integer>with((Property<Comparable>)TurtleEggBlock.EGGS, 1));
    }
    
    @Override
    public void onSteppedOn(final World world, final BlockPos pos, final Entity entity) {
        this.tryBreakEgg(world, pos, entity, 100);
        super.onSteppedOn(world, pos, entity);
    }
    
    @Override
    public void onLandedUpon(final World world, final BlockPos pos, final Entity entity, final float distance) {
        if (!(entity instanceof ZombieEntity)) {
            this.tryBreakEgg(world, pos, entity, 3);
        }
        super.onLandedUpon(world, pos, entity, distance);
    }
    
    private void tryBreakEgg(final World world, final BlockPos pos, final Entity entity, final int inverseChance) {
        if (!this.breaksEgg(world, entity)) {
            super.onSteppedOn(world, pos, entity);
            return;
        }
        if (!world.isClient && world.random.nextInt(inverseChance) == 0) {
            this.breakEgg(world, pos, world.getBlockState(pos));
        }
    }
    
    private void breakEgg(final World world, final BlockPos pos, final BlockState state) {
        world.playSound(null, pos, SoundEvents.lY, SoundCategory.e, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        final int integer4 = state.<Integer>get((Property<Integer>)TurtleEggBlock.EGGS);
        if (integer4 <= 1) {
            world.breakBlock(pos, false);
        }
        else {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)TurtleEggBlock.EGGS, integer4 - 1), 2);
            world.playLevelEvent(2001, pos, Block.getRawIdFromState(state));
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (this.shouldHatchProgress(world) && this.isSand(world, pos)) {
            final int integer5 = state.<Integer>get((Property<Integer>)TurtleEggBlock.HATCH);
            if (integer5 < 2) {
                world.playSound(null, pos, SoundEvents.lZ, SoundCategory.e, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)TurtleEggBlock.HATCH, integer5 + 1), 2);
            }
            else {
                world.playSound(null, pos, SoundEvents.ma, SoundCategory.e, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.clearBlockState(pos, false);
                if (!world.isClient) {
                    for (int integer6 = 0; integer6 < state.<Integer>get((Property<Integer>)TurtleEggBlock.EGGS); ++integer6) {
                        world.playLevelEvent(2001, pos, Block.getRawIdFromState(state));
                        final TurtleEntity turtleEntity7 = EntityType.TURTLE.create(world);
                        turtleEntity7.setBreedingAge(-24000);
                        turtleEntity7.setHomePos(pos);
                        turtleEntity7.setPositionAndAngles(pos.getX() + 0.3 + integer6 * 0.2, pos.getY(), pos.getZ() + 0.3, 0.0f, 0.0f);
                        world.spawnEntity(turtleEntity7);
                    }
                }
            }
        }
    }
    
    private boolean isSand(final BlockView world, final BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock() == Blocks.C;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (this.isSand(world, pos) && !world.isClient) {
            world.playLevelEvent(2005, pos, 0);
        }
    }
    
    private boolean shouldHatchProgress(final World world) {
        final float float2 = world.getSkyAngle(1.0f);
        return (float2 < 0.69 && float2 > 0.65) || world.random.nextInt(500) == 0;
    }
    
    @Override
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, @Nullable final BlockEntity blockEntity, final ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        this.breakEgg(world, pos, state);
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        return (ctx.getItemStack().getItem() == this.getItem() && state.<Integer>get((Property<Integer>)TurtleEggBlock.EGGS) < 4) || super.canReplace(state, ctx);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState2.getBlock() == this) {
            return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Integer>with((Property<Comparable>)TurtleEggBlock.EGGS, Math.min(4, blockState2.<Integer>get((Property<Integer>)TurtleEggBlock.EGGS) + 1));
        }
        return super.getPlacementState(ctx);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (state.<Integer>get((Property<Integer>)TurtleEggBlock.EGGS) > 1) {
            return TurtleEggBlock.LARGE_SHAPE;
        }
        return TurtleEggBlock.SMALL_SHAPE;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(TurtleEggBlock.HATCH, TurtleEggBlock.EGGS);
    }
    
    private boolean breaksEgg(final World world, final Entity entity) {
        return !(entity instanceof TurtleEntity) && (!(entity instanceof LivingEntity) || entity instanceof PlayerEntity || world.getGameRules().getBoolean("mobGriefing"));
    }
    
    static {
        SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
        LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
        HATCH = Properties.HATCH;
        EGGS = Properties.EGGS;
    }
}
