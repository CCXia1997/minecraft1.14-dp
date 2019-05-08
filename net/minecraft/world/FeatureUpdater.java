package net.minecraft.world;

import net.minecraft.util.SystemUtil;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.nbt.ListTag;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.IOException;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.nbt.Tag;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.Locale;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Iterator;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Map;

public class FeatureUpdater
{
    private static final Map<String, String> OLD_TO_NEW;
    private static final Map<String, String> ANCIENT_TO_OLD;
    private final boolean needsUpdate;
    private final Map<String, Long2ObjectMap<CompoundTag>> featureIdToChunkTag;
    private final Map<String, ChunkUpdateState> updateStates;
    private final List<String> f;
    private final List<String> g;
    
    public FeatureUpdater(@Nullable final PersistentStateManager persistentStateManager, final List<String> list2, final List<String> list3) {
        this.featureIdToChunkTag = Maps.newHashMap();
        this.updateStates = Maps.newHashMap();
        this.f = list2;
        this.g = list3;
        this.init(persistentStateManager);
        boolean boolean4 = false;
        for (final String string6 : this.g) {
            boolean4 |= (this.featureIdToChunkTag.get(string6) != null);
        }
        this.needsUpdate = boolean4;
    }
    
    public void markResolved(final long long1) {
        for (final String string4 : this.f) {
            final ChunkUpdateState chunkUpdateState5 = this.updateStates.get(string4);
            if (chunkUpdateState5 != null && chunkUpdateState5.isRemaining(long1)) {
                chunkUpdateState5.markResolved(long1);
                chunkUpdateState5.markDirty();
            }
        }
    }
    
    public CompoundTag getUpdatedReferences(CompoundTag compoundTag) {
        final CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        final ChunkPos chunkPos3 = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
        if (this.needsUpdate(chunkPos3.x, chunkPos3.z)) {
            compoundTag = this.getUpdatedStarts(compoundTag, chunkPos3);
        }
        final CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        final CompoundTag compoundTag4 = compoundTag3.getCompound("References");
        for (final String string7 : this.g) {
            final StructureFeature<?> structureFeature8 = Feature.STRUCTURES.get(string7.toLowerCase(Locale.ROOT));
            if (!compoundTag4.containsKey(string7, 12)) {
                if (structureFeature8 == null) {
                    continue;
                }
                final int integer9 = structureFeature8.getRadius();
                final LongList longList10 = (LongList)new LongArrayList();
                for (int integer10 = chunkPos3.x - integer9; integer10 <= chunkPos3.x + integer9; ++integer10) {
                    for (int integer11 = chunkPos3.z - integer9; integer11 <= chunkPos3.z + integer9; ++integer11) {
                        if (this.needsUpdate(integer10, integer11, string7)) {
                            longList10.add(ChunkPos.toLong(integer10, integer11));
                        }
                    }
                }
                compoundTag4.putLongArray(string7, (List<Long>)longList10);
            }
        }
        compoundTag3.put("References", compoundTag4);
        compoundTag2.put("Structures", compoundTag3);
        compoundTag.put("Level", compoundTag2);
        return compoundTag;
    }
    
    private boolean needsUpdate(final int chunkX, final int chunkZ, final String id) {
        return this.needsUpdate && (this.featureIdToChunkTag.get(id) != null && this.updateStates.get(FeatureUpdater.OLD_TO_NEW.get(id)).contains(ChunkPos.toLong(chunkX, chunkZ)));
    }
    
    private boolean needsUpdate(final int chunkX, final int chunkZ) {
        if (!this.needsUpdate) {
            return false;
        }
        for (final String string4 : this.g) {
            if (this.featureIdToChunkTag.get(string4) != null && this.updateStates.get(FeatureUpdater.OLD_TO_NEW.get(string4)).isRemaining(ChunkPos.toLong(chunkX, chunkZ))) {
                return true;
            }
        }
        return false;
    }
    
    private CompoundTag getUpdatedStarts(final CompoundTag compoundTag, final ChunkPos chunkPos) {
        final CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        final CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        final CompoundTag compoundTag4 = compoundTag3.getCompound("Starts");
        for (final String string7 : this.g) {
            final Long2ObjectMap<CompoundTag> long2ObjectMap8 = this.featureIdToChunkTag.get(string7);
            if (long2ObjectMap8 == null) {
                continue;
            }
            final long long9 = chunkPos.toLong();
            if (!this.updateStates.get(FeatureUpdater.OLD_TO_NEW.get(string7)).isRemaining(long9)) {
                continue;
            }
            final CompoundTag compoundTag5 = (CompoundTag)long2ObjectMap8.get(long9);
            if (compoundTag5 == null) {
                continue;
            }
            compoundTag4.put(string7, compoundTag5);
        }
        compoundTag3.put("Starts", compoundTag4);
        compoundTag2.put("Structures", compoundTag3);
        compoundTag.put("Level", compoundTag2);
        return compoundTag;
    }
    
