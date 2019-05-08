package net.minecraft.structure.pool;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.IWorld;
import java.util.Iterator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.Structure;
import java.util.Random;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.List;

public class ListPoolElement extends StructurePoolElement
{
    private final List<StructurePoolElement> elements;
    
    @Deprecated
    public ListPoolElement(final List<StructurePoolElement> list) {
        this(list, StructurePool.Projection.RIGID);
    }
    
    public ListPoolElement(final List<StructurePoolElement> list, final StructurePool.Projection projection) {
        super(projection);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list;
        this.b(projection);
    }
    
    public ListPoolElement(final Dynamic<?> dynamic) {
        super(dynamic);
        final List<StructurePoolElement> list2 = (List<StructurePoolElement>)dynamic.get("elements").asList(dynamic -> DynamicDeserializer.<Object, EmptyPoolElement, StructurePoolElementType>deserialize(dynamic, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE));
        if (list2.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list2;
    }
    
    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation, final Random random) {
        return this.elements.get(0).getStructureBlockInfos(structureManager, pos, rotation, random);
    }
    
    @Override
    public MutableIntBoundingBox getBoundingBox(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation) {
        final MutableIntBoundingBox mutableIntBoundingBox4 = MutableIntBoundingBox.empty();
        for (final StructurePoolElement structurePoolElement6 : this.elements) {
            final MutableIntBoundingBox mutableIntBoundingBox5 = structurePoolElement6.getBoundingBox(structureManager, pos, rotation);
            mutableIntBoundingBox4.setFrom(mutableIntBoundingBox5);
        }
        return mutableIntBoundingBox4;
    }
    
    @Override
    public boolean generate(final StructureManager structureManager, final IWorld world, final BlockPos pos, final BlockRotation rotation, final MutableIntBoundingBox boundingBox, final Random random) {
        for (final StructurePoolElement structurePoolElement8 : this.elements) {
            if (!structurePoolElement8.generate(structureManager, world, pos, rotation, boundingBox, random)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.LIST_POOL_ELEMENT;
    }
    
    @Override
    public StructurePoolElement setProjection(final StructurePool.Projection projection) {
        super.setProjection(projection);
        this.b(projection);
        return this;
    }
    
    public <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        final T object2 = (T)dynamicOps.createList((Stream)this.elements.stream().map(structurePoolElement -> structurePoolElement.<T>b(dynamicOps).getValue()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.<Object, T>of(dynamicOps.createString("elements"), object2)));
    }
    
    @Override
    public String toString() {
        return "List[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
    
    private void b(final StructurePool.Projection projection) {
        this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
    }
}
