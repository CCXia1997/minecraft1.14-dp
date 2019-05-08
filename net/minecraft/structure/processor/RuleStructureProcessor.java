package net.minecraft.structure.processor;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockState;
import java.util.Random;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MathHelper;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ImmutableList;

public class RuleStructureProcessor extends StructureProcessor
{
    private final ImmutableList<StructureProcessorRule> rules;
    
    public RuleStructureProcessor(final List<StructureProcessorRule> list) {
        this.rules = ImmutableList.<StructureProcessorRule>copyOf(list);
    }
    
    public RuleStructureProcessor(final Dynamic<?> dynamic) {
        this(dynamic.get("rules").asList((Function)StructureProcessorRule::a));
    }
    
    @Nullable
    @Override
    public Structure.StructureBlockInfo process(final ViewableWorld world, final BlockPos pos, final Structure.StructureBlockInfo structureBlockInfo3, final Structure.StructureBlockInfo structureBlockInfo4, final StructurePlacementData placementData) {
        final Random random6 = new Random(MathHelper.hashCode(structureBlockInfo4.pos));
        final BlockState blockState7 = world.getBlockState(structureBlockInfo4.pos);
        for (final StructureProcessorRule structureProcessorRule9 : this.rules) {
            if (structureProcessorRule9.test(structureBlockInfo4.state, blockState7, random6)) {
                return new Structure.StructureBlockInfo(structureBlockInfo4.pos, structureProcessorRule9.getOutputState(), structureProcessorRule9.getTag());
            }
        }
        return structureBlockInfo4;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.f;
    }
    
    @Override
    protected <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("rules"), dynamicOps.createList((Stream)this.rules.stream().map(structureProcessorRule -> structureProcessorRule.<T>a(dynamicOps).getValue())))));
    }
}
