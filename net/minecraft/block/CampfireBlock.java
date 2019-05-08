package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.state.property.Properties;
import net.minecraft.fluid.FluidState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import java.util.Random;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import javax.annotation.Nullable;
import net.minecraft.world.IWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ItemScatterer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.stat.Stats;
import net.minecraft.recipe.cooking.CampfireCookingRecipe;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.shape.VoxelShape;

public class CampfireBlock extends BlockWithEntity implements Waterloggable
{
    protected static final VoxelShape SHAPE;
    public static final BooleanProperty LIT;
    public static final BooleanProperty SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;
    
    public CampfireBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)CampfireBlock.LIT, true)).with((Property<Comparable>)CampfireBlock.SIGNAL_FIRE, false)).with((Property<Comparable>)CampfireBlock.WATERLOGGED, false)).<Comparable, Direction>with((Property<Comparable>)CampfireBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (state.<Boolean>get((Property<Boolean>)CampfireBlock.LIT)) {
            final BlockEntity blockEntity7 = world.getBlockEntity(pos);
            if (blockEntity7 instanceof CampfireBlockEntity) {
                final CampfireBlockEntity campfireBlockEntity8 = (CampfireBlockEntity)blockEntity7;
                final ItemStack itemStack9 = player.getStackInHand(hand);
                final Optional<CampfireCookingRecipe> optional10 = campfireBlockEntity8.getRecipeFor(itemStack9);
                if (optional10.isPresent()) {
                    if (!world.isClient && campfireBlockEntity8.addItem(player.abilities.creativeMode ? itemStack9.copy() : itemStack9, optional10.get().getCookTime())) {
                        player.incrementStat(Stats.at);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (!entity.isFireImmune() && state.<Boolean>get((Property<Boolean>)CampfireBlock.LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(DamageSource.IN_FIRE, 1.0f);
        }
        super.onEntityCollision(state, world, pos, entity);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof CampfireBlockEntity) {
            ItemScatterer.spawn(world, pos, ((CampfireBlockEntity)blockEntity6).getItemsBeingCooked());
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final IWorld iWorld2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final boolean boolean4 = iWorld2.getFluidState(blockPos3).getFluid() == Fluids.WATER;
        return (((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)CampfireBlock.WATERLOGGED, boolean4)).with((Property<Comparable>)CampfireBlock.SIGNAL_FIRE, this.doesBlockCauseSignalFire(iWorld2.getBlockState(blockPos3.down())))).with((Property<Comparable>)CampfireBlock.LIT, !boolean4)).<Comparable, Direction>with((Property<Comparable>)CampfireBlock.FACING, ctx.getPlayerHorizontalFacing());
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing == Direction.DOWN) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)CampfireBlock.SIGNAL_FIRE, this.doesBlockCauseSignalFire(neighborState));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    private boolean doesBlockCauseSignalFire(final BlockState blockState) {
        return blockState.getBlock() == Blocks.gs;
    }
    
    @Override
    public int getLuminance(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)CampfireBlock.LIT) ? super.getLuminance(state) : 0;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CampfireBlock.SHAPE;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!state.<Boolean>get((Property<Boolean>)CampfireBlock.LIT)) {
            return;
        }
        if (rnd.nextInt(10) == 0) {
            world.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.aC, SoundCategory.e, 0.5f + rnd.nextFloat(), rnd.nextFloat() * 0.7f + 0.6f, false);
        }
        if (rnd.nextInt(5) == 0) {
            for (int integer5 = 0; integer5 < rnd.nextInt(1) + 1; ++integer5) {
                world.addParticle(ParticleTypes.K, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, rnd.nextFloat() / 2.0f, 5.0E-5, rnd.nextFloat() / 2.0f);
            }
        }
    }
    
    @Override
    public boolean tryFillWithFluid(final IWorld world, final BlockPos pos, final BlockState state, final FluidState fluidState) {
        if (!state.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            final boolean boolean5 = state.<Boolean>get((Property<Boolean>)CampfireBlock.LIT);
            if (boolean5) {
                if (world.isClient()) {
                    for (int integer6 = 0; integer6 < 20; ++integer6) {
                        spawnSmokeParticle(world.getWorld(), pos, state.<Boolean>get((Property<Boolean>)CampfireBlock.SIGNAL_FIRE), true);
                    }
                }
                else {
                    world.playSound(null, pos, SoundEvents.dK, SoundCategory.e, 1.0f, 1.0f);
                }
                final BlockEntity blockEntity6 = world.getBlockEntity(pos);
                if (blockEntity6 instanceof CampfireBlockEntity) {
                    ((CampfireBlockEntity)blockEntity6).spawnItemsBeingCooked();
                }
            }
            world.setBlockState(pos, (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)CampfireBlock.WATERLOGGED, true)).<Comparable, Boolean>with((Property<Comparable>)CampfireBlock.LIT, false), 3);
            world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        }
        return false;
    }
    
    public static void spawnSmokeParticle(final World world, final BlockPos blockPos, final boolean isSignal, final boolean lotsOfSmoke) {
        final Random random5 = world.getRandom();
        final DefaultParticleType defaultParticleType6 = isSignal ? ParticleTypes.af : ParticleTypes.ae;
        world.addImportantParticle(defaultParticleType6, true, blockPos.getX() + 0.5 + random5.nextDouble() / 3.0 * (random5.nextBoolean() ? 1 : -1), blockPos.getY() + random5.nextDouble() + random5.nextDouble(), blockPos.getZ() + 0.5 + random5.nextDouble() / 3.0 * (random5.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (lotsOfSmoke) {
            world.addParticle(ParticleTypes.Q, blockPos.getX() + 0.25 + random5.nextDouble() / 2.0 * (random5.nextBoolean() ? 1 : -1), blockPos.getY() + 0.4, blockPos.getZ() + 0.25 + random5.nextDouble() / 2.0 * (random5.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)CampfireBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)CampfireBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)CampfireBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CampfireBlock.LIT, CampfireBlock.SIGNAL_FIRE, CampfireBlock.WATERLOGGED, CampfireBlock.FACING);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new CampfireBlockEntity();
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
        LIT = Properties.LIT;
        SIGNAL_FIRE = Properties.SIGNAL_FIRE;
        WATERLOGGED = Properties.WATERLOGGED;
        FACING = Properties.FACING_HORIZONTAL;
    }
}
