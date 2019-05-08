package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import java.util.Random;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;

public class StrongholdGenerator
{
    private static final f[] a;
    private static List<f> b;
    private static Class<? extends Piece> c;
    private static int d;
    private static final StoneBrickRandomizer e;
    
    public static void a() {
        StrongholdGenerator.b = Lists.newArrayList();
        for (final f f4 : StrongholdGenerator.a) {
            f4.c = 0;
            StrongholdGenerator.b.add(f4);
        }
        StrongholdGenerator.c = null;
    }
    
    private static boolean c() {
        boolean boolean1 = false;
        StrongholdGenerator.d = 0;
        for (final f f3 : StrongholdGenerator.b) {
            if (f3.d > 0 && f3.c < f3.d) {
                boolean1 = true;
            }
            StrongholdGenerator.d += f3.b;
        }
        return boolean1;
    }
    
    private static Piece a(final Class<? extends Piece> class1, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, @Nullable final Direction direction, final int integer8) {
        Piece piece9 = null;
        if (class1 == Corridor.class) {
            piece9 = Corridor.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == PrisonHall.class) {
            piece9 = PrisonHall.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == LeftTurn.class) {
            piece9 = LeftTurn.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == RightTurn.class) {
            piece9 = RightTurn.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == SquareRoom.class) {
            piece9 = SquareRoom.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == Stairs.class) {
            piece9 = Stairs.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == SpiralStaircase.class) {
            piece9 = SpiralStaircase.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == FiveWayCrossing.class) {
            piece9 = FiveWayCrossing.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == ChestCorridor.class) {
            piece9 = ChestCorridor.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == Library.class) {
            piece9 = Library.a(list, random, integer4, integer5, integer6, direction, integer8);
        }
        else if (class1 == PortalRoom.class) {
            piece9 = PortalRoom.a(list, integer4, integer5, integer6, direction, integer8);
        }
        return piece9;
    }
    
    private static Piece b(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, final Direction direction, final int integer8) {
        if (!c()) {
            return null;
        }
        if (StrongholdGenerator.c != null) {
            final Piece piece9 = a(StrongholdGenerator.c, list, random, integer4, integer5, integer6, direction, integer8);
            StrongholdGenerator.c = null;
            if (piece9 != null) {
                return piece9;
            }
        }
        int integer9 = 0;
        while (integer9 < 5) {
            ++integer9;
            int integer10 = random.nextInt(StrongholdGenerator.d);
            for (final f f12 : StrongholdGenerator.b) {
                integer10 -= f12.b;
                if (integer10 < 0) {
                    if (!f12.a(integer8)) {
                        break;
                    }
                    if (f12 == start.a) {
                        break;
                    }
                    final Piece piece10 = a(f12.a, list, random, integer4, integer5, integer6, direction, integer8);
                    if (piece10 != null) {
                        final f f13 = f12;
                        ++f13.c;
                        start.a = f12;
                        if (!f12.a()) {
                            StrongholdGenerator.b.remove(f12);
                        }
                        return piece10;
                    }
                    continue;
                }
            }
        }
        final MutableIntBoundingBox mutableIntBoundingBox10 = SmallCorridor.a(list, random, integer4, integer5, integer6, direction);
        if (mutableIntBoundingBox10 != null && mutableIntBoundingBox10.minY > 1) {
            return new SmallCorridor(integer8, mutableIntBoundingBox10, direction);
        }
        return null;
    }
    
    private static StructurePiece c(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, @Nullable final Direction direction, final int integer8) {
        if (integer8 > 50) {
            return null;
        }
        if (Math.abs(integer4 - start.getBoundingBox().minX) > 112 || Math.abs(integer6 - start.getBoundingBox().minZ) > 112) {
            return null;
        }
        final StructurePiece structurePiece9 = b(start, list, random, integer4, integer5, integer6, direction, integer8 + 1);
        if (structurePiece9 != null) {
            list.add(structurePiece9);
            start.c.add(structurePiece9);
        }
        return structurePiece9;
    }
    
    static {
        a = new f[] { new f(Corridor.class, 40, 0), new f(PrisonHall.class, 5, 5), new f(LeftTurn.class, 20, 0), new f(RightTurn.class, 20, 0), new f(SquareRoom.class, 10, 6), new f(Stairs.class, 5, 5), new f(SpiralStaircase.class, 5, 5), new f(FiveWayCrossing.class, 5, 4), new f(ChestCorridor.class, 5, 4), new f(Library.class, 10, 2) {
                @Override
                public boolean a(final int integer) {
                    return super.a(integer) && integer > 4;
                }
            }, new f(PortalRoom.class, 20, 1) {
                @Override
                public boolean a(final int integer) {
                    return super.a(integer) && integer > 5;
                }
            } };
        e = new StoneBrickRandomizer();
    }
    
    static class f
    {
        public final Class<? extends Piece> a;
        public final int b;
        public int c;
        public final int d;
        
