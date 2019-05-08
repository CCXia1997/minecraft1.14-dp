package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class SweetBerryBushBlock extends PlantBlock implements Fertilizable
{
    public static final IntegerProperty AGE;
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;
    
    public SweetBerryBushBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, 0));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(Items.pR);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE) == 0) {
            return SweetBerryBushBlock.SMALL_SHAPE;
        }
        if (state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE) < 3) {
            return SweetBerryBushBlock.LARGE_SHAPE;
        }
        return super.getOutlineShape(state, view, pos, verticalEntityPosition);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        super.onScheduledTick(state, world, pos, random);
        final int integer5 = state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE);
        if (integer5 < 3 && random.nextInt(5) == 0 && world.getLightLevel(pos.up(), 0) >= 9) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, integer5 + 1), 2);
        }
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.B) {
            return;
        }
        entity.slowMovement(state, new Vec3d(0.800000011920929, 0.75, 0.800000011920929));
        if (!world.isClient && state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE) > 0 && (entity.prevRenderX != entity.x || entity.prevRenderZ != entity.z)) {
            final double double5 = Math.abs(entity.x - entity.prevRenderX);
            final double double6 = Math.abs(entity.z - entity.prevRenderZ);
            if (double5 >= 0.003000000026077032 || double6 >= 0.003000000026077032) {
                entity.damage(DamageSource.SWEET_BERRY_BUSH, 1.0f);
            }
        }
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final int integer7 = state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE);
        final boolean boolean8 = integer7 == 3;
        if (!boolean8 && player.getStackInHand(hand).getItem() == Items.lw) {
            return false;
        }
        if (integer7 > 1) {
            final int integer8 = 1 + world.random.nextInt(2);
            Block.dropStack(world, pos, new ItemStack(Items.pR, integer8 + (boolean8 ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.lB, SoundCategory.e, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, 1), 2);
            return true;
        }
        return super.activate(state, world, pos, player, hand, blockHitResult);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SweetBerryBushBlock.AGE);
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE) < 3;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        final int integer5 = Math.min(3, state.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE) + 1);
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, integer5), 2);
    }
    
    static {
        AGE = Properties.AGE_3;
        SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
        LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
