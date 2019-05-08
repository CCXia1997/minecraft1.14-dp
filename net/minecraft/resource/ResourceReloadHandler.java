package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import net.minecraft.util.SystemUtil;
import java.util.concurrent.CompletionStage;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.DummyProfiler;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.List;
import net.minecraft.util.Void;
import java.util.concurrent.CompletableFuture;

public class ResourceReloadHandler<S> implements ResourceReloadMonitor
{
    protected final ResourceManager resourceManager;
    protected final CompletableFuture<Void> loadStageFuture;
    protected final CompletableFuture<List<S>> applyStageFuture;
    private final Set<ResourceReloadListener> loadStageLoaders;
    private final int listenerCount;
    private int f;
    private int g;
    private final AtomicInteger h;
    private final AtomicInteger i;
    
    public static ResourceReloadHandler<java.lang.Void> create(final ResourceManager resourceManager, final List<ResourceReloadListener> list, final Executor executor3, final Executor executor4, final CompletableFuture<Void> completableFuture) {
        return new ResourceReloadHandler<java.lang.Void>(executor3, executor4, resourceManager, list, (helper, resourceManager, resourceReloadListener, executor5, executor6) -> resourceReloadListener.a(helper, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor3, executor6), completableFuture);
    }
    
    protected ResourceReloadHandler(final Executor executor1, final Executor executor2, final ResourceManager resourceManager, final List<ResourceReloadListener> listeners, final a<S> a, final CompletableFuture<Void> completableFuture) {
        this.loadStageFuture = new CompletableFuture<Void>();
        this.h = new AtomicInteger();
        this.i = new AtomicInteger();
        this.resourceManager = resourceManager;
        this.listenerCount = listeners.size();
        this.h.incrementAndGet();
        completableFuture.thenRun(this.i::incrementAndGet);
        final List<CompletableFuture<S>> list7 = new ArrayList<CompletableFuture<S>>();
        CompletableFuture<?> completableFuture2 = completableFuture;
        this.loadStageLoaders = Sets.newHashSet(listeners);
        for (final ResourceReloadListener resourceReloadListener10 : listeners) {
            final CompletableFuture<?> completableFuture3 = completableFuture2;
            final CompletableFuture<S> completableFuture4 = a.create(new ResourceReloadListener.Helper() {
                @Override
                public <T> CompletableFuture<T> waitForAll(final T passedObject) {
                    executor2.execute(() -> {
                        ResourceReloadHandler.this.loadStageLoaders.remove(resourceReloadListener10);
                        if (ResourceReloadHandler.this.loadStageLoaders.isEmpty()) {
                            ResourceReloadHandler.this.loadStageFuture.complete(Void.INSTANCE);
                        }
                        return;
                    });
                    return ResourceReloadHandler.this.loadStageFuture.<Object, T>thenCombine(completableFuture3, (void2, object3) -> passedObject);
                }
            }, resourceManager, resourceReloadListener10, runnable -> {
                this.h.incrementAndGet();
                executor1.execute(() -> {
                    runnable.run();
                    this.i.incrementAndGet();
                });
                return;
            }, runnable -> {
                ++this.f;
                executor2.execute(() -> {
                    runnable.run();
                    ++this.g;
                });
                return;
            });
            list7.add(completableFuture4);
            completableFuture2 = completableFuture4;
        }
        this.applyStageFuture = SystemUtil.<S>thenCombine(list7);
    }
    
    @Override
    public CompletableFuture<Void> whenComplete() {
        return this.applyStageFuture.<Void>thenApply(list -> Void.INSTANCE);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getProgress() {
        final int integer1 = this.listenerCount - this.loadStageLoaders.size();
        final float float2 = (float)(this.i.get() * 2 + this.g * 2 + integer1 * 1);
        final float float3 = (float)(this.h.get() * 2 + this.f * 2 + this.listenerCount * 1);
        return float2 / float3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean isLoadStageComplete() {
        return this.loadStageFuture.isDone();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean isApplyStageComplete() {
        return this.applyStageFuture.isDone();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void throwExceptions() {
        if (this.applyStageFuture.isCompletedExceptionally()) {
            this.applyStageFuture.join();
        }
    }
    
    public interface a<S>
    {
        CompletableFuture<S> create(final ResourceReloadListener.Helper arg1, final ResourceManager arg2, final ResourceReloadListener arg3, final Executor arg4, final Executor arg5);
    }
}
