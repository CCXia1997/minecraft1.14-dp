package net.minecraft.state;

import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.property.Property;
import org.apache.logging.log4j.Logger;

public interface PropertyContainer<C>
{
    public static final Logger LOGGER = LogManager.getLogger();
    
     <T extends Comparable<T>> T get(final Property<T> arg1);
    
     <T extends Comparable<T>, V extends T> C with(final Property<T> arg1, final V arg2);
    
    ImmutableMap<Property<?>, Comparable<?>> getEntries();
    
    default <T extends Comparable<T>> String getValueAsString(final Property<T> property, final Comparable<?> comparable) {
        return property.getValueAsString((T)comparable);
    }
    
    default <S extends PropertyContainer<S>, T extends Comparable<T>> S deserialize(final S state, final Property<T> property, final String propertyName, final String input, final String value) {
        final Optional<T> optional6 = property.getValue(value);
        if (optional6.isPresent()) {
            return state.<T, T>with(property, optional6.get());
        }
        PropertyContainer.LOGGER.warn("Unable to read property: {} with value: {} for input: {}", propertyName, value, input);
        return state;
    }
}
