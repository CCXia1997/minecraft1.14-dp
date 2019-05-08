package net.minecraft.world;

import java.io.IOException;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.SharedConstants;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Supplier;
import net.minecraft.world.dimension.DimensionType;
import java.io.File;
import javax.annotation.Nullable;
import com.mojang.datafixers.DataFixer;
import net.minecraft.world.storage.RegionBasedStorage;

public class VersionedChunkStorage extends RegionBasedStorage
{
    protected final DataFixer dataFixer;
    @Nullable
    private FeatureUpdater featureUpdater;
    
    public VersionedChunkStorage(final File file, final DataFixer dataFixer) {
        super(file);
        this.dataFixer = dataFixer;
    }
    
    public CompoundTag updateChunkTag(final DimensionType dimensionType, final Supplier<PersistentStateManager> persistentStateManagerFactory, CompoundTag tag) {
        final int integer4 = getDataVersion(tag);
        final int integer5 = 1493;
        if (integer4 < 1493) {
            tag = TagHelper.update(this.dataFixer, DataFixTypes.c, tag, integer4, 1493);
            if (tag.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                if (this.featureUpdater == null) {
                    this.featureUpdater = FeatureUpdater.create(dimensionType, persistentStateManagerFactory.get());
                }
                tag = this.featureUpdater.getUpdatedReferences(tag);
            }
        }
        tag = TagHelper.update(this.dataFixer, DataFixTypes.c, tag, Math.max(1493, integer4));
        if (integer4 < SharedConstants.getGameVersion().getWorldVersion()) {
            tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        return tag;
    }
    
    public static int getDataVersion(final CompoundTag tag) {
        return tag.containsKey("DataVersion", 99) ? tag.getInt("DataVersion") : -1;
    }
    
    public void setTagAt(final ChunkPos pos, final CompoundTag tag) throws IOException {
        super.setTagAt(pos, tag);
        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(pos.toLong());
        }
    }
}
