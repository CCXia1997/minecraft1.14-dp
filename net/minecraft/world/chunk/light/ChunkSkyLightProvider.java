package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BlockPos;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.util.math.Direction;

public final class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage>
{
    private static final Direction[] DIRECTIONS_SKYLIGHT;
    private static final Direction[] HORIZONTAL_DIRECTIONS;
    
    public ChunkSkyLightProvider(final ChunkProvider chunkProvider) {
        super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
    }
    
    @Override
    protected int getPropagatedLevel(final long fromId, final long toId, int currentLevel) {
        if (toId == Long.MAX_VALUE) {
            return 15;
        }
        if (fromId == Long.MAX_VALUE) {
            if (!((SkyLightStorage)this.lightStorage).l(toId)) {
                return 15;
            }
            currentLevel = 0;
        }
        if (currentLevel >= 15) {
            return currentLevel;
        }
        final AtomicInteger atomicInteger6 = new AtomicInteger();
        final VoxelShape voxelShape7 = this.a(toId, atomicInteger6);
        if (atomicInteger6.get() >= 15) {
            return 15;
        }
        final int integer8 = BlockPos.unpackLongX(fromId);
        final int integer9 = BlockPos.unpackLongY(fromId);
        final int integer10 = BlockPos.unpackLongZ(fromId);
        final int integer11 = BlockPos.unpackLongX(toId);
        final int integer12 = BlockPos.unpackLongY(toId);
        final int integer13 = BlockPos.unpackLongZ(toId);
        final boolean boolean14 = integer8 == integer11 && integer10 == integer13;
        final int integer14 = Integer.signum(integer11 - integer8);
        final int integer15 = Integer.signum(integer12 - integer9);
        final int integer16 = Integer.signum(integer13 - integer10);
        Direction direction18;
        if (fromId == Long.MAX_VALUE) {
            direction18 = Direction.DOWN;
        }
        else {
            direction18 = Direction.fromVector(integer14, integer15, integer16);
        }
        final VoxelShape voxelShape8 = this.a(fromId, null);
        if (direction18 != null) {
            if (VoxelShapes.b(voxelShape8, voxelShape7, direction18)) {
                return 15;
            }
        }
        else {
            if (VoxelShapes.b(voxelShape8, VoxelShapes.empty(), Direction.DOWN)) {
                return 15;
            }
            final int integer17 = boolean14 ? -1 : 0;
            final Direction direction19 = Direction.fromVector(integer14, integer17, integer16);
            if (direction19 == null) {
                return 15;
            }
            if (VoxelShapes.b(VoxelShapes.empty(), voxelShape7, direction19)) {
                return 15;
            }
        }
        final boolean boolean15 = fromId == Long.MAX_VALUE || (boolean14 && integer9 > integer12);
        if (boolean15 && currentLevel == 0 && atomicInteger6.get() == 0) {
            return 0;
        }
        return currentLevel + Math.max(1, atomicInteger6.get());
    }
    
    @Override
    protected void updateNeighborsRecursively(final long id, final int targetLevel, final boolean mergeAsMin) {
        final long long5 = ChunkSectionPos.toChunkLong(id);
        final int integer7 = BlockPos.unpackLongY(id);
        final int integer8 = ChunkSectionPos.toLocalCoord(integer7);
        final int integer9 = ChunkSectionPos.toChunkCoord(integer7);
        int integer10;
        if (integer8 != 0) {
            integer10 = 0;
        }
        else {
            int integer11;
            for (integer11 = 0; !((SkyLightStorage)this.lightStorage).hasChunk(ChunkSectionPos.offsetPacked(long5, 0, -integer11 - 1, 0)) && ((SkyLightStorage)this.lightStorage).isAboveMinimumHeight(integer9 - integer11 - 1); ++integer11) {}
            integer10 = integer11;
        }
        final long long6 = BlockPos.add(id, 0, -1 - integer10 * 16, 0);
        final long long7 = ChunkSectionPos.toChunkLong(long6);
        if (long5 == long7 || ((SkyLightStorage)this.lightStorage).hasChunk(long7)) {
            this.updateRecursively(id, long6, targetLevel, mergeAsMin);
        }
        final long long8 = BlockPos.offset(id, Direction.UP);
        final long long9 = ChunkSectionPos.toChunkLong(long8);
        if (long5 == long9 || ((SkyLightStorage)this.lightStorage).hasChunk(long9)) {
            this.updateRecursively(id, long8, targetLevel, mergeAsMin);
        }
        for (final Direction direction22 : ChunkSkyLightProvider.HORIZONTAL_DIRECTIONS) {
            int integer12 = 0;
            do {
                final long long10 = BlockPos.add(id, direction22.getOffsetX(), -integer12, direction22.getOffsetZ());
                final long long11 = ChunkSectionPos.toChunkLong(long10);
                if (long5 == long11) {
                    this.updateRecursively(id, long10, targetLevel, mergeAsMin);
                    break;
                }
                if (!((SkyLightStorage)this.lightStorage).hasChunk(long11)) {
                    continue;
                }
                this.updateRecursively(id, long10, targetLevel, mergeAsMin);
            } while (++integer12 <= integer10 * 16);
        }
    }
    
