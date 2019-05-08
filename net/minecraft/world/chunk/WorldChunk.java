package net.minecraft.world.chunk;

import org.apache.logging.log4j.LogManager;
import net.minecraft.server.world.ServerWorld;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.IWorld;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Collections;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import java.util.function.Predicate;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.block.BlockState;
import java.util.Collection;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Iterator;
import net.minecraft.entity.EntityType;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.DummyClientTickScheduler;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkHolder;
import java.util.function.Supplier;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.world.TickScheduler;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Logger;

public class WorldChunk implements Chunk
{
    private static final Logger LOGGER;
    public static final ChunkSection EMPTY_SECTION;
    private final ChunkSection[] sections;
    private final Biome[] biomeArray;
    private final Map<BlockPos, CompoundTag> pendingBlockEntityTags;
    private boolean loadedToWorld;
    private final World world;
    private final Map<Heightmap.Type, Heightmap> heightmaps;
    private final UpgradeData upgradeData;
    private final Map<BlockPos, BlockEntity> blockEntities;
    private final TypeFilterableList<Entity>[] entitySections;
    private final Map<String, StructureStart> structureStarts;
    private final Map<String, LongSet> structureReferences;
    private final ShortList[] postProcessingLists;
    private TickScheduler<Block> blockTickScheduler;
    private TickScheduler<Fluid> fluidTickScheduler;
    private boolean q;
    private long lastSaveTime;
    private boolean shouldSave;
    private long inhabitedTime;
    @Nullable
    private Supplier<ChunkHolder.LevelType> levelTypeProvider;
    @Nullable
    private Consumer<WorldChunk> loadToWorldConsumer;
    private final ChunkPos pos;
    private volatile boolean isLightOn;
    
    @Environment(EnvType.CLIENT)
    public WorldChunk(final World world, final ChunkPos chunkPos, final Biome[] arr) {
        this(world, chunkPos, arr, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
    }
    
    public WorldChunk(final World world, final ChunkPos chunkPos, final Biome[] arr, final UpgradeData upgradeData, final TickScheduler<Block> tickScheduler5, final TickScheduler<Fluid> tickScheduler6, final long long7, @Nullable final ChunkSection[] arr, @Nullable final Consumer<WorldChunk> consumer10) {
        this.sections = new ChunkSection[16];
        this.pendingBlockEntityTags = Maps.newHashMap();
        this.heightmaps = Maps.newEnumMap(Heightmap.Type.class);
        this.blockEntities = Maps.newHashMap();
        this.structureStarts = Maps.newHashMap();
        this.structureReferences = Maps.newHashMap();
        this.postProcessingLists = new ShortList[16];
        this.entitySections = new TypeFilterableList[16];
        this.world = world;
        this.pos = chunkPos;
        this.upgradeData = upgradeData;
        for (final Heightmap.Type type14 : Heightmap.Type.values()) {
            if (ChunkStatus.FULL.isSurfaceGenerated().contains(type14)) {
                this.heightmaps.put(type14, new Heightmap(this, type14));
            }
        }
        for (int integer11 = 0; integer11 < this.entitySections.length; ++integer11) {
            this.entitySections[integer11] = new TypeFilterableList<Entity>(Entity.class);
        }
        this.biomeArray = arr;
        this.blockTickScheduler = tickScheduler5;
        this.fluidTickScheduler = tickScheduler6;
        this.inhabitedTime = long7;
        this.loadToWorldConsumer = consumer10;
        if (arr != null) {
            if (this.sections.length == arr.length) {
                System.arraycopy(arr, 0, this.sections, 0, this.sections.length);
            }
            else {
                WorldChunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", arr.length, this.sections.length);
            }
        }
    }
    
    public WorldChunk(final World world, final ProtoChunk protoChunk) {
        this(world, protoChunk.getPos(), protoChunk.getBiomeArray(), protoChunk.getUpgradeData(), protoChunk.getBlockTickScheduler(), protoChunk.getFluidTickScheduler(), protoChunk.getInhabitedTime(), protoChunk.getSectionArray(), null);
        for (final CompoundTag compoundTag4 : protoChunk.getEntities()) {
            EntityType.loadEntityWithPassengers(compoundTag4, world, entity -> {
                this.addEntity(entity);
                return entity;
            });
        }
        for (final BlockEntity blockEntity4 : protoChunk.getBlockEntities().values()) {
            this.addBlockEntity(blockEntity4);
        }
        this.pendingBlockEntityTags.putAll(protoChunk.getBlockEntityTags());
        for (int integer3 = 0; integer3 < protoChunk.getPostProcessingLists().length; ++integer3) {
            this.postProcessingLists[integer3] = protoChunk.getPostProcessingLists()[integer3];
        }
        this.setStructureStarts(protoChunk.getStructureStarts());
        this.setStructureReferences(protoChunk.getStructureReferences());
        for (final Map.Entry<Heightmap.Type, Heightmap> entry4 : protoChunk.getHeightmaps()) {
            if (ChunkStatus.FULL.isSurfaceGenerated().contains(entry4.getKey())) {
                this.getHeightmap(entry4.getKey()).setTo(entry4.getValue().asLongArray());
            }
        }
        this.setLightOn(protoChunk.isLightOn());
        this.shouldSave = true;
    }
    
    @Override
    public Heightmap getHeightmap(final Heightmap.Type type) {
        return this.heightmaps.computeIfAbsent(type, type -> new Heightmap(this, type));
    }
    
    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        final Set<BlockPos> set1 = Sets.newHashSet(this.pendingBlockEntityTags.keySet());
        set1.addAll(this.blockEntities.keySet());
        return set1;
    }
    
