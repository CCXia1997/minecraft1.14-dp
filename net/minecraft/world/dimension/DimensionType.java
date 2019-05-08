package net.minecraft.world.dimension;

import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import java.io.File;
import net.minecraft.util.Identifier;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import java.util.function.BiFunction;
import net.minecraft.util.DynamicSerializable;

public class DimensionType implements DynamicSerializable
{
    public static final DimensionType a;
    public static final DimensionType b;
    public static final DimensionType c;
    private final int id;
    private final String suffix;
    private final String saveDir;
    private final BiFunction<World, DimensionType, ? extends Dimension> factory;
    private final boolean hasSkyLight;
    
    private static DimensionType register(final String id, final DimensionType dimension) {
        return Registry.<DimensionType>register(Registry.DIMENSION, dimension.id, id, dimension);
    }
    
    protected DimensionType(final int dimensionId, final String suffix, final String saveDir, final BiFunction<World, DimensionType, ? extends Dimension> factory, final boolean boolean5) {
        this.id = dimensionId;
        this.suffix = suffix;
        this.saveDir = saveDir;
        this.factory = factory;
        this.hasSkyLight = boolean5;
    }
    
    public static DimensionType a(final Dynamic<?> dynamic) {
        return Registry.DIMENSION.get(new Identifier(dynamic.asString("")));
    }
    
    public static Iterable<DimensionType> getAll() {
        return Registry.DIMENSION;
    }
    
    public int getRawId() {
        return this.id - 1;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public File getFile(final File file) {
        if (this.saveDir.isEmpty()) {
            return file;
        }
        return new File(file, this.saveDir);
    }
    
    public Dimension create(final World world) {
        return (Dimension)this.factory.apply(world, this);
    }
    
    @Override
    public String toString() {
        return getId(this).toString();
    }
    
    @Nullable
    public static DimensionType byRawId(final int integer) {
        return Registry.DIMENSION.get(integer + 1);
    }
    
    @Nullable
    public static DimensionType byId(final Identifier identifier) {
        return Registry.DIMENSION.get(identifier);
    }
    
    @Nullable
    public static Identifier getId(final DimensionType dimensionType) {
        return Registry.DIMENSION.getId(dimensionType);
    }
    
    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> ops) {
        return (T)ops.createString(Registry.DIMENSION.getId(this).toString());
    }
    
    static {
        a = register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true));
        b = register("the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new, false));
        c = register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false));
    }
}
