package net.minecraft.resource;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class SupplyingResourceReloadListener<T> implements ResourceReloadListener
{
    @Override
    public final CompletableFuture<Void> a(final Helper helper, final ResourceManager resourceManager, final Profiler prepareProfiler, final Profiler applyProfiler, final Executor prepareExecutor, final Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> this.load(resourceManager, prepareProfiler), prepareExecutor).thenCompose(helper::waitForAll).thenAcceptAsync(object -> this.apply(object, resourceManager, applyProfiler), applyExecutor);
    }
    
    protected abstract T load(final ResourceManager arg1, final Profiler arg2);
    
    protected abstract void apply(final T arg1, final ResourceManager arg2, final Profiler arg3);
}