    @Override
    public ChunkSection[] getSectionArray() {
        return this.sections;
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        final int integer2 = pos.getX();
        final int integer3 = pos.getY();
        final int integer4 = pos.getZ();
        if (this.world.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            BlockState blockState5 = null;
            if (integer3 == 60) {
                blockState5 = Blocks.gg.getDefaultState();
            }
            if (integer3 == 70) {
                blockState5 = DebugChunkGenerator.getBlockState(integer2, integer4);
            }
            return (blockState5 == null) ? Blocks.AIR.getDefaultState() : blockState5;
        }
        try {
            if (integer3 >= 0 && integer3 >> 4 < this.sections.length) {
                final ChunkSection chunkSection5 = this.sections[integer3 >> 4];
                if (!ChunkSection.isEmpty(chunkSection5)) {
                    return chunkSection5.getBlockState(integer2 & 0xF, integer3 & 0xF, integer4 & 0xF);
                }
            }
            return Blocks.AIR.getDefaultState();
        }
        catch (Throwable throwable5) {
            final CrashReport crashReport6 = CrashReport.create(throwable5, "Getting block state");
            final CrashReportSection crashReportSection7 = crashReport6.addElement("Block being got");
            crashReportSection7.add("Location", () -> CrashReportSection.createPositionString(integer2, integer3, integer4));
            throw new CrashException(crashReport6);
        }
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public FluidState getFluidState(final int x, final int y, final int integer3) {
        try {
            if (y >= 0 && y >> 4 < this.sections.length) {
                final ChunkSection chunkSection4 = this.sections[y >> 4];
                if (!ChunkSection.isEmpty(chunkSection4)) {
                    return chunkSection4.getFluidState(x & 0xF, y & 0xF, integer3 & 0xF);
                }
            }
            return Fluids.EMPTY.getDefaultState();
        }
        catch (Throwable throwable4) {
            final CrashReport crashReport5 = CrashReport.create(throwable4, "Getting fluid state");
            final CrashReportSection crashReportSection6 = crashReport5.addElement("Block being got");
            crashReportSection6.add("Location", () -> CrashReportSection.createPositionString(x, y, integer3));
            throw new CrashException(crashReport5);
        }
    }
    
    @Nullable
    @Override
    public BlockState setBlockState(final BlockPos pos, final BlockState state, final boolean boolean3) {
        final int integer4 = pos.getX() & 0xF;
        final int integer5 = pos.getY();
        final int integer6 = pos.getZ() & 0xF;
        ChunkSection chunkSection7 = this.sections[integer5 >> 4];
        if (chunkSection7 == WorldChunk.EMPTY_SECTION) {
            if (state.isAir()) {
                return null;
            }
            chunkSection7 = new ChunkSection(integer5 >> 4 << 4);
            this.sections[integer5 >> 4] = chunkSection7;
        }
        final boolean boolean4 = chunkSection7.isEmpty();
        final BlockState blockState9 = chunkSection7.setBlockState(integer4, integer5 & 0xF, integer6, state);
        if (blockState9 == state) {
            return null;
        }
        final Block block10 = state.getBlock();
        final Block block11 = blockState9.getBlock();
        this.heightmaps.get(Heightmap.Type.e).trackUpdate(integer4, integer5, integer6, state);
        this.heightmaps.get(Heightmap.Type.f).trackUpdate(integer4, integer5, integer6, state);
        this.heightmaps.get(Heightmap.Type.d).trackUpdate(integer4, integer5, integer6, state);
        this.heightmaps.get(Heightmap.Type.b).trackUpdate(integer4, integer5, integer6, state);
        final boolean boolean5 = chunkSection7.isEmpty();
        if (boolean4 != boolean5) {
            this.world.getChunkManager().getLightingProvider().updateSectionStatus(pos, boolean5);
        }
        if (!this.world.isClient) {
            blockState9.onBlockRemoved(this.world, pos, state, boolean3);
        }
        else if (block11 != block10 && block11 instanceof BlockEntityProvider) {
            this.world.removeBlockEntity(pos);
        }
        if (chunkSection7.getBlockState(integer4, integer5 & 0xF, integer6).getBlock() != block10) {
            return null;
        }
        if (block11 instanceof BlockEntityProvider) {
            final BlockEntity blockEntity13 = this.getBlockEntity(pos, CreationType.c);
            if (blockEntity13 != null) {
                blockEntity13.resetBlock();
            }
        }
        if (!this.world.isClient) {
            state.onBlockAdded(this.world, pos, blockState9, boolean3);
        }
        if (block10 instanceof BlockEntityProvider) {
            BlockEntity blockEntity13 = this.getBlockEntity(pos, CreationType.c);
            if (blockEntity13 == null) {
                blockEntity13 = ((BlockEntityProvider)block10).createBlockEntity(this.world);
                this.world.setBlockEntity(pos, blockEntity13);
            }
            else {
                blockEntity13.resetBlock();
            }
        }
        this.shouldSave = true;
        return blockState9;
    }
    
    @Nullable
    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getChunkManager().getLightingProvider();
    }
    
