package net.minecraft.fluid;

import net.minecraft.util.shape.VoxelShape;
import java.util.Iterator;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import net.minecraft.state.property.Property;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.math.Direction;
import net.minecraft.tag.Tag;
import net.minecraft.block.BlockRenderLayer;
import javax.annotation.Nullable;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;
import java.util.Random;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.PropertyContainer;

public interface FluidState extends PropertyContainer<FluidState>
{
    Fluid getFluid();
    
    default boolean isStill() {
        return this.getFluid().isStill(this);
    }
    
    default boolean isEmpty() {
        return this.getFluid().isEmpty();
    }
    
    default float getHeight(final BlockView blockView, final BlockPos blockPos) {
        return this.getFluid().getHeight(this, blockView, blockPos);
    }
    
    default int getLevel() {
        return this.getFluid().getLevel(this);
    }
    
    @Environment(EnvType.CLIENT)
    default boolean b(final BlockView blockView, final BlockPos blockPos) {
        for (int integer3 = -1; integer3 <= 1; ++integer3) {
            for (int integer4 = -1; integer4 <= 1; ++integer4) {
                final BlockPos blockPos2 = blockPos.add(integer3, 0, integer4);
                final FluidState fluidState6 = blockView.getFluidState(blockPos2);
                if (!fluidState6.getFluid().matchesType(this.getFluid()) && !blockView.getBlockState(blockPos2).isFullOpaque(blockView, blockPos2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    default void onScheduledTick(final World world, final BlockPos blockPos) {
        this.getFluid().onScheduledTick(world, blockPos, this);
    }
    
    @Environment(EnvType.CLIENT)
    default void randomDisplayTick(final World world, final BlockPos blockPos, final Random random) {
        this.getFluid().randomDisplayTick(world, blockPos, this, random);
    }
    
    default boolean hasRandomTicks() {
        return this.getFluid().hasRandomTicks();
    }
    
    default void onRandomTick(final World world, final BlockPos blockPos, final Random random) {
        this.getFluid().onRandomTick(world, blockPos, this, random);
    }
    
    default Vec3d getVelocity(final BlockView world, final BlockPos pos) {
        return this.getFluid().getVelocity(world, pos, this);
    }
    
    default BlockState getBlockState() {
        return this.getFluid().toBlockState(this);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    default ParticleParameters getParticle() {
        return this.getFluid().getParticle();
    }
    
    @Environment(EnvType.CLIENT)
    default BlockRenderLayer getRenderLayer() {
        return this.getFluid().getRenderLayer();
    }
    
    default boolean matches(final Tag<Fluid> tag) {
        return this.getFluid().matches(tag);
    }
    
    default float getBlastResistance() {
        return this.getFluid().getBlastResistance();
    }
    
    default boolean a(final BlockView blockView, final BlockPos blockPos, final Fluid fluid, final Direction direction) {
        return this.getFluid().a(this, blockView, blockPos, fluid, direction);
    }
    
    default <T> Dynamic<T> serialize(final DynamicOps<T> ops, final FluidState state) {
        final ImmutableMap<Property<?>, Comparable<?>> immutableMap3 = state.getEntries();
        T object4;
        if (immutableMap3.isEmpty()) {
            object4 = (T)ops.createMap((Map)ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.FLUID.getId(state.getFluid()).toString())));
        }
        else {
            object4 = (T)ops.createMap((Map)ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.FLUID.getId(state.getFluid()).toString()), ops.createString("Properties"), ops.createMap((Map)immutableMap3.entrySet().stream().map(entry -> Pair.of(ops.createString(entry.getKey().getName()), ops.createString(PropertyContainer.<Comparable>getValueAsString((Property<Comparable>)entry.getKey(), entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, object4);
    }
    
    default <T> FluidState deserialize(final Dynamic<T> dynamic) {
        final Fluid fluid2 = Registry.FLUID.get(new Identifier(dynamic.getElement("Name").<String>flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:empty")));
        final Map<String, String> map3 = (Map<String, String>)dynamic.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        FluidState fluidState4 = fluid2.getDefaultState();
        final StateFactory<Fluid, FluidState> stateFactory5 = fluid2.getStateFactory();
        for (final Map.Entry<String, String> entry7 : map3.entrySet()) {
            final String string8 = entry7.getKey();
            final Property<?> property9 = stateFactory5.getProperty(string8);
            if (property9 != null) {
                fluidState4 = PropertyContainer.deserialize(fluidState4, property9, string8, dynamic.toString(), entry7.getValue());
            }
        }
        return fluidState4;
    }
    
    default VoxelShape getShape(final BlockView blockView, final BlockPos blockPos) {
        return this.getFluid().getShape(this, blockView, blockPos);
    }
}
