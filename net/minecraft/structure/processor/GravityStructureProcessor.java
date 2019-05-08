package net.minecraft.structure.processor;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.Heightmap;

public class GravityStructureProcessor extends StructureProcessor
{
    private final Heightmap.Type heightmap;
    private final int offset;
    
    public GravityStructureProcessor(final Heightmap.Type heightmap, final int offset) {
        this.heightmap = heightmap;
        this.offset = offset;
    }
    
    public GravityStructureProcessor(final Dynamic<?> dynamic) {
        this(Heightmap.Type.byName(dynamic.get("heightmap").asString(Heightmap.Type.a.getName())), dynamic.get("offset").asInt(0));
    }
    
    @Nullable
    @Override
    public Structure.StructureBlockInfo process(final ViewableWorld world, final BlockPos pos, final Structure.StructureBlockInfo structureBlockInfo3, final Structure.StructureBlockInfo structureBlockInfo4, final StructurePlacementData placementData) {
        final int integer6 = world.getTop(this.heightmap, structureBlockInfo4.pos.getX(), structureBlockInfo4.pos.getZ()) + this.offset;
        final int integer7 = structureBlockInfo3.pos.getY();
        return new Structure.StructureBlockInfo(new BlockPos(structureBlockInfo4.pos.getX(), integer6 + integer7, structureBlockInfo4.pos.getZ()), structureBlockInfo4.state, structureBlockInfo4.tag);
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.d;
    }
    
    @Override
    protected <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("heightmap"), dynamicOps.createString(this.heightmap.getName()), dynamicOps.createString("offset"), dynamicOps.createInt(this.offset))));
    }
}