    public int getLightLevel(final BlockPos blockPos, final int integer) {
        return this.getLightLevel(blockPos, integer, this.world.getDimension().hasSkyLight());
    }
    
    @Override
    public void addEntity(final Entity entity) {
        this.q = true;
        final int integer2 = MathHelper.floor(entity.x / 16.0);
        final int integer3 = MathHelper.floor(entity.z / 16.0);
        if (integer2 != this.pos.x || integer3 != this.pos.z) {
            WorldChunk.LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", integer2, integer3, this.pos.x, this.pos.z, entity);
            entity.removed = true;
        }
        int integer4 = MathHelper.floor(entity.y / 16.0);
        if (integer4 < 0) {
            integer4 = 0;
        }
        if (integer4 >= this.entitySections.length) {
            integer4 = this.entitySections.length - 1;
        }
        entity.Y = true;
        entity.chunkX = this.pos.x;
        entity.chunkY = integer4;
        entity.chunkZ = this.pos.z;
        this.entitySections[integer4].add(entity);
    }
    
    @Override
    public void setHeightmap(final Heightmap.Type type, final long[] heightmap) {
        this.heightmaps.get(type).setTo(heightmap);
    }
    
    public void remove(final Entity entity) {
        this.remove(entity, entity.chunkY);
    }
    
    public void remove(final Entity entity, int integer) {
        if (integer < 0) {
            integer = 0;
        }
        if (integer >= this.entitySections.length) {
            integer = this.entitySections.length - 1;
        }
        this.entitySections[integer].remove(entity);
    }
    
    @Override
    public int sampleHeightmap(final Heightmap.Type type, final int x, final int z) {
        return this.heightmaps.get(type).get(x & 0xF, z & 0xF) - 1;
    }
    
    @Nullable
    private BlockEntity createBlockEntity(final BlockPos blockPos) {
        final BlockState blockState2 = this.getBlockState(blockPos);
        final Block block3 = blockState2.getBlock();
        if (!block3.hasBlockEntity()) {
            return null;
        }
        return ((BlockEntityProvider)block3).createBlockEntity(this.world);
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        return this.getBlockEntity(pos, CreationType.c);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos pos, final CreationType creationType) {
        BlockEntity blockEntity3 = this.blockEntities.get(pos);
        if (blockEntity3 == null) {
            final CompoundTag compoundTag4 = this.pendingBlockEntityTags.remove(pos);
            if (compoundTag4 != null) {
                final BlockEntity blockEntity4 = this.loadBlockEntity(pos, compoundTag4);
                if (blockEntity4 != null) {
                    return blockEntity4;
                }
            }
        }
        if (blockEntity3 == null) {
            if (creationType == CreationType.a) {
                blockEntity3 = this.createBlockEntity(pos);
                this.world.setBlockEntity(pos, blockEntity3);
            }
        }
        else if (blockEntity3.isInvalid()) {
            this.blockEntities.remove(pos);
            return null;
        }
        return blockEntity3;
    }
    
