package net.minecraft.world.chunk;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.GenerationStep;
import java.util.Set;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.ChunkRegion;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ChunkHolder;
import com.mojang.datafixers.util.Either;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.server.world.ServerWorld;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.util.registry.Registry;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.world.Heightmap;
import java.util.EnumSet;

public class ChunkStatus
{
    private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS;
    private static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS;
    public static final ChunkStatus EMPTY;
    public static final ChunkStatus STRUCTURE_STARTS;
    public static final ChunkStatus STRUCTURE_REFERENCES;
    public static final ChunkStatus BIOMES;
    public static final ChunkStatus NOISE;
    public static final ChunkStatus SURFACE;
    public static final ChunkStatus CARVERS;
    public static final ChunkStatus LIQUID_CARVERS;
    public static final ChunkStatus FEATURES;
    public static final ChunkStatus LIGHT;
    public static final ChunkStatus SPAWN;
    public static final ChunkStatus HEIGHTMAPS;
    public static final ChunkStatus FULL;
    private static final List<ChunkStatus> DISTANCE_TO_TARGET_GENERATION_STATUS;
    private static final IntList STATUS_TO_TARGET_GENERATION_RADIUS;
    private final String name;
    private final int index;
    private final ChunkStatus previous;
    private final Task task;
    private final int taskMargin;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Type> surfaceGenerated;
    
    private static ChunkStatus register(final String string, @Nullable final ChunkStatus chunkStatus, final int integer, final EnumSet<Heightmap.Type> enumSet, final ChunkType chunkType, final SimpleTask simpleTask) {
        return register(string, chunkStatus, integer, enumSet, chunkType, (Task)simpleTask);
    }
    
