package net.minecraft.server.world;

import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.WorldSaveHandler;
import java.util.concurrent.Executor;
import net.minecraft.server.MinecraftServer;

public class SecondaryServerWorld extends ServerWorld
{
    public SecondaryServerWorld(final ServerWorld serverWorld, final MinecraftServer minecraftServer, final Executor executor, final WorldSaveHandler worldSaveHandler, final DimensionType dimensionType, final Profiler profiler, final WorldGenerationProgressListener worldGenerationProgressListener) {
        super(minecraftServer, executor, worldSaveHandler, new UnmodifiableLevelProperties(serverWorld.getLevelProperties()), dimensionType, profiler, worldGenerationProgressListener);
        serverWorld.getWorldBorder().addListener(new WorldBorderListener.WorldBorderSyncer(this.getWorldBorder()));
    }
    
    @Override
    protected void tickTime() {
    }
}
