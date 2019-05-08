package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.world.loot.LootTables;
import net.minecraft.block.StairsBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.state.property.Property;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.IWorld;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MutableIntBoundingBox;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import java.util.Random;
import java.util.List;

public class NetherFortressGenerator
{
    private static final n[] a;
    private static final n[] b;
    
    private static Piece generatePiece(final n n, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, final Direction direction, final int integer8) {
        final Class<? extends Piece> class9 = n.a;
        Piece piece10 = null;
        if (class9 == Bridge.class) {
            piece10 = Bridge.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == BridgeCrossing.class) {
            piece10 = BridgeCrossing.a(list, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == BridgeSmallCrossing.class) {
            piece10 = BridgeSmallCrossing.a(list, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == BridgeStairs.class) {
            piece10 = BridgeStairs.a(list, integer4, integer5, integer6, integer8, direction);
        }
        else if (class9 == BridgePlatform.class) {
            piece10 = BridgePlatform.a(list, integer4, integer5, integer6, integer8, direction);
        }
        else if (class9 == CorridorExit.class) {
            piece10 = CorridorExit.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == SmallCorridor.class) {
            piece10 = SmallCorridor.a(list, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == CorridorRightTurn.class) {
            piece10 = CorridorRightTurn.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == CorridorLeftTurn.class) {
            piece10 = CorridorLeftTurn.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == CorridorStairs.class) {
            piece10 = CorridorStairs.a(list, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == CorridorBalcony.class) {
            piece10 = CorridorBalcony.a(list, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == CorridorCrossing.class) {
            piece10 = CorridorCrossing.a(list, integer4, integer5, integer6, direction, integer8);
        }
        else if (class9 == CorridorNetherWartsRoom.class) {
            piece10 = CorridorNetherWartsRoom.a(list, integer4, integer5, integer6, direction, integer8);
        }
        return piece10;
    }
    
    static {
        a = new n[] { new n(Bridge.class, 30, 0, true), new n(BridgeCrossing.class, 10, 4), new n(BridgeSmallCrossing.class, 10, 4), new n(BridgeStairs.class, 10, 3), new n(BridgePlatform.class, 5, 2), new n(CorridorExit.class, 5, 1) };
        b = new n[] { new n(SmallCorridor.class, 25, 0, true), new n(CorridorCrossing.class, 15, 5), new n(CorridorRightTurn.class, 5, 10), new n(CorridorLeftTurn.class, 5, 10), new n(CorridorStairs.class, 10, 3, true), new n(CorridorBalcony.class, 7, 2), new n(CorridorNetherWartsRoom.class, 5, 2) };
    }
    
    static class n
    {
        public final Class<? extends Piece> a;
        public final int b;
        public int c;
        public final int d;
        public final boolean e;
        
        public n(final Class<? extends Piece> class1, final int integer2, final int integer3, final boolean boolean4) {
            this.a = class1;
            this.b = integer2;
            this.d = integer3;
            this.e = boolean4;
        }
        
        public n(final Class<? extends Piece> class1, final int integer2, final int integer3) {
            this(class1, integer2, integer3, false);
        }
        
        public boolean a(final int integer) {
            return this.d == 0 || this.c < this.d;
        }
        
        public boolean a() {
            return this.d == 0 || this.c < this.d;
        }
    }
    
    abstract static class Piece extends StructurePiece
    {
        protected Piece(final StructurePieceType type, final int integer) {
            super(type, integer);
        }
        
        public Piece(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
        }
        
        private int a(final List<n> list) {
            boolean boolean2 = false;
            int integer3 = 0;
            for (final n n5 : list) {
                if (n5.d > 0 && n5.c < n5.d) {
                    boolean2 = true;
                }
                integer3 += n5.b;
            }
            return boolean2 ? integer3 : -1;
        }
        
        private Piece a(final Start start, final List<n> list2, final List<StructurePiece> list3, final Random random, final int integer5, final int integer6, final int integer7, final Direction direction, final int integer9) {
            final int integer10 = this.a(list2);
            final boolean boolean11 = integer10 > 0 && integer9 <= 30;
            int integer11 = 0;
            while (integer11 < 5 && boolean11) {
                ++integer11;
                int integer12 = random.nextInt(integer10);
                for (final n n15 : list2) {
                    integer12 -= n15.b;
                    if (integer12 < 0) {
                        if (!n15.a(integer9)) {
                            break;
                        }
                        if (n15 == start.a && !n15.e) {
                            break;
                        }
                        final Piece piece16 = generatePiece(n15, list3, random, integer5, integer6, integer7, direction, integer9);
                        if (piece16 != null) {
                            final n n16 = n15;
                            ++n16.c;
                            start.a = n15;
                            if (!n15.a()) {
                                list2.remove(n15);
                            }
                            return piece16;
                        }
                        continue;
                    }
                }
            }
            return BridgeEnd.a(list3, random, integer5, integer6, integer7, direction, integer9);
        }
        
        private StructurePiece a(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, @Nullable final Direction direction, final int integer8, final boolean boolean9) {
            if (Math.abs(integer4 - start.getBoundingBox().minX) > 112 || Math.abs(integer6 - start.getBoundingBox().minZ) > 112) {
                return BridgeEnd.a(list, random, integer4, integer5, integer6, direction, integer8);
            }
            List<n> list2 = start.bridgePieces;
            if (boolean9) {
                list2 = start.corridorPieces;
            }
            final StructurePiece structurePiece11 = this.a(start, list2, list, random, integer4, integer5, integer6, direction, integer8 + 1);
            if (structurePiece11 != null) {
                list.add(structurePiece11);
                start.d.add(structurePiece11);
            }
            return structurePiece11;
        }
        
        @Nullable
        protected StructurePiece a(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final boolean boolean6) {
            final Direction direction7 = this.getFacing();
            if (direction7 != null) {
                switch (direction7) {
                    case NORTH: {
                        return this.a(start, list, random, this.boundingBox.minX + integer4, this.boundingBox.minY + integer5, this.boundingBox.minZ - 1, direction7, this.h(), boolean6);
                    }
                    case SOUTH: {
                        return this.a(start, list, random, this.boundingBox.minX + integer4, this.boundingBox.minY + integer5, this.boundingBox.maxZ + 1, direction7, this.h(), boolean6);
                    }
                    case WEST: {
                        return this.a(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + integer5, this.boundingBox.minZ + integer4, direction7, this.h(), boolean6);
                    }
                    case EAST: {
                        return this.a(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + integer5, this.boundingBox.minZ + integer4, direction7, this.h(), boolean6);
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructurePiece b(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final boolean boolean6) {
            final Direction direction7 = this.getFacing();
            if (direction7 != null) {
                switch (direction7) {
                    case NORTH: {
                        return this.a(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.WEST, this.h(), boolean6);
                    }
                    case SOUTH: {
                        return this.a(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.WEST, this.h(), boolean6);
                    }
                    case WEST: {
                        return this.a(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.minZ - 1, Direction.NORTH, this.h(), boolean6);
                    }
                    case EAST: {
                        return this.a(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.minZ - 1, Direction.NORTH, this.h(), boolean6);
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructurePiece c(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final boolean boolean6) {
            final Direction direction7 = this.getFacing();
            if (direction7 != null) {
                switch (direction7) {
                    case NORTH: {
                        return this.a(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.EAST, this.h(), boolean6);
                    }
                    case SOUTH: {
                        return this.a(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.EAST, this.h(), boolean6);
                    }
                    case WEST: {
                        return this.a(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.maxZ + 1, Direction.SOUTH, this.h(), boolean6);
                    }
                    case EAST: {
                        return this.a(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.maxZ + 1, Direction.SOUTH, this.h(), boolean6);
                    }
                }
            }
            return null;
        }
        
        protected static boolean a(final MutableIntBoundingBox mutableIntBoundingBox) {
            return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
        }
    }
    
    public static class Start extends BridgeCrossing
    {
        public n a;
        public List<n> bridgePieces;
        public List<n> corridorPieces;
        public final List<StructurePiece> d;
        
        public Start(final Random random, final int x, final int z) {
            super(random, x, z);
            this.d = Lists.newArrayList();
            this.bridgePieces = Lists.newArrayList();
            for (final n n7 : NetherFortressGenerator.a) {
                n7.c = 0;
                this.bridgePieces.add(n7);
            }
            this.corridorPieces = Lists.newArrayList();
            for (final n n7 : NetherFortressGenerator.b) {
                n7.c = 0;
                this.corridorPieces.add(n7);
            }
        }
        
        public Start(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_START, compoundTag);
            this.d = Lists.newArrayList();
        }
    }
    
    public static class Bridge extends Piece
    {
        public Bridge(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public Bridge(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 3, false);
        }
        
        public static Bridge a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -3, 0, 5, 10, 19, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new Bridge(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 4, 4, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 0, 5, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 5, 0, 4, 5, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 2, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 13, 4, 2, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 0, 15, 4, 1, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer5 = 0; integer5 <= 4; ++integer5) {
                for (int integer6 = 0; integer6 <= 2; ++integer6) {
                    this.b(world, Blocks.dN.getDefaultState(), integer5, -1, integer6, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), integer5, -1, 18 - integer6, boundingBox);
                }
            }
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)blockState5).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)blockState5).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true);
            this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 4, 1, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 0, 3, 4, 0, 4, 4, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 0, 3, 14, 0, 4, 14, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 17, 0, 4, 17, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 4, 1, blockState7, blockState7, false);
            this.fillWithOutline(world, boundingBox, 4, 3, 4, 4, 4, 4, blockState7, blockState7, false);
            this.fillWithOutline(world, boundingBox, 4, 3, 14, 4, 4, 14, blockState7, blockState7, false);
            this.fillWithOutline(world, boundingBox, 4, 1, 17, 4, 4, 17, blockState7, blockState7, false);
            return true;
        }
    }
    
    public static class BridgeEnd extends Piece
    {
        private final int seed;
        
        public BridgeEnd(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.seed = random.nextInt();
        }
        
        public BridgeEnd(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, compoundTag);
            this.seed = compoundTag.getInt("Seed");
        }
        
        public static BridgeEnd a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -3, 0, 5, 10, 8, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new BridgeEnd(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putInt("Seed", this.seed);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final Random random2 = new Random(this.seed);
            for (int integer6 = 0; integer6 <= 4; ++integer6) {
                for (int integer7 = 3; integer7 <= 4; ++integer7) {
                    final int integer8 = random2.nextInt(8);
                    this.fillWithOutline(world, boundingBox, integer6, integer7, 0, integer6, integer7, integer8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                }
            }
            int integer6 = random2.nextInt(8);
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 0, 5, integer6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            integer6 = random2.nextInt(8);
            this.fillWithOutline(world, boundingBox, 4, 5, 0, 4, 5, integer6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (integer6 = 0; integer6 <= 4; ++integer6) {
                final int integer7 = random2.nextInt(5);
                this.fillWithOutline(world, boundingBox, integer6, 2, 0, integer6, 2, integer7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            }
            for (integer6 = 0; integer6 <= 4; ++integer6) {
                for (int integer7 = 0; integer7 <= 1; ++integer7) {
                    final int integer8 = random2.nextInt(3);
                    this.fillWithOutline(world, boundingBox, integer6, integer7, 0, integer6, integer7, integer8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                }
            }
            return true;
        }
    }
    
    public static class BridgeCrossing extends Piece
    {
        public BridgeCrossing(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        protected BridgeCrossing(final Random random, final int x, final int z) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
            this.setOrientation(Direction.Type.HORIZONTAL.random(random));
            if (this.getFacing().getAxis() == Direction.Axis.Z) {
                this.boundingBox = new MutableIntBoundingBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
            }
            else {
                this.boundingBox = new MutableIntBoundingBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
            }
        }
        
        protected BridgeCrossing(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
        }
        
        public BridgeCrossing(final StructureManager structureManager, final CompoundTag compoundTag) {
            this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 8, 3, false);
            this.b((Start)structurePiece, list, random, 3, 8, false);
            this.c((Start)structurePiece, list, random, 3, 8, false);
        }
        
        public static BridgeCrossing a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -8, -3, 0, 19, 10, 19, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new BridgeCrossing(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 7, 3, 0, 11, 4, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 3, 7, 18, 4, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 7, 5, 0, 7, 5, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 7, 5, 11, 7, 5, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 11, 5, 0, 11, 5, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 11, 5, 11, 11, 5, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 7, 7, 5, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 11, 5, 7, 18, 5, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 11, 7, 5, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 11, 5, 11, 18, 5, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 7, 2, 0, 11, 2, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 7, 2, 13, 11, 2, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 7, 0, 0, 11, 1, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 7, 0, 15, 11, 1, 18, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer5 = 7; integer5 <= 11; ++integer5) {
                for (int integer6 = 0; integer6 <= 2; ++integer6) {
                    this.b(world, Blocks.dN.getDefaultState(), integer5, -1, integer6, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), integer5, -1, 18 - integer6, boundingBox);
                }
            }
            this.fillWithOutline(world, boundingBox, 0, 2, 7, 5, 2, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 13, 2, 7, 18, 2, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 0, 7, 3, 1, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 15, 0, 7, 18, 1, 11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer5 = 0; integer5 <= 2; ++integer5) {
                for (int integer6 = 7; integer6 <= 11; ++integer6) {
                    this.b(world, Blocks.dN.getDefaultState(), integer5, -1, integer6, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), 18 - integer5, -1, integer6, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class BridgeSmallCrossing extends Piece
    {
        public BridgeSmallCrossing(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public BridgeSmallCrossing(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 2, 0, false);
            this.b((Start)structurePiece, list, random, 0, 2, false);
            this.c((Start)structurePiece, list, random, 0, 2, false);
        }
        
        public static BridgeSmallCrossing a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -2, 0, 0, 7, 9, 7, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new BridgeSmallCrossing(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 6, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 6, 1, 6, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 6, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 2, 6, 6, 6, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 6, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 6, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 2, 0, 6, 6, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 2, 5, 6, 6, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            this.fillWithOutline(world, boundingBox, 2, 6, 0, 4, 6, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 5, 0, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 2, 6, 6, 4, 6, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 6, 4, 5, 6, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 0, 6, 2, 0, 6, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 2, 0, 5, 4, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 6, 6, 2, 6, 6, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 5, 2, 6, 5, 4, blockState6, blockState6, false);
            for (int integer7 = 0; integer7 <= 6; ++integer7) {
                for (int integer8 = 0; integer8 <= 6; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class BridgeStairs extends Piece
    {
        public BridgeStairs(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public BridgeStairs(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.c((Start)structurePiece, list, random, 6, 2, false);
        }
        
        public static BridgeStairs a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final int integer5, final Direction direction) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -2, 0, 0, 7, 11, 7, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new BridgeStairs(integer5, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 8, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 8, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 8, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 2, 1, 6, 8, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 2, 6, 5, 8, 6, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            this.fillWithOutline(world, boundingBox, 0, 3, 2, 0, 5, 4, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 6, 3, 2, 6, 5, 2, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 6, 3, 4, 6, 5, 4, blockState6, blockState6, false);
            this.addBlock(world, Blocks.dN.getDefaultState(), 5, 2, 5, boundingBox);
            this.fillWithOutline(world, boundingBox, 4, 2, 5, 4, 3, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 3, 2, 5, 3, 4, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 2, 5, 2, 5, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 2, 5, 1, 6, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 7, 1, 5, 7, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 6, 0, 4, 8, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 5, 0, blockState5, blockState5, false);
            for (int integer7 = 0; integer7 <= 6; ++integer7) {
                for (int integer8 = 0; integer8 <= 6; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class BridgePlatform extends Piece
    {
        private boolean hasBlazeSpawner;
        
        public BridgePlatform(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public BridgePlatform(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, compoundTag);
            this.hasBlazeSpawner = compoundTag.getBoolean("Mob");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Mob", this.hasBlazeSpawner);
        }
        
        public static BridgePlatform a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final int integer5, final Direction direction) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -2, 0, 0, 7, 8, 9, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new BridgePlatform(integer5, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 0, 0, 5, 1, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 2, 1, 5, 2, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 3, 2, 5, 3, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 4, 3, 5, 4, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 2, 0, 1, 4, 2, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 2, 0, 5, 4, 2, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 5, 2, 1, 5, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 5, 2, 5, 5, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 3, 0, 5, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 5, 3, 6, 5, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 5, 8, 5, 5, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 1, 6, 3, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 5, 6, 3, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.EAST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.NORTH, true), 0, 6, 3, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.NORTH, true), 6, 6, 3, boundingBox);
            this.fillWithOutline(world, boundingBox, 0, 6, 4, 0, 6, 7, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 6, 6, 4, 6, 6, 7, blockState6, blockState6, false);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.EAST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true), 0, 6, 8, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true), 6, 6, 8, boundingBox);
            this.fillWithOutline(world, boundingBox, 1, 6, 8, 5, 6, 8, blockState5, blockState5, false);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 1, 7, 8, boundingBox);
            this.fillWithOutline(world, boundingBox, 2, 7, 8, 4, 7, 8, blockState5, blockState5, false);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 5, 7, 8, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 2, 8, 8, boundingBox);
            this.addBlock(world, blockState5, 3, 8, 8, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 4, 8, 8, boundingBox);
            if (!this.hasBlazeSpawner) {
                final BlockPos blockPos7 = new BlockPos(this.applyXTransform(3, 5), this.applyYTransform(5), this.applyZTransform(3, 5));
                if (boundingBox.contains(blockPos7)) {
                    this.hasBlazeSpawner = true;
                    world.setBlockState(blockPos7, Blocks.bN.getDefaultState(), 2);
                    final BlockEntity blockEntity8 = world.getBlockEntity(blockPos7);
                    if (blockEntity8 instanceof MobSpawnerBlockEntity) {
                        ((MobSpawnerBlockEntity)blockEntity8).getLogic().setEntityId(EntityType.BLAZE);
                    }
                }
            }
            for (int integer7 = 0; integer7 <= 6; ++integer7) {
                for (int integer8 = 0; integer8 <= 6; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class CorridorExit extends Piece
    {
        public CorridorExit(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public CorridorExit(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 5, 3, true);
        }
        
        public static CorridorExit a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -5, -3, 0, 13, 14, 13, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new CorridorExit(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 8, 0, 7, 8, 0, Blocks.dO.getDefaultState(), Blocks.dO.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            for (int integer7 = 1; integer7 <= 11; integer7 += 2) {
                this.fillWithOutline(world, boundingBox, integer7, 10, 0, integer7, 11, 0, blockState5, blockState5, false);
                this.fillWithOutline(world, boundingBox, integer7, 10, 12, integer7, 11, 12, blockState5, blockState5, false);
                this.fillWithOutline(world, boundingBox, 0, 10, integer7, 0, 11, integer7, blockState6, blockState6, false);
                this.fillWithOutline(world, boundingBox, 12, 10, integer7, 12, 11, integer7, blockState6, blockState6, false);
                this.addBlock(world, Blocks.dN.getDefaultState(), integer7, 13, 0, boundingBox);
                this.addBlock(world, Blocks.dN.getDefaultState(), integer7, 13, 12, boundingBox);
                this.addBlock(world, Blocks.dN.getDefaultState(), 0, 13, integer7, boundingBox);
                this.addBlock(world, Blocks.dN.getDefaultState(), 12, 13, integer7, boundingBox);
                if (integer7 != 11) {
                    this.addBlock(world, blockState5, integer7 + 1, 13, 0, boundingBox);
                    this.addBlock(world, blockState5, integer7 + 1, 13, 12, boundingBox);
                    this.addBlock(world, blockState6, 0, 13, integer7 + 1, boundingBox);
                    this.addBlock(world, blockState6, 12, 13, integer7 + 1, boundingBox);
                }
            }
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 0, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 12, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 12, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 0, boundingBox);
            for (int integer7 = 3; integer7 <= 9; integer7 += 2) {
                this.fillWithOutline(world, boundingBox, 1, 7, integer7, 1, 8, integer7, ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), false);
                this.fillWithOutline(world, boundingBox, 11, 7, integer7, 11, 8, integer7, ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), false);
            }
            this.fillWithOutline(world, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer7 = 4; integer7 <= 8; ++integer7) {
                for (int integer8 = 0; integer8 <= 2; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, 12 - integer8, boundingBox);
                }
            }
            for (int integer7 = 0; integer7 <= 2; ++integer7) {
                for (int integer8 = 4; integer8 <= 8; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), 12 - integer7, -1, integer8, boundingBox);
                }
            }
            this.fillWithOutline(world, boundingBox, 5, 5, 5, 7, 5, 7, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.addBlock(world, Blocks.dN.getDefaultState(), 6, 0, 6, boundingBox);
            this.addBlock(world, Blocks.B.getDefaultState(), 6, 5, 6, boundingBox);
            final BlockPos blockPos7 = new BlockPos(this.applyXTransform(6, 6), this.applyYTransform(5), this.applyZTransform(6, 6));
            if (boundingBox.contains(blockPos7)) {
                world.getFluidTickScheduler().schedule(blockPos7, Fluids.LAVA, 0);
            }
            return true;
        }
    }
    
    public static class CorridorNetherWartsRoom extends Piece
    {
        public CorridorNetherWartsRoom(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public CorridorNetherWartsRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 5, 3, true);
            this.a((Start)structurePiece, list, random, 5, 11, true);
        }
        
        public static CorridorNetherWartsRoom a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -5, -3, 0, 13, 14, 13, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new CorridorNetherWartsRoom(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true);
            final BlockState blockState8 = ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            for (int integer9 = 1; integer9 <= 11; integer9 += 2) {
                this.fillWithOutline(world, boundingBox, integer9, 10, 0, integer9, 11, 0, blockState5, blockState5, false);
                this.fillWithOutline(world, boundingBox, integer9, 10, 12, integer9, 11, 12, blockState5, blockState5, false);
                this.fillWithOutline(world, boundingBox, 0, 10, integer9, 0, 11, integer9, blockState6, blockState6, false);
                this.fillWithOutline(world, boundingBox, 12, 10, integer9, 12, 11, integer9, blockState6, blockState6, false);
                this.addBlock(world, Blocks.dN.getDefaultState(), integer9, 13, 0, boundingBox);
                this.addBlock(world, Blocks.dN.getDefaultState(), integer9, 13, 12, boundingBox);
                this.addBlock(world, Blocks.dN.getDefaultState(), 0, 13, integer9, boundingBox);
                this.addBlock(world, Blocks.dN.getDefaultState(), 12, 13, integer9, boundingBox);
                if (integer9 != 11) {
                    this.addBlock(world, blockState5, integer9 + 1, 13, 0, boundingBox);
                    this.addBlock(world, blockState5, integer9 + 1, 13, 12, boundingBox);
                    this.addBlock(world, blockState6, 0, 13, integer9 + 1, boundingBox);
                    this.addBlock(world, blockState6, 12, 13, integer9 + 1, boundingBox);
                }
            }
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 0, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 12, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 12, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 0, boundingBox);
            for (int integer9 = 3; integer9 <= 9; integer9 += 2) {
                this.fillWithOutline(world, boundingBox, 1, 7, integer9, 1, 8, integer9, blockState7, blockState7, false);
                this.fillWithOutline(world, boundingBox, 11, 7, integer9, 11, 8, integer9, blockState8, blockState8, false);
            }
            final BlockState blockState9 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dP.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.NORTH);
            for (int integer10 = 0; integer10 <= 6; ++integer10) {
                final int integer11 = integer10 + 4;
                for (int integer12 = 5; integer12 <= 7; ++integer12) {
                    this.addBlock(world, blockState9, integer12, 5 + integer10, integer11, boundingBox);
                }
                if (integer11 >= 5 && integer11 <= 8) {
                    this.fillWithOutline(world, boundingBox, 5, 5, integer11, 7, integer10 + 4, integer11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                }
                else if (integer11 >= 9 && integer11 <= 10) {
                    this.fillWithOutline(world, boundingBox, 5, 8, integer11, 7, integer10 + 4, integer11, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                }
                if (integer10 >= 1) {
                    this.fillWithOutline(world, boundingBox, 5, 6 + integer10, integer11, 7, 9 + integer10, integer11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                }
            }
            for (int integer10 = 5; integer10 <= 7; ++integer10) {
                this.addBlock(world, blockState9, integer10, 12, 11, boundingBox);
            }
            this.fillWithOutline(world, boundingBox, 5, 6, 7, 5, 7, 7, blockState8, blockState8, false);
            this.fillWithOutline(world, boundingBox, 7, 6, 7, 7, 7, 7, blockState7, blockState7, false);
            this.fillWithOutline(world, boundingBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 2, 3, 5, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 9, 3, 5, 10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 2, 5, 4, 2, 5, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 9, 5, 2, 10, 5, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 9, 5, 9, 10, 5, 10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 10, 5, 4, 10, 5, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            final BlockState blockState10 = ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.EAST);
            final BlockState blockState11 = ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.WEST);
            this.addBlock(world, blockState11, 4, 5, 2, boundingBox);
            this.addBlock(world, blockState11, 4, 5, 3, boundingBox);
            this.addBlock(world, blockState11, 4, 5, 9, boundingBox);
            this.addBlock(world, blockState11, 4, 5, 10, boundingBox);
            this.addBlock(world, blockState10, 8, 5, 2, boundingBox);
            this.addBlock(world, blockState10, 8, 5, 3, boundingBox);
            this.addBlock(world, blockState10, 8, 5, 9, boundingBox);
            this.addBlock(world, blockState10, 8, 5, 10, boundingBox);
            this.fillWithOutline(world, boundingBox, 3, 4, 4, 4, 4, 8, Blocks.cK.getDefaultState(), Blocks.cK.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 4, 4, 9, 4, 8, Blocks.cK.getDefaultState(), Blocks.cK.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 3, 5, 4, 4, 5, 8, Blocks.dQ.getDefaultState(), Blocks.dQ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 4, 9, 5, 8, Blocks.dQ.getDefaultState(), Blocks.dQ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer12 = 4; integer12 <= 8; ++integer12) {
                for (int integer13 = 0; integer13 <= 2; ++integer13) {
                    this.b(world, Blocks.dN.getDefaultState(), integer12, -1, integer13, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), integer12, -1, 12 - integer13, boundingBox);
                }
            }
            for (int integer12 = 0; integer12 <= 2; ++integer12) {
                for (int integer13 = 4; integer13 <= 8; ++integer13) {
                    this.b(world, Blocks.dN.getDefaultState(), integer12, -1, integer13, boundingBox);
                    this.b(world, Blocks.dN.getDefaultState(), 12 - integer12, -1, integer13, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class SmallCorridor extends Piece
    {
        public SmallCorridor(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public SmallCorridor(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 0, true);
        }
        
        public static SmallCorridor a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -1, 0, 0, 5, 7, 5, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new SmallCorridor(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 4, 1, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 4, 3, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 4, 3, 1, 4, 4, 1, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 4, 3, 3, 4, 4, 3, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer6 = 0; integer6 <= 4; ++integer6) {
                for (int integer7 = 0; integer7 <= 4; ++integer7) {
                    this.b(world, Blocks.dN.getDefaultState(), integer6, -1, integer7, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class CorridorCrossing extends Piece
    {
        public CorridorCrossing(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public CorridorCrossing(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 0, true);
            this.b((Start)structurePiece, list, random, 0, 1, true);
            this.c((Start)structurePiece, list, random, 0, 1, true);
        }
        
        public static CorridorCrossing a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -1, 0, 0, 5, 7, 5, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new CorridorCrossing(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 4, 0, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 2, 4, 4, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer5 = 0; integer5 <= 4; ++integer5) {
                for (int integer6 = 0; integer6 <= 4; ++integer6) {
                    this.b(world, Blocks.dN.getDefaultState(), integer5, -1, integer6, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class CorridorRightTurn extends Piece
    {
        private boolean containsChest;
        
        public CorridorRightTurn(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.containsChest = (random.nextInt(3) == 0);
        }
        
        public CorridorRightTurn(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, compoundTag);
            this.containsChest = compoundTag.getBoolean("Chest");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Chest", this.containsChest);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.c((Start)structurePiece, list, random, 0, 1, true);
        }
        
        public static CorridorRightTurn a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, 0, 0, 5, 7, 5, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new CorridorRightTurn(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 4, 1, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 4, 3, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 2, 4, 4, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 3, 4, 1, 4, 4, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 3, 3, 4, 3, 4, 4, blockState5, blockState5, false);
            if (this.containsChest && boundingBox.contains(new BlockPos(this.applyXTransform(1, 3), this.applyYTransform(2), this.applyZTransform(1, 3)))) {
                this.containsChest = false;
                this.addChest(world, boundingBox, random, 1, 2, 3, LootTables.CHEST_NETHER_BRIDGE);
            }
            this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer7 = 0; integer7 <= 4; ++integer7) {
                for (int integer8 = 0; integer8 <= 4; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class CorridorLeftTurn extends Piece
    {
        private boolean containsChest;
        
        public CorridorLeftTurn(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.containsChest = (random.nextInt(3) == 0);
        }
        
        public CorridorLeftTurn(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, compoundTag);
            this.containsChest = compoundTag.getBoolean("Chest");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Chest", this.containsChest);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.b((Start)structurePiece, list, random, 0, 1, true);
        }
        
        public static CorridorLeftTurn a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, 0, 0, 5, 7, 5, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new CorridorLeftTurn(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 3, 1, 4, 4, 1, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 4, 3, 3, 4, 4, 3, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 4, 3, 5, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 3, 4, 1, 4, 4, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 3, 3, 4, 3, 4, 4, blockState5, blockState5, false);
            if (this.containsChest && boundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
                this.containsChest = false;
                this.addChest(world, boundingBox, random, 3, 2, 3, LootTables.CHEST_NETHER_BRIDGE);
            }
            this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            for (int integer7 = 0; integer7 <= 4; ++integer7) {
                for (int integer8 = 0; integer8 <= 4; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer7, -1, integer8, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class CorridorStairs extends Piece
    {
        public CorridorStairs(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public CorridorStairs(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 0, true);
        }
        
        public static CorridorStairs a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -1, -7, 0, 5, 14, 10, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new CorridorStairs(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dP.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.SOUTH);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            for (int integer7 = 0; integer7 <= 9; ++integer7) {
                final int integer8 = Math.max(1, 7 - integer7);
                final int integer9 = Math.min(Math.max(integer8 + 5, 14 - integer7), 13);
                final int integer10 = integer7;
                this.fillWithOutline(world, boundingBox, 0, 0, integer10, 4, integer8, integer10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 1, integer8 + 1, integer10, 3, integer9 - 1, integer10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                if (integer7 <= 6) {
                    this.addBlock(world, blockState5, 1, integer8 + 1, integer10, boundingBox);
                    this.addBlock(world, blockState5, 2, integer8 + 1, integer10, boundingBox);
                    this.addBlock(world, blockState5, 3, integer8 + 1, integer10, boundingBox);
                }
                this.fillWithOutline(world, boundingBox, 0, integer9, integer10, 4, integer9, integer10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 0, integer8 + 1, integer10, 0, integer9 - 1, integer10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 4, integer8 + 1, integer10, 4, integer9 - 1, integer10, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
                if ((integer7 & 0x1) == 0x0) {
                    this.fillWithOutline(world, boundingBox, 0, integer8 + 2, integer10, 0, integer8 + 3, integer10, blockState6, blockState6, false);
                    this.fillWithOutline(world, boundingBox, 4, integer8 + 2, integer10, 4, integer8 + 3, integer10, blockState6, blockState6, false);
                }
                for (int integer11 = 0; integer11 <= 4; ++integer11) {
                    this.b(world, Blocks.dN.getDefaultState(), integer11, -1, integer10, boundingBox);
                }
            }
            return true;
        }
    }
    
    public static class CorridorBalcony extends Piece
    {
        public CorridorBalcony(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public CorridorBalcony(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            int integer4 = 1;
            final Direction direction5 = this.getFacing();
            if (direction5 == Direction.WEST || direction5 == Direction.NORTH) {
                integer4 = 5;
            }
            this.b((Start)structurePiece, list, random, 0, integer4, random.nextInt(8) > 0);
            this.c((Start)structurePiece, list, random, 0, integer4, random.nextInt(8) > 0);
        }
        
        public static CorridorBalcony a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -3, 0, 0, 9, 7, 9, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new CorridorBalcony(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final BlockState blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 8, 1, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 6, 0, 8, 6, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 2, 5, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 2, 0, 8, 5, 0, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 3, 0, 1, 4, 0, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 4, 0, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 0, 2, 4, 8, 2, 8, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 3, 8, 7, 3, 8, blockState6, blockState6, false);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.EAST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true), 0, 3, 8, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dO.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true), 8, 3, 8, boundingBox);
            this.fillWithOutline(world, boundingBox, 0, 3, 6, 0, 3, 7, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 8, 3, 6, 8, 3, 7, blockState5, blockState5, false);
            this.fillWithOutline(world, boundingBox, 0, 3, 4, 0, 5, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 3, 4, 8, 5, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 3, 5, 2, 5, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 6, 3, 5, 7, 5, 5, Blocks.dN.getDefaultState(), Blocks.dN.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 4, 5, 1, 5, 5, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 7, 4, 5, 7, 5, 5, blockState6, blockState6, false);
            for (int integer7 = 0; integer7 <= 5; ++integer7) {
                for (int integer8 = 0; integer8 <= 8; ++integer8) {
                    this.b(world, Blocks.dN.getDefaultState(), integer8, -1, integer7, boundingBox);
                }
            }
            return true;
        }
    }
}
