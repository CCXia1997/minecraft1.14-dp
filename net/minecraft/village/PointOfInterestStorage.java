package net.minecraft.village;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import net.minecraft.util.SectionRelativeLevelPropagator;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.chunk.ChunkSection;
import java.util.function.BooleanSupplier;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import java.util.function.Function;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkPos;
import java.util.stream.Stream;
import java.util.function.Predicate;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage extends SerializingRegionBasedStorage<PointOfInterestSet>
{
    private final PointOfInterestDistanceTracker pointOfInterestDistanceTracker;
    
    public PointOfInterestStorage(final File file, final DataFixer dataFixer) {
        super(file, PointOfInterestSet::new, PointOfInterestSet::new, dataFixer, DataFixTypes.j);
        this.pointOfInterestDistanceTracker = new PointOfInterestDistanceTracker();
    }
    
    public void add(final BlockPos pos, final PointOfInterestType type) {
        this.getOrCreate(ChunkSectionPos.from(pos).asLong()).add(pos, type);
    }
    
    public void remove(final BlockPos pos) {
        this.getOrCreate(ChunkSectionPos.from(pos).asLong()).remove(pos);
    }
    
    public long count(final Predicate<PointOfInterestType> typePredicate, final BlockPos pos, final int radius, final OccupationStatus occupationStatus) {
        return this.get(typePredicate, pos, radius, occupationStatus).count();
    }
    
    private Stream<PointOfInterest> get(final Predicate<PointOfInterestType> typePredicate, final BlockPos pos, final int radius, final OccupationStatus occupationStatus) {
        final int integer5 = radius * radius;
        return ChunkPos.stream(new ChunkPos(pos), Math.floorDiv(radius, 16)).<PointOfInterest>flatMap(chunkPos -> this.get(typePredicate, chunkPos, occupationStatus).filter(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(pos) <= integer5));
    }
    
    public Stream<PointOfInterest> get(final Predicate<PointOfInterestType> typePredicate, final ChunkPos pos, final OccupationStatus occupationStatus) {
        return IntStream.range(0, 16).boxed().<PointOfInterest>flatMap(integer -> this.get(typePredicate, ChunkSectionPos.from(pos, integer).asLong(), occupationStatus));
    }
    
    private Stream<PointOfInterest> get(final Predicate<PointOfInterestType> typePredicate, final long pos, final OccupationStatus occupationStatus) {
        return this.get(pos).<Stream<PointOfInterest>>map(pointOfInterestSet -> pointOfInterestSet.get(typePredicate, occupationStatus)).orElseGet(Stream::empty);
    }
    
    public Optional<BlockPos> getPosition(final Predicate<PointOfInterestType> typePredicate, final Predicate<BlockPos> positionPredicate, final BlockPos pos, final int radius, final OccupationStatus occupationStatus) {
        return this.get(typePredicate, pos, radius, occupationStatus).<BlockPos>map(PointOfInterest::getPos).filter(positionPredicate).findFirst();
    }
    
    public Optional<BlockPos> getNearestPosition(final Predicate<PointOfInterestType> typePredicate, final Predicate<BlockPos> blockPosPredicate, final BlockPos pos, final int radius, final OccupationStatus occupationStatus) {
        return this.get(typePredicate, pos, radius, occupationStatus).<BlockPos>map(PointOfInterest::getPos).sorted(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(pos))).filter(blockPosPredicate).findFirst();
    }
    
    public Optional<BlockPos> getPosition(final Predicate<PointOfInterestType> typePredicate, final Predicate<BlockPos> positionPredicate, final BlockPos pos, final int radius) {
        return this.get(typePredicate, pos, radius, OccupationStatus.HAS_SPACE).filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos())).findFirst().<BlockPos>map(pointOfInterest -> {
            pointOfInterest.reserveTicket();
            return pointOfInterest.getPos();
        });
    }
    
    public Optional<BlockPos> getNearestPosition(final Predicate<PointOfInterestType> typePredicate, final Predicate<BlockPos> positionPredicate, final BlockPos pos, final int radius) {
        return this.get(typePredicate, pos, radius, OccupationStatus.HAS_SPACE).sorted(Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(pos))).filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos())).findFirst().<BlockPos>map(pointOfInterest -> {
            pointOfInterest.reserveTicket();
            return pointOfInterest.getPos();
        });
    }
    
    public Optional<BlockPos> getPosition(final Predicate<PointOfInterestType> typePredicate, final Predicate<BlockPos> positionPredicate, final OccupationStatus occupationStatus, final BlockPos pos, final int radius, final Random random) {
        final List<PointOfInterest> list7 = this.get(typePredicate, pos, radius, occupationStatus).collect(Collectors.toList());
        Collections.shuffle(list7, random);
        return list7.stream().filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos())).findFirst().<BlockPos>map(PointOfInterest::getPos);
    }
    
    public boolean releaseTicket(final BlockPos pos) {
        return this.getOrCreate(ChunkSectionPos.from(pos).asLong()).releaseTicket(pos);
    }
    
    public boolean test(final BlockPos pos, final Predicate<PointOfInterestType> predicate) {
        return this.get(ChunkSectionPos.from(pos).asLong()).<Boolean>map(pointOfInterestSet -> pointOfInterestSet.test(pos, predicate)).orElse(false);
    }
    
    public Optional<PointOfInterestType> getType(final BlockPos pos) {
        final PointOfInterestSet pointOfInterestSet2 = this.getOrCreate(ChunkSectionPos.from(pos).asLong());
        return pointOfInterestSet2.getType(pos);
    }
    
    public int getDistanceFromNearestOccupied(final ChunkSectionPos pos) {
        this.pointOfInterestDistanceTracker.update();
        return this.pointOfInterestDistanceTracker.getLevel(pos.asLong());
    }
    
    private boolean isOccupied(final long pos) {
        return this.get(PointOfInterestType.ALWAYS_TRUE, pos, OccupationStatus.IS_OCCUPIED).count() > 0L;
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        super.tick(booleanSupplier);
        this.pointOfInterestDistanceTracker.update();
    }
    
    @Override
    protected void onUpdate(final long pos) {
        super.onUpdate(pos);
        this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
    }
    
    @Override
    protected void onLoad(final long pos) {
        this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
    }
    
    public void initForPalette(final ChunkPos chunkPos, final ChunkSection chunkSection) {
        final ChunkSectionPos chunkSectionPos3 = ChunkSectionPos.from(chunkPos, chunkSection.getYOffset() >> 4);
        final ChunkSectionPos chunkSectionPos4;
        final ChunkSectionPos chunkSectionPos5;
        PointOfInterestSet pointOfInterestSet2;
        SystemUtil.<PointOfInterestSet>ifPresentOrElse(this.get(chunkSectionPos3.asLong()), pointOfInterestSet -> pointOfInterestSet.updatePointsOfInterest(biConsumer -> {
            if (shouldScan(chunkSection)) {
                this.scanAndPopulate(chunkSection, chunkSectionPos4, biConsumer);
            }
        }), () -> {
            if (shouldScan(chunkSection)) {
                pointOfInterestSet2 = this.getOrCreate(chunkSectionPos5.asLong());
                this.scanAndPopulate(chunkSection, chunkSectionPos5, pointOfInterestSet2::add);
            }
        });
    }
    
    private static boolean shouldScan(final ChunkSection chunkSection) {
        return PointOfInterestType.getAllAssociatedStates().anyMatch(chunkSection::a);
    }
    
    private void scanAndPopulate(final ChunkSection chunkSection, final ChunkSectionPos chunkSectionPos, final BiConsumer<BlockPos, PointOfInterestType> biConsumer) {
        final BlockState blockState4;
        chunkSectionPos.streamBlocks().forEach(blockPos -> {
            blockState4 = chunkSection.getBlockState(ChunkSectionPos.toLocalCoord(blockPos.getX()), ChunkSectionPos.toLocalCoord(blockPos.getY()), ChunkSectionPos.toLocalCoord(blockPos.getZ()));
            PointOfInterestType.from(blockState4).ifPresent(pointOfInterestType -> biConsumer.accept(blockPos, pointOfInterestType));
        });
    }
    
    public enum OccupationStatus
    {
        HAS_SPACE(PointOfInterest::hasSpace), 
        IS_OCCUPIED(PointOfInterest::isOccupied), 
        ANY(pointOfInterest -> true);
        
        private final Predicate<? super PointOfInterest> predicate;
        
        private OccupationStatus(final Predicate<? super PointOfInterest> predicate) {
            this.predicate = predicate;
        }
        
        public Predicate<? super PointOfInterest> getPredicate() {
            return this.predicate;
        }
    }
    
    final class PointOfInterestDistanceTracker extends SectionRelativeLevelPropagator
    {
        private final Long2ByteMap distances;
        
        protected PointOfInterestDistanceTracker() {
            super(7, 16, 256);
            (this.distances = (Long2ByteMap)new Long2ByteOpenHashMap()).defaultReturnValue((byte)7);
        }
        
        @Override
        protected int getInitialLevel(final long id) {
            return PointOfInterestStorage.this.isOccupied(id) ? 0 : 7;
        }
        
        @Override
        protected int getLevel(final long id) {
            return this.distances.get(id);
        }
        
        @Override
        protected void setLevel(final long id, final int level) {
            if (level > 6) {
                this.distances.remove(id);
            }
            else {
                this.distances.put(id, (byte)level);
            }
        }
        
        public void update() {
            super.updateAllRecursively(Integer.MAX_VALUE);
        }
    }
}