    @Override
    protected int getMergedLevel(final long id, final long sourceId, final int limitLevel) {
        int integer6 = limitLevel;
        if (Long.MAX_VALUE != sourceId) {
            final int integer7 = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
            if (integer6 > integer7) {
                integer6 = integer7;
            }
            if (integer6 == 0) {
                return integer6;
            }
        }
        final long long7 = ChunkSectionPos.toChunkLong(id);
        final ChunkNibbleArray chunkNibbleArray9 = ((SkyLightStorage)this.lightStorage).getDataForChunk(long7, true);
        for (final Direction direction13 : ChunkSkyLightProvider.DIRECTIONS_SKYLIGHT) {
            long long8 = BlockPos.offset(id, direction13);
            long long9 = ChunkSectionPos.toChunkLong(long8);
            ChunkNibbleArray chunkNibbleArray10;
            if (long7 == long9) {
                chunkNibbleArray10 = chunkNibbleArray9;
            }
            else {
                chunkNibbleArray10 = ((SkyLightStorage)this.lightStorage).getDataForChunk(long9, true);
            }
            if (chunkNibbleArray10 != null) {
                if (long8 != sourceId) {
                    final int integer8 = this.getPropagatedLevel(long8, id, this.getCurrentLevelFromArray(chunkNibbleArray10, long8));
                    if (integer6 > integer8) {
                        integer6 = integer8;
                    }
                    if (integer6 == 0) {
                        return integer6;
                    }
                }
            }
            else if (direction13 != Direction.DOWN) {
                for (long8 = BlockPos.removeChunkSectionLocalY(long8); !((SkyLightStorage)this.lightStorage).hasChunk(long9) && !((SkyLightStorage)this.lightStorage).m(long9); long9 = ChunkSectionPos.offsetPacked(long9, Direction.UP), long8 = BlockPos.add(long8, 0, 16, 0)) {}
                final ChunkNibbleArray chunkNibbleArray11 = ((SkyLightStorage)this.lightStorage).getDataForChunk(long9, true);
                if (long8 != sourceId) {
                    int integer9;
                    if (chunkNibbleArray11 != null) {
                        integer9 = this.getPropagatedLevel(long8, id, this.getCurrentLevelFromArray(chunkNibbleArray11, long8));
                    }
                    else {
                        integer9 = (((SkyLightStorage)this.lightStorage).n(long9) ? 0 : 15);
                    }
                    if (integer6 > integer9) {
                        integer6 = integer9;
                    }
                    if (integer6 == 0) {
                        return integer6;
                    }
                }
            }
        }
        return integer6;
    }
    
    @Override
    protected void fullyUpdate(long id) {
        ((SkyLightStorage)this.lightStorage).updateAll();
        long long3 = ChunkSectionPos.toChunkLong(id);
        if (((SkyLightStorage)this.lightStorage).hasChunk(long3)) {
            super.fullyUpdate(id);
        }
        else {
            for (id = BlockPos.removeChunkSectionLocalY(id); !((SkyLightStorage)this.lightStorage).hasChunk(long3) && !((SkyLightStorage)this.lightStorage).m(long3); long3 = ChunkSectionPos.offsetPacked(long3, Direction.UP), id = BlockPos.add(id, 0, 16, 0)) {}
            if (((SkyLightStorage)this.lightStorage).hasChunk(long3)) {
                super.fullyUpdate(id);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public String b(final long long1) {
        return super.b(long1) + (((SkyLightStorage)this.lightStorage).m(long1) ? "*" : "");
    }
    
    static {
        DIRECTIONS_SKYLIGHT = Direction.values();
        HORIZONTAL_DIRECTIONS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    }
}
