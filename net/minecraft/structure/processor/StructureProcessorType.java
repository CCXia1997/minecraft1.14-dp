package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.DynamicDeserializer;

public interface StructureProcessorType extends DynamicDeserializer<StructureProcessor>
{
    public static final StructureProcessorType b = register("block_ignore", BlockIgnoreStructureProcessor::new);
    public static final StructureProcessorType c = register("block_rot", BlockRotStructureProcessor::new);
    public static final StructureProcessorType d = register("gravity", GravityStructureProcessor::new);
    public static final StructureProcessorType e = register("jigsaw_replacement", dynamic -> JigsawReplacementStructureProcessor.INSTANCE);
    public static final StructureProcessorType f = register("rule", RuleStructureProcessor::new);
    public static final StructureProcessorType g = register("nop", dynamic -> NopStructureProcessor.INSTANCE);
    
    default StructureProcessorType register(final String id, final StructureProcessorType processor) {
        return Registry.<StructureProcessorType>register(Registry.STRUCTURE_PROCESSOR, id, processor);
    }
}
