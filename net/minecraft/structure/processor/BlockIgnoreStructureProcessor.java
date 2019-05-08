package net.minecraft.structure.processor;

import net.minecraft.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.BlockState;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import com.google.common.collect.ImmutableList;

public class BlockIgnoreStructureProcessor extends StructureProcessor
{
    public static final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS;
    public static final BlockIgnoreStructureProcessor IGNORE_AIR;
    public static final BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS;
    private final ImmutableList<Block> blocks;
    
    public BlockIgnoreStructureProcessor(final List<Block> list) {
        this.blocks = ImmutableList.<Block>copyOf(list);
    }
    
    public BlockIgnoreStructureProcessor(final Dynamic<?> dynamic) {
        this(dynamic.get("blocks").asList(dynamic -> BlockState.deserialize(dynamic).getBlock()));
    }
    
    @Nullable
    @Override
    public Structure.StructureBlockInfo process(final ViewableWorld world, final BlockPos pos, final Structure.StructureBlockInfo structureBlockInfo3, final Structure.StructureBlockInfo structureBlockInfo4, final StructurePlacementData placementData) {
        if (this.blocks.contains(structureBlockInfo4.state.getBlock())) {
            return null;
        }
        return structureBlockInfo4;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.b;
    }
    
    @Override
    protected <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("blocks"), dynamicOps.createList((Stream)this.blocks.stream().map(block -> BlockState.<T>serialize(dynamicOps, block.getDefaultState()).getValue())))));
    }
    
    static {
        IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(ImmutableList.<Block>of(Blocks.lX));
        IGNORE_AIR = new BlockIgnoreStructureProcessor(ImmutableList.<Block>of(Blocks.AIR));
        IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(ImmutableList.<Block>of(Blocks.AIR, Blocks.lX));
    }
}
