package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class RedstoneOreBlock extends Block
{
    public static final BooleanProperty LIT;
    
    public RedstoneOreBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)RedstoneOreBlock.LIT, false));
    }
    
    @Override
    public int getLuminance(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)RedstoneOreBlock.LIT) ? super.getLuminance(state) : 0;
    }
    
    @Override
    public void onBlockBreakStart(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
        light(state, world, pos);
        super.onBlockBreakStart(state, world, pos, player);
    }
    
    @Override
    public void onSteppedOn(final World world, final BlockPos pos, final Entity entity) {
        light(world.getBlockState(pos), world, pos);
        super.onSteppedOn(world, pos, entity);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        light(state, world, pos);
        return super.activate(state, world, pos, player, hand, blockHitResult);
    }
    
    private static void light(final BlockState state, final World world, final BlockPos pos) {
        spawnParticles(world, pos);
        if (!state.<Boolean>get((Property<Boolean>)RedstoneOreBlock.LIT)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)RedstoneOreBlock.LIT, true), 3);
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (state.<Boolean>get((Property<Boolean>)RedstoneOreBlock.LIT)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)RedstoneOreBlock.LIT, false), 3);
        }
    }
    
    @Override
    public void onStacksDropped(final BlockState state, final World world, final BlockPos pos, final ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        if (EnchantmentHelper.getLevel(Enchantments.t, stack) == 0) {
            final int integer5 = 1 + world.random.nextInt(5);
            this.dropExperience(world, pos, integer5);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (state.<Boolean>get((Property<Boolean>)RedstoneOreBlock.LIT)) {
            spawnParticles(world, pos);
        }
    }
    
    private static void spawnParticles(final World world, final BlockPos pos) {
        final double double3 = 0.5625;
        final Random random5 = world.random;
        for (final Direction direction9 : Direction.values()) {
            final BlockPos blockPos10 = pos.offset(direction9);
            if (!world.getBlockState(blockPos10).isFullOpaque(world, blockPos10)) {
                final Direction.Axis axis11 = direction9.getAxis();
                final double double4 = (axis11 == Direction.Axis.X) ? (0.5 + 0.5625 * direction9.getOffsetX()) : random5.nextFloat();
                final double double5 = (axis11 == Direction.Axis.Y) ? (0.5 + 0.5625 * direction9.getOffsetY()) : random5.nextFloat();
                final double double6 = (axis11 == Direction.Axis.Z) ? (0.5 + 0.5625 * direction9.getOffsetZ()) : random5.nextFloat();
                world.addParticle(DustParticleParameters.RED, pos.getX() + double4, pos.getY() + double5, pos.getZ() + double6, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RedstoneOreBlock.LIT);
    }
    
    static {
        LIT = RedstoneTorchBlock.LIT;
    }
}
