package net.minecraft.world;

import java.util.stream.Stream;
import java.util.Set;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Random;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.Dimension;

public interface IWorld extends EntityView, ViewableWorld, ModifiableTestableWorld
{
    long getSeed();
    
    default float getMoonSize() {
        return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay())];
    }
    
    default float getSkyAngle(final float delta) {
        return this.getDimension().getSkyAngle(this.getLevelProperties().getTimeOfDay(), delta);
    }
    
    @Environment(EnvType.CLIENT)
    default int getMoonPhase() {
        return this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay());
    }
    
    TickScheduler<Block> getBlockTickScheduler();
    
    TickScheduler<Fluid> getFluidTickScheduler();
    
    World getWorld();
    
    LevelProperties getLevelProperties();
    
    LocalDifficulty getLocalDifficulty(final BlockPos arg1);
    
    default Difficulty getDifficulty() {
        return this.getLevelProperties().getDifficulty();
    }
    
    ChunkManager getChunkManager();
    
    default boolean isChunkLoaded(final int chunkX, final int chunkZ) {
        return this.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }
    
    Random getRandom();
    
    void updateNeighbors(final BlockPos arg1, final Block arg2);
    
    @Environment(EnvType.CLIENT)
    BlockPos getSpawnPos();
    
    void playSound(@Nullable final PlayerEntity arg1, final BlockPos arg2, final SoundEvent arg3, final SoundCategory arg4, final float arg5, final float arg6);
    
    void addParticle(final ParticleParameters arg1, final double arg2, final double arg3, final double arg4, final double arg5, final double arg6, final double arg7);
    
    void playLevelEvent(@Nullable final PlayerEntity arg1, final int arg2, final BlockPos arg3, final int arg4);
    
    default void playLevelEvent(final int integer1, final BlockPos blockPos, final int integer3) {
        this.playLevelEvent(null, integer1, blockPos, integer3);
    }
    
    default Stream<VoxelShape> getCollisionShapes(@Nullable final Entity entity, final VoxelShape entityBoundingBox, final Set<Entity> otherEntities) {
        return super.getCollisionShapes(entity, entityBoundingBox, otherEntities);
    }
    
    default boolean intersectsEntities(@Nullable final Entity except, final VoxelShape shape) {
        return super.intersectsEntities(except, shape);
    }
}
