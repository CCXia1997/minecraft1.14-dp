package net.minecraft.structure.pool;

import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.Structure;
import java.util.List;
import java.util.Random;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import com.mojang.datafixers.Dynamic;
import javax.annotation.Nullable;

public abstract class StructurePoolElement
{
    @Nullable
    private volatile StructurePool.Projection projection;
    
    protected StructurePoolElement(final StructurePool.Projection projection) {
        this.projection = projection;
    }
    
    protected StructurePoolElement(final Dynamic<?> dynamic) {
        this.projection = StructurePool.Projection.getById(dynamic.get("projection").asString(StructurePool.Projection.RIGID.getId()));
    }
    
    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(final StructureManager arg1, final BlockPos arg2, final BlockRotation arg3, final Random arg4);
    
    public abstract MutableIntBoundingBox getBoundingBox(final StructureManager arg1, final BlockPos arg2, final BlockRotation arg3);
    
    public abstract boolean generate(final StructureManager arg1, final IWorld arg2, final BlockPos arg3, final BlockRotation arg4, final MutableIntBoundingBox arg5, final Random arg6);
    
    public abstract StructurePoolElementType getType();
    
    public void a(final IWorld iWorld, final Structure.StructureBlockInfo structureBlockInfo, final BlockPos blockPos, final BlockRotation blockRotation, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
    }
    
    public StructurePoolElement setProjection(final StructurePool.Projection projection) {
        this.projection = projection;
        return this;
    }
    
    public StructurePool.Projection getProjection() {
        final StructurePool.Projection projection1 = this.projection;
        if (projection1 == null) {
            throw new IllegalStateException();
        }
        return projection1;
    }
    
    protected abstract <T> Dynamic<T> a(final DynamicOps<T> arg1);
    
    public <T> Dynamic<T> b(final DynamicOps<T> dynamicOps) {
        final T object2 = (T)this.a((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue();
        final T object3 = (T)dynamicOps.mergeInto(object2, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(object3, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getId())));
    }
    
    public int d() {
        return 1;
    }
}