    public void addBlockEntity(final BlockEntity blockEntity) {
        this.setBlockEntity(blockEntity.getPos(), blockEntity);
        if (this.loadedToWorld || this.world.isClient()) {
            this.world.setBlockEntity(blockEntity.getPos(), blockEntity);
        }
    }
    
    @Override
    public void setBlockEntity(final BlockPos pos, final BlockEntity blockEntity) {
        if (!(this.getBlockState(pos).getBlock() instanceof BlockEntityProvider)) {
            return;
        }
        blockEntity.setWorld(this.world);
        blockEntity.setPos(pos);
        blockEntity.validate();
        final BlockEntity blockEntity2 = this.blockEntities.put(pos.toImmutable(), blockEntity);
        if (blockEntity2 != null && blockEntity2 != blockEntity) {
            blockEntity2.invalidate();
        }
    }
    
    @Override
    public void addPendingBlockEntityTag(final CompoundTag compoundTag) {
        this.pendingBlockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
    }
    
    @Override
    public void removeBlockEntity(final BlockPos blockPos) {
        if (this.loadedToWorld || this.world.isClient()) {
            final BlockEntity blockEntity2 = this.blockEntities.remove(blockPos);
            if (blockEntity2 != null) {
                blockEntity2.invalidate();
            }
        }
    }
    
    public void loadToWorld() {
        if (this.loadToWorldConsumer != null) {
            this.loadToWorldConsumer.accept(this);
            this.loadToWorldConsumer = null;
        }
    }
    
    public void markDirty() {
        this.shouldSave = true;
    }
    
