package net.minecraft.structure.pool;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Consumer;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.Collections;
import java.util.Random;
import java.util.Iterator;
import net.minecraft.block.enums.StructureBlockMode;
import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;
import net.minecraft.structure.processor.NopStructureProcessor;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.List;
import net.minecraft.structure.processor.StructureProcessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Identifier;

public class SinglePoolElement extends StructurePoolElement
{
    protected final Identifier location;
    protected final ImmutableList<StructureProcessor> processors;
    
    @Deprecated
    public SinglePoolElement(final String location, final List<StructureProcessor> processors) {
        this(location, processors, StructurePool.Projection.RIGID);
    }
    
    public SinglePoolElement(final String string, final List<StructureProcessor> list, final StructurePool.Projection projection) {
        super(projection);
        this.location = new Identifier(string);
        this.processors = ImmutableList.<StructureProcessor>copyOf(list);
    }
    
    @Deprecated
    public SinglePoolElement(final String string) {
        this(string, ImmutableList.of());
    }
    
    public SinglePoolElement(final Dynamic<?> dynamic) {
        super(dynamic);
        this.location = new Identifier(dynamic.get("location").asString(""));
        this.processors = ImmutableList.<StructureProcessor>copyOf(dynamic.get("processors").asList(dynamic -> DynamicDeserializer.<Object, NopStructureProcessor, StructureProcessorType>deserialize(dynamic, Registry.STRUCTURE_PROCESSOR, "processor_type", NopStructureProcessor.INSTANCE)));
    }
    
    public List<Structure.StructureBlockInfo> a(final StructureManager structureManager, final BlockPos blockPos, final BlockRotation blockRotation, final boolean boolean4) {
        final Structure structure5 = structureManager.getStructureOrBlank(this.location);
        final List<Structure.StructureBlockInfo> list6 = structure5.a(blockPos, new StructurePlacementData().setRotation(blockRotation), Blocks.lX, boolean4);
        final List<Structure.StructureBlockInfo> list7 = Lists.newArrayList();
        for (final Structure.StructureBlockInfo structureBlockInfo9 : list6) {
            if (structureBlockInfo9.tag == null) {
                continue;
            }
            final StructureBlockMode structureBlockMode10 = StructureBlockMode.valueOf(structureBlockInfo9.tag.getString("mode"));
            if (structureBlockMode10 != StructureBlockMode.d) {
                continue;
            }
            list7.add(structureBlockInfo9);
        }
        return list7;
    }
    
    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation, final Random random) {
        final Structure structure5 = structureManager.getStructureOrBlank(this.location);
        final List<Structure.StructureBlockInfo> list6 = structure5.a(pos, new StructurePlacementData().setRotation(rotation), Blocks.lY, true);
        Collections.shuffle(list6, random);
        return list6;
    }
    
    @Override
    public MutableIntBoundingBox getBoundingBox(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation) {
        final Structure structure4 = structureManager.getStructureOrBlank(this.location);
        return structure4.calculateBoundingBox(new StructurePlacementData().setRotation(rotation), pos);
    }
    
    @Override
    public boolean generate(final StructureManager structureManager, final IWorld world, final BlockPos pos, final BlockRotation rotation, final MutableIntBoundingBox boundingBox, final Random random) {
        final Structure structure7 = structureManager.getStructureOrBlank(this.location);
        final StructurePlacementData structurePlacementData8 = this.a(rotation, boundingBox);
        if (structure7.a(world, pos, structurePlacementData8, 18)) {
            final List<Structure.StructureBlockInfo> list9 = Structure.process(world, pos, structurePlacementData8, this.a(structureManager, pos, rotation, false));
            for (final Structure.StructureBlockInfo structureBlockInfo11 : list9) {
                this.a(world, structureBlockInfo11, pos, rotation, random, boundingBox);
            }
            return true;
        }
        return false;
    }
    
    protected StructurePlacementData a(final BlockRotation blockRotation, final MutableIntBoundingBox mutableIntBoundingBox) {
        final StructurePlacementData structurePlacementData3 = new StructurePlacementData();
        structurePlacementData3.setBoundingBox(mutableIntBoundingBox);
        structurePlacementData3.setRotation(blockRotation);
        structurePlacementData3.c(true);
        structurePlacementData3.setIgnoreEntities(false);
        structurePlacementData3.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        structurePlacementData3.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
        this.processors.forEach(structurePlacementData3::addProcessor);
        this.getProjection().getProcessors().forEach(structurePlacementData3::addProcessor);
        return structurePlacementData3;
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.SINGLE_POOL_ELEMENT;
    }
    
    public <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("location"), dynamicOps.createString(this.location.toString()), dynamicOps.createString("processors"), dynamicOps.createList((Stream)this.processors.stream().map(structureProcessor -> structureProcessor.<T>b(dynamicOps).getValue())))));
    }
    
    @Override
    public String toString() {
        return "Single[" + this.location + "]";
    }
}
