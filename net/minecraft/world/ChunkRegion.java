package net.minecraft.world;

import org.apache.logging.log4j.LogManager;
import java.util.Collections;
import net.minecraft.util.math.BoundingBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.dimension.Dimension;
import java.util.Random;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class ChunkRegion implements IWorld
{
    private static final Logger LOGGER;
    private final List<Chunk> chunks;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int width;
    private final ServerWorld world;
    private final long seed;
    private final int seaLevel;
    private final LevelProperties levelProperties;
    private final Random random;
    private final Dimension dimension;
    private final ChunkGeneratorConfig generatorSettings;
    private final TickScheduler<Block> blockTickScheduler;
    private final TickScheduler<Fluid> fluidTickScheduler;
    
    public ChunkRegion(final ServerWorld serverWorld, final List<Chunk> chunks) {
        this.blockTickScheduler = new MultiTickScheduler<Block>(blockPos -> this.getChunk(blockPos).getBlockTickScheduler());
        this.fluidTickScheduler = new MultiTickScheduler<Fluid>(blockPos -> this.getChunk(blockPos).getFluidTickScheduler());
        final int integer3 = MathHelper.floor(Math.sqrt(chunks.size()));
        if (integer3 * integer3 != chunks.size()) {
            throw new IllegalStateException("Cache size is not a square.");
        }
        final ChunkPos chunkPos4 = chunks.get(chunks.size() / 2).getPos();
        this.chunks = chunks;
        this.centerChunkX = chunkPos4.x;
        this.centerChunkZ = chunkPos4.z;
        this.width = integer3;
        this.world = serverWorld;
        this.seed = serverWorld.getSeed();
        this.generatorSettings = (ChunkGeneratorConfig)serverWorld.getChunkManager().getChunkGenerator().getConfig();
        this.seaLevel = serverWorld.getSeaLevel();
        this.levelProperties = serverWorld.getLevelProperties();
        this.random = serverWorld.getRandom();
        this.dimension = serverWorld.getDimension();
    }
    
    public int getCenterChunkX() {
        return this.centerChunkX;
    }
    
    public int getCenterChunkZ() {
        return this.centerChunkZ;
    }
    
    @Override
    public Chunk getChunk(final int chunkX, final int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
    }
    
    @Nullable
    @Override
    public Chunk getChunk(final int chunkX, final int chunkZ, final ChunkStatus leastStatus, final boolean create) {
        Chunk chunk5;
        if (this.isChunkLoaded(chunkX, chunkZ)) {
            final ChunkPos chunkPos6 = this.chunks.get(0).getPos();
            final int integer7 = chunkX - chunkPos6.x;
            final int integer8 = chunkZ - chunkPos6.z;
            chunk5 = this.chunks.get(integer7 + integer8 * this.width);
            if (chunk5.getStatus().isAtLeast(leastStatus)) {
                return chunk5;
            }
        }
        else {
            chunk5 = null;
        }
        if (!create) {
            return null;
        }
        final Chunk chunk6 = this.chunks.get(0);
        final Chunk chunk7 = this.chunks.get(this.chunks.size() - 1);
        ChunkRegion.LOGGER.error("Requested chunk : {} {}", chunkX, chunkZ);
        ChunkRegion.LOGGER.error("Region bounds : {} {} | {} {}", chunk6.getPos().x, chunk6.getPos().z, chunk7.getPos().x, chunk7.getPos().z);
        if (chunk5 != null) {
            throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", leastStatus, chunk5.getStatus(), chunkX, chunkZ));
        }
        throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", chunkX, chunkZ));
    }
    
    @Override
    public boolean isChunkLoaded(final int chunkX, final int chunkZ) {
        final Chunk chunk3 = this.chunks.get(0);
        final Chunk chunk4 = this.chunks.get(this.chunks.size() - 1);
        return chunkX >= chunk3.getPos().x && chunkX <= chunk4.getPos().x && chunkZ >= chunk3.getPos().z && chunkZ <= chunk4.getPos().z;
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4).getBlockState(pos);
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return this.getChunk(pos).getFluidState(pos);
    }
    
    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(final double x, final double y, final double z, final double maxDistance, final Predicate<Entity> targetPredicate) {
        return null;
    }
    
    @Override
    public int getAmbientDarkness() {
        return 0;
    }
    
    @Override
    public Biome getBiome(final BlockPos blockPos) {
        final Biome biome2 = this.getChunk(blockPos).getBiomeArray()[(blockPos.getX() & 0xF) | (blockPos.getZ() & 0xF) << 4];
        if (biome2 == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", blockPos));
        }
        return biome2;
    }
    
    @Override
    public int getLightLevel(final LightType type, final BlockPos blockPos) {
        return this.getChunkManager().getLightingProvider().get(type).getLightLevel(blockPos);
    }
    
    @Override
    public int getLightLevel(final BlockPos blockPos, final int integer) {
        return this.getChunk(blockPos).getLightLevel(blockPos, integer, this.getDimension().hasSkyLight());
    }
    
    @Override
    public boolean breakBlock(final BlockPos pos, final boolean boolean2) {
        final BlockState blockState3 = this.getBlockState(pos);
        if (blockState3.isAir()) {
            return false;
        }
        if (boolean2) {
            final BlockEntity blockEntity4 = blockState3.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(blockState3, this.world, pos, blockEntity4);
        }
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        final Chunk chunk2 = this.getChunk(pos);
        BlockEntity blockEntity3 = chunk2.getBlockEntity(pos);
        if (blockEntity3 != null) {
            return blockEntity3;
        }
        final CompoundTag compoundTag4 = chunk2.getBlockEntityTagAt(pos);
        if (compoundTag4 != null) {
            if ("DUMMY".equals(compoundTag4.getString("id"))) {
                final Block block5 = this.getBlockState(pos).getBlock();
                if (!(block5 instanceof BlockEntityProvider)) {
                    return null;
                }
                blockEntity3 = ((BlockEntityProvider)block5).createBlockEntity(this.world);
            }
            else {
                blockEntity3 = BlockEntity.createFromTag(compoundTag4);
            }
            if (blockEntity3 != null) {
                chunk2.setBlockEntity(pos, blockEntity3);
                return blockEntity3;
            }
        }
        if (chunk2.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
            ChunkRegion.LOGGER.warn("Tried to access a block entity before it was created. {}", pos);
        }
        return null;
    }
    
    @Override
    public boolean setBlockState(final BlockPos pos, final BlockState state, final int flags) {
        final Chunk chunk4 = this.getChunk(pos);
        final BlockState blockState5 = chunk4.setBlockState(pos, state, false);
        if (blockState5 != null) {
            this.world.onBlockChanged(pos, blockState5, state);
        }
        final Block block6 = state.getBlock();
        if (block6.hasBlockEntity()) {
            if (chunk4.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                chunk4.setBlockEntity(pos, ((BlockEntityProvider)block6).createBlockEntity(this));
            }
            else {
                final CompoundTag compoundTag7 = new CompoundTag();
                compoundTag7.putInt("x", pos.getX());
                compoundTag7.putInt("y", pos.getY());
                compoundTag7.putInt("z", pos.getZ());
                compoundTag7.putString("id", "DUMMY");
                chunk4.addPendingBlockEntityTag(compoundTag7);
            }
        }
        else if (blockState5 != null && blockState5.getBlock().hasBlockEntity()) {
            chunk4.removeBlockEntity(pos);
        }
        if (state.shouldPostProcess(this, pos)) {
            this.markBlockForPostProcessing(pos);
        }
        return true;
    }
    
    private void markBlockForPostProcessing(final BlockPos blockPos) {
        this.getChunk(blockPos).markBlockForPostProcessing(blockPos);
    }
    
    @Override
    public boolean spawnEntity(final Entity entity) {
        final int integer2 = MathHelper.floor(entity.x / 16.0);
        final int integer3 = MathHelper.floor(entity.z / 16.0);
        this.getChunk(integer2, integer3).addEntity(entity);
        return true;
    }
    
    @Override
    public boolean clearBlockState(final BlockPos blockPos, final boolean boolean2) {
        return this.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
    }
    
    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }
    
    @Override
    public boolean intersectsEntities(@Nullable final Entity except, final VoxelShape shape) {
        return true;
    }
    
    @Override
    public int getEmittedStrongRedstonePower(final BlockPos pos, final Direction direction) {
        return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
    }
    
    @Override
    public boolean isClient() {
        return false;
    }
    
    @Deprecated
    @Override
    public ServerWorld getWorld() {
        return this.world;
    }
    
    @Override
    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }
    
    @Override
    public LocalDifficulty getLocalDifficulty(final BlockPos pos) {
        if (!this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.getMoonSize());
    }
    
    @Override
    public ChunkManager getChunkManager() {
        return this.world.getChunkManager();
    }
    
    @Override
    public long getSeed() {
        return this.seed;
    }
    
    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }
    
    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }
    
    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }
    
    @Override
    public Random getRandom() {
        return this.random;
    }
    
    @Override
    public void updateNeighbors(final BlockPos blockPos, final Block block) {
    }
    
    @Override
    public int getTop(final Heightmap.Type type, final int x, final int integer3) {
        return this.getChunk(x >> 4, integer3 >> 4).sampleHeightmap(type, x & 0xF, integer3 & 0xF) + 1;
    }
    
    @Override
    public void playSound(@Nullable final PlayerEntity playerEntity, final BlockPos blockPos, final SoundEvent soundEvent, final SoundCategory soundCategory, final float float5, final float float6) {
    }
    
    @Override
    public void addParticle(final ParticleParameters parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
    }
    
    @Override
    public void playLevelEvent(@Nullable final PlayerEntity playerEntity, final int integer2, final BlockPos blockPos, final int integer4) {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public BlockPos getSpawnPos() {
        return this.world.getSpawnPos();
    }
    
    @Override
    public Dimension getDimension() {
        return this.dimension;
    }
    
    @Override
    public boolean testBlockState(final BlockPos blockPos, final Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(blockPos));
    }
    
    @Override
    public <T extends Entity> List<T> getEntities(final Class<? extends T> entityClass, final BoundingBox box, @Nullable final Predicate<? super T> predicate) {
        return Collections.<T>emptyList();
    }
    
    @Override
    public List<Entity> getEntities(@Nullable final Entity except, final BoundingBox box, @Nullable final Predicate<? super Entity> predicate) {
        return Collections.<Entity>emptyList();
    }
    
    @Override
    public List<PlayerEntity> getPlayers() {
        return Collections.<PlayerEntity>emptyList();
    }
    
    @Override
    public BlockPos getTopPosition(final Heightmap.Type type, final BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getTop(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