        public f(final Class<? extends Piece> class1, final int integer2, final int integer3) {
            this.a = class1;
            this.b = integer2;
            this.d = integer3;
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
        protected EntranceType entryDoor;
        
        protected Piece(final StructurePieceType type, final int integer) {
            super(type, integer);
            this.entryDoor = EntranceType.a;
        }
        
        public Piece(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
            this.entryDoor = EntranceType.a;
            this.entryDoor = EntranceType.valueOf(tag.getString("EntryDoor"));
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            tag.putString("EntryDoor", this.entryDoor.name());
        }
        
        protected void generateEntrance(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final EntranceType type, final int x, final int y, final int z) {
            switch (type) {
                case a: {
                    this.fillWithOutline(world, boundingBox, x, y, z, x + 3 - 1, y + 3 - 1, z, Piece.AIR, Piece.AIR, false);
                    break;
                }
                case b: {
                    this.addBlock(world, Blocks.dn.getDefaultState(), x, y, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 1, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 2, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 2, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 2, y, z, boundingBox);
                    this.addBlock(world, Blocks.cd.getDefaultState(), x + 1, y, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.cd.getDefaultState()).<DoubleBlockHalf, DoubleBlockHalf>with(DoorBlock.HALF, DoubleBlockHalf.a), x + 1, y + 1, z, boundingBox);
                    break;
                }
                case c: {
                    this.addBlock(world, Blocks.kT.getDefaultState(), x + 1, y, z, boundingBox);
                    this.addBlock(world, Blocks.kT.getDefaultState(), x + 1, y + 1, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WEST, true), x, y, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WEST, true), x, y + 1, z, boundingBox);
                    this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.EAST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WEST, true), x, y + 2, z, boundingBox);
                    this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.EAST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WEST, true), x + 1, y + 2, z, boundingBox);
                    this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.EAST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WEST, true), x + 2, y + 2, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true), x + 2, y + 1, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true), x + 2, y, z, boundingBox);
                    break;
                }
                case d: {
                    this.addBlock(world, Blocks.dn.getDefaultState(), x, y, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 1, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 2, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 2, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), x + 2, y, z, boundingBox);
                    this.addBlock(world, Blocks.cp.getDefaultState(), x + 1, y, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.cp.getDefaultState()).<DoubleBlockHalf, DoubleBlockHalf>with(DoorBlock.HALF, DoubleBlockHalf.a), x + 1, y + 1, z, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.cz.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)AbstractButtonBlock.FACING, Direction.NORTH), x + 2, y + 1, z + 1, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.cz.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)AbstractButtonBlock.FACING, Direction.SOUTH), x + 2, y + 1, z - 1, boundingBox);
                    break;
                }
            }
        }
        
        protected EntranceType getRandomEntrance(final Random random) {
            final int integer2 = random.nextInt(5);
            switch (integer2) {
                default: {
                    return EntranceType.a;
                }
                case 2: {
                    return EntranceType.b;
                }
                case 3: {
                    return EntranceType.c;
                }
                case 4: {
                    return EntranceType.d;
                }
            }
        }
        
        @Nullable
        protected StructurePiece a(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5) {
            final Direction direction6 = this.getFacing();
            if (direction6 != null) {
                switch (direction6) {
                    case NORTH: {
                        return c(start, list, random, this.boundingBox.minX + integer4, this.boundingBox.minY + integer5, this.boundingBox.minZ - 1, direction6, this.h());
                    }
                    case SOUTH: {
                        return c(start, list, random, this.boundingBox.minX + integer4, this.boundingBox.minY + integer5, this.boundingBox.maxZ + 1, direction6, this.h());
                    }
                    case WEST: {
                        return c(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + integer5, this.boundingBox.minZ + integer4, direction6, this.h());
                    }
                    case EAST: {
                        return c(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + integer5, this.boundingBox.minZ + integer4, direction6, this.h());
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructurePiece b(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5) {
            final Direction direction6 = this.getFacing();
            if (direction6 != null) {
                switch (direction6) {
                    case NORTH: {
                        return c(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.WEST, this.h());
                    }
                    case SOUTH: {
                        return c(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.WEST, this.h());
                    }
                    case WEST: {
                        return c(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.minZ - 1, Direction.NORTH, this.h());
                    }
                    case EAST: {
                        return c(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.minZ - 1, Direction.NORTH, this.h());
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructurePiece c(final Start start, final List<StructurePiece> list, final Random random, final int integer4, final int integer5) {
            final Direction direction6 = this.getFacing();
            if (direction6 != null) {
                switch (direction6) {
                    case NORTH: {
                        return c(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.EAST, this.h());
                    }
                    case SOUTH: {
                        return c(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + integer4, this.boundingBox.minZ + integer5, Direction.EAST, this.h());
                    }
                    case WEST: {
                        return c(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.maxZ + 1, Direction.SOUTH, this.h());
                    }
                    case EAST: {
                        return c(start, list, random, this.boundingBox.minX + integer5, this.boundingBox.minY + integer4, this.boundingBox.maxZ + 1, Direction.SOUTH, this.h());
                    }
                }
            }
            return null;
        }
        
        protected static boolean a(final MutableIntBoundingBox mutableIntBoundingBox) {
            return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
        }
        
        public enum EntranceType
        {
            a, 
            b, 
            c, 
            d;
        }
    }
    
    public static class SmallCorridor extends Piece
    {
        private final int length;
        
        public SmallCorridor(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.length = ((direction == Direction.NORTH || direction == Direction.SOUTH) ? mutableIntBoundingBox.getBlockCountZ() : mutableIntBoundingBox.getBlockCountX());
        }
        
        public SmallCorridor(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, compoundTag);
            this.length = compoundTag.getInt("Steps");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putInt("Steps", this.length);
        }
        
        public static MutableIntBoundingBox a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction) {
            final int integer6 = 3;
            MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, 4, direction);
            final StructurePiece structurePiece9 = StructurePiece.a(list, mutableIntBoundingBox8);
            if (structurePiece9 == null) {
                return null;
            }
            if (structurePiece9.getBoundingBox().minY == mutableIntBoundingBox8.minY) {
                for (int integer7 = 3; integer7 >= 1; --integer7) {
                    mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, integer7 - 1, direction);
                    if (!structurePiece9.getBoundingBox().intersects(mutableIntBoundingBox8)) {
                        return MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, integer7, direction);
                    }
                }
            }
            return null;
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            for (int integer5 = 0; integer5 < this.length; ++integer5) {
                this.addBlock(world, Blocks.dn.getDefaultState(), 0, 0, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 1, 0, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 2, 0, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 3, 0, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 4, 0, integer5, boundingBox);
                for (int integer6 = 1; integer6 <= 3; ++integer6) {
                    this.addBlock(world, Blocks.dn.getDefaultState(), 0, integer6, integer5, boundingBox);
                    this.addBlock(world, Blocks.kT.getDefaultState(), 1, integer6, integer5, boundingBox);
                    this.addBlock(world, Blocks.kT.getDefaultState(), 2, integer6, integer5, boundingBox);
                    this.addBlock(world, Blocks.kT.getDefaultState(), 3, integer6, integer5, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 4, integer6, integer5, boundingBox);
                }
                this.addBlock(world, Blocks.dn.getDefaultState(), 0, 4, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 1, 4, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 2, 4, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 3, 4, integer5, boundingBox);
                this.addBlock(world, Blocks.dn.getDefaultState(), 4, 4, integer5, boundingBox);
            }
            return true;
        }
    }
    
    public static class SpiralStaircase extends Piece
    {
        private final boolean isStructureStart;
        
        public SpiralStaircase(final StructurePieceType structurePieceType, final int integer2, final Random random, final int integer4, final int integer5) {
            super(structurePieceType, integer2);
            this.isStructureStart = true;
            this.setOrientation(Direction.Type.HORIZONTAL.random(random));
            this.entryDoor = EntranceType.a;
            if (this.getFacing().getAxis() == Direction.Axis.Z) {
                this.boundingBox = new MutableIntBoundingBox(integer4, 64, integer5, integer4 + 5 - 1, 74, integer5 + 5 - 1);
            }
            else {
                this.boundingBox = new MutableIntBoundingBox(integer4, 64, integer5, integer4 + 5 - 1, 74, integer5 + 5 - 1);
            }
        }
        
        public SpiralStaircase(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, integer);
            this.isStructureStart = false;
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public SpiralStaircase(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
            this.isStructureStart = tag.getBoolean("Source");
        }
        
        public SpiralStaircase(final StructureManager structureManager, final CompoundTag compoundTag) {
            this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, compoundTag);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Source", this.isStructureStart);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            if (this.isStructureStart) {
                StrongholdGenerator.c = FiveWayCrossing.class;
            }
            this.a((Start)structurePiece, list, random, 1, 1);
        }
        
        public static SpiralStaircase a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -7, 0, 5, 11, 5, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new SpiralStaircase(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateEntrance(world, random, boundingBox, EntranceType.a, 1, 1, 4);
            this.addBlock(world, Blocks.dn.getDefaultState(), 2, 6, 1, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 1, 5, 1, boundingBox);
            this.addBlock(world, Blocks.hJ.getDefaultState(), 1, 6, 1, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 1, 5, 2, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 1, 4, 3, boundingBox);
            this.addBlock(world, Blocks.hJ.getDefaultState(), 1, 5, 3, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 2, 4, 3, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 3, 3, 3, boundingBox);
            this.addBlock(world, Blocks.hJ.getDefaultState(), 3, 4, 3, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 3, 3, 2, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 3, 2, 1, boundingBox);
            this.addBlock(world, Blocks.hJ.getDefaultState(), 3, 3, 1, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 2, 2, 1, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 1, 1, 1, boundingBox);
            this.addBlock(world, Blocks.hJ.getDefaultState(), 1, 2, 1, boundingBox);
            this.addBlock(world, Blocks.dn.getDefaultState(), 1, 1, 2, boundingBox);
            this.addBlock(world, Blocks.hJ.getDefaultState(), 1, 1, 3, boundingBox);
            return true;
        }
    }
    
    public static class Start extends SpiralStaircase
    {
        public f a;
        @Nullable
        public PortalRoom b;
        public final List<StructurePiece> c;
        
        public Start(final Random random, final int integer2, final int integer3) {
            super(StructurePieceType.STRONGHOLD_START, 0, random, integer2, integer3);
            this.c = Lists.newArrayList();
        }
        
        public Start(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_START, compoundTag);
            this.c = Lists.newArrayList();
        }
    }
    
    public static class Corridor extends Piece
    {
        private final boolean leftExitExists;
        private final boolean rightExitExixts;
        
        public Corridor(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.leftExitExists = (random.nextInt(2) == 0);
            this.rightExitExixts = (random.nextInt(2) == 0);
        }
        
        public Corridor(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, compoundTag);
            this.leftExitExists = compoundTag.getBoolean("Left");
            this.rightExitExixts = compoundTag.getBoolean("Right");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Left", this.leftExitExists);
            tag.putBoolean("Right", this.rightExitExixts);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 1);
            if (this.leftExitExists) {
                this.b((Start)structurePiece, list, random, 1, 2);
            }
            if (this.rightExitExixts) {
                this.c((Start)structurePiece, list, random, 1, 2);
            }
        }
        
        public static Corridor a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, 7, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new Corridor(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateEntrance(world, random, boundingBox, EntranceType.a, 1, 1, 6);
            final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.EAST);
            final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.WEST);
            this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1f, 1, 2, 1, blockState5);
            this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1f, 3, 2, 1, blockState6);
            this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1f, 1, 2, 5, blockState5);
            this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1f, 3, 2, 5, blockState6);
            if (this.leftExitExists) {
                this.fillWithOutline(world, boundingBox, 0, 1, 2, 0, 3, 4, Corridor.AIR, Corridor.AIR, false);
            }
            if (this.rightExitExixts) {
                this.fillWithOutline(world, boundingBox, 4, 1, 2, 4, 3, 4, Corridor.AIR, Corridor.AIR, false);
            }
            return true;
        }
    }
    
    public static class ChestCorridor extends Piece
    {
        private boolean chestGenerated;
        
        public ChestCorridor(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public ChestCorridor(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
            this.chestGenerated = compoundTag.getBoolean("Chest");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Chest", this.chestGenerated);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 1);
        }
        
        public static ChestCorridor a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, 7, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new ChestCorridor(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateEntrance(world, random, boundingBox, EntranceType.a, 1, 1, 6);
            this.fillWithOutline(world, boundingBox, 3, 1, 2, 3, 1, 4, Blocks.dn.getDefaultState(), Blocks.dn.getDefaultState(), false);
            this.addBlock(world, Blocks.hP.getDefaultState(), 3, 1, 1, boundingBox);
            this.addBlock(world, Blocks.hP.getDefaultState(), 3, 1, 5, boundingBox);
            this.addBlock(world, Blocks.hP.getDefaultState(), 3, 2, 2, boundingBox);
            this.addBlock(world, Blocks.hP.getDefaultState(), 3, 2, 4, boundingBox);
            for (int integer5 = 2; integer5 <= 4; ++integer5) {
                this.addBlock(world, Blocks.hP.getDefaultState(), 2, 1, integer5, boundingBox);
            }
            if (!this.chestGenerated && boundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
                this.chestGenerated = true;
                this.addChest(world, boundingBox, random, 3, 2, 3, LootTables.CHEST_STRONGHOLD_CORRIDOR);
            }
            return true;
        }
    }
    
    public static class Stairs extends Piece
    {
        public Stairs(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_STAIRS, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public Stairs(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_STAIRS, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 1);
        }
        
        public static Stairs a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -7, 0, 5, 11, 8, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new Stairs(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateEntrance(world, random, boundingBox, EntranceType.a, 1, 1, 7);
            final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.SOUTH);
            for (int integer6 = 0; integer6 < 6; ++integer6) {
                this.addBlock(world, blockState5, 1, 6 - integer6, 1 + integer6, boundingBox);
                this.addBlock(world, blockState5, 2, 6 - integer6, 1 + integer6, boundingBox);
                this.addBlock(world, blockState5, 3, 6 - integer6, 1 + integer6, boundingBox);
                if (integer6 < 5) {
                    this.addBlock(world, Blocks.dn.getDefaultState(), 1, 5 - integer6, 1 + integer6, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 2, 5 - integer6, 1 + integer6, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 3, 5 - integer6, 1 + integer6, boundingBox);
                }
            }
            return true;
        }
    }
    
    public abstract static class q extends Piece
    {
        protected q(final StructurePieceType type, final int integer) {
            super(type, integer);
        }
        
        public q(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
        }
    }
    
    public static class LeftTurn extends q
    {
        public LeftTurn(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public LeftTurn(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            final Direction direction4 = this.getFacing();
            if (direction4 == Direction.NORTH || direction4 == Direction.EAST) {
                this.b((Start)structurePiece, list, random, 1, 1);
            }
            else {
                this.c((Start)structurePiece, list, random, 1, 1);
            }
        }
        
        public static LeftTurn a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, 5, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new LeftTurn(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
            final Direction direction5 = this.getFacing();
            if (direction5 == Direction.NORTH || direction5 == Direction.EAST) {
                this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 3, LeftTurn.AIR, LeftTurn.AIR, false);
            }
            else {
                this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 3, 3, LeftTurn.AIR, LeftTurn.AIR, false);
            }
            return true;
        }
    }
    
    public static class RightTurn extends q
    {
        public RightTurn(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public RightTurn(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            final Direction direction4 = this.getFacing();
            if (direction4 == Direction.NORTH || direction4 == Direction.EAST) {
                this.c((Start)structurePiece, list, random, 1, 1);
            }
            else {
                this.b((Start)structurePiece, list, random, 1, 1);
            }
        }
        
        public static RightTurn a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 5, 5, 5, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new RightTurn(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
            final Direction direction5 = this.getFacing();
            if (direction5 == Direction.NORTH || direction5 == Direction.EAST) {
                this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 3, 3, RightTurn.AIR, RightTurn.AIR, false);
            }
            else {
                this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 3, RightTurn.AIR, RightTurn.AIR, false);
            }
            return true;
        }
    }
    
    public static class SquareRoom extends Piece
    {
        protected final int roomType;
        
        public SquareRoom(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.roomType = random.nextInt(5);
        }
        
        public SquareRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, compoundTag);
            this.roomType = compoundTag.getInt("Type");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putInt("Type", this.roomType);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 4, 1);
            this.b((Start)structurePiece, list, random, 1, 4);
            this.c((Start)structurePiece, list, random, 1, 4);
        }
        
        public static SquareRoom a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -4, -1, 0, 11, 7, 11, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new SquareRoom(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.fillWithOutline(world, boundingBox, 4, 1, 10, 6, 3, 10, SquareRoom.AIR, SquareRoom.AIR, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 4, 0, 3, 6, SquareRoom.AIR, SquareRoom.AIR, false);
            this.fillWithOutline(world, boundingBox, 10, 1, 4, 10, 3, 6, SquareRoom.AIR, SquareRoom.AIR, false);
            switch (this.roomType) {
                case 0: {
                    this.addBlock(world, Blocks.dn.getDefaultState(), 5, 1, 5, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 5, 2, 5, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 5, 3, 5, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 4, 1, 4, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 4, 1, 5, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 4, 1, 6, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 6, 1, 4, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 6, 1, 5, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 6, 1, 6, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 5, 1, 4, boundingBox);
                    this.addBlock(world, Blocks.hJ.getDefaultState(), 5, 1, 6, boundingBox);
                    break;
                }
                case 1: {
                    for (int integer5 = 0; integer5 < 5; ++integer5) {
                        this.addBlock(world, Blocks.dn.getDefaultState(), 3, 1, 3 + integer5, boundingBox);
                        this.addBlock(world, Blocks.dn.getDefaultState(), 7, 1, 3 + integer5, boundingBox);
                        this.addBlock(world, Blocks.dn.getDefaultState(), 3 + integer5, 1, 3, boundingBox);
                        this.addBlock(world, Blocks.dn.getDefaultState(), 3 + integer5, 1, 7, boundingBox);
                    }
                    this.addBlock(world, Blocks.dn.getDefaultState(), 5, 1, 5, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 5, 2, 5, boundingBox);
                    this.addBlock(world, Blocks.dn.getDefaultState(), 5, 3, 5, boundingBox);
                    this.addBlock(world, Blocks.A.getDefaultState(), 5, 4, 5, boundingBox);
                    break;
                }
                case 2: {
                    for (int integer5 = 1; integer5 <= 9; ++integer5) {
                        this.addBlock(world, Blocks.m.getDefaultState(), 1, 3, integer5, boundingBox);
                        this.addBlock(world, Blocks.m.getDefaultState(), 9, 3, integer5, boundingBox);
                    }
                    for (int integer5 = 1; integer5 <= 9; ++integer5) {
                        this.addBlock(world, Blocks.m.getDefaultState(), integer5, 3, 1, boundingBox);
                        this.addBlock(world, Blocks.m.getDefaultState(), integer5, 3, 9, boundingBox);
                    }
                    this.addBlock(world, Blocks.m.getDefaultState(), 5, 1, 4, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 5, 1, 6, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 5, 3, 4, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 5, 3, 6, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 4, 1, 5, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 6, 1, 5, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 4, 3, 5, boundingBox);
                    this.addBlock(world, Blocks.m.getDefaultState(), 6, 3, 5, boundingBox);
                    for (int integer5 = 1; integer5 <= 3; ++integer5) {
                        this.addBlock(world, Blocks.m.getDefaultState(), 4, integer5, 4, boundingBox);
                        this.addBlock(world, Blocks.m.getDefaultState(), 6, integer5, 4, boundingBox);
                        this.addBlock(world, Blocks.m.getDefaultState(), 4, integer5, 6, boundingBox);
                        this.addBlock(world, Blocks.m.getDefaultState(), 6, integer5, 6, boundingBox);
                    }
                    this.addBlock(world, Blocks.bK.getDefaultState(), 5, 3, 5, boundingBox);
                    for (int integer5 = 2; integer5 <= 8; ++integer5) {
                        this.addBlock(world, Blocks.n.getDefaultState(), 2, 3, integer5, boundingBox);
                        this.addBlock(world, Blocks.n.getDefaultState(), 3, 3, integer5, boundingBox);
                        if (integer5 <= 3 || integer5 >= 7) {
                            this.addBlock(world, Blocks.n.getDefaultState(), 4, 3, integer5, boundingBox);
                            this.addBlock(world, Blocks.n.getDefaultState(), 5, 3, integer5, boundingBox);
                            this.addBlock(world, Blocks.n.getDefaultState(), 6, 3, integer5, boundingBox);
                        }
                        this.addBlock(world, Blocks.n.getDefaultState(), 7, 3, integer5, boundingBox);
                        this.addBlock(world, Blocks.n.getDefaultState(), 8, 3, integer5, boundingBox);
                    }
                    final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.ce.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)LadderBlock.FACING, Direction.WEST);
                    this.addBlock(world, blockState5, 9, 1, 3, boundingBox);
                    this.addBlock(world, blockState5, 9, 2, 3, boundingBox);
                    this.addBlock(world, blockState5, 9, 3, 3, boundingBox);
                    this.addChest(world, boundingBox, random, 3, 4, 8, LootTables.CHEST_STRONGHOLD_CROSSING);
                    break;
                }
            }
            return true;
        }
    }
    
    public static class PrisonHall extends Piece
    {
        public PrisonHall(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public PrisonHall(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            this.a((Start)structurePiece, list, random, 1, 1);
        }
        
        public static PrisonHall a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -1, -1, 0, 9, 5, 11, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new PrisonHall(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.fillWithOutline(world, boundingBox, 1, 1, 10, 3, 3, 10, PrisonHall.AIR, PrisonHall.AIR, false);
            this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdGenerator.e);
            for (int integer5 = 1; integer5 <= 3; ++integer5) {
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.SOUTH, true), 4, integer5, 4, boundingBox);
                this.addBlock(world, ((((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, true)).with((Property<Comparable>)PaneBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true), 4, integer5, 5, boundingBox);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.SOUTH, true), 4, integer5, 6, boundingBox);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true), 5, integer5, 5, boundingBox);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true), 6, integer5, 5, boundingBox);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true), 7, integer5, 5, boundingBox);
            }
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.SOUTH, true), 4, 3, 2, boundingBox);
            this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.SOUTH, true), 4, 3, 8, boundingBox);
            final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cp.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)DoorBlock.FACING, Direction.WEST);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.cp.getDefaultState()).with((Property<Comparable>)DoorBlock.FACING, Direction.WEST)).<DoubleBlockHalf, DoubleBlockHalf>with(DoorBlock.HALF, DoubleBlockHalf.a);
            this.addBlock(world, blockState5, 4, 1, 2, boundingBox);
            this.addBlock(world, blockState6, 4, 2, 2, boundingBox);
            this.addBlock(world, blockState5, 4, 1, 8, boundingBox);
            this.addBlock(world, blockState6, 4, 2, 8, boundingBox);
            return true;
        }
    }
    
    public static class Library extends Piece
    {
        private final boolean tall;
        
        public Library(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGJOLD_LIBRARY, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.tall = (mutableIntBoundingBox.getBlockCountY() > 6);
        }
        
        public Library(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGJOLD_LIBRARY, compoundTag);
            this.tall = compoundTag.getBoolean("Tall");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Tall", this.tall);
        }
        
        public static Library a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -4, -1, 0, 14, 11, 15, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -4, -1, 0, 14, 6, 15, direction);
                if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                    return null;
                }
            }
            return new Library(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            int integer5 = 11;
            if (!this.tall) {
                integer5 = 6;
            }
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 13, integer5 - 1, 14, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.fillWithOutlineUnderSealevel(world, boundingBox, random, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.aP.getDefaultState(), Blocks.aP.getDefaultState(), false, false);
            final int integer6 = 1;
            final int integer7 = 12;
            for (int integer8 = 1; integer8 <= 13; ++integer8) {
                if ((integer8 - 1) % 4 == 0) {
                    this.fillWithOutline(world, boundingBox, 1, 1, integer8, 1, 4, integer8, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                    this.fillWithOutline(world, boundingBox, 12, 1, integer8, 12, 4, integer8, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.EAST), 2, 3, integer8, boundingBox);
                    this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.WEST), 11, 3, integer8, boundingBox);
                    if (this.tall) {
                        this.fillWithOutline(world, boundingBox, 1, 6, integer8, 1, 9, integer8, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                        this.fillWithOutline(world, boundingBox, 12, 6, integer8, 12, 9, integer8, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                    }
                }
                else {
                    this.fillWithOutline(world, boundingBox, 1, 1, integer8, 1, 4, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
                    this.fillWithOutline(world, boundingBox, 12, 1, integer8, 12, 4, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
                    if (this.tall) {
                        this.fillWithOutline(world, boundingBox, 1, 6, integer8, 1, 9, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
                        this.fillWithOutline(world, boundingBox, 12, 6, integer8, 12, 9, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
                    }
                }
            }
            for (int integer8 = 3; integer8 < 12; integer8 += 2) {
                this.fillWithOutline(world, boundingBox, 3, 1, integer8, 4, 3, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 6, 1, integer8, 7, 3, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 9, 1, integer8, 10, 3, integer8, Blocks.bH.getDefaultState(), Blocks.bH.getDefaultState(), false);
            }
            if (this.tall) {
                this.fillWithOutline(world, boundingBox, 1, 5, 1, 3, 5, 13, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 10, 5, 1, 12, 5, 13, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 4, 5, 1, 9, 5, 2, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                this.fillWithOutline(world, boundingBox, 4, 5, 12, 9, 5, 13, Blocks.n.getDefaultState(), Blocks.n.getDefaultState(), false);
                this.addBlock(world, Blocks.n.getDefaultState(), 9, 5, 11, boundingBox);
                this.addBlock(world, Blocks.n.getDefaultState(), 8, 5, 11, boundingBox);
                this.addBlock(world, Blocks.n.getDefaultState(), 9, 5, 10, boundingBox);
                final BlockState blockState8 = (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
                final BlockState blockState9 = (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true);
                this.fillWithOutline(world, boundingBox, 3, 6, 3, 3, 6, 11, blockState9, blockState9, false);
                this.fillWithOutline(world, boundingBox, 10, 6, 3, 10, 6, 9, blockState9, blockState9, false);
                this.fillWithOutline(world, boundingBox, 4, 6, 2, 9, 6, 2, blockState8, blockState8, false);
                this.fillWithOutline(world, boundingBox, 4, 6, 12, 7, 6, 12, blockState8, blockState8, false);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 3, 6, 2, boundingBox);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 3, 6, 12, boundingBox);
                this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 10, 6, 2, boundingBox);
                for (int integer9 = 0; integer9 <= 2; ++integer9) {
                    this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), 8 + integer9, 6, 12 - integer9, boundingBox);
                    if (integer9 != 2) {
                        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), 8 + integer9, 6, 11 - integer9, boundingBox);
                    }
                }
                final BlockState blockState10 = ((AbstractPropertyContainer<O, BlockState>)Blocks.ce.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)LadderBlock.FACING, Direction.SOUTH);
                this.addBlock(world, blockState10, 10, 1, 13, boundingBox);
                this.addBlock(world, blockState10, 10, 2, 13, boundingBox);
                this.addBlock(world, blockState10, 10, 3, 13, boundingBox);
                this.addBlock(world, blockState10, 10, 4, 13, boundingBox);
                this.addBlock(world, blockState10, 10, 5, 13, boundingBox);
                this.addBlock(world, blockState10, 10, 6, 13, boundingBox);
                this.addBlock(world, blockState10, 10, 7, 13, boundingBox);
                final int integer10 = 7;
                final int integer11 = 7;
                final BlockState blockState11 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
                this.addBlock(world, blockState11, 6, 9, 7, boundingBox);
                final BlockState blockState12 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true);
                this.addBlock(world, blockState12, 7, 9, 7, boundingBox);
                this.addBlock(world, blockState11, 6, 8, 7, boundingBox);
                this.addBlock(world, blockState12, 7, 8, 7, boundingBox);
                final BlockState blockState13 = (((AbstractPropertyContainer<O, BlockState>)blockState9).with((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true);
                this.addBlock(world, blockState13, 6, 7, 7, boundingBox);
                this.addBlock(world, blockState13, 7, 7, 7, boundingBox);
                this.addBlock(world, blockState11, 5, 7, 7, boundingBox);
                this.addBlock(world, blockState12, 8, 7, 7, boundingBox);
                this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.NORTH, true), 6, 7, 6, boundingBox);
                this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true), 6, 7, 8, boundingBox);
                this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState12).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.NORTH, true), 7, 7, 6, boundingBox);
                this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState12).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.SOUTH, true), 7, 7, 8, boundingBox);
                final BlockState blockState14 = Blocks.bK.getDefaultState();
                this.addBlock(world, blockState14, 5, 8, 7, boundingBox);
                this.addBlock(world, blockState14, 8, 8, 7, boundingBox);
                this.addBlock(world, blockState14, 6, 8, 6, boundingBox);
                this.addBlock(world, blockState14, 6, 8, 8, boundingBox);
                this.addBlock(world, blockState14, 7, 8, 6, boundingBox);
                this.addBlock(world, blockState14, 7, 8, 8, boundingBox);
            }
            this.addChest(world, boundingBox, random, 3, 3, 5, LootTables.CHEST_STRONGHOLD_LIBRARY);
            if (this.tall) {
                this.addBlock(world, Library.AIR, 12, 9, 1, boundingBox);
                this.addChest(world, boundingBox, random, 12, 8, 1, LootTables.CHEST_STRONGHOLD_LIBRARY);
            }
            return true;
        }
    }
    
    public static class FiveWayCrossing extends Piece
    {
        private final boolean lowerLeftExists;
        private final boolean upperLeftExists;
        private final boolean lowerRightExists;
        private final boolean upperRightExists;
        
        public FiveWayCrossing(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, integer);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.lowerLeftExists = random.nextBoolean();
            this.upperLeftExists = random.nextBoolean();
            this.lowerRightExists = random.nextBoolean();
            this.upperRightExists = (random.nextInt(3) > 0);
        }
        
        public FiveWayCrossing(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, compoundTag);
            this.lowerLeftExists = compoundTag.getBoolean("leftLow");
            this.upperLeftExists = compoundTag.getBoolean("leftHigh");
            this.lowerRightExists = compoundTag.getBoolean("rightLow");
            this.upperRightExists = compoundTag.getBoolean("rightHigh");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("leftLow", this.lowerLeftExists);
            tag.putBoolean("leftHigh", this.upperLeftExists);
            tag.putBoolean("rightLow", this.lowerRightExists);
            tag.putBoolean("rightHigh", this.upperRightExists);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            int integer4 = 3;
            int integer5 = 5;
            final Direction direction6 = this.getFacing();
            if (direction6 == Direction.WEST || direction6 == Direction.NORTH) {
                integer4 = 8 - integer4;
                integer5 = 8 - integer5;
            }
            this.a((Start)structurePiece, list, random, 5, 1);
            if (this.lowerLeftExists) {
                this.b((Start)structurePiece, list, random, integer4, 1);
            }
            if (this.upperLeftExists) {
                this.b((Start)structurePiece, list, random, integer5, 7);
            }
            if (this.lowerRightExists) {
                this.c((Start)structurePiece, list, random, integer4, 1);
            }
            if (this.upperRightExists) {
                this.c((Start)structurePiece, list, random, integer5, 7);
            }
        }
        
        public static FiveWayCrossing a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction, final int integer7) {
            final MutableIntBoundingBox mutableIntBoundingBox8 = MutableIntBoundingBox.createRotated(integer3, integer4, integer5, -4, -3, 0, 10, 9, 11, direction);
            if (!Piece.a(mutableIntBoundingBox8) || StructurePiece.a(list, mutableIntBoundingBox8) != null) {
                return null;
            }
            return new FiveWayCrossing(integer7, random, mutableIntBoundingBox8, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, this.entryDoor, 4, 3, 0);
            if (this.lowerLeftExists) {
                this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 5, 3, FiveWayCrossing.AIR, FiveWayCrossing.AIR, false);
            }
            if (this.lowerRightExists) {
                this.fillWithOutline(world, boundingBox, 9, 3, 1, 9, 5, 3, FiveWayCrossing.AIR, FiveWayCrossing.AIR, false);
            }
            if (this.upperLeftExists) {
                this.fillWithOutline(world, boundingBox, 0, 5, 7, 0, 7, 9, FiveWayCrossing.AIR, FiveWayCrossing.AIR, false);
            }
            if (this.upperRightExists) {
                this.fillWithOutline(world, boundingBox, 9, 5, 7, 9, 7, 9, FiveWayCrossing.AIR, FiveWayCrossing.AIR, false);
            }
            this.fillWithOutline(world, boundingBox, 5, 1, 10, 7, 3, 10, FiveWayCrossing.AIR, FiveWayCrossing.AIR, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 1, 3, 4, 3, 3, 4, Blocks.hJ.getDefaultState(), Blocks.hJ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 1, 4, 6, 3, 4, 6, Blocks.hJ.getDefaultState(), Blocks.hJ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 5, 1, 9, 7, 1, 9, Blocks.hJ.getDefaultState(), Blocks.hJ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 2, 7, 7, 2, 7, Blocks.hJ.getDefaultState(), Blocks.hJ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 4, 5, 7, 4, 5, 9, Blocks.hJ.getDefaultState(), Blocks.hJ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 8, 5, 7, 8, 5, 9, Blocks.hJ.getDefaultState(), Blocks.hJ.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 5, 5, 7, 7, 5, 9, ((AbstractPropertyContainer<O, BlockState>)Blocks.hJ.getDefaultState()).<SlabType, SlabType>with(SlabBlock.TYPE, SlabType.c), ((AbstractPropertyContainer<O, BlockState>)Blocks.hJ.getDefaultState()).<SlabType, SlabType>with(SlabBlock.TYPE, SlabType.c), false);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, boundingBox);
            return true;
        }
    }
    
    public static class PortalRoom extends Piece
    {
        private boolean spawnerPlaced;
        
        public PortalRoom(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, integer);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public PortalRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
            this.spawnerPlaced = compoundTag.getBoolean("Mob");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Mob", this.spawnerPlaced);
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            if (structurePiece != null) {
                ((Start)structurePiece).b = this;
            }
        }
        
        public static PortalRoom a(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction direction, final int integer6) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = MutableIntBoundingBox.createRotated(integer2, integer3, integer4, -4, -1, 0, 11, 8, 16, direction);
            if (!Piece.a(mutableIntBoundingBox7) || StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return new PortalRoom(integer6, mutableIntBoundingBox7, direction);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdGenerator.e);
            this.generateEntrance(world, random, boundingBox, EntranceType.c, 4, 1, 0);
            int integer5 = 6;
            this.fillWithOutline(world, boundingBox, 1, integer5, 1, 1, integer5, 14, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 9, integer5, 1, 9, integer5, 14, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 2, integer5, 1, 8, integer5, 2, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 2, integer5, 14, 8, integer5, 14, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 1, 1, 1, 1, 1, 3, Blocks.B.getDefaultState(), Blocks.B.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 9, 1, 1, 9, 1, 3, Blocks.B.getDefaultState(), Blocks.B.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 1, 9, 6, 1, 11, Blocks.B.getDefaultState(), Blocks.B.getDefaultState(), false);
            final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.SOUTH, true);
            final BlockState blockState7 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, true);
            for (int integer6 = 3; integer6 < 14; integer6 += 2) {
                this.fillWithOutline(world, boundingBox, 0, 3, integer6, 0, 4, integer6, blockState6, blockState6, false);
                this.fillWithOutline(world, boundingBox, 10, 3, integer6, 10, 4, integer6, blockState6, blockState6, false);
            }
            for (int integer6 = 2; integer6 < 9; integer6 += 2) {
                this.fillWithOutline(world, boundingBox, integer6, 3, 15, integer6, 4, 15, blockState7, blockState7, false);
            }
            final BlockState blockState8 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dK.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.NORTH);
            this.fillWithOutline(world, boundingBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdGenerator.e);
            this.fillWithOutline(world, boundingBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdGenerator.e);
            for (int integer7 = 4; integer7 <= 6; ++integer7) {
                this.addBlock(world, blockState8, integer7, 1, 4, boundingBox);
                this.addBlock(world, blockState8, integer7, 2, 5, boundingBox);
                this.addBlock(world, blockState8, integer7, 3, 6, boundingBox);
            }
            final BlockState blockState9 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dV.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndPortalFrameBlock.FACING, Direction.NORTH);
            final BlockState blockState10 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dV.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndPortalFrameBlock.FACING, Direction.SOUTH);
            final BlockState blockState11 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dV.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndPortalFrameBlock.FACING, Direction.EAST);
            final BlockState blockState12 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dV.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndPortalFrameBlock.FACING, Direction.WEST);
            boolean boolean13 = true;
            final boolean[] arr14 = new boolean[12];
            for (int integer8 = 0; integer8 < arr14.length; ++integer8) {
                arr14[integer8] = (random.nextFloat() > 0.9f);
                boolean13 &= arr14[integer8];
            }
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[0]), 4, 3, 8, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[1]), 5, 3, 8, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[2]), 6, 3, 8, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[3]), 4, 3, 12, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[4]), 5, 3, 12, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[5]), 6, 3, 12, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[6]), 3, 3, 9, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[7]), 3, 3, 10, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[8]), 3, 3, 11, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState12).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[9]), 7, 3, 9, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState12).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[10]), 7, 3, 10, boundingBox);
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState12).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, arr14[11]), 7, 3, 11, boundingBox);
            if (boolean13) {
                final BlockState blockState13 = Blocks.dU.getDefaultState();
                this.addBlock(world, blockState13, 4, 3, 9, boundingBox);
                this.addBlock(world, blockState13, 5, 3, 9, boundingBox);
                this.addBlock(world, blockState13, 6, 3, 9, boundingBox);
                this.addBlock(world, blockState13, 4, 3, 10, boundingBox);
                this.addBlock(world, blockState13, 5, 3, 10, boundingBox);
                this.addBlock(world, blockState13, 6, 3, 10, boundingBox);
                this.addBlock(world, blockState13, 4, 3, 11, boundingBox);
                this.addBlock(world, blockState13, 5, 3, 11, boundingBox);
                this.addBlock(world, blockState13, 6, 3, 11, boundingBox);
            }
            if (!this.spawnerPlaced) {
                integer5 = this.applyYTransform(3);
                final BlockPos blockPos15 = new BlockPos(this.applyXTransform(5, 6), integer5, this.applyZTransform(5, 6));
                if (boundingBox.contains(blockPos15)) {
                    this.spawnerPlaced = true;
                    world.setBlockState(blockPos15, Blocks.bN.getDefaultState(), 2);
                    final BlockEntity blockEntity16 = world.getBlockEntity(blockPos15);
                    if (blockEntity16 instanceof MobSpawnerBlockEntity) {
                        ((MobSpawnerBlockEntity)blockEntity16).getLogic().setEntityId(EntityType.SILVERFISH);
                    }
                }
            }
            return true;
        }
    }
    
    static class StoneBrickRandomizer extends StructurePiece.BlockRandomizer
    {
        private StoneBrickRandomizer() {
        }
        
        @Override
        public void setBlock(final Random random, final int x, final int y, final int z, final boolean placeBlock) {
            if (placeBlock) {
                final float float6 = random.nextFloat();
                if (float6 < 0.2f) {
                    this.block = Blocks.dp.getDefaultState();
                }
                else if (float6 < 0.5f) {
                    this.block = Blocks.do_.getDefaultState();
                }
                else if (float6 < 0.55f) {
                    this.block = Blocks.dt.getDefaultState();
                }
                else {
                    this.block = Blocks.dn.getDefaultState();
                }
            }
            else {
                this.block = Blocks.kT.getDefaultState();
            }
        }
    }
}
