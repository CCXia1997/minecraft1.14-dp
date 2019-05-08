package net.minecraft.structure.pool;

import java.util.Iterator;
import net.minecraft.structure.JigsawJunction;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3i;
import net.minecraft.state.property.Property;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.util.BlockRotation;
import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.util.shape.VoxelShape;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.structure.PoolStructurePiece;
import org.apache.logging.log4j.LogManager;
import net.minecraft.structure.StructureFeatures;
import java.util.Random;
import net.minecraft.structure.StructurePiece;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator
{
    private static final Logger LOGGER;
    public static final StructurePoolRegistry REGISTRY;
    
    public static void addPieces(final Identifier startPoolId, final int size, final PieceFactory pieceFactory, final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final BlockPos pos, final List<StructurePiece> pieces, final Random random) {
        StructureFeatures.initialize();
        new c(startPoolId, size, pieceFactory, chunkGenerator, structureManager, pos, pieces, random);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        (REGISTRY = new StructurePoolRegistry()).add(StructurePool.EMPTY);
    }
    
    static final class b
    {
        private final PoolStructurePiece a;
        private final AtomicReference<VoxelShape> b;
        private final int c;
        private final int d;
        
        private b(final PoolStructurePiece poolStructurePiece, final AtomicReference<VoxelShape> atomicReference, final int integer3, final int integer4) {
            this.a = poolStructurePiece;
            this.b = atomicReference;
            this.c = integer3;
            this.d = integer4;
        }
    }
    
    static final class c
    {
        private final int a;
        private final PieceFactory b;
        private final ChunkGenerator<?> c;
        private final StructureManager d;
        private final List<StructurePiece> e;
        private final Random f;
        private final Deque<b> g;
        
        public c(final Identifier identifier, final int integer, final PieceFactory pieceFactory, final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final BlockPos blockPos, final List<StructurePiece> list, final Random random) {
            this.g = Queues.newArrayDeque();
            this.a = integer;
            this.b = pieceFactory;
            this.c = chunkGenerator;
            this.d = structureManager;
            this.e = list;
            this.f = random;
            final BlockRotation blockRotation9 = BlockRotation.random(random);
            final StructurePool structurePool10 = StructurePoolBasedGenerator.REGISTRY.get(identifier);
            final StructurePoolElement structurePoolElement11 = structurePool10.getRandomElement(random);
            final PoolStructurePiece poolStructurePiece12 = pieceFactory.create(structureManager, structurePoolElement11, blockPos, structurePoolElement11.d(), blockRotation9, structurePoolElement11.getBoundingBox(structureManager, blockPos, blockRotation9));
            final MutableIntBoundingBox mutableIntBoundingBox13 = poolStructurePiece12.getBoundingBox();
            final int integer2 = (mutableIntBoundingBox13.maxX + mutableIntBoundingBox13.minX) / 2;
            final int integer3 = (mutableIntBoundingBox13.maxZ + mutableIntBoundingBox13.minZ) / 2;
            final int integer4 = chunkGenerator.b(integer2, integer3, Heightmap.Type.a);
            poolStructurePiece12.translate(0, integer4 - (mutableIntBoundingBox13.minY + poolStructurePiece12.getGroundLevelDelta()), 0);
            list.add(poolStructurePiece12);
            if (integer <= 0) {
                return;
            }
            final int integer5 = 80;
            final BoundingBox boundingBox18 = new BoundingBox(integer2 - 80, integer4 - 80, integer3 - 80, integer2 + 80 + 1, integer4 + 80 + 1, integer3 + 80 + 1);
            this.g.addLast(new b(poolStructurePiece12, new AtomicReference((V)VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(boundingBox18), VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox13)), BooleanBiFunction.ONLY_FIRST)), integer4 + 80, 0));
            while (!this.g.isEmpty()) {
                final b b19 = this.g.removeFirst();
                this.a(b19.a, b19.b, b19.c, b19.d);
            }
        }
        
        private void a(final PoolStructurePiece poolStructurePiece, final AtomicReference<VoxelShape> atomicReference, final int integer3, final int integer4) {
            final StructurePoolElement structurePoolElement5 = poolStructurePiece.getPoolElement();
            final BlockPos blockPos6 = poolStructurePiece.getPos();
            final BlockRotation blockRotation7 = poolStructurePiece.getRotation();
            final StructurePool.Projection projection8 = structurePoolElement5.getProjection();
            final boolean boolean9 = projection8 == StructurePool.Projection.RIGID;
            final AtomicReference<VoxelShape> atomicReference2 = new AtomicReference<VoxelShape>();
            final MutableIntBoundingBox mutableIntBoundingBox11 = poolStructurePiece.getBoundingBox();
            final int integer5 = mutableIntBoundingBox11.minY;
            for (final Structure.StructureBlockInfo structureBlockInfo2 : structurePoolElement5.getStructureBlockInfos(this.d, blockPos6, blockRotation7, this.f)) {
                final Direction direction15 = structureBlockInfo2.state.<Direction>get((Property<Direction>)JigsawBlock.FACING);
                final BlockPos blockPos7 = structureBlockInfo2.pos;
                final BlockPos blockPos8 = blockPos7.offset(direction15);
                final int integer6 = blockPos7.getY() - integer5;
                int integer7 = -1;
                final StructurePool structurePool20 = StructurePoolBasedGenerator.REGISTRY.get(new Identifier(structureBlockInfo2.tag.getString("target_pool")));
                final StructurePool structurePool21 = StructurePoolBasedGenerator.REGISTRY.get(structurePool20.getTerminatorsId());
                if (structurePool20 == StructurePool.INVALID || (structurePool20.getElementCount() == 0 && structurePool20 != StructurePool.EMPTY)) {
                    StructurePoolBasedGenerator.LOGGER.warn("Empty or none existent pool: {}", structureBlockInfo2.tag.getString("target_pool"));
                }
                else {
                    final boolean boolean10 = mutableIntBoundingBox11.contains(blockPos8);
                    AtomicReference<VoxelShape> atomicReference3;
                    int integer8;
                    if (boolean10) {
                        atomicReference3 = atomicReference2;
                        integer8 = integer5;
                        if (atomicReference2.get() == null) {
                            atomicReference2.set(VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox11)));
                        }
                    }
                    else {
                        atomicReference3 = atomicReference;
                        integer8 = integer3;
                    }
                    final List<StructurePoolElement> list25 = Lists.newArrayList();
                    if (integer4 != this.a) {
                        list25.addAll(structurePool20.getElementIndicesInRandomOrder(this.f));
                    }
                    list25.addAll(structurePool21.getElementIndicesInRandomOrder(this.f));
                Label_1101:
                    for (final StructurePoolElement structurePoolElement6 : list25) {
                        if (structurePoolElement6 == EmptyPoolElement.INSTANCE) {
                            break;
                        }
                        for (final BlockRotation blockRotation8 : BlockRotation.randomRotationOrder(this.f)) {
                            final List<Structure.StructureBlockInfo> list26 = structurePoolElement6.getStructureBlockInfos(this.d, BlockPos.ORIGIN, blockRotation8, this.f);
                            final MutableIntBoundingBox mutableIntBoundingBox12 = structurePoolElement6.getBoundingBox(this.d, BlockPos.ORIGIN, blockRotation8);
                            int integer9;
                            if (mutableIntBoundingBox12.getBlockCountY() > 16) {
                                integer9 = 0;
                            }
                            else {
                                Identifier identifier3;
                                StructurePool structurePool22;
                                StructurePool structurePool23;
                                integer9 = list26.stream().mapToInt(structureBlockInfo -> {
                                    if (!mutableIntBoundingBox12.contains(structureBlockInfo.pos.offset(structureBlockInfo.state.<Direction>get((Property<Direction>)JigsawBlock.FACING)))) {
                                        return 0;
                                    }
                                    else {
                                        identifier3 = new Identifier(structureBlockInfo.tag.getString("target_pool"));
                                        structurePool22 = StructurePoolBasedGenerator.REGISTRY.get(identifier3);
                                        structurePool23 = StructurePoolBasedGenerator.REGISTRY.get(structurePool22.getTerminatorsId());
                                        return Math.max(structurePool22.a(this.d), structurePool23.a(this.d));
                                    }
                                }).max().orElse(0);
                            }
                            for (final Structure.StructureBlockInfo structureBlockInfo3 : list26) {
                                if (!JigsawBlock.attachmentMatches(structureBlockInfo2, structureBlockInfo3)) {
                                    continue;
                                }
                                final BlockPos blockPos9 = structureBlockInfo3.pos;
                                final BlockPos blockPos10 = new BlockPos(blockPos8.getX() - blockPos9.getX(), blockPos8.getY() - blockPos9.getY(), blockPos8.getZ() - blockPos9.getZ());
                                final MutableIntBoundingBox mutableIntBoundingBox13 = structurePoolElement6.getBoundingBox(this.d, blockPos10, blockRotation8);
                                final int integer10 = mutableIntBoundingBox13.minY;
                                final StructurePool.Projection projection9 = structurePoolElement6.getProjection();
                                final boolean boolean11 = projection9 == StructurePool.Projection.RIGID;
                                final int integer11 = blockPos9.getY();
                                final int integer12 = integer6 - integer11 + structureBlockInfo2.state.<Direction>get((Property<Direction>)JigsawBlock.FACING).getOffsetY();
                                int integer13;
                                if (boolean9 && boolean11) {
                                    integer13 = integer5 + integer12;
                                }
                                else {
                                    if (integer7 == -1) {
                                        integer7 = this.c.b(blockPos7.getX(), blockPos7.getZ(), Heightmap.Type.a);
                                    }
                                    integer13 = integer7 - integer11;
                                }
                                final int integer14 = integer13 - integer10;
                                final MutableIntBoundingBox mutableIntBoundingBox14 = mutableIntBoundingBox13.b(0, integer14, 0);
                                final BlockPos blockPos11 = blockPos10.add(0, integer14, 0);
                                if (integer9 > 0) {
                                    final int integer15 = Math.max(integer9 + 1, mutableIntBoundingBox14.maxY - mutableIntBoundingBox14.minY);
                                    mutableIntBoundingBox14.maxY = mutableIntBoundingBox14.minY + integer15;
                                }
                                if (VoxelShapes.matchesAnywhere(atomicReference3.get(), VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox14).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) {
                                    continue;
                                }
                                atomicReference3.set(VoxelShapes.combine(atomicReference3.get(), VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox14)), BooleanBiFunction.ONLY_FIRST));
                                final int integer15 = poolStructurePiece.getGroundLevelDelta();
                                int integer16;
                                if (boolean11) {
                                    integer16 = integer15 - integer12;
                                }
                                else {
                                    integer16 = structurePoolElement6.d();
                                }
                                final PoolStructurePiece poolStructurePiece2 = this.b.create(this.d, structurePoolElement6, blockPos11, integer16, blockRotation8, mutableIntBoundingBox14);
                                int integer17;
                                if (boolean9) {
                                    integer17 = integer5 + integer6;
                                }
                                else if (boolean11) {
                                    integer17 = integer13 + integer11;
                                }
                                else {
                                    if (integer7 == -1) {
                                        integer7 = this.c.b(blockPos7.getX(), blockPos7.getZ(), Heightmap.Type.a);
                                    }
                                    integer17 = integer7 + integer12 / 2;
                                }
                                poolStructurePiece.addJunction(new JigsawJunction(blockPos8.getX(), integer17 - integer6 + integer15, blockPos8.getZ(), integer12, projection9));
                                poolStructurePiece2.addJunction(new JigsawJunction(blockPos7.getX(), integer17 - integer11 + integer16, blockPos7.getZ(), -integer12, projection8));
                                this.e.add(poolStructurePiece2);
                                if (integer4 + 1 <= this.a) {
                                    this.g.addLast(new b(poolStructurePiece2, (AtomicReference)atomicReference3, integer8, integer4 + 1));
                                    break Label_1101;
                                }
                                break Label_1101;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public interface PieceFactory
    {
        PoolStructurePiece create(final StructureManager arg1, final StructurePoolElement arg2, final BlockPos arg3, final int arg4, final BlockRotation arg5, final MutableIntBoundingBox arg6);
    }
}
