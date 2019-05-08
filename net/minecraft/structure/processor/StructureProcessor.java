package net.minecraft.structure.processor;

import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public abstract class StructureProcessor
{
    @Nullable
    public abstract Structure.StructureBlockInfo process(final ViewableWorld arg1, final BlockPos arg2, final Structure.StructureBlockInfo arg3, final Structure.StructureBlockInfo arg4, final StructurePlacementData arg5);
    
    protected abstract StructureProcessorType getType();
    
    protected abstract <T> Dynamic<T> a(final DynamicOps<T> arg1);
    
    public <T> Dynamic<T> b(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(this.a((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue(), dynamicOps.createString("processor_type"), dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getType()).toString())));
    }
}
