package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.function.Supplier;

public interface ReadableProfiler extends Profiler
{
    void push(final String arg1);
    
    void push(final Supplier<String> arg1);
    
    void pop();
    
    void swap(final String arg1);
    
    @Environment(EnvType.CLIENT)
    void swap(final Supplier<String> arg1);
    
    ProfileResult getResults();
}
