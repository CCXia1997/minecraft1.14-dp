package net.minecraft.util;

import java.util.Optional;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos implements DynamicSerializable
{
    private final DimensionType dimension;
    private final BlockPos pos;
    
    private GlobalPos(final DimensionType dimensionType, final BlockPos blockPos) {
        this.dimension = dimensionType;
        this.pos = blockPos;
    }
    
    public static GlobalPos create(final DimensionType dimensionType, final BlockPos blockPos) {
        return new GlobalPos(dimensionType, blockPos);
    }
    
    public static GlobalPos deserialize(final Dynamic<?> dynamic) {
        return dynamic.get("dimension").map((Function)DimensionType::a).<GlobalPos>flatMap(dimensionType -> dynamic.get("pos").map((Function)BlockPos::deserialize).map(blockPos -> new GlobalPos(dimensionType, blockPos))).<Throwable>orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
    }
    
    public DimensionType getDimension() {
        return this.dimension;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GlobalPos globalPos2 = (GlobalPos)o;
        return Objects.equals(this.dimension, globalPos2.dimension) && Objects.equals(this.pos, globalPos2.pos);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> ops) {
        return (T)ops.createMap((Map)ImmutableMap.of(ops.createString("dimension"), this.dimension.<V>serialize((com.mojang.datafixers.types.DynamicOps<V>)ops), ops.createString("pos"), this.pos.<V>serialize((com.mojang.datafixers.types.DynamicOps<V>)ops)));
    }
    
    @Override
    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}
