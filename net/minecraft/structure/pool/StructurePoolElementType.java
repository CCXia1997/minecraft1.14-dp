package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.DynamicDeserializer;

public interface StructurePoolElementType extends DynamicDeserializer<StructurePoolElement>
{
    public static final StructurePoolElementType SINGLE_POOL_ELEMENT = register("single_pool_element", SinglePoolElement::new);
    public static final StructurePoolElementType LIST_POOL_ELEMENT = register("list_pool_element", ListPoolElement::new);
    public static final StructurePoolElementType FEATURE_POOL_ELEMENT = register("feature_pool_element", FeaturePoolElement::new);
    public static final StructurePoolElementType EMPTY_POOL_ELEMENT = register("empty_pool_element", dynamic -> EmptyPoolElement.INSTANCE);
    
    default StructurePoolElementType register(final String string, final StructurePoolElementType structurePoolElementType) {
        return Registry.<StructurePoolElementType>register(Registry.STRUCTURE_POOL_ELEMENT, string, structurePoolElementType);
    }
}