    private static ChunkStatus register(final String string, @Nullable final ChunkStatus chunkStatus, final int integer, final EnumSet<Heightmap.Type> enumSet, final ChunkType chunkType, final Task task) {
        return Registry.<ChunkStatus>register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, integer, enumSet, chunkType, task));
    }
    
    public static List<ChunkStatus> createOrderedList() {
        final List<ChunkStatus> list1 = Lists.newArrayList();
        ChunkStatus chunkStatus2;
        for (chunkStatus2 = ChunkStatus.FULL; chunkStatus2.getPrevious() != chunkStatus2; chunkStatus2 = chunkStatus2.getPrevious()) {
            list1.add(chunkStatus2);
        }
        list1.add(chunkStatus2);
        Collections.reverse(list1);
        return list1;
    }
    
    public static ChunkStatus getTargetGenerationStatus(final int distance) {
        if (distance >= ChunkStatus.DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
            return ChunkStatus.EMPTY;
        }
        if (distance < 0) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.DISTANCE_TO_TARGET_GENERATION_STATUS.get(distance);
    }
    
    public static int getMaxTargetGenerationRadius() {
        return ChunkStatus.DISTANCE_TO_TARGET_GENERATION_STATUS.size();
    }
    
    public static int getTargetGenerationRadius(final ChunkStatus status) {
        return ChunkStatus.STATUS_TO_TARGET_GENERATION_RADIUS.getInt(status.getIndex());
    }
    
    ChunkStatus(final String name, @Nullable final ChunkStatus previous, final int taskMargin, final EnumSet<Heightmap.Type> surfaceGenerated, final ChunkType type, final Task task) {
        this.name = name;
        this.previous = ((previous == null) ? this : previous);
        this.task = task;
        this.taskMargin = taskMargin;
        this.chunkType = type;
        this.surfaceGenerated = surfaceGenerated;
        this.index = ((previous == null) ? 0 : (previous.getIndex() + 1));
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ChunkStatus getPrevious() {
        return this.previous;
    }
    
    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runTask(final ServerWorld serverWorld, final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final ServerLightingProvider serverLightingProvider, final Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, final List<Chunk> list) {
        return this.task.doWork(this, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, list.get(list.size() / 2));
    }
    
    public int getTaskMargin() {
        return this.taskMargin;
    }
    
    public ChunkType getChunkType() {
        return this.chunkType;
    }
    
    public static ChunkStatus get(final String id) {
        return Registry.CHUNK_STATUS.get(Identifier.create(id));
    }
    
    public EnumSet<Heightmap.Type> isSurfaceGenerated() {
        return this.surfaceGenerated;
    }
    
    public boolean isAtLeast(final ChunkStatus chunk) {
        return this.getIndex() >= chunk.getIndex();
    }
    
    @Override
    public String toString() {
        return Registry.CHUNK_STATUS.getId(this).toString();
    }
    
    static {
        PRE_CARVER_HEIGHTMAPS = EnumSet.<Heightmap.Type>of(Heightmap.Type.c, Heightmap.Type.a);
        POST_CARVER_HEIGHTMAPS = EnumSet.<Heightmap.Type>of(Heightmap.Type.d, Heightmap.Type.b, Heightmap.Type.e, Heightmap.Type.f);
        EMPTY = register("empty", null, -1, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> {});
        STRUCTURE_STARTS = register("structure_starts", ChunkStatus.EMPTY, 0, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
            if (!chunk.getStatus().isAtLeast(chunkStatus)) {
                if (serverWorld.getLevelProperties().hasStructures()) {
                    chunkGenerator.setStructureStarts(chunk, chunkGenerator, structureManager);
                }
                if (chunk instanceof ProtoChunk) {
                    chunk.setStatus(chunkStatus);
                }
            }
            return CompletableFuture.<Either<Chunk, ChunkHolder.Unloaded>>completedFuture(Either.left(chunk));
        });
        STRUCTURE_REFERENCES = register("structure_references", ChunkStatus.STRUCTURE_STARTS, 8, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(serverWorld, list), chunk));
        BIOMES = register("biomes", ChunkStatus.STRUCTURE_REFERENCES, 0, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk));
        NOISE = register("noise", ChunkStatus.BIOMES, 8, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), chunk));
        SURFACE = register("surface", ChunkStatus.NOISE, 0, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(chunk));
        CARVERS = register("carvers", ChunkStatus.SURFACE, 0, ChunkStatus.PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.AIR));
        LIQUID_CARVERS = register("liquid_carvers", ChunkStatus.CARVERS, 0, ChunkStatus.POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.LIQUID));
        FEATURES = register("features", ChunkStatus.LIQUID_CARVERS, 8, ChunkStatus.POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
            chunk.setLightingProvider(serverLightingProvider);
            if (!chunk.getStatus().isAtLeast(chunkStatus)) {
                Heightmap.populateHeightmaps(chunk, EnumSet.<Heightmap.Type>of(Heightmap.Type.e, Heightmap.Type.f, Heightmap.Type.d, Heightmap.Type.b));
                chunkGenerator.generateFeatures(new ChunkRegion(serverWorld, list));
                if (chunk instanceof ProtoChunk) {
                    chunk.setStatus(chunkStatus);
                }
            }
            return CompletableFuture.<Either<Chunk, ChunkHolder.Unloaded>>completedFuture(Either.left(chunk));
        });
        final boolean boolean9;
        LIGHT = register("light", ChunkStatus.FEATURES, 1, ChunkStatus.POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
            boolean9 = (chunk.getStatus().isAtLeast(chunkStatus) && chunk.isLightOn());
            if (!chunk.getStatus().isAtLeast(chunkStatus)) {
                chunk.setStatus(chunkStatus);
            }
            return serverLightingProvider.light(chunk, boolean9).thenApply(Either::left);
        });
        SPAWN = register("spawn", ChunkStatus.LIGHT, 0, ChunkStatus.POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new ChunkRegion(serverWorld, list)));
        HEIGHTMAPS = register("heightmaps", ChunkStatus.SPAWN, 0, ChunkStatus.POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> {});
        FULL = register("full", ChunkStatus.HEIGHTMAPS, 0, ChunkStatus.POST_CARVER_HEIGHTMAPS, ChunkType.LEVELCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> function.apply(chunk));
        DISTANCE_TO_TARGET_GENERATION_STATUS = ImmutableList.<ChunkStatus>of(ChunkStatus.FULL, ChunkStatus.FEATURES, ChunkStatus.LIQUID_CARVERS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS);
        int integer2;
        int integer3;
        STATUS_TO_TARGET_GENERATION_RADIUS = SystemUtil.<IntList>consume((IntList)new IntArrayList(createOrderedList().size()), intArrayList -> {
            integer2 = 0;
            for (integer3 = createOrderedList().size() - 1; integer3 >= 0; --integer3) {
                while (integer2 + 1 < ChunkStatus.DISTANCE_TO_TARGET_GENERATION_STATUS.size() && integer3 <= ChunkStatus.DISTANCE_TO_TARGET_GENERATION_STATUS.get(integer2 + 1).getIndex()) {
                    ++integer2;
                }
                intArrayList.add(0, integer2);
            }
        });
    }
    
    interface SimpleTask extends Task
    {
        default CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(final ChunkStatus targetStatus, final ServerWorld serverWorld, final ChunkGenerator<?> generator, final StructureManager structureManager, final ServerLightingProvider serverLightingProvider, final Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, final List<Chunk> list, final Chunk chunk) {
            if (!chunk.getStatus().isAtLeast(targetStatus)) {
                this.doWork(serverWorld, generator, list, chunk);
                if (chunk instanceof ProtoChunk) {
                    ((ProtoChunk)chunk).setStatus(targetStatus);
                }
            }
            return CompletableFuture.<Either<Chunk, ChunkHolder.Unloaded>>completedFuture(Either.left(chunk));
        }
        
        void doWork(final ServerWorld arg1, final ChunkGenerator<?> arg2, final List<Chunk> arg3, final Chunk arg4);
    }
    
    public enum ChunkType
    {
        PROTOCHUNK, 
        LEVELCHUNK;
    }
    
    interface Task
    {
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(final ChunkStatus arg1, final ServerWorld arg2, final ChunkGenerator<?> arg3, final StructureManager arg4, final ServerLightingProvider arg5, final Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> arg6, final List<Chunk> arg7, final Chunk arg8);
    }
}
