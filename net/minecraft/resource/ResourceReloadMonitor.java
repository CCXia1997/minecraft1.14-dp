package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;
import java.util.concurrent.CompletableFuture;

public interface ResourceReloadMonitor
{
    CompletableFuture<Void> whenComplete();
    
    @Environment(EnvType.CLIENT)
    float getProgress();
    
    @Environment(EnvType.CLIENT)
    boolean isLoadStageComplete();
    
    @Environment(EnvType.CLIENT)
    boolean isApplyStageComplete();
    
    @Environment(EnvType.CLIENT)
    void throwExceptions();
}
