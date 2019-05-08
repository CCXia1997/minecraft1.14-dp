package net.minecraft.structure.processor;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import com.mojang.datafixers.Dynamic;

public class BlockRotStructureProcessor extends StructureProcessor
{
    private final float integrity;
    
    public BlockRotStructureProcessor(final float integrity) {
        this.integrity = integrity;
    }
    
    public BlockRotStructureProcessor(final Dynamic<?> dynamic) {
        this(dynamic.get("integrity").asFloat(1.0f));
    }
    
    @Nullable
    @Override
    public Structure.StructureBlockInfo process(final ViewableWorld world, final BlockPos pos, final Structure.StructureBlockInfo structureBlockInfo3, final Structure.StructureBlockInfo structureBlockInfo4, final StructurePlacementData placementData) {
        final Random random6 = placementData.getRandom(structureBlockInfo4.pos);
        if (this.integrity >= 1.0f || random6.nextFloat() <= this.integrity) {
            return structureBlockInfo4;
        }
        return null;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.c;
    }
    
    @Override
    protected <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("integrity"), dynamicOps.createFloat(this.integrity))));
    }
}
