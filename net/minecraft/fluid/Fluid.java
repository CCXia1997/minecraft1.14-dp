package net.minecraft.fluid;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.tag.Tag;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.particle.ParticleParameters;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.IdList;

public abstract class Fluid
{
    public static final IdList<FluidState> STATE_IDS;
    protected final StateFactory<Fluid, FluidState> stateFactory;
    private FluidState defaultState;
    
    protected Fluid() {
        final StateFactory.Builder<Fluid, FluidState> builder1 = new StateFactory.Builder<Fluid, FluidState>(this);
        this.appendProperties(builder1);
        this.stateFactory = builder1.<FluidStateImpl>build(FluidStateImpl::new);
        this.setDefaultState(this.stateFactory.getDefaultState());
    }
    
    protected void appendProperties(final StateFactory.Builder<Fluid, FluidState> builder) {
    }
    
    public StateFactory<Fluid, FluidState> getStateFactory() {
        return this.stateFactory;
    }
    
    protected final void setDefaultState(final FluidState fluidState) {
        this.defaultState = fluidState;
    }
    
    public final FluidState getDefaultState() {
        return this.defaultState;
    }
    
    @Environment(EnvType.CLIENT)
    protected abstract BlockRenderLayer getRenderLayer();
    
    public abstract Item getBucketItem();
    
    @Environment(EnvType.CLIENT)
    protected void randomDisplayTick(final World world, final BlockPos blockPos, final FluidState fluidState, final Random random) {
    }
    
    protected void onScheduledTick(final World world, final BlockPos pos, final FluidState state) {
    }
    
    protected void onRandomTick(final World world, final BlockPos blockPos, final FluidState fluidState, final Random random) {
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    protected ParticleParameters getParticle() {
        return null;
    }
    
    protected abstract boolean a(final FluidState arg1, final BlockView arg2, final BlockPos arg3, final Fluid arg4, final Direction arg5);
    
    protected abstract Vec3d getVelocity(final BlockView arg1, final BlockPos arg2, final FluidState arg3);
    
    public abstract int getTickRate(final ViewableWorld arg1);
    
    protected boolean hasRandomTicks() {
        return false;
    }
    
    protected boolean isEmpty() {
        return false;
    }
    
    protected abstract float getBlastResistance();
    
    public abstract float getHeight(final FluidState arg1, final BlockView arg2, final BlockPos arg3);
    
    protected abstract BlockState toBlockState(final FluidState arg1);
    
    public abstract boolean isStill(final FluidState arg1);
    
    public abstract int getLevel(final FluidState arg1);
    
    public boolean matchesType(final Fluid fluid) {
        return fluid == this;
    }
    
    public boolean matches(final Tag<Fluid> tag) {
        return tag.contains(this);
    }
    
    public abstract VoxelShape getShape(final FluidState arg1, final BlockView arg2, final BlockPos arg3);
    
    static {
        STATE_IDS = new IdList<FluidState>();
    }
}
