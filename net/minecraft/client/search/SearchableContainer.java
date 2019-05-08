package net.minecraft.client.search;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SearchableContainer<T> extends Searchable<T>
{
    void add(final T arg1);
    
    void clear();
    
    void reload();
}