    public void appendEntities(@Nullable final Entity except, final BoundingBox box, final List<Entity> entityList, @Nullable final Predicate<? super Entity> predicate) {
        int integer5 = MathHelper.floor((box.minY - 2.0) / 16.0);
        int integer6 = MathHelper.floor((box.maxY + 2.0) / 16.0);
        integer5 = MathHelper.clamp(integer5, 0, this.entitySections.length - 1);
        integer6 = MathHelper.clamp(integer6, 0, this.entitySections.length - 1);
        for (int integer7 = integer5; integer7 <= integer6; ++integer7) {
            if (!this.entitySections[integer7].isEmpty()) {
                for (final Entity entity9 : this.entitySections[integer7]) {
                    if (entity9.getBoundingBox().intersects(box) && entity9 != except) {
                        if (predicate == null || predicate.test(entity9)) {
                            entityList.add(entity9);
                        }
                        if (!(entity9 instanceof EnderDragonEntity)) {
                            continue;
                        }
                        for (final EnderDragonPart enderDragonPart13 : ((EnderDragonEntity)entity9).dT()) {
                            if (enderDragonPart13 != except && enderDragonPart13.getBoundingBox().intersects(box) && (predicate == null || predicate.test(enderDragonPart13))) {
                                entityList.add(enderDragonPart13);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void appendEntities(@Nullable final EntityType<?> type, final BoundingBox box, final List<Entity> list, final Predicate<? super Entity> predicate) {
        int integer5 = MathHelper.floor((box.minY - 2.0) / 16.0);
        int integer6 = MathHelper.floor((box.maxY + 2.0) / 16.0);
        integer5 = MathHelper.clamp(integer5, 0, this.entitySections.length - 1);
        integer6 = MathHelper.clamp(integer6, 0, this.entitySections.length - 1);
        for (int integer7 = integer5; integer7 <= integer6; ++integer7) {
            for (final Entity entity9 : this.entitySections[integer7].<Entity>getAllOfType(Entity.class)) {
                if (type != null && entity9.getType() != type) {
                    continue;
                }
                if (!entity9.getBoundingBox().intersects(box) || !predicate.test(entity9)) {
                    continue;
                }
                list.add(entity9);
            }
        }
    }
    
    public <T extends Entity> void appendEntities(final Class<? extends T> entityClass, final BoundingBox box, final List<T> entityList, @Nullable final Predicate<? super T> predicate) {
        int integer5 = MathHelper.floor((box.minY - 2.0) / 16.0);
        int integer6 = MathHelper.floor((box.maxY + 2.0) / 16.0);
        integer5 = MathHelper.clamp(integer5, 0, this.entitySections.length - 1);
        integer6 = MathHelper.clamp(integer6, 0, this.entitySections.length - 1);
        for (int integer7 = integer5; integer7 <= integer6; ++integer7) {
            for (final T entity9 : this.entitySections[integer7].getAllOfType(entityClass)) {
                if (entity9.getBoundingBox().intersects(box) && (predicate == null || predicate.test(entity9))) {
                    entityList.add(entity9);
                }
            }
        }
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public ChunkPos getPos() {
        return this.pos;
    }
    
    @Environment(EnvType.CLIENT)
    public void loadFromPacket(final PacketByteBuf data, final CompoundTag nbt, final int updatedSectionsBits, final boolean clearOld) {
        final Predicate<BlockPos> predicate5 = clearOld ? (blockPos -> true) : (blockPos -> (updatedSectionsBits & 1 << (blockPos.getY() >> 4)) != 0x0);
        Sets.newHashSet(this.blockEntities.keySet()).stream().filter(predicate5).forEach(this.world::removeBlockEntity);
        for (int integer6 = 0; integer6 < this.sections.length; ++integer6) {
            ChunkSection chunkSection7 = this.sections[integer6];
            if ((updatedSectionsBits & 1 << integer6) == 0x0) {
                if (clearOld && chunkSection7 != WorldChunk.EMPTY_SECTION) {
                    this.sections[integer6] = WorldChunk.EMPTY_SECTION;
                }
            }
            else {
                if (chunkSection7 == WorldChunk.EMPTY_SECTION) {
                    chunkSection7 = new ChunkSection(integer6 << 4);
                    this.sections[integer6] = chunkSection7;
                }
                chunkSection7.fromPacket(data);
            }
        }
        if (clearOld) {
            for (int integer6 = 0; integer6 < this.biomeArray.length; ++integer6) {
                this.biomeArray[integer6] = Registry.BIOME.get(data.readInt());
            }
        }
        for (final Heightmap.Type type9 : Heightmap.Type.values()) {
            final String string10 = type9.getName();
            if (nbt.containsKey(string10, 12)) {
                this.setHeightmap(type9, nbt.getLongArray(string10));
            }
        }
        for (final BlockEntity blockEntity7 : this.blockEntities.values()) {
            blockEntity7.resetBlock();
        }
    }
    
    @Override
    public Biome[] getBiomeArray() {
        return this.biomeArray;
    }
    
    public void setLoadedToWorld(final boolean boolean1) {
        this.loadedToWorld = boolean1;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }
    
    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }
    
    public TypeFilterableList<Entity>[] getEntitySectionArray() {
        return this.entitySections;
    }
    
    @Override
    public CompoundTag getBlockEntityTagAt(final BlockPos pos) {
        return this.pendingBlockEntityTags.get(pos);
    }
    
    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return StreamSupport.<BlockPos>stream(BlockPos.iterate(this.pos.getStartX(), 0, this.pos.getStartZ(), this.pos.getEndX(), 255, this.pos.getEndZ()).spliterator(), false).filter(blockPos -> this.getBlockState(blockPos).getLuminance() != 0);
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
    public void setShouldSave(final boolean shouldSave) {
        this.shouldSave = shouldSave;
    }
    
    @Override
    public boolean needsSaving() {
        return this.shouldSave || (this.q && this.world.getTime() != this.lastSaveTime);
    }
    
    public void d(final boolean boolean1) {
        this.q = boolean1;
    }
    
    @Override
    public void setLastSaveTime(final long lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }
    
    @Nullable
    @Override
    public StructureStart getStructureStart(final String structureId) {
        return this.structureStarts.get(structureId);
    }
    
    @Override
    public void setStructureStart(final String structureId, final StructureStart structureStart) {
        this.structureStarts.put(structureId, structureStart);
    }
    
    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return this.structureStarts;
    }
    
    @Override
    public void setStructureStarts(final Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
    }
    
    @Override
    public LongSet getStructureReferences(final String structureId) {
        return this.structureReferences.computeIfAbsent(structureId, string -> new LongOpenHashSet());
    }
    
    @Override
    public void addStructureReference(final String structureId, final long reference) {
        this.structureReferences.computeIfAbsent(structureId, string -> new LongOpenHashSet()).add(reference);
    }
    
    @Override
    public Map<String, LongSet> getStructureReferences() {
        return this.structureReferences;
    }
    
    @Override
    public void setStructureReferences(final Map<String, LongSet> structureReferences) {
        this.structureReferences.clear();
        this.structureReferences.putAll(structureReferences);
    }
    
    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }
    
    @Override
    public void setInhabitedTime(final long inhabitedTime) {
        this.inhabitedTime = inhabitedTime;
    }
    
    public void runPostProcessing() {
        final ChunkPos chunkPos1 = this.getPos();
        for (int integer2 = 0; integer2 < this.postProcessingLists.length; ++integer2) {
            if (this.postProcessingLists[integer2] != null) {
                for (final Short short4 : this.postProcessingLists[integer2]) {
                    final BlockPos blockPos2 = ProtoChunk.joinBlockPos(short4, integer2, chunkPos1);
                    final BlockState blockState6 = this.getBlockState(blockPos2);
                    final BlockState blockState7 = Block.getRenderingState(blockState6, this.world, blockPos2);
                    this.world.setBlockState(blockPos2, blockState7, 20);
                }
                this.postProcessingLists[integer2].clear();
            }
        }
        if (this.blockTickScheduler instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.blockTickScheduler).tick(this.world.getBlockTickScheduler(), blockPos -> this.getBlockState(blockPos).getBlock());
        }
        else if (this.blockTickScheduler instanceof SimpleTickScheduler) {
            this.world.getBlockTickScheduler().a(((SimpleTickScheduler)this.blockTickScheduler).stream());
            this.blockTickScheduler = DummyClientTickScheduler.get();
        }
        if (this.fluidTickScheduler instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.fluidTickScheduler).tick(this.world.getFluidTickScheduler(), blockPos -> this.getFluidState(blockPos).getFluid());
        }
        else if (this.fluidTickScheduler instanceof SimpleTickScheduler) {
            this.world.getFluidTickScheduler().a(((SimpleTickScheduler)this.fluidTickScheduler).stream());
            this.fluidTickScheduler = DummyClientTickScheduler.get();
        }
        for (final BlockPos blockPos3 : Sets.<BlockPos>newHashSet(this.pendingBlockEntityTags.keySet())) {
            this.getBlockEntity(blockPos3);
        }
        this.pendingBlockEntityTags.clear();
        this.upgradeData.a(this);
    }
    
    @Nullable
    private BlockEntity loadBlockEntity(final BlockPos pos, final CompoundTag compoundTag) {
        BlockEntity blockEntity3;
        if ("DUMMY".equals(compoundTag.getString("id"))) {
            final Block block4 = this.getBlockState(pos).getBlock();
            if (block4 instanceof BlockEntityProvider) {
                blockEntity3 = ((BlockEntityProvider)block4).createBlockEntity(this.world);
            }
            else {
                blockEntity3 = null;
                WorldChunk.LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", pos, this.getBlockState(pos));
            }
        }
        else {
            blockEntity3 = BlockEntity.createFromTag(compoundTag);
        }
        if (blockEntity3 != null) {
            blockEntity3.setPos(pos);
            this.addBlockEntity(blockEntity3);
        }
        else {
            WorldChunk.LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(pos), pos);
        }
        return blockEntity3;
    }
    
    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }
    
    @Override
    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }
    
