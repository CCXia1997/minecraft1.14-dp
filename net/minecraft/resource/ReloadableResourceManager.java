package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.concurrent.Executor;

public interface ReloadableResourceManager extends ResourceManager
{
    CompletableFuture<Void> beginReload(final Executor arg1, final Executor arg2, final List<ResourcePack> arg3, final CompletableFuture<Void> arg4);
    
    @Environment(EnvType.CLIENT)
    ResourceReloadMonitor beginInitialMonitoredReload(final Executor arg1, final Executor arg2, final CompletableFuture<Void> arg3);
    
    @Environment(EnvType.CLIENT)
    ResourceReloadMonitor beginMonitoredReload(final Executor arg1, final Executor arg2, final CompletableFuture<Void> arg3, final List<ResourcePack> arg4);
    
    void registerListener(final ResourceReloadListener arg1);
}
