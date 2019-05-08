package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.util.math.Direction;
import net.minecraft.util.LevelPropagator;
import net.minecraft.world.chunk.WorldNibbleStorage;

public abstract class ChunkLightProvider<M extends WorldNibbleStorage<M>, S extends LightStorage<M>> extends LevelPropagator implements ChunkLightingView
{
    private static final Direction[] DIRECTIONS;
    protected final ChunkProvider chunkProvider;
    protected final LightType type;
    protected final S lightStorage;
    private boolean e;
    private final BlockPos.Mutable f;
    private final long[] g;
    private final BlockView[] h;
    
    public ChunkLightProvider(final ChunkProvider chunkProvider, final LightType type, final S lightStorage) {
        super(16, 256, 8192);
        this.f = new BlockPos.Mutable();
        this.g = new long[2];
        this.h = new BlockView[2];
        this.chunkProvider = chunkProvider;
        this.type = type;
        this.lightStorage = lightStorage;
        this.c();
    }
    
    @Override
    protected void fullyUpdate(final long id) {
        this.lightStorage.updateAll();
        if (this.lightStorage.hasChunk(ChunkSectionPos.toChunkLong(id))) {
            super.fullyUpdate(id);
        }
    }
    
    @Nullable
    private BlockView a(final int chunkX, final int chunkZ) {
        final long long3 = ChunkPos.toLong(chunkX, chunkZ);
        for (int integer5 = 0; integer5 < 2; ++integer5) {
            if (long3 == this.g[integer5]) {
                return this.h[integer5];
            }
        }
        final BlockView blockView5 = this.chunkProvider.getChunk(chunkX, chunkZ);
        for (int integer6 = 1; integer6 > 0; --integer6) {
            this.g[integer6] = this.g[integer6 - 1];
            this.h[integer6] = this.h[integer6 - 1];
        }
        this.g[0] = long3;
        return this.h[0] = blockView5;
    }
    
    private void c() {
        Arrays.fill(this.g, ChunkPos.INVALID);
        Arrays.fill(this.h, null);
    }
    
    protected VoxelShape a(final long long1, @Nullable final AtomicInteger atomicInteger3) {
        if (long1 == Long.MAX_VALUE) {
            if (atomicInteger3 != null) {
                atomicInteger3.set(0);
            }
            return VoxelShapes.empty();
        }
        final int integer4 = ChunkSectionPos.toChunkCoord(BlockPos.unpackLongX(long1));
        final int integer5 = ChunkSectionPos.toChunkCoord(BlockPos.unpackLongZ(long1));
        final BlockView blockView6 = this.a(integer4, integer5);
        if (blockView6 == null) {
            if (atomicInteger3 != null) {
                atomicInteger3.set(16);
            }
            return VoxelShapes.fullCube();
        }
        this.f.setFromLong(long1);
        final BlockState blockState7 = blockView6.getBlockState(this.f);
        final boolean boolean8 = blockState7.isFullBoundsCubeForCulling() && blockState7.g();
        if (atomicInteger3 != null) {
            atomicInteger3.set(blockState7.getLightSubtracted(this.chunkProvider.getWorld(), this.f));
        }
        return boolean8 ? blockState7.j(this.chunkProvider.getWorld(), this.f) : VoxelShapes.empty();
    }
    
    public static int a(final BlockView blockView, final BlockState blockState2, final BlockPos blockPos3, final BlockState blockState4, final BlockPos blockPos5, final Direction direction, final int integer) {
        final boolean boolean8 = blockState2.isFullBoundsCubeForCulling() && blockState2.g();
        final boolean boolean9 = blockState4.isFullBoundsCubeForCulling() && blockState4.g();
        if (!boolean8 && !boolean9) {
            return integer;
        }
        final VoxelShape voxelShape10 = boolean8 ? blockState2.j(blockView, blockPos3) : VoxelShapes.empty();
        final VoxelShape voxelShape11 = boolean9 ? blockState4.j(blockView, blockPos5) : VoxelShapes.empty();
        if (VoxelShapes.b(voxelShape10, voxelShape11, direction)) {
            return 16;
        }
        return integer;
    }
    
    @Override
    protected boolean isInvalid(final long id) {
        return id == Long.MAX_VALUE;
    }
    
    @Override
    protected int getMergedLevel(final long id, final long sourceId, final int limitLevel) {
        return 0;
    }
    
    @Override
    protected int getLevel(final long id) {
        if (id == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - this.lightStorage.get(id);
    }
    
    protected int getCurrentLevelFromArray(final ChunkNibbleArray array, final long blockPos) {
        return 15 - array.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }
    
    @Override
    protected void setLevel(final long id, final int level) {
        this.lightStorage.set(id, Math.min(15, 15 - level));
    }
    
    @Override
    protected int getPropagatedLevel(final long fromId, final long toId, final int currentLevel) {
        return 0;
    }
    
    public boolean hasUpdates() {
        return this.hasLevelUpdates() || this.lightStorage.hasLevelUpdates() || this.lightStorage.hasLightUpdates();
    }
    
    public int doLightUpdates(int stepCount, final boolean boolean2, final boolean boolean3) {
        if (!this.e) {
            if (this.lightStorage.hasLevelUpdates()) {
                stepCount = this.lightStorage.updateAllRecursively(stepCount);
                if (stepCount == 0) {
                    return stepCount;
                }
            }
            this.lightStorage.processUpdates(this, boolean2, boolean3);
        }
        this.e = true;
        if (this.hasLevelUpdates()) {
            stepCount = this.updateAllRecursively(stepCount);
            this.c();
            if (stepCount == 0) {
                return stepCount;
            }
        }
        this.e = false;
        this.lightStorage.notifyChunkProvider();
        return stepCount;
    }
    
    protected void setSection(final long long1, final ChunkNibbleArray chunkNibbleArray3) {
        this.lightStorage.scheduleToUpdate(long1, chunkNibbleArray3);
    }
    
    @Nullable
    @Override
    public ChunkNibbleArray getChunkLightArray(final ChunkSectionPos chunkSectionPos) {
        return this.lightStorage.getDataForChunk(chunkSectionPos.asLong(), false);
    }
    
    @Override
    public int getLightLevel(final BlockPos blockPos) {
        return this.lightStorage.getLight(blockPos.asLong());
    }
    
    @Environment(EnvType.CLIENT)
    public String b(final long long1) {
        return "" + this.lightStorage.getLevel(long1);
    }
    
    public void queueLightCheck(final BlockPos blockPos) {
        final long long2 = blockPos.asLong();
        this.fullyUpdate(long2);
        for (final Direction direction7 : ChunkLightProvider.DIRECTIONS) {
            this.fullyUpdate(BlockPos.offset(long2, direction7));
        }
    }
    
    public void a(final BlockPos blockPos, final int integer) {
    }
    
    @Override
    public void updateSectionStatus(final ChunkSectionPos pos, final boolean status) {
        this.lightStorage.scheduleChunkLightUpdate(pos.asLong(), status);
    }
    
    public void a(final ChunkPos chunkPos, final boolean boolean2) {
        final long long3 = ChunkSectionPos.toLightStorageIndex(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
        this.lightStorage.updateAll();
        this.lightStorage.b(long3, boolean2);
    }
    
    static {
        DIRECTIONS = Direction.values();
    }
}
