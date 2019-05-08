package net.minecraft.world;

import org.apache.logging.log4j.LogManager;
import net.minecraft.tag.TagManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.map.MapState;
import net.minecraft.entity.EntityType;
import java.util.function.Predicate;
import java.io.IOException;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.block.Material;
import net.minecraft.util.math.BoundingBox;
import java.util.function.Consumer;
import net.minecraft.block.entity.BlockEntityType;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.util.Tickable;
import org.apache.logging.log4j.util.Supplier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.block.Blocks;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Block;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.block.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import java.util.function.BiFunction;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import java.util.List;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.Logger;

public abstract class World implements ExtendedBlockView, IWorld, AutoCloseable
{
    protected static final Logger LOGGER;
    private static final Direction[] DIRECTIONS;
    public final List<BlockEntity> blockEntities;
    public final List<BlockEntity> tickingBlockEntities;
    protected final List<BlockEntity> pendingBlockEntities;
    protected final List<BlockEntity> unloadedBlockEntities;
    private final long unusedWhite = 16777215L;
    private final Thread thread;
    private int ambientDarkness;
    protected int lcgBlockSeed;
    protected final int unusedIncrement = 1013904223;
    protected float rainGradientPrev;
    protected float rainGradient;
    protected float thunderGradientPrev;
    protected float thunderGradient;
    private int ticksSinceLightning;
    public final Random random;
    public final Dimension dimension;
    protected final ChunkManager chunkManager;
    protected final LevelProperties properties;
    private final Profiler profiler;
    public final boolean isClient;
    protected boolean iteratingTickingBlockEntities;
    private final WorldBorder border;
    
    protected World(final LevelProperties levelProperties, final DimensionType dimensionType, final BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, final Profiler profiler, final boolean isClient) {
        this.blockEntities = Lists.newArrayList();
        this.tickingBlockEntities = Lists.newArrayList();
        this.pendingBlockEntities = Lists.newArrayList();
        this.unloadedBlockEntities = Lists.newArrayList();
        this.lcgBlockSeed = new Random().nextInt();
        this.random = new Random();
        this.profiler = profiler;
        this.properties = levelProperties;
        this.dimension = dimensionType.create(this);
        this.chunkManager = chunkManagerProvider.apply(this, this.dimension);
        this.isClient = isClient;
        this.border = this.dimension.createWorldBorder();
        this.thread = Thread.currentThread();
    }
    