    public void a(final ServerWorld serverWorld) {
        this.blockTickScheduler = new SimpleTickScheduler<Block>(Registry.BLOCK::getId, serverWorld.getBlockTickScheduler().getScheduledTicksInChunk(true, this.pos));
        this.fluidTickScheduler = new SimpleTickScheduler<Fluid>(Registry.FLUID::getId, serverWorld.getFluidTickScheduler().getScheduledTicksInChunk(true, this.pos));
        this.setShouldSave(true);
    }
    
    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }
    
    public ChunkHolder.LevelType getLevelType() {
        if (this.levelTypeProvider == null) {
            return ChunkHolder.LevelType.BORDER;
        }
        return this.levelTypeProvider.get();
    }
    
    public void setLevelTypeProvider(final Supplier<ChunkHolder.LevelType> levelTypeProvider) {
        this.levelTypeProvider = levelTypeProvider;
    }
    
    @Override
    public void setLightingProvider(final LightingProvider lightingProvider) {
    }
    
    @Override
    public boolean isLightOn() {
        return this.isLightOn;
    }
    
    @Override
    public void setLightOn(final boolean lightOn) {
        this.isLightOn = lightOn;
        this.setShouldSave(true);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY_SECTION = null;
    }
    
    public enum CreationType
    {
        a, 
        b, 
        c;
    }
}
