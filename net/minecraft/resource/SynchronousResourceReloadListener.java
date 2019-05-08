package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener extends ResourceReloadListener
{
    default CompletableFuture<Void> a(final Helper helper, final ResourceManager resourceManager, final Profiler prepareProfiler, final Profiler applyProfiler, final Executor prepareExecutor, final Executor applyExecutor) {
        return helper.<net.minecraft.util.Void>waitForAll(net.minecraft.util.Void.INSTANCE).thenRunAsync(() -> this.apply(resourceManager), applyExecutor);
    }
    
    void apply(final ResourceManager arg1);
}