    @Override
    public Biome getBiome(final BlockPos blockPos) {
        final ChunkManager chunkManager2 = this.getChunkManager();
        final WorldChunk worldChunk3 = chunkManager2.getWorldChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, false);
        if (worldChunk3 != null) {
            return worldChunk3.getBiome(blockPos);
        }
        final ChunkGenerator<?> chunkGenerator4 = this.getChunkManager().getChunkGenerator();
        if (chunkGenerator4 == null) {
            return Biomes.c;
        }
        return chunkGenerator4.getBiomeSource().getBiome(blockPos);
    }
    
    @Override
    public boolean isClient() {
        return this.isClient;
    }
    
    @Nullable
    public MinecraftServer getServer() {
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    public void setDefaultSpawnClient() {
        this.setSpawnPos(new BlockPos(8, 64, 8));
    }
    
    public BlockState getTopNonAirState(final BlockPos blockPos) {
        BlockPos blockPos2;
        for (blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ()); !this.isAir(blockPos2.up()); blockPos2 = blockPos2.up()) {}
        return this.getBlockState(blockPos2);
    }
    
    public static boolean isValid(final BlockPos pos) {
        return !isHeightInvalid(pos) && pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
    }
    
    public static boolean isHeightInvalid(final BlockPos pos) {
        return isHeightInvalid(pos.getY());
    }
    
    public static boolean isHeightInvalid(final int y) {
        return y < 0 || y >= 256;
    }
    
    public WorldChunk getWorldChunk(final BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }
    
    @Override
    public WorldChunk getChunk(final int chunkX, final int chunkZ) {
        return (WorldChunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL);
    }
    
    @Override
    public Chunk getChunk(final int chunkX, final int chunkZ, final ChunkStatus leastStatus, final boolean create) {
        final Chunk chunk5 = this.chunkManager.getChunk(chunkX, chunkZ, leastStatus, create);
        if (chunk5 == null && create) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return chunk5;
    }
    
    @Override
    public boolean setBlockState(final BlockPos pos, final BlockState state, final int flags) {
        if (isHeightInvalid(pos)) {
            return false;
        }
        if (!this.isClient && this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        }
        final WorldChunk worldChunk4 = this.getWorldChunk(pos);
        final Block block5 = state.getBlock();
        final BlockState blockState6 = worldChunk4.setBlockState(pos, state, (flags & 0x40) != 0x0);
        if (blockState6 != null) {
            final BlockState blockState7 = this.getBlockState(pos);
            if (blockState7 != blockState6 && (blockState7.getLightSubtracted(this, pos) != blockState6.getLightSubtracted(this, pos) || blockState7.getLuminance() != blockState6.getLuminance() || blockState7.g() || blockState6.g())) {
                this.profiler.push("queueCheckLight");
                this.getChunkManager().getLightingProvider().enqueueLightUpdate(pos);
                this.profiler.pop();
            }
            if (blockState7 == state) {
                if (blockState6 != blockState7) {
                    this.scheduleBlockRender(pos);
                }
                if ((flags & 0x2) != 0x0 && (!this.isClient || (flags & 0x4) == 0x0) && (this.isClient || (worldChunk4.getLevelType() != null && worldChunk4.getLevelType().isAfter(ChunkHolder.LevelType.TICKING)))) {
                    this.updateListeners(pos, blockState6, state, flags);
                }
                if (!this.isClient && (flags & 0x1) != 0x0) {
                    this.updateNeighbors(pos, blockState6.getBlock());
                    if (state.hasComparatorOutput()) {
                        this.updateHorizontalAdjacent(pos, block5);
                    }
                }
                if ((flags & 0x10) == 0x0) {
                    final int integer8 = flags & 0xFFFFFFFE;
                    blockState6.b(this, pos, integer8);
                    state.updateNeighborStates(this, pos, integer8);
                    state.b(this, pos, integer8);
                }
                this.onBlockChanged(pos, blockState6, blockState7);
            }
            return true;
        }
        return false;
    }
    
    public void onBlockChanged(final BlockPos pos, final BlockState oldBlock, final BlockState newBlock) {
    }
    
    @Override
    public boolean clearBlockState(final BlockPos blockPos, final boolean boolean2) {
        final FluidState fluidState3 = this.getFluidState(blockPos);
        return this.setBlockState(blockPos, fluidState3.getBlockState(), 0x3 | (boolean2 ? 64 : 0));
    }
    
    @Override
    public boolean breakBlock(final BlockPos pos, final boolean boolean2) {
        final BlockState blockState3 = this.getBlockState(pos);
        if (blockState3.isAir()) {
            return false;
        }
        final FluidState fluidState4 = this.getFluidState(pos);
        this.playLevelEvent(2001, pos, Block.getRawIdFromState(blockState3));
        if (boolean2) {
            final BlockEntity blockEntity5 = blockState3.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(blockState3, this, pos, blockEntity5);
        }
        return this.setBlockState(pos, fluidState4.getBlockState(), 3);
    }
    
    public boolean setBlockState(final BlockPos pos, final BlockState blockState) {
        return this.setBlockState(pos, blockState, 3);
    }
    
    public abstract void updateListeners(final BlockPos arg1, final BlockState arg2, final BlockState arg3, final int arg4);
    
    @Override
    public void updateNeighbors(final BlockPos blockPos, final Block block) {
        if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.updateNeighborsAlways(blockPos, block);
        }
    }
    
    public void scheduleBlockRender(final BlockPos blockPos) {
    }
    
    public void updateNeighborsAlways(final BlockPos pos, final Block block) {
        this.updateNeighbor(pos.west(), block, pos);
        this.updateNeighbor(pos.east(), block, pos);
        this.updateNeighbor(pos.down(), block, pos);
        this.updateNeighbor(pos.up(), block, pos);
        this.updateNeighbor(pos.north(), block, pos);
        this.updateNeighbor(pos.south(), block, pos);
    }
    
    public void updateNeighborsExcept(final BlockPos pos, final Block sourceBlock, final Direction direction) {
        if (direction != Direction.WEST) {
            this.updateNeighbor(pos.west(), sourceBlock, pos);
        }
        if (direction != Direction.EAST) {
            this.updateNeighbor(pos.east(), sourceBlock, pos);
        }
        if (direction != Direction.DOWN) {
            this.updateNeighbor(pos.down(), sourceBlock, pos);
        }
        if (direction != Direction.UP) {
            this.updateNeighbor(pos.up(), sourceBlock, pos);
        }
        if (direction != Direction.NORTH) {
            this.updateNeighbor(pos.north(), sourceBlock, pos);
        }
        if (direction != Direction.SOUTH) {
            this.updateNeighbor(pos.south(), sourceBlock, pos);
        }
    }
    
    public void updateNeighbor(final BlockPos pos, final Block sourceBlock, final BlockPos blockPos3) {
        if (this.isClient) {
            return;
        }
        final BlockState blockState4 = this.getBlockState(pos);
        try {
            blockState4.neighborUpdate(this, pos, sourceBlock, blockPos3, false);
        }
        catch (Throwable throwable5) {
            final CrashReport crashReport6 = CrashReport.create(throwable5, "Exception while updating neighbours");
            final CrashReportSection crashReportSection7 = crashReport6.addElement("Block being updated");
            crashReportSection7.add("Source block type", () -> {
                try {
                    return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
                }
                catch (Throwable throwable6) {
                    return "ID #" + Registry.BLOCK.getId(sourceBlock);
                }
            });
            CrashReportSection.addBlockInfo(crashReportSection7, pos, blockState4);
            throw new CrashException(crashReport6);
        }
    }
    
    @Override
    public int getLightLevel(BlockPos blockPos, final int integer) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        if (blockPos.getY() < 0) {
            return 0;
        }
        if (blockPos.getY() >= 256) {
            blockPos = new BlockPos(blockPos.getX(), 255, blockPos.getZ());
        }
        return this.getWorldChunk(blockPos).getLightLevel(blockPos, integer);
    }
    
    @Override
    public int getTop(final Heightmap.Type type, final int x, final int integer3) {
        int integer4;
        if (x < -30000000 || integer3 < -30000000 || x >= 30000000 || integer3 >= 30000000) {
            integer4 = this.getSeaLevel() + 1;
        }
        else if (this.isChunkLoaded(x >> 4, integer3 >> 4)) {
            integer4 = this.getChunk(x >> 4, integer3 >> 4).sampleHeightmap(type, x & 0xF, integer3 & 0xF) + 1;
        }
        else {
            integer4 = 0;
        }
        return integer4;
    }
    
    @Override
    public int getLightLevel(final LightType type, final BlockPos blockPos) {
        return this.getChunkManager().getLightingProvider().get(type).getLightLevel(blockPos);
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        if (isHeightInvalid(pos)) {
            return Blocks.kS.getDefaultState();
        }
        final WorldChunk worldChunk2 = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        return worldChunk2.getBlockState(pos);
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        if (isHeightInvalid(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        final WorldChunk worldChunk2 = this.getWorldChunk(pos);
        return worldChunk2.getFluidState(pos);
    }
    
    public boolean isDaylight() {
        return this.ambientDarkness < 4;
    }
    
    @Override
    public void playSound(@Nullable final PlayerEntity playerEntity, final BlockPos blockPos, final SoundEvent soundEvent, final SoundCategory soundCategory, final float float5, final float float6) {
        this.playSound(playerEntity, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, soundEvent, soundCategory, float5, float6);
    }
    
    public abstract void playSound(@Nullable final PlayerEntity arg1, final double arg2, final double arg3, final double arg4, final SoundEvent arg5, final SoundCategory arg6, final float arg7, final float arg8);
    
    public abstract void playSoundFromEntity(@Nullable final PlayerEntity arg1, final Entity arg2, final SoundEvent arg3, final SoundCategory arg4, final float arg5, final float arg6);
    
    public void playSound(final double x, final double y, final double z, final SoundEvent sound, final SoundCategory soundCategory, final float float9, final float float10, final boolean boolean11) {
    }
    
    @Override
    public void addParticle(final ParticleParameters parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
    }
    
    @Environment(EnvType.CLIENT)
    public void addParticle(final ParticleParameters parameters, final boolean alwaysSpawn, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
    }
    
    public void addImportantParticle(final ParticleParameters parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
    }
    
    public void addImportantParticle(final ParticleParameters parameters, final boolean alwaysSpawn, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
    }
    
    @Environment(EnvType.CLIENT)
    public float getAmbientLight(final float delta) {
        final float float2 = this.getSkyAngle(delta);
        float float3 = 1.0f - (MathHelper.cos(float2 * 6.2831855f) * 2.0f + 0.2f);
        float3 = MathHelper.clamp(float3, 0.0f, 1.0f);
        float3 = 1.0f - float3;
        float3 *= (float)(1.0 - this.getRainGradient(delta) * 5.0f / 16.0);
        float3 *= (float)(1.0 - this.getThunderGradient(delta) * 5.0f / 16.0);
        return float3 * 0.8f + 0.2f;
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3d getSkyColor(final BlockPos blockPos, final float float2) {
        final float float3 = this.getSkyAngle(float2);
        float float4 = MathHelper.cos(float3 * 6.2831855f) * 2.0f + 0.5f;
        float4 = MathHelper.clamp(float4, 0.0f, 1.0f);
        final Biome biome5 = this.getBiome(blockPos);
        final float float5 = biome5.getTemperature(blockPos);
        final int integer7 = biome5.getSkyColor(float5);
        float float6 = (integer7 >> 16 & 0xFF) / 255.0f;
        float float7 = (integer7 >> 8 & 0xFF) / 255.0f;
        float float8 = (integer7 & 0xFF) / 255.0f;
        float6 *= float4;
        float7 *= float4;
        float8 *= float4;
        final float float9 = this.getRainGradient(float2);
        if (float9 > 0.0f) {
            final float float10 = (float6 * 0.3f + float7 * 0.59f + float8 * 0.11f) * 0.6f;
            final float float11 = 1.0f - float9 * 0.75f;
            float6 = float6 * float11 + float10 * (1.0f - float11);
            float7 = float7 * float11 + float10 * (1.0f - float11);
            float8 = float8 * float11 + float10 * (1.0f - float11);
        }
        final float float10 = this.getThunderGradient(float2);
        if (float10 > 0.0f) {
            final float float11 = (float6 * 0.3f + float7 * 0.59f + float8 * 0.11f) * 0.2f;
            final float float12 = 1.0f - float10 * 0.75f;
            float6 = float6 * float12 + float11 * (1.0f - float12);
            float7 = float7 * float12 + float11 * (1.0f - float12);
            float8 = float8 * float12 + float11 * (1.0f - float12);
        }
        if (this.ticksSinceLightning > 0) {
            float float11 = this.ticksSinceLightning - float2;
            if (float11 > 1.0f) {
                float11 = 1.0f;
            }
            float11 *= 0.45f;
            float6 = float6 * (1.0f - float11) + 0.8f * float11;
            float7 = float7 * (1.0f - float11) + 0.8f * float11;
            float8 = float8 * (1.0f - float11) + 1.0f * float11;
        }
        return new Vec3d(float6, float7, float8);
    }
    
    public float b(final float float1) {
        final float float2 = this.getSkyAngle(float1);
        return float2 * 6.2831855f;
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3d getCloudColor(final float float1) {
        final float float2 = this.getSkyAngle(float1);
        float float3 = MathHelper.cos(float2 * 6.2831855f) * 2.0f + 0.5f;
        float3 = MathHelper.clamp(float3, 0.0f, 1.0f);
        float float4 = 1.0f;
        float float5 = 1.0f;
        float float6 = 1.0f;
        final float float7 = this.getRainGradient(float1);
        if (float7 > 0.0f) {
            final float float8 = (float4 * 0.3f + float5 * 0.59f + float6 * 0.11f) * 0.6f;
            final float float9 = 1.0f - float7 * 0.95f;
            float4 = float4 * float9 + float8 * (1.0f - float9);
            float5 = float5 * float9 + float8 * (1.0f - float9);
            float6 = float6 * float9 + float8 * (1.0f - float9);
        }
        float4 *= float3 * 0.9f + 0.1f;
        float5 *= float3 * 0.9f + 0.1f;
        float6 *= float3 * 0.85f + 0.15f;
        final float float8 = this.getThunderGradient(float1);
        if (float8 > 0.0f) {
            final float float9 = (float4 * 0.3f + float5 * 0.59f + float6 * 0.11f) * 0.2f;
            final float float10 = 1.0f - float8 * 0.95f;
            float4 = float4 * float10 + float9 * (1.0f - float10);
            float5 = float5 * float10 + float9 * (1.0f - float10);
            float6 = float6 * float10 + float9 * (1.0f - float10);
        }
        return new Vec3d(float4, float5, float6);
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3d getFogColor(final float float1) {
        final float float2 = this.getSkyAngle(float1);
        return this.dimension.getFogColor(float2, float1);
    }
    
    @Environment(EnvType.CLIENT)
    public float getStarsBrightness(final float float1) {
        final float float2 = this.getSkyAngle(float1);
        float float3 = 1.0f - (MathHelper.cos(float2 * 6.2831855f) * 2.0f + 0.25f);
        float3 = MathHelper.clamp(float3, 0.0f, 1.0f);
        return float3 * float3 * 0.5f;
    }
    
    public boolean addBlockEntity(final BlockEntity blockEntity) {
        if (this.iteratingTickingBlockEntities) {
            World.LOGGER.error("Adding block entity while ticking: {} @ {}", new Supplier[] { () -> Registry.BLOCK_ENTITY.getId(blockEntity.getType()), blockEntity::getPos });
        }
        final boolean boolean2 = this.blockEntities.add(blockEntity);
        if (boolean2 && blockEntity instanceof Tickable) {
            this.tickingBlockEntities.add(blockEntity);
        }
        if (this.isClient) {
            final BlockPos blockPos3 = blockEntity.getPos();
            final BlockState blockState4 = this.getBlockState(blockPos3);
            this.updateListeners(blockPos3, blockState4, blockState4, 2);
        }
        return boolean2;
    }
    
    public void addBlockEntities(final Collection<BlockEntity> collection) {
        if (this.iteratingTickingBlockEntities) {
            this.pendingBlockEntities.addAll(collection);
        }
        else {
            for (final BlockEntity blockEntity3 : collection) {
                this.addBlockEntity(blockEntity3);
            }
        }
    }
    
    public void tickBlockEntities() {
        final Profiler profiler1 = this.getProfiler();
        profiler1.push("blockEntities");
        if (!this.unloadedBlockEntities.isEmpty()) {
            this.tickingBlockEntities.removeAll(this.unloadedBlockEntities);
            this.blockEntities.removeAll(this.unloadedBlockEntities);
            this.unloadedBlockEntities.clear();
        }
        this.iteratingTickingBlockEntities = true;
        final Iterator<BlockEntity> iterator2 = this.tickingBlockEntities.iterator();
        while (iterator2.hasNext()) {
            final BlockEntity blockEntity3 = iterator2.next();
            if (!blockEntity3.isInvalid() && blockEntity3.hasWorld()) {
                final BlockPos blockPos4 = blockEntity3.getPos();
                if (this.isBlockLoaded(blockPos4) && this.getWorldBorder().contains(blockPos4)) {
                    try {
                        profiler1.push(() -> String.valueOf(BlockEntityType.getId(blockEntity3.getType())));
                        ((Tickable)blockEntity3).tick();
                        profiler1.pop();
                    }
                    catch (Throwable throwable5) {
                        final CrashReport crashReport6 = CrashReport.create(throwable5, "Ticking block entity");
                        final CrashReportSection crashReportSection7 = crashReport6.addElement("Block entity being ticked");
                        blockEntity3.populateCrashReport(crashReportSection7);
                        throw new CrashException(crashReport6);
                    }
                }
            }
            if (blockEntity3.isInvalid()) {
                iterator2.remove();
                this.blockEntities.remove(blockEntity3);
                if (!this.isBlockLoaded(blockEntity3.getPos())) {
                    continue;
                }
                this.getWorldChunk(blockEntity3.getPos()).removeBlockEntity(blockEntity3.getPos());
            }
        }
        this.iteratingTickingBlockEntities = false;
        profiler1.swap("pendingBlockEntities");
        if (!this.pendingBlockEntities.isEmpty()) {
            for (int integer3 = 0; integer3 < this.pendingBlockEntities.size(); ++integer3) {
                final BlockEntity blockEntity4 = this.pendingBlockEntities.get(integer3);
                if (!blockEntity4.isInvalid()) {
                    if (!this.blockEntities.contains(blockEntity4)) {
                        this.addBlockEntity(blockEntity4);
                    }
                    if (this.isBlockLoaded(blockEntity4.getPos())) {
                        final WorldChunk worldChunk5 = this.getWorldChunk(blockEntity4.getPos());
                        final BlockState blockState6 = worldChunk5.getBlockState(blockEntity4.getPos());
                        worldChunk5.setBlockEntity(blockEntity4.getPos(), blockEntity4);
                        this.updateListeners(blockEntity4.getPos(), blockState6, blockState6, 3);
                    }
                }
            }
            this.pendingBlockEntities.clear();
        }
        profiler1.pop();
    }
    
    public void tickEntity(final Consumer<Entity> consumer, final Entity entity) {
        try {
            consumer.accept(entity);
        }
        catch (Throwable throwable3) {
            final CrashReport crashReport4 = CrashReport.create(throwable3, "Ticking entity");
            final CrashReportSection crashReportSection5 = crashReport4.addElement("Entity being ticked");
            entity.populateCrashReport(crashReportSection5);
            throw new CrashException(crashReport4);
        }
    }
    
    public boolean isAreaNotEmpty(final BoundingBox boundingBox) {
        final int integer2 = MathHelper.floor(boundingBox.minX);
        final int integer3 = MathHelper.ceil(boundingBox.maxX);
        final int integer4 = MathHelper.floor(boundingBox.minY);
        final int integer5 = MathHelper.ceil(boundingBox.maxY);
        final int integer6 = MathHelper.floor(boundingBox.minZ);
        final int integer7 = MathHelper.ceil(boundingBox.maxZ);
        try (final BlockPos.PooledMutable pooledMutable8 = BlockPos.PooledMutable.get()) {
            for (int integer8 = integer2; integer8 < integer3; ++integer8) {
                for (int integer9 = integer4; integer9 < integer5; ++integer9) {
                    for (int integer10 = integer6; integer10 < integer7; ++integer10) {
                        final BlockState blockState13 = this.getBlockState(pooledMutable8.set(integer8, integer9, integer10));
                        if (!blockState13.isAir()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean doesAreaContainFireSource(final BoundingBox boundingBox) {
        final int integer2 = MathHelper.floor(boundingBox.minX);
        final int integer3 = MathHelper.ceil(boundingBox.maxX);
        final int integer4 = MathHelper.floor(boundingBox.minY);
        final int integer5 = MathHelper.ceil(boundingBox.maxY);
        final int integer6 = MathHelper.floor(boundingBox.minZ);
        final int integer7 = MathHelper.ceil(boundingBox.maxZ);
        if (this.isAreaLoaded(integer2, integer4, integer6, integer3, integer5, integer7)) {
            try (final BlockPos.PooledMutable pooledMutable8 = BlockPos.PooledMutable.get()) {
                for (int integer8 = integer2; integer8 < integer3; ++integer8) {
                    for (int integer9 = integer4; integer9 < integer5; ++integer9) {
                        for (int integer10 = integer6; integer10 < integer7; ++integer10) {
                            final Block block13 = this.getBlockState(pooledMutable8.set(integer8, integer9, integer10)).getBlock();
                            if (block13 == Blocks.bM || block13 == Blocks.B) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public BlockState getBlockState(final BoundingBox area, final Block block) {
        final int integer3 = MathHelper.floor(area.minX);
        final int integer4 = MathHelper.ceil(area.maxX);
        final int integer5 = MathHelper.floor(area.minY);
        final int integer6 = MathHelper.ceil(area.maxY);
        final int integer7 = MathHelper.floor(area.minZ);
        final int integer8 = MathHelper.ceil(area.maxZ);
        if (this.isAreaLoaded(integer3, integer5, integer7, integer4, integer6, integer8)) {
            try (final BlockPos.PooledMutable pooledMutable9 = BlockPos.PooledMutable.get()) {
                for (int integer9 = integer3; integer9 < integer4; ++integer9) {
                    for (int integer10 = integer5; integer10 < integer6; ++integer10) {
                        for (int integer11 = integer7; integer11 < integer8; ++integer11) {
                            final BlockState blockState14 = this.getBlockState(pooledMutable9.set(integer9, integer10, integer11));
                            if (blockState14.getBlock() == block) {
                                return blockState14;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public boolean containsBlockWithMaterial(final BoundingBox area, final Material material) {
        final int integer3 = MathHelper.floor(area.minX);
        final int integer4 = MathHelper.ceil(area.maxX);
        final int integer5 = MathHelper.floor(area.minY);
        final int integer6 = MathHelper.ceil(area.maxY);
        final int integer7 = MathHelper.floor(area.minZ);
        final int integer8 = MathHelper.ceil(area.maxZ);
        final MaterialPredicate materialPredicate9 = MaterialPredicate.create(material);
        return BlockPos.stream(integer3, integer5, integer7, integer4 - 1, integer6 - 1, integer8 - 1).anyMatch(blockPos -> materialPredicate9.a(this.getBlockState(blockPos)));
    }
    
    public Explosion createExplosion(@Nullable final Entity entity, final double x, final double y, final double double6, final float float8, final Explosion.DestructionType destructionType9) {
        return this.createExplosion(entity, null, x, y, double6, float8, false, destructionType9);
    }
    
    public Explosion createExplosion(@Nullable final Entity entity, final double x, final double y, final double z, final float float8, final boolean boolean9, final Explosion.DestructionType destructionType10) {
        return this.createExplosion(entity, null, x, y, z, float8, boolean9, destructionType10);
    }
    
    public Explosion createExplosion(@Nullable final Entity entity, @Nullable final DamageSource damageSource, final double x, final double y, final double z, final float float9, final boolean boolean10, final Explosion.DestructionType destructionType11) {
        final Explosion explosion12 = new Explosion(this, entity, x, y, z, float9, boolean10, destructionType11);
        if (damageSource != null) {
            explosion12.setDamageSource(damageSource);
        }
        explosion12.collectBlocksAndDamageEntities();
        explosion12.affectWorld(true);
        return explosion12;
    }
    
    public boolean a(@Nullable final PlayerEntity playerEntity, BlockPos blockPos, final Direction direction) {
        blockPos = blockPos.offset(direction);
        if (this.getBlockState(blockPos).getBlock() == Blocks.bM) {
            this.playLevelEvent(playerEntity, 1009, blockPos, 0);
            this.clearBlockState(blockPos, false);
            return true;
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public String getChunkProviderStatus() {
        return this.chunkManager.getStatus();
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        if (isHeightInvalid(pos)) {
            return null;
        }
        if (!this.isClient && Thread.currentThread() != this.thread) {
            return null;
        }
        BlockEntity blockEntity2 = null;
        if (this.iteratingTickingBlockEntities) {
            blockEntity2 = this.getPendingBlockEntity(pos);
        }
        if (blockEntity2 == null) {
            blockEntity2 = this.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.a);
        }
        if (blockEntity2 == null) {
            blockEntity2 = this.getPendingBlockEntity(pos);
        }
        return blockEntity2;
    }
    
    @Nullable
    private BlockEntity getPendingBlockEntity(final BlockPos blockPos) {
        for (int integer2 = 0; integer2 < this.pendingBlockEntities.size(); ++integer2) {
            final BlockEntity blockEntity3 = this.pendingBlockEntities.get(integer2);
            if (!blockEntity3.isInvalid() && blockEntity3.getPos().equals(blockPos)) {
                return blockEntity3;
            }
        }
        return null;
    }
    
    public void setBlockEntity(final BlockPos pos, @Nullable final BlockEntity blockEntity) {
        if (isHeightInvalid(pos)) {
            return;
        }
        if (blockEntity != null && !blockEntity.isInvalid()) {
            if (this.iteratingTickingBlockEntities) {
                blockEntity.setPos(pos);
                final Iterator<BlockEntity> iterator3 = this.pendingBlockEntities.iterator();
                while (iterator3.hasNext()) {
                    final BlockEntity blockEntity2 = iterator3.next();
                    if (blockEntity2.getPos().equals(pos)) {
                        blockEntity2.invalidate();
                        iterator3.remove();
                    }
                }
                this.pendingBlockEntities.add(blockEntity);
            }
            else {
                this.getWorldChunk(pos).setBlockEntity(pos, blockEntity);
                this.addBlockEntity(blockEntity);
            }
        }
    }
    
    public void removeBlockEntity(final BlockPos blockPos) {
        final BlockEntity blockEntity2 = this.getBlockEntity(blockPos);
        if (blockEntity2 != null && this.iteratingTickingBlockEntities) {
            blockEntity2.invalidate();
            this.pendingBlockEntities.remove(blockEntity2);
        }
        else {
            if (blockEntity2 != null) {
                this.pendingBlockEntities.remove(blockEntity2);
                this.blockEntities.remove(blockEntity2);
                this.tickingBlockEntities.remove(blockEntity2);
            }
            this.getWorldChunk(blockPos).removeBlockEntity(blockPos);
        }
    }
    
    public boolean isHeightValidAndBlockLoaded(final BlockPos blockPos) {
        return !isHeightInvalid(blockPos) && this.chunkManager.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }
    
    public boolean doesBlockHaveSolidTopSurface(final BlockPos blockPos, final Entity entity) {
        if (isHeightInvalid(blockPos)) {
            return false;
        }
        final Chunk chunk3 = this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
        return chunk3 != null && chunk3.getBlockState(blockPos).hasSolidTopSurface(this, blockPos, entity);
    }
    
    public void calculateAmbientDarkness() {
        final double double1 = 1.0 - this.getRainGradient(1.0f) * 5.0f / 16.0;
        final double double2 = 1.0 - this.getThunderGradient(1.0f) * 5.0f / 16.0;
        final double double3 = 0.5 + 2.0 * MathHelper.clamp(MathHelper.cos(this.getSkyAngle(1.0f) * 6.2831855f), -0.25, 0.25);
        this.ambientDarkness = (int)((1.0 - double3 * double1 * double2) * 11.0);
    }
    
    public void setMobSpawnOptions(final boolean spawnMonsters, final boolean spawnAnimals) {
        this.getChunkManager().setMobSpawnOptions(spawnMonsters, spawnAnimals);
    }
    
    protected void initWeatherGradients() {
        if (this.properties.isRaining()) {
            this.rainGradient = 1.0f;
            if (this.properties.isThundering()) {
                this.thunderGradient = 1.0f;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.chunkManager.close();
    }
    
    @Override
    public ChunkStatus getLeastChunkStatusForCollisionCalculation() {
        return ChunkStatus.FULL;
    }
    
    @Override
    public List<Entity> getEntities(@Nullable final Entity except, final BoundingBox box, @Nullable final Predicate<? super Entity> predicate) {
        final List<Entity> list4 = Lists.newArrayList();
        final int integer5 = MathHelper.floor((box.minX - 2.0) / 16.0);
        final int integer6 = MathHelper.floor((box.maxX + 2.0) / 16.0);
        final int integer7 = MathHelper.floor((box.minZ - 2.0) / 16.0);
        final int integer8 = MathHelper.floor((box.maxZ + 2.0) / 16.0);
        for (int integer9 = integer5; integer9 <= integer6; ++integer9) {
            for (int integer10 = integer7; integer10 <= integer8; ++integer10) {
                final WorldChunk worldChunk11 = this.getChunkManager().getWorldChunk(integer9, integer10, false);
                if (worldChunk11 != null) {
                    worldChunk11.appendEntities(except, box, list4, predicate);
                }
            }
        }
        return list4;
    }
    
    public List<Entity> getEntities(@Nullable final EntityType<?> type, final BoundingBox box, final Predicate<? super Entity> predicate) {
        final int integer4 = MathHelper.floor((box.minX - 2.0) / 16.0);
        final int integer5 = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        final int integer6 = MathHelper.floor((box.minZ - 2.0) / 16.0);
        final int integer7 = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        final List<Entity> list8 = Lists.newArrayList();
        for (int integer8 = integer4; integer8 < integer5; ++integer8) {
            for (int integer9 = integer6; integer9 < integer7; ++integer9) {
                final WorldChunk worldChunk11 = this.getChunkManager().getWorldChunk(integer8, integer9, false);
                if (worldChunk11 != null) {
                    worldChunk11.appendEntities(type, box, list8, predicate);
                }
            }
        }
        return list8;
    }
    
    @Override
    public <T extends Entity> List<T> getEntities(final Class<? extends T> entityClass, final BoundingBox box, @Nullable final Predicate<? super T> predicate) {
        final int integer4 = MathHelper.floor((box.minX - 2.0) / 16.0);
        final int integer5 = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        final int integer6 = MathHelper.floor((box.minZ - 2.0) / 16.0);
        final int integer7 = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        final List<T> list8 = Lists.newArrayList();
        for (int integer8 = integer4; integer8 < integer5; ++integer8) {
            for (int integer9 = integer6; integer9 < integer7; ++integer9) {
                final WorldChunk worldChunk11 = this.getChunkManager().getWorldChunk(integer8, integer9, false);
                if (worldChunk11 != null) {
                    worldChunk11.<T>appendEntities(entityClass, box, list8, predicate);
                }
            }
        }
        return list8;
    }
    
    @Nullable
    public abstract Entity getEntityById(final int arg1);
    
    public void markDirty(final BlockPos pos, final BlockEntity blockEntity) {
        if (this.isBlockLoaded(pos)) {
            this.getWorldChunk(pos).markDirty();
        }
    }
    
    @Override
    public int getSeaLevel() {
        return 63;
    }
    
    @Override
    public World getWorld() {
        return this;
    }
    
    @Override
    public int getEmittedStrongRedstonePower(final BlockPos pos, final Direction direction) {
        return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
    }
    
    public LevelGeneratorType getGeneratorType() {
        return this.properties.getGeneratorType();
    }
    
    public int getReceivedStrongRedstonePower(final BlockPos blockPos) {
        int integer2 = 0;
        integer2 = Math.max(integer2, this.getEmittedStrongRedstonePower(blockPos.down(), Direction.DOWN));
        if (integer2 >= 15) {
            return integer2;
        }
        integer2 = Math.max(integer2, this.getEmittedStrongRedstonePower(blockPos.up(), Direction.UP));
        if (integer2 >= 15) {
            return integer2;
        }
        integer2 = Math.max(integer2, this.getEmittedStrongRedstonePower(blockPos.north(), Direction.NORTH));
        if (integer2 >= 15) {
            return integer2;
        }
        integer2 = Math.max(integer2, this.getEmittedStrongRedstonePower(blockPos.south(), Direction.SOUTH));
        if (integer2 >= 15) {
            return integer2;
        }
        integer2 = Math.max(integer2, this.getEmittedStrongRedstonePower(blockPos.west(), Direction.WEST));
        if (integer2 >= 15) {
            return integer2;
        }
        integer2 = Math.max(integer2, this.getEmittedStrongRedstonePower(blockPos.east(), Direction.EAST));
        if (integer2 >= 15) {
            return integer2;
        }
        return integer2;
    }
    
    public boolean isEmittingRedstonePower(final BlockPos pos, final Direction direction) {
        return this.getEmittedRedstonePower(pos, direction) > 0;
    }
    
    public int getEmittedRedstonePower(final BlockPos pos, final Direction direction) {
        final BlockState blockState3 = this.getBlockState(pos);
        if (blockState3.isSimpleFullBlock(this, pos)) {
            return this.getReceivedStrongRedstonePower(pos);
        }
        return blockState3.getWeakRedstonePower(this, pos, direction);
    }
    
    public boolean isReceivingRedstonePower(final BlockPos blockPos) {
        return this.getEmittedRedstonePower(blockPos.down(), Direction.DOWN) > 0 || this.getEmittedRedstonePower(blockPos.up(), Direction.UP) > 0 || this.getEmittedRedstonePower(blockPos.north(), Direction.NORTH) > 0 || this.getEmittedRedstonePower(blockPos.south(), Direction.SOUTH) > 0 || this.getEmittedRedstonePower(blockPos.west(), Direction.WEST) > 0 || this.getEmittedRedstonePower(blockPos.east(), Direction.EAST) > 0;
    }
    
    public int getReceivedRedstonePower(final BlockPos blockPos) {
        int integer2 = 0;
        for (final Direction direction6 : World.DIRECTIONS) {
            final int integer3 = this.getEmittedRedstonePower(blockPos.offset(direction6), direction6);
            if (integer3 >= 15) {
                return 15;
            }
            if (integer3 > integer2) {
                integer2 = integer3;
            }
        }
        return integer2;
    }
    
    @Environment(EnvType.CLIENT)
    public void disconnect() {
    }
    
    public void setTime(final long long1) {
        this.properties.setTime(long1);
    }
    
    @Override
    public long getSeed() {
        return this.properties.getSeed();
    }
    
    public long getTime() {
        return this.properties.getTime();
    }
    
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }
    
    public void setTimeOfDay(final long long1) {
        this.properties.setTimeOfDay(long1);
    }
    
    protected void tickTime() {
        this.setTime(this.properties.getTime() + 1L);
        if (this.properties.getGameRules().getBoolean("doDaylightCycle")) {
            this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
        }
    }
    
    @Override
    public BlockPos getSpawnPos() {
        BlockPos blockPos1 = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
        if (!this.getWorldBorder().contains(blockPos1)) {
            blockPos1 = this.getTopPosition(Heightmap.Type.e, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockPos1;
    }
    
    public void setSpawnPos(final BlockPos blockPos) {
        this.properties.setSpawnPos(blockPos);
    }
    
    public boolean canPlayerModifyAt(final PlayerEntity player, final BlockPos blockPos) {
        return true;
    }
    
    public void sendEntityStatus(final Entity entity, final byte status) {
    }
    
    @Override
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }
    
    public void addBlockAction(final BlockPos pos, final Block block, final int type, final int data) {
        this.getBlockState(pos).onBlockAction(this, pos, type, data);
    }
    
    @Override
    public LevelProperties getLevelProperties() {
        return this.properties;
    }
    
    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }
    
    public float getThunderGradient(final float float1) {
        return MathHelper.lerp(float1, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(float1);
    }
    
    @Environment(EnvType.CLIENT)
    public void setThunderGradient(final float float1) {
        this.thunderGradientPrev = float1;
        this.thunderGradient = float1;
    }
    
    public float getRainGradient(final float float1) {
        return MathHelper.lerp(float1, this.rainGradientPrev, this.rainGradient);
    }
    
    @Environment(EnvType.CLIENT)
    public void setRainGradient(final float float1) {
        this.rainGradientPrev = float1;
        this.rainGradient = float1;
    }
    
    public boolean isThundering() {
        return this.dimension.hasSkyLight() && !this.dimension.isNether() && this.getThunderGradient(1.0f) > 0.9;
    }
    
    public boolean isRaining() {
        return this.getRainGradient(1.0f) > 0.2;
    }
    
    public boolean hasRain(final BlockPos pos) {
        return this.isRaining() && this.isSkyVisible(pos) && this.getTopPosition(Heightmap.Type.e, pos).getY() <= pos.getY() && this.getBiome(pos).getPrecipitation() == Biome.Precipitation.RAIN;
    }
    
    public boolean hasHighHumidity(final BlockPos blockPos) {
        final Biome biome2 = this.getBiome(blockPos);
        return biome2.hasHighHumidity();
    }
    
    @Nullable
    public abstract MapState getMapState(final String arg1);
    
    public abstract void putMapState(final MapState arg1);
    
    public abstract int getNextMapId();
    
    public void playGlobalEvent(final int type, final BlockPos pos, final int data) {
    }
    
    public int getEffectiveHeight() {
        return this.dimension.isNether() ? 128 : 256;
    }
    
    @Environment(EnvType.CLIENT)
    public double getHorizonHeight() {
        if (this.properties.getGeneratorType() == LevelGeneratorType.FLAT) {
            return 0.0;
        }
        return 63.0;
    }
    
    public CrashReportSection addDetailsToCrashReport(final CrashReport report) {
        final CrashReportSection crashReportSection2 = report.addElement("Affected level", 1);
        crashReportSection2.add("Level name", (this.properties == null) ? "????" : this.properties.getLevelName());
        crashReportSection2.add("All players", () -> this.getPlayers().size() + " total; " + this.getPlayers());
        crashReportSection2.add("Chunk stats", this.chunkManager::getStatus);
        try {
            this.properties.populateCrashReport(crashReportSection2);
        }
        catch (Throwable throwable3) {
            crashReportSection2.add("Level Data Unobtainable", throwable3);
        }
        return crashReportSection2;
    }
    
    public abstract void setBlockBreakingProgress(final int arg1, final BlockPos arg2, final int arg3);
    
    @Environment(EnvType.CLIENT)
    public void addFireworkParticle(final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, @Nullable final CompoundTag tag) {
    }
    
    public abstract Scoreboard getScoreboard();
    
    public void updateHorizontalAdjacent(final BlockPos pos, final Block block) {
        for (final Direction direction4 : Direction.Type.HORIZONTAL) {
            BlockPos blockPos5 = pos.offset(direction4);
            if (this.isBlockLoaded(blockPos5)) {
                BlockState blockState6 = this.getBlockState(blockPos5);
                if (blockState6.getBlock() == Blocks.fm) {
                    blockState6.neighborUpdate(this, blockPos5, block, pos, false);
                }
                else {
                    if (!blockState6.isSimpleFullBlock(this, blockPos5)) {
                        continue;
                    }
                    blockPos5 = blockPos5.offset(direction4);
                    blockState6 = this.getBlockState(blockPos5);
                    if (blockState6.getBlock() != Blocks.fm) {
                        continue;
                    }
                    blockState6.neighborUpdate(this, blockPos5, block, pos, false);
                }
            }
        }
    }
    
    @Override
    public LocalDifficulty getLocalDifficulty(final BlockPos pos) {
        long long2 = 0L;
        float float4 = 0.0f;
        if (this.isBlockLoaded(pos)) {
            float4 = this.getMoonSize();
            long2 = this.getWorldChunk(pos).getInhabitedTime();
        }
        return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), long2, float4);
    }
    
    @Override
    public int getAmbientDarkness() {
        return this.ambientDarkness;
    }
    
    @Environment(EnvType.CLIENT)
    public int getTicksSinceLightning() {
        return this.ticksSinceLightning;
    }
    
    public void setTicksSinceLightning(final int ticksSinceLightning) {
        this.ticksSinceLightning = ticksSinceLightning;
    }
    
    @Override
    public WorldBorder getWorldBorder() {
        return this.border;
    }
    
    public void sendPacket(final Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }
    
    @Nullable
    public BlockPos locateStructure(final String id, final BlockPos center, final int radius, final boolean skipExistingChunks) {
        return null;
    }
    
    @Override
    public Dimension getDimension() {
        return this.dimension;
    }
    
    @Override
    public Random getRandom() {
        return this.random;
    }
    
    @Override
    public boolean testBlockState(final BlockPos blockPos, final Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(blockPos));
    }
    
    public abstract RecipeManager getRecipeManager();
    
    public abstract TagManager getTagManager();
    
    public BlockPos getRandomPosInChunk(final int x, final int y, final int z, final int integer4) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        final int integer5 = this.lcgBlockSeed >> 2;
        return new BlockPos(x + (integer5 & 0xF), y + (integer5 >> 16 & integer4), z + (integer5 >> 8 & 0xF));
    }
    
    public boolean isSavingDisabled() {
        return false;
    }
    
    public Profiler getProfiler() {
        return this.profiler;
    }
    
    @Override
    public BlockPos getTopPosition(final Heightmap.Type type, final BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getTop(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DIRECTIONS = Direction.values();
    }
}
