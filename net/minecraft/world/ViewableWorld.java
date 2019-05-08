package net.minecraft.world;

import net.minecraft.world.dimension.Dimension;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.shape.VoxelSet;
import java.util.function.Predicate;
import net.minecraft.util.shape.OffsetVoxelShape;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.BooleanBiFunction;
import java.util.stream.Stream;
import java.util.Set;
import java.util.Collections;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.Entity;
import net.minecraft.world.border.WorldBorder;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ViewableWorld extends ExtendedBlockView
{
    default boolean isAir(final BlockPos pos) {
        return this.getBlockState(pos).isAir();
    }
    
    default boolean v(final BlockPos blockPos) {
        if (blockPos.getY() >= this.getSeaLevel()) {
            return this.isSkyVisible(blockPos);
        }
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
        if (!this.isSkyVisible(blockPos2)) {
            return false;
        }
        for (blockPos2 = blockPos2.down(); blockPos2.getY() > blockPos.getY(); blockPos2 = blockPos2.down()) {
            final BlockState blockState3 = this.getBlockState(blockPos2);
            if (blockState3.getLightSubtracted(this, blockPos2) > 0 && !blockState3.getMaterial().isLiquid()) {
                return false;
            }
        }
        return true;
    }
    
    int getLightLevel(final BlockPos arg1, final int arg2);
    
    @Nullable
    Chunk getChunk(final int arg1, final int arg2, final ChunkStatus arg3, final boolean arg4);
    
    @Deprecated
    boolean isChunkLoaded(final int arg1, final int arg2);
    
    BlockPos getTopPosition(final Heightmap.Type arg1, final BlockPos arg2);
    
    int getTop(final Heightmap.Type arg1, final int arg2, final int arg3);
    
    default float getBrightness(final BlockPos blockPos) {
        return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
    }
    
    int getAmbientDarkness();
    
    WorldBorder getWorldBorder();
    
    boolean intersectsEntities(@Nullable final Entity arg1, final VoxelShape arg2);
    
    int getEmittedStrongRedstonePower(final BlockPos arg1, final Direction arg2);
    
    boolean isClient();
    
    int getSeaLevel();
    
    default Chunk getChunk(final BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }
    
    default Chunk getChunk(final int chunkX, final int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
    }
    
    default Chunk getChunk(final int chunkX, final int chunkZ, final ChunkStatus requiredState) {
        return this.getChunk(chunkX, chunkZ, requiredState, true);
    }
    
    default ChunkStatus getLeastChunkStatusForCollisionCalculation() {
        return ChunkStatus.EMPTY;
    }
    
    default boolean canPlace(final BlockState state, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final VoxelShape voxelShape4 = state.getCollisionShape(this, pos, verticalEntityPosition);
        return voxelShape4.isEmpty() || this.intersectsEntities(null, voxelShape4.offset(pos.getX(), pos.getY(), pos.getZ()));
    }
    
    default boolean intersectsEntities(final Entity entity) {
        return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
    }
    
    default boolean doesNotCollide(final BoundingBox boundingBox) {
        return this.getCollisionShapes(null, boundingBox, Collections.<Entity>emptySet()).allMatch(VoxelShape::isEmpty);
    }
    
    default boolean doesNotCollide(final Entity entity) {
        return this.getCollisionShapes(entity, entity.getBoundingBox(), Collections.<Entity>emptySet()).allMatch(VoxelShape::isEmpty);
    }
    
    default boolean doesNotCollide(final Entity entity, final BoundingBox entityBoundingBox) {
        return this.getCollisionShapes(entity, entityBoundingBox, Collections.<Entity>emptySet()).allMatch(VoxelShape::isEmpty);
    }
    
    default boolean doesNotCollide(final Entity entity, final BoundingBox entityBoundingBox, final Set<Entity> otherEntities) {
        return this.getCollisionShapes(entity, entityBoundingBox, otherEntities).allMatch(VoxelShape::isEmpty);
    }
    
    default Stream<VoxelShape> getCollisionShapes(@Nullable final Entity entity, final VoxelShape shape, final Set<Entity> otherEntities) {
        return Stream.<VoxelShape>empty();
    }
    
    default Stream<VoxelShape> getCollisionShapes(@Nullable final Entity entity, final BoundingBox boundingBox, final Set<Entity> otherEntities) {
        final VoxelShape voxelShape3 = VoxelShapes.cuboid(boundingBox);
        Stream<VoxelShape> stream5;
        if (entity == null) {
            final VerticalEntityPosition verticalEntityPosition6 = VerticalEntityPosition.minValue();
            stream5 = Stream.<VoxelShape>empty();
        }
        else {
            final VerticalEntityPosition verticalEntityPosition6 = VerticalEntityPosition.fromEntity(entity);
            final VoxelShape voxelShape4 = this.getWorldBorder().asVoxelShape();
            final boolean boolean8 = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
            final boolean boolean9 = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
            if (!boolean8 && boolean9) {
                stream5 = Stream.<VoxelShape>concat(Stream.of((T)voxelShape4), this.getCollisionShapes(entity, voxelShape3, otherEntities));
            }
            else {
                stream5 = this.getCollisionShapes(entity, voxelShape3, otherEntities);
            }
        }
        final int integer7 = MathHelper.floor(voxelShape3.getMinimum(Direction.Axis.X)) - 1;
        final int integer8 = MathHelper.ceil(voxelShape3.getMaximum(Direction.Axis.X)) + 1;
        final int integer9 = MathHelper.floor(voxelShape3.getMinimum(Direction.Axis.Y)) - 1;
        final int integer10 = MathHelper.ceil(voxelShape3.getMaximum(Direction.Axis.Y)) + 1;
        final int integer11 = MathHelper.floor(voxelShape3.getMinimum(Direction.Axis.Z)) - 1;
        final int integer12 = MathHelper.ceil(voxelShape3.getMaximum(Direction.Axis.Z)) + 1;
        final VoxelSet voxelSet13 = new BitSetVoxelSet(integer8 - integer7, integer10 - integer9, integer12 - integer11);
        final VoxelShape shape1;
        final Predicate<VoxelShape> predicate14 = voxelShape2 -> !voxelShape2.isEmpty() && VoxelShapes.matchesAnywhere(shape1, voxelShape2, BooleanBiFunction.AND);
        final AtomicReference<ChunkPos> atomicReference15 = new AtomicReference<ChunkPos>(new ChunkPos(integer7 >> 4, integer11 >> 4));
        final AtomicReference<Chunk> atomicReference16 = new AtomicReference<Chunk>(this.getChunk(integer7 >> 4, integer11 >> 4, this.getLeastChunkStatusForCollisionCalculation(), false));
        final int integer13;
        final int integer14;
        final int integer15;
        final int n;
        final int n2;
        boolean boolean10;
        final int n3;
        final int n4;
        boolean boolean11;
        final int n5;
        final int n6;
        boolean boolean12;
        final AtomicReference<ChunkPos> atomicReference17;
        ChunkPos chunkPos18;
        int integer16;
        int integer17;
        Chunk chunk19;
        final AtomicReference<Chunk> atomicReference18;
        final VerticalEntityPosition ePos;
        VoxelShape voxelShape5;
        VoxelShape voxelShape6;
        final VoxelSet set;
        final Stream<VoxelShape> stream6 = BlockPos.stream(integer7, integer9, integer11, integer8 - 1, integer10 - 1, integer12 - 1).<VoxelShape>map(blockPos -> {
            integer13 = blockPos.getX();
            integer14 = blockPos.getY();
            integer15 = blockPos.getZ();
            if (World.isHeightInvalid(integer14)) {
                return VoxelShapes.empty();
            }
            else {
                boolean10 = (integer13 == n || integer13 == n2 - 1);
                boolean11 = (integer14 == n3 || integer14 == n4 - 1);
                boolean12 = (integer15 == n5 || integer15 == n6 - 1);
                chunkPos18 = atomicReference17.get();
                integer16 = integer13 >> 4;
                integer17 = integer15 >> 4;
                if (chunkPos18.x != integer16 || chunkPos18.z != integer17) {
                    chunk19 = this.getChunk(integer16, integer17, this.getLeastChunkStatusForCollisionCalculation(), false);
                    atomicReference17.set(new ChunkPos(integer16, integer17));
                    atomicReference18.set(chunk19);
                }
                else {
                    chunk19 = atomicReference18.get();
                }
                if ((boolean10 && boolean11) || (boolean11 && boolean12) || (boolean12 && boolean10) || chunk19 == null) {
                    return VoxelShapes.empty();
                }
                else {
                    voxelShape5 = chunk19.getBlockState(blockPos).getCollisionShape(this, blockPos, ePos);
                    voxelShape6 = VoxelShapes.empty().offset(-integer13, -integer14, -integer15);
                    if (VoxelShapes.matchesAnywhere(voxelShape6, voxelShape5, BooleanBiFunction.AND)) {
                        return VoxelShapes.empty();
                    }
                    else if (voxelShape5 == VoxelShapes.fullCube()) {
                        set.set(integer13 - n, integer14 - n3, integer15 - n5, true, true);
                        return VoxelShapes.empty();
                    }
                    else {
                        return voxelShape5.offset(integer13, integer14, integer15);
                    }
                }
            }
        });
        return Stream.<VoxelShape>concat(stream5, Stream.concat(stream6, Stream.<OffsetVoxelShape>generate(() -> new OffsetVoxelShape(voxelSet13, integer7, integer9, integer11)).limit(1L)).filter(predicate14));
    }
    
    default boolean isWaterAt(final BlockPos pos) {
        return this.getFluidState(pos).matches(FluidTags.a);
    }
    
    default boolean intersectsFluid(final BoundingBox boundingBox) {
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
                        if (!blockState13.getFluidState().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    default int getLightLevel(final BlockPos blockPos) {
        return this.d(blockPos, this.getAmbientDarkness());
    }
    
    default int d(final BlockPos blockPos, final int darkness) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        return this.getLightLevel(blockPos, darkness);
    }
    
    @Deprecated
    default boolean isBlockLoaded(final BlockPos blockPos) {
        return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }
    
    @Deprecated
    default boolean isAreaLoaded(final BlockPos min, final BlockPos max) {
        return this.isAreaLoaded(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }
    
    @Deprecated
    default boolean isAreaLoaded(int minX, final int minY, int minZ, int maxX, final int integer5, int integer6) {
        if (integer5 < 0 || minY >= 256) {
            return false;
        }
        minX >>= 4;
        minZ >>= 4;
        maxX >>= 4;
        integer6 >>= 4;
        for (int integer7 = minX; integer7 <= maxX; ++integer7) {
            for (int integer8 = minZ; integer8 <= integer6; ++integer8) {
                if (!this.isChunkLoaded(integer7, integer8)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    Dimension getDimension();
}