    private void init(@Nullable final PersistentStateManager persistentStateManager) {
        if (persistentStateManager == null) {
            return;
        }
        for (final String string3 : this.f) {
            CompoundTag compoundTag4 = new CompoundTag();
            try {
                compoundTag4 = persistentStateManager.a(string3, 1493).getCompound("data").getCompound("Features");
                if (compoundTag4.isEmpty()) {
                    continue;
                }
            }
            catch (IOException ex) {}
            for (final String string4 : compoundTag4.getKeys()) {
                final CompoundTag compoundTag5 = compoundTag4.getCompound(string4);
                final long long8 = ChunkPos.toLong(compoundTag5.getInt("ChunkX"), compoundTag5.getInt("ChunkZ"));
                final ListTag listTag10 = compoundTag5.getList("Children", 10);
                if (!listTag10.isEmpty()) {
                    final String string5 = listTag10.getCompoundTag(0).getString("id");
                    final String string6 = FeatureUpdater.ANCIENT_TO_OLD.get(string5);
                    if (string6 != null) {
                        compoundTag5.putString("id", string6);
                    }
                }
                final String string5 = compoundTag5.getString("id");
                this.featureIdToChunkTag.computeIfAbsent(string5, string -> new Long2ObjectOpenHashMap()).put(long8, compoundTag5);
            }
            final String string7 = string3 + "_index";
            final ChunkUpdateState chunkUpdateState6 = persistentStateManager.<ChunkUpdateState>getOrCreate(() -> new ChunkUpdateState(string7), string7);
            if (chunkUpdateState6.getAll().isEmpty()) {
                final ChunkUpdateState chunkUpdateState7 = new ChunkUpdateState(string7);
                this.updateStates.put(string3, chunkUpdateState7);
                for (final String string8 : compoundTag4.getKeys()) {
                    final CompoundTag compoundTag6 = compoundTag4.getCompound(string8);
                    chunkUpdateState7.add(ChunkPos.toLong(compoundTag6.getInt("ChunkX"), compoundTag6.getInt("ChunkZ")));
                }
                chunkUpdateState7.markDirty();
            }
            else {
                this.updateStates.put(string3, chunkUpdateState6);
            }
        }
    }
    
    public static FeatureUpdater create(final DimensionType dimensionType, @Nullable final PersistentStateManager persistentStateManager) {
        if (dimensionType == DimensionType.a) {
            return new FeatureUpdater(persistentStateManager, ImmutableList.<String>of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.<String>of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
        }
        if (dimensionType == DimensionType.b) {
            final List<String> list3 = ImmutableList.<String>of("Fortress");
            return new FeatureUpdater(persistentStateManager, list3, list3);
        }
        if (dimensionType == DimensionType.c) {
            final List<String> list3 = ImmutableList.<String>of("EndCity");
            return new FeatureUpdater(persistentStateManager, list3, list3);
        }
        throw new RuntimeException(String.format("Unknown dimension type : %s", dimensionType));
    }
    
    static {
        OLD_TO_NEW = SystemUtil.<Map<String, String>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put("Village", "Village");
            hashMap.put("Mineshaft", "Mineshaft");
            hashMap.put("Mansion", "Mansion");
            hashMap.put("Igloo", "Temple");
            hashMap.put("Desert_Pyramid", "Temple");
            hashMap.put("Jungle_Pyramid", "Temple");
            hashMap.put("Swamp_Hut", "Temple");
            hashMap.put("Stronghold", "Stronghold");
            hashMap.put("Monument", "Monument");
            hashMap.put("Fortress", "Fortress");
            hashMap.put("EndCity", "EndCity");
            return;
        });
        ANCIENT_TO_OLD = SystemUtil.<Map<String, String>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put("Iglu", "Igloo");
            hashMap.put("TeDP", "Desert_Pyramid");
            hashMap.put("TeJP", "Jungle_Pyramid");
            hashMap.put("TeSH", "Swamp_Hut");
        });
    }
}
