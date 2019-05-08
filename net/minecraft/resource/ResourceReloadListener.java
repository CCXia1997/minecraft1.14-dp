package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public interface ResourceReloadListener
{
    CompletableFuture<Void> a(final Helper arg1, final ResourceManager arg2, final Profiler arg3, final Profiler arg4, final Executor arg5, final Executor arg6);
    
    public interface Helper
    {
         <T> CompletableFuture<T> waitForAll(final T arg1);
    }
}
