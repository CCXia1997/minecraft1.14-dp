package net.minecraft.state.property;

import java.util.Optional;
import java.util.Collection;

public interface Property<T extends Comparable<T>>
{
    String getName();
    
    Collection<T> getValues();
    
    Class<T> getValueClass();
    
    Optional<T> getValue(final String arg1);
    
    String getValueAsString(final T arg1);
}
