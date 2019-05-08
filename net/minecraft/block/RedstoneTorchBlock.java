package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.WeakHashMap;
import net.minecraft.state.property.Properties;
import com.google.common.collect.Lists;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import java.util.Random;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import java.util.List;
import net.minecraft.world.BlockView;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;

public class RedstoneTorchBlock extends TorchBlock
{
    public static final BooleanProperty LIT;
    private static final Map<BlockView, List<a>> b;
    
    protected RedstoneTorchBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)RedstoneTorchBlock.LIT, true));
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 2;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        for (final Direction direction9 : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction9), this);
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5) {
            return;
        }
        for (final Direction direction9 : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction9), this);
        }
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (state.<Boolean>get((Property<Boolean>)RedstoneTorchBlock.LIT) && Direction.UP != facing) {
            return 15;
        }
        return 0;
    }
    
    protected boolean a(final World world, final BlockPos pos, final BlockState state) {
        return world.isEmittingRedstonePower(pos.down(), Direction.DOWN);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        update(state, world, pos, random, this.a(world, pos, state));
    }
    
    public static void update(final BlockState blockState, final World world, final BlockPos blockPos, final Random random, final boolean boolean5) {
        final List<a> list6 = RedstoneTorchBlock.b.get(world);
        while (list6 != null && !list6.isEmpty() && world.getTime() - list6.get(0).b > 60L) {
            list6.remove(0);
        }
        if (blockState.<Boolean>get((Property<Boolean>)RedstoneTorchBlock.LIT)) {
            if (boolean5) {
                world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Boolean>with((Property<Comparable>)RedstoneTorchBlock.LIT, false), 3);
                if (a(world, blockPos, true)) {
                    world.playLevelEvent(1502, blockPos, 0);
                    world.getBlockTickScheduler().schedule(blockPos, world.getBlockState(blockPos).getBlock(), 160);
                }
            }
        }
        else if (!boolean5 && !a(world, blockPos, false)) {
            world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Boolean>with((Property<Comparable>)RedstoneTorchBlock.LIT, true), 3);
        }
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (state.<Boolean>get((Property<Boolean>)RedstoneTorchBlock.LIT) == this.a(world, pos, state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        }
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (facing == Direction.DOWN) {
            return state.getWeakRedstonePower(view, pos, facing);
        }
        return 0;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!state.<Boolean>get((Property<Boolean>)RedstoneTorchBlock.LIT)) {
            return;
        }
        final double double5 = pos.getX() + 0.5 + (rnd.nextDouble() - 0.5) * 0.2;
        final double double6 = pos.getY() + 0.7 + (rnd.nextDouble() - 0.5) * 0.2;
        final double double7 = pos.getZ() + 0.5 + (rnd.nextDouble() - 0.5) * 0.2;
        world.addParticle(DustParticleParameters.RED, double5, double6, double7, 0.0, 0.0, 0.0);
    }
    
    @Override
    public int getLuminance(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)RedstoneTorchBlock.LIT) ? super.getLuminance(state) : 0;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RedstoneTorchBlock.LIT);
    }
    
    private static boolean a(final World world, final BlockPos blockPos, final boolean boolean3) {
        final List<a> list4 = RedstoneTorchBlock.b.computeIfAbsent(world, blockView -> Lists.newArrayList());
        if (boolean3) {
            list4.add(new a(blockPos.toImmutable(), world.getTime()));
        }
        int integer5 = 0;
        for (int integer6 = 0; integer6 < list4.size(); ++integer6) {
            final a a7 = list4.get(integer6);
            if (a7.a.equals(blockPos) && ++integer5 >= 8) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LIT = Properties.LIT;
        b = new WeakHashMap<BlockView, List<a>>();
    }
    
    public static class a
    {
        private final BlockPos a;
        private final long b;
        
        public a(final BlockPos blockPos, final long long2) {
            this.a = blockPos;
            this.b = long2;
        }
    }
}
