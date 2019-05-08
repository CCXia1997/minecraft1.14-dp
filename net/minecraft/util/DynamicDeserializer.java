package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import org.apache.logging.log4j.Logger;

public interface DynamicDeserializer<T>
{
    public static final Logger LOGGER = LogManager.getLogger();
    
    T deserialize(final Dynamic<?> arg1);
    
    default <T, V, U extends DynamicDeserializer<V>> V deserialize(final Dynamic<T> dynamic, final Registry<U> dynamic, final String registry, final V typeFieldName) {
        final U dynamicDeserializer5 = dynamic.get(new Identifier(dynamic.get(registry).asString("")));
        V object6;
        if (dynamicDeserializer5 != null) {
            object6 = dynamicDeserializer5.deserialize(dynamic);
        }
        else {
            DynamicDeserializer.LOGGER.error("Unknown type {}, replacing with {}", dynamic.get(registry).asString(""), typeFieldName);
            object6 = typeFieldName;
        }
        return object6;
    }
}
