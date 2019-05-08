package net.minecraft.structure;

import net.minecraft.world.chunk.ChunkPos;
import java.util.Collections;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.Random;
import java.util.List;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Block;
import java.util.Set;
import net.minecraft.block.BlockState;

public class OceanMonumentGenerator
{
    public abstract static class r extends StructurePiece
    {
        protected static final BlockState PRISMARINE;
        protected static final BlockState PRISMARINE_BRICKS;
        protected static final BlockState DARK_PRISMARINE;
        protected static final BlockState d;
        protected static final BlockState SEA_LANTERN;
        protected static final BlockState WATER;
        protected static final Set<Block> ICE_BLOCKS;
        protected static final int h;
        protected static final int i;
        protected static final int j;
        protected static final int k;
        protected v l;
        
        protected static final int b(final int integer1, final int integer2, final int integer3) {
            return integer2 * 25 + integer3 * 5 + integer1;
        }
        
        public r(final StructurePieceType type, final int integer) {
            super(type, integer);
        }
        
        public r(final StructurePieceType structurePieceType, final Direction direction, final MutableIntBoundingBox mutableIntBoundingBox) {
            super(structurePieceType, 1);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        protected r(final StructurePieceType structurePieceType, final int integer2, final Direction direction, final v v, final int integer5, final int integer6, final int integer7) {
            super(structurePieceType, integer2);
            this.setOrientation(direction);
            this.l = v;
            final int integer8 = v.a;
            final int integer9 = integer8 % 5;
            final int integer10 = integer8 / 5 % 5;
            final int integer11 = integer8 / 25;
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                this.boundingBox = new MutableIntBoundingBox(0, 0, 0, integer5 * 8 - 1, integer6 * 4 - 1, integer7 * 8 - 1);
            }
            else {
                this.boundingBox = new MutableIntBoundingBox(0, 0, 0, integer7 * 8 - 1, integer6 * 4 - 1, integer5 * 8 - 1);
            }
            switch (direction) {
                case NORTH: {
                    this.boundingBox.translate(integer9 * 8, integer11 * 4, -(integer10 + integer7) * 8 + 1);
                    break;
                }
                case SOUTH: {
                    this.boundingBox.translate(integer9 * 8, integer11 * 4, integer10 * 8);
                    break;
                }
                case WEST: {
                    this.boundingBox.translate(-(integer10 + integer7) * 8 + 1, integer11 * 4, integer9 * 8);
                    break;
                }
                default: {
                    this.boundingBox.translate(integer10 * 8, integer11 * 4, integer9 * 8);
                    break;
                }
            }
        }
        
        public r(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
        }
        
        protected void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
            for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
                for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                    for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                        final BlockState blockState12 = this.getBlockAt(iWorld, integer10, integer9, integer11, mutableIntBoundingBox);
                        if (!r.ICE_BLOCKS.contains(blockState12.getBlock())) {
                            if (this.applyYTransform(integer9) >= iWorld.getSeaLevel() && blockState12 != r.WATER) {
                                this.addBlock(iWorld, Blocks.AIR.getDefaultState(), integer10, integer9, integer11, mutableIntBoundingBox);
                            }
                            else {
                                this.addBlock(iWorld, r.WATER, integer10, integer9, integer11, mutableIntBoundingBox);
                            }
                        }
                    }
                }
            }
        }
        
        protected void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final boolean boolean5) {
            if (boolean5) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 0, 0, integer4 + 0, integer3 + 2, 0, integer4 + 8 - 1, r.PRISMARINE, r.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 5, 0, integer4 + 0, integer3 + 8 - 1, 0, integer4 + 8 - 1, r.PRISMARINE, r.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 3, 0, integer4 + 0, integer3 + 4, 0, integer4 + 2, r.PRISMARINE, r.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 3, 0, integer4 + 5, integer3 + 4, 0, integer4 + 8 - 1, r.PRISMARINE, r.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 3, 0, integer4 + 2, integer3 + 4, 0, integer4 + 2, r.PRISMARINE_BRICKS, r.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 3, 0, integer4 + 5, integer3 + 4, 0, integer4 + 5, r.PRISMARINE_BRICKS, r.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 2, 0, integer4 + 3, integer3 + 2, 0, integer4 + 4, r.PRISMARINE_BRICKS, r.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 5, 0, integer4 + 3, integer3 + 5, 0, integer4 + 4, r.PRISMARINE_BRICKS, r.PRISMARINE_BRICKS, false);
            }
            else {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3 + 0, 0, integer4 + 0, integer3 + 8 - 1, 0, integer4 + 8 - 1, r.PRISMARINE, r.PRISMARINE, false);
            }
        }
        
        protected void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final BlockState blockState) {
            for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
                for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                    for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                        if (this.getBlockAt(iWorld, integer10, integer9, integer11, mutableIntBoundingBox) == r.WATER) {
                            this.addBlock(iWorld, blockState, integer10, integer9, integer11, mutableIntBoundingBox);
                        }
                    }
                }
            }
        }
        
        protected boolean a(final MutableIntBoundingBox mutableIntBoundingBox, final int integer2, final int integer3, final int integer4, final int integer5) {
            final int integer6 = this.applyXTransform(integer2, integer3);
            final int integer7 = this.applyZTransform(integer2, integer3);
            final int integer8 = this.applyXTransform(integer4, integer5);
            final int integer9 = this.applyZTransform(integer4, integer5);
            return mutableIntBoundingBox.intersectsXZ(Math.min(integer6, integer8), Math.min(integer7, integer9), Math.max(integer6, integer8), Math.max(integer7, integer9));
        }
        
        protected boolean a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5) {
            final int integer6 = this.applyXTransform(integer3, integer5);
            final int integer7 = this.applyYTransform(integer4);
            final int integer8 = this.applyZTransform(integer3, integer5);
            if (mutableIntBoundingBox.contains(new BlockPos(integer6, integer7, integer8))) {
                final ElderGuardianEntity elderGuardianEntity9 = EntityType.ELDER_GUARDIAN.create(iWorld.getWorld());
                elderGuardianEntity9.heal(elderGuardianEntity9.getHealthMaximum());
                elderGuardianEntity9.setPositionAndAngles(integer6 + 0.5, integer7, integer8 + 0.5, 0.0f, 0.0f);
                elderGuardianEntity9.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(elderGuardianEntity9)), SpawnType.d, null, null);
                iWorld.spawnEntity(elderGuardianEntity9);
                return true;
            }
            return false;
        }
        
        static {
            PRISMARINE = Blocks.gi.getDefaultState();
            PRISMARINE_BRICKS = Blocks.gj.getDefaultState();
            DARK_PRISMARINE = Blocks.gk.getDefaultState();
            d = r.PRISMARINE_BRICKS;
            SEA_LANTERN = Blocks.gr.getDefaultState();
            WATER = Blocks.A.getDefaultState();
            ICE_BLOCKS = ImmutableSet.<Block>builder().add(Blocks.cB).add(Blocks.gL).add(Blocks.kN).add(r.WATER.getBlock()).build();
            h = b(2, 0, 0);
            i = b(2, 2, 0);
            j = b(0, 1, 0);
            k = b(4, 1, 0);
        }
    }
    
    public static class Base extends r
    {
        private v p;
        private v q;
        private final List<r> r;
        
        public Base(final Random random, final int integer2, final int integer3, final Direction direction) {
            super(StructurePieceType.OCEAN_MONUMENT_BASE, 0);
            this.r = Lists.newArrayList();
            this.setOrientation(direction);
            final Direction direction2 = this.getFacing();
            if (direction2.getAxis() == Direction.Axis.Z) {
                this.boundingBox = new MutableIntBoundingBox(integer2, 39, integer3, integer2 + 58 - 1, 61, integer3 + 58 - 1);
            }
            else {
                this.boundingBox = new MutableIntBoundingBox(integer2, 39, integer3, integer2 + 58 - 1, 61, integer3 + 58 - 1);
            }
            final List<v> list6 = this.a(random);
            this.p.d = true;
            this.r.add(new Entry(direction2, this.p));
            this.r.add(new CoreRoom(direction2, this.q));
            final List<i> list7 = Lists.newArrayList();
            list7.add(new b());
            list7.add(new d());
            list7.add(new e());
            list7.add(new a());
            list7.add(new c());
            list7.add(new g());
            list7.add(new f());
            for (final v v9 : list6) {
                if (!v9.d && !v9.b()) {
                    for (final i i11 : list7) {
                        if (i11.a(v9)) {
                            this.r.add(i11.a(direction2, v9, random));
                            break;
                        }
                    }
                }
            }
            final int integer4 = this.boundingBox.minY;
            final int integer5 = this.applyXTransform(9, 22);
            final int integer6 = this.applyZTransform(9, 22);
            for (final r r12 : this.r) {
                r12.getBoundingBox().translate(integer5, integer4, integer6);
            }
            final MutableIntBoundingBox mutableIntBoundingBox11 = MutableIntBoundingBox.create(this.applyXTransform(1, 1), this.applyYTransform(1), this.applyZTransform(1, 1), this.applyXTransform(23, 21), this.applyYTransform(8), this.applyZTransform(23, 21));
            final MutableIntBoundingBox mutableIntBoundingBox12 = MutableIntBoundingBox.create(this.applyXTransform(34, 1), this.applyYTransform(1), this.applyZTransform(34, 1), this.applyXTransform(56, 21), this.applyYTransform(8), this.applyZTransform(56, 21));
            final MutableIntBoundingBox mutableIntBoundingBox13 = MutableIntBoundingBox.create(this.applyXTransform(22, 22), this.applyYTransform(13), this.applyZTransform(22, 22), this.applyXTransform(35, 35), this.applyYTransform(17), this.applyZTransform(35, 35));
            int integer7 = random.nextInt();
            this.r.add(new WingRoom(direction2, mutableIntBoundingBox11, integer7++));
            this.r.add(new WingRoom(direction2, mutableIntBoundingBox12, integer7++));
            this.r.add(new Penthouse(direction2, mutableIntBoundingBox13));
        }
        
        public Base(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_BASE, compoundTag);
            this.r = Lists.newArrayList();
        }
        
        private List<v> a(final Random random) {
            final v[] arr2 = new v[75];
            for (int integer3 = 0; integer3 < 5; ++integer3) {
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    final int integer5 = 0;
                    final int integer6 = OceanMonumentGenerator.r.b(integer3, 0, integer4);
                    arr2[integer6] = new v(integer6);
                }
            }
            for (int integer3 = 0; integer3 < 5; ++integer3) {
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    final int integer5 = 1;
                    final int integer6 = OceanMonumentGenerator.r.b(integer3, 1, integer4);
                    arr2[integer6] = new v(integer6);
                }
            }
            for (int integer3 = 1; integer3 < 4; ++integer3) {
                for (int integer4 = 0; integer4 < 2; ++integer4) {
                    final int integer5 = 2;
                    final int integer6 = OceanMonumentGenerator.r.b(integer3, 2, integer4);
                    arr2[integer6] = new v(integer6);
                }
            }
            this.p = arr2[Base.h];
            for (int integer3 = 0; integer3 < 5; ++integer3) {
                for (int integer4 = 0; integer4 < 5; ++integer4) {
                    for (int integer5 = 0; integer5 < 3; ++integer5) {
                        final int integer6 = OceanMonumentGenerator.r.b(integer3, integer5, integer4);
                        if (arr2[integer6] != null) {
                            for (final Direction direction10 : Direction.values()) {
                                final int integer7 = integer3 + direction10.getOffsetX();
                                final int integer8 = integer5 + direction10.getOffsetY();
                                final int integer9 = integer4 + direction10.getOffsetZ();
                                if (integer7 >= 0 && integer7 < 5 && integer9 >= 0 && integer9 < 5 && integer8 >= 0 && integer8 < 3) {
                                    final int integer10 = OceanMonumentGenerator.r.b(integer7, integer8, integer9);
                                    if (arr2[integer10] != null) {
                                        if (integer9 == integer4) {
                                            arr2[integer6].a(direction10, arr2[integer10]);
                                        }
                                        else {
                                            arr2[integer6].a(direction10.getOpposite(), arr2[integer10]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final v v3 = new v(1003);
            final v v4 = new v(1001);
            final v v5 = new v(1002);
            arr2[Base.i].a(Direction.UP, v3);
            arr2[Base.j].a(Direction.SOUTH, v4);
            arr2[Base.k].a(Direction.SOUTH, v5);
            v3.d = true;
            v4.d = true;
            v5.d = true;
            this.p.e = true;
            (this.q = arr2[OceanMonumentGenerator.r.b(random.nextInt(4), 0, 2)]).d = true;
            this.q.b[Direction.EAST.getId()].d = true;
            this.q.b[Direction.NORTH.getId()].d = true;
            this.q.b[Direction.EAST.getId()].b[Direction.NORTH.getId()].d = true;
            this.q.b[Direction.UP.getId()].d = true;
            this.q.b[Direction.EAST.getId()].b[Direction.UP.getId()].d = true;
            this.q.b[Direction.NORTH.getId()].b[Direction.UP.getId()].d = true;
            this.q.b[Direction.EAST.getId()].b[Direction.NORTH.getId()].b[Direction.UP.getId()].d = true;
            final List<v> list6 = Lists.newArrayList();
            for (final v v6 : arr2) {
                if (v6 != null) {
                    v6.a();
                    list6.add(v6);
                }
            }
            v3.a();
            Collections.shuffle(list6, random);
            int integer11 = 1;
            for (final v v7 : list6) {
                int integer12 = 0;
                int integer7 = 0;
                while (integer12 < 2 && integer7 < 5) {
                    ++integer7;
                    final int integer8 = random.nextInt(6);
                    if (v7.c[integer8]) {
                        final int integer9 = Direction.byId(integer8).getOpposite().getId();
                        v7.c[integer8] = false;
                        v7.b[integer8].c[integer9] = false;
                        if (v7.a(integer11++) && v7.b[integer8].a(integer11++)) {
                            ++integer12;
                        }
                        else {
                            v7.c[integer8] = true;
                            v7.b[integer8].c[integer9] = true;
                        }
                    }
                }
            }
            list6.add(v3);
            list6.add(v4);
            list6.add(v5);
            return list6;
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final int integer5 = Math.max(world.getSeaLevel(), 64) - this.boundingBox.minY;
            this.a(world, boundingBox, 0, 0, 0, 58, integer5, 58);
            this.a(false, 0, world, random, boundingBox);
            this.a(true, 33, world, random, boundingBox);
            this.a(world, random, boundingBox);
            this.b(world, random, boundingBox);
            this.c(world, random, boundingBox);
            this.d(world, random, boundingBox);
            this.e(world, random, boundingBox);
            this.f(world, random, boundingBox);
            for (int integer6 = 0; integer6 < 7; ++integer6) {
                int integer7 = 0;
                while (integer7 < 7) {
                    if (integer7 == 0 && integer6 == 3) {
                        integer7 = 6;
                    }
                    final int integer8 = integer6 * 9;
                    final int integer9 = integer7 * 9;
                    for (int integer10 = 0; integer10 < 4; ++integer10) {
                        for (int integer11 = 0; integer11 < 4; ++integer11) {
                            this.addBlock(world, Base.PRISMARINE_BRICKS, integer8 + integer10, 0, integer9 + integer11, boundingBox);
                            this.b(world, Base.PRISMARINE_BRICKS, integer8 + integer10, -1, integer9 + integer11, boundingBox);
                        }
                    }
                    if (integer6 == 0 || integer6 == 6) {
                        ++integer7;
                    }
                    else {
                        integer7 += 6;
                    }
                }
            }
            for (int integer6 = 0; integer6 < 5; ++integer6) {
                this.a(world, boundingBox, -1 - integer6, 0 + integer6 * 2, -1 - integer6, -1 - integer6, 23, 58 + integer6);
                this.a(world, boundingBox, 58 + integer6, 0 + integer6 * 2, -1 - integer6, 58 + integer6, 23, 58 + integer6);
                this.a(world, boundingBox, 0 - integer6, 0 + integer6 * 2, -1 - integer6, 57 + integer6, 23, -1 - integer6);
                this.a(world, boundingBox, 0 - integer6, 0 + integer6 * 2, 58 + integer6, 57 + integer6, 23, 58 + integer6);
            }
            for (final r r7 : this.r) {
                if (r7.getBoundingBox().intersects(boundingBox)) {
                    r7.generate(world, random, boundingBox, pos);
                }
            }
            return true;
        }
        
        private void a(final boolean boolean1, final int integer, final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            final int integer2 = 24;
            if (this.a(mutableIntBoundingBox, integer, 0, integer + 23, 20)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 0, 0, 0, integer + 24, 0, 20, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, integer + 0, 1, 0, integer + 24, 10, 20);
                for (int integer3 = 0; integer3 < 4; ++integer3) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + integer3, integer3 + 1, integer3, integer + integer3, integer3 + 1, 20, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + integer3 + 7, integer3 + 5, integer3 + 7, integer + integer3 + 7, integer3 + 5, 20, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 17 - integer3, integer3 + 5, integer3 + 7, integer + 17 - integer3, integer3 + 5, 20, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 24 - integer3, integer3 + 1, integer3, integer + 24 - integer3, integer3 + 1, 20, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + integer3 + 1, integer3 + 1, integer3, integer + 23 - integer3, integer3 + 1, integer3, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + integer3 + 8, integer3 + 5, integer3 + 7, integer + 16 - integer3, integer3 + 5, integer3 + 7, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 4, 4, 4, integer + 6, 4, 20, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 7, 4, 4, integer + 17, 4, 6, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 18, 4, 4, integer + 20, 4, 20, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 11, 8, 11, integer + 13, 8, 20, Base.PRISMARINE, Base.PRISMARINE, false);
                this.addBlock(iWorld, Base.d, integer + 12, 9, 12, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.d, integer + 12, 9, 15, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.d, integer + 12, 9, 18, mutableIntBoundingBox);
                int integer3 = integer + (boolean1 ? 19 : 5);
                final int integer4 = integer + (boolean1 ? 5 : 19);
                for (int integer5 = 20; integer5 >= 5; integer5 -= 3) {
                    this.addBlock(iWorld, Base.d, integer3, 5, integer5, mutableIntBoundingBox);
                }
                for (int integer5 = 19; integer5 >= 7; integer5 -= 3) {
                    this.addBlock(iWorld, Base.d, integer4, 5, integer5, mutableIntBoundingBox);
                }
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    final int integer6 = boolean1 ? (integer + 24 - (17 - integer5 * 3)) : (integer + 17 - integer5 * 3);
                    this.addBlock(iWorld, Base.d, integer6, 5, 5, mutableIntBoundingBox);
                }
                this.addBlock(iWorld, Base.d, integer4, 5, 5, mutableIntBoundingBox);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 11, 1, 12, integer + 13, 7, 12, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer + 12, 1, 11, integer + 12, 7, 13, Base.PRISMARINE, Base.PRISMARINE, false);
            }
        }
        
        private void a(final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            if (this.a(mutableIntBoundingBox, 22, 5, 35, 17)) {
                this.a(iWorld, mutableIntBoundingBox, 25, 0, 0, 32, 8, 20);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 24, 2, 5 + integer4 * 4, 24, 4, 5 + integer4 * 4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 22, 4, 5 + integer4 * 4, 23, 4, 5 + integer4 * 4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 25, 5, 5 + integer4 * 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 26, 6, 5 + integer4 * 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.SEA_LANTERN, 26, 5, 5 + integer4 * 4, mutableIntBoundingBox);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 33, 2, 5 + integer4 * 4, 33, 4, 5 + integer4 * 4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 34, 4, 5 + integer4 * 4, 35, 4, 5 + integer4 * 4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 32, 5, 5 + integer4 * 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 31, 6, 5 + integer4 * 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.SEA_LANTERN, 31, 5, 5 + integer4 * 4, mutableIntBoundingBox);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 27, 6, 5 + integer4 * 4, 30, 6, 5 + integer4 * 4, Base.PRISMARINE, Base.PRISMARINE, false);
                }
            }
        }
        
        private void b(final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            if (this.a(mutableIntBoundingBox, 15, 20, 42, 21)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 15, 0, 21, 42, 0, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 26, 1, 21, 31, 3, 21);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 21, 12, 21, 36, 12, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 17, 11, 21, 40, 11, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 16, 10, 21, 41, 10, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 15, 7, 21, 42, 9, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 16, 6, 21, 41, 6, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 17, 5, 21, 40, 5, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 21, 4, 21, 36, 4, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 22, 3, 21, 26, 3, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 31, 3, 21, 35, 3, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 23, 2, 21, 25, 2, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 32, 2, 21, 34, 2, 21, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 28, 4, 20, 29, 4, 21, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 27, 3, 21, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 30, 3, 21, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 26, 2, 21, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 31, 2, 21, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 25, 1, 21, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 32, 1, 21, mutableIntBoundingBox);
                for (int integer4 = 0; integer4 < 7; ++integer4) {
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 28 - integer4, 6 + integer4, 21, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 29 + integer4, 6 + integer4, 21, mutableIntBoundingBox);
                }
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 28 - integer4, 9 + integer4, 21, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 29 + integer4, 9 + integer4, 21, mutableIntBoundingBox);
                }
                this.addBlock(iWorld, Base.DARK_PRISMARINE, 28, 12, 21, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.DARK_PRISMARINE, 29, 12, 21, mutableIntBoundingBox);
                for (int integer4 = 0; integer4 < 3; ++integer4) {
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 22 - integer4 * 2, 8, 21, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 22 - integer4 * 2, 9, 21, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 35 + integer4 * 2, 8, 21, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.DARK_PRISMARINE, 35 + integer4 * 2, 9, 21, mutableIntBoundingBox);
                }
                this.a(iWorld, mutableIntBoundingBox, 15, 13, 21, 42, 15, 21);
                this.a(iWorld, mutableIntBoundingBox, 15, 1, 21, 15, 6, 21);
                this.a(iWorld, mutableIntBoundingBox, 16, 1, 21, 16, 5, 21);
                this.a(iWorld, mutableIntBoundingBox, 17, 1, 21, 20, 4, 21);
                this.a(iWorld, mutableIntBoundingBox, 21, 1, 21, 21, 3, 21);
                this.a(iWorld, mutableIntBoundingBox, 22, 1, 21, 22, 2, 21);
                this.a(iWorld, mutableIntBoundingBox, 23, 1, 21, 24, 1, 21);
                this.a(iWorld, mutableIntBoundingBox, 42, 1, 21, 42, 6, 21);
                this.a(iWorld, mutableIntBoundingBox, 41, 1, 21, 41, 5, 21);
                this.a(iWorld, mutableIntBoundingBox, 37, 1, 21, 40, 4, 21);
                this.a(iWorld, mutableIntBoundingBox, 36, 1, 21, 36, 3, 21);
                this.a(iWorld, mutableIntBoundingBox, 33, 1, 21, 34, 1, 21);
                this.a(iWorld, mutableIntBoundingBox, 35, 1, 21, 35, 2, 21);
            }
        }
        
        private void c(final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            if (this.a(mutableIntBoundingBox, 21, 21, 36, 36)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 21, 0, 22, 36, 0, 36, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 21, 1, 22, 36, 23, 36);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 21 + integer4, 13 + integer4, 21 + integer4, 36 - integer4, 13 + integer4, 21 + integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 21 + integer4, 13 + integer4, 36 - integer4, 36 - integer4, 13 + integer4, 36 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 21 + integer4, 13 + integer4, 22 + integer4, 21 + integer4, 13 + integer4, 35 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 36 - integer4, 13 + integer4, 22 + integer4, 36 - integer4, 13 + integer4, 35 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 25, 16, 25, 32, 16, 32, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 25, 17, 25, 25, 19, 25, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 32, 17, 25, 32, 19, 25, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 25, 17, 32, 25, 19, 32, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 32, 17, 32, 32, 19, 32, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 26, 20, 26, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 27, 21, 27, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.SEA_LANTERN, 27, 20, 27, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 26, 20, 31, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 27, 21, 30, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.SEA_LANTERN, 27, 20, 30, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 31, 20, 31, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 30, 21, 30, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.SEA_LANTERN, 30, 20, 30, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 31, 20, 26, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.PRISMARINE_BRICKS, 30, 21, 27, mutableIntBoundingBox);
                this.addBlock(iWorld, Base.SEA_LANTERN, 30, 20, 27, mutableIntBoundingBox);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 28, 21, 27, 29, 21, 27, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 27, 21, 28, 27, 21, 29, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 28, 21, 30, 29, 21, 30, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 30, 21, 28, 30, 21, 29, Base.PRISMARINE, Base.PRISMARINE, false);
            }
        }
        
        private void d(final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            if (this.a(mutableIntBoundingBox, 0, 21, 6, 58)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 21, 6, 0, 57, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 0, 1, 21, 6, 7, 57);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 4, 21, 6, 4, 53, Base.PRISMARINE, Base.PRISMARINE, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer4, integer4 + 1, 21, integer4, integer4 + 1, 57 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 23; integer4 < 53; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, 5, 5, integer4, mutableIntBoundingBox);
                }
                this.addBlock(iWorld, Base.d, 5, 5, 52, mutableIntBoundingBox);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer4, integer4 + 1, 21, integer4, integer4 + 1, 57 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 52, 6, 3, 52, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 51, 5, 3, 53, Base.PRISMARINE, Base.PRISMARINE, false);
            }
            if (this.a(mutableIntBoundingBox, 51, 21, 58, 58)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 51, 0, 21, 57, 0, 57, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 51, 1, 21, 57, 7, 57);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 51, 4, 21, 53, 4, 53, Base.PRISMARINE, Base.PRISMARINE, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 57 - integer4, integer4 + 1, 21, 57 - integer4, integer4 + 1, 57 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 23; integer4 < 53; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, 52, 5, integer4, mutableIntBoundingBox);
                }
                this.addBlock(iWorld, Base.d, 52, 5, 52, mutableIntBoundingBox);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 51, 1, 52, 53, 3, 52, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 52, 1, 51, 52, 3, 53, Base.PRISMARINE, Base.PRISMARINE, false);
            }
            if (this.a(mutableIntBoundingBox, 0, 51, 57, 57)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 0, 51, 50, 0, 57, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 7, 1, 51, 50, 10, 57);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer4 + 1, integer4 + 1, 57 - integer4, 56 - integer4, integer4 + 1, 57 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
            }
        }
        
        private void e(final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            if (this.a(mutableIntBoundingBox, 7, 21, 13, 50)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 0, 21, 13, 0, 50, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 7, 1, 21, 13, 10, 50);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 8, 21, 13, 8, 53, Base.PRISMARINE, Base.PRISMARINE, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer4 + 7, integer4 + 5, 21, integer4 + 7, integer4 + 5, 54, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 21; integer4 <= 45; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, 12, 9, integer4, mutableIntBoundingBox);
                }
            }
            if (this.a(mutableIntBoundingBox, 44, 21, 50, 54)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 44, 0, 21, 50, 0, 50, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 44, 1, 21, 50, 10, 50);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 44, 8, 21, 46, 8, 53, Base.PRISMARINE, Base.PRISMARINE, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 50 - integer4, integer4 + 5, 21, 50 - integer4, integer4 + 5, 54, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 21; integer4 <= 45; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, 45, 9, integer4, mutableIntBoundingBox);
                }
            }
            if (this.a(mutableIntBoundingBox, 8, 44, 49, 54)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 14, 0, 44, 43, 0, 50, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 14, 1, 44, 43, 10, 50);
                for (int integer4 = 12; integer4 <= 45; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, integer4, 9, 45, mutableIntBoundingBox);
                    this.addBlock(iWorld, Base.d, integer4, 9, 52, mutableIntBoundingBox);
                    if (integer4 == 12 || integer4 == 18 || integer4 == 24 || integer4 == 33 || integer4 == 39 || integer4 == 45) {
                        this.addBlock(iWorld, Base.d, integer4, 9, 47, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 9, 50, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 10, 45, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 10, 46, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 10, 51, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 10, 52, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 11, 47, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 11, 50, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 12, 48, mutableIntBoundingBox);
                        this.addBlock(iWorld, Base.d, integer4, 12, 49, mutableIntBoundingBox);
                    }
                }
                for (int integer4 = 0; integer4 < 3; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 8 + integer4, 5 + integer4, 54, 49 - integer4, 5 + integer4, 54, Base.PRISMARINE, Base.PRISMARINE, false);
                }
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 8, 54, 46, 8, 54, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 14, 8, 44, 43, 8, 53, Base.PRISMARINE, Base.PRISMARINE, false);
            }
        }
        
        private void f(final IWorld iWorld, final Random random, final MutableIntBoundingBox mutableIntBoundingBox) {
            if (this.a(mutableIntBoundingBox, 14, 21, 20, 43)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 14, 0, 21, 20, 0, 43, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 14, 1, 22, 20, 14, 43);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 18, 12, 22, 20, 12, 39, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 18, 12, 21, 20, 12, 21, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, integer4 + 14, integer4 + 9, 21, integer4 + 14, integer4 + 9, 43 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 23; integer4 <= 39; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, 19, 13, integer4, mutableIntBoundingBox);
                }
            }
            if (this.a(mutableIntBoundingBox, 37, 21, 43, 43)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 37, 0, 21, 43, 0, 43, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 37, 1, 22, 43, 14, 43);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 37, 12, 22, 39, 12, 39, Base.PRISMARINE, Base.PRISMARINE, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 37, 12, 21, 39, 12, 21, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 43 - integer4, integer4 + 9, 21, 43 - integer4, integer4 + 9, 43 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 23; integer4 <= 39; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, 38, 13, integer4, mutableIntBoundingBox);
                }
            }
            if (this.a(mutableIntBoundingBox, 15, 37, 42, 43)) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 21, 0, 37, 36, 0, 43, Base.PRISMARINE, Base.PRISMARINE, false);
                this.a(iWorld, mutableIntBoundingBox, 21, 1, 37, 36, 14, 43);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 21, 12, 37, 36, 12, 39, Base.PRISMARINE, Base.PRISMARINE, false);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 15 + integer4, integer4 + 9, 43 - integer4, 42 - integer4, integer4 + 9, 43 - integer4, Base.PRISMARINE_BRICKS, Base.PRISMARINE_BRICKS, false);
                }
                for (int integer4 = 21; integer4 <= 36; integer4 += 3) {
                    this.addBlock(iWorld, Base.d, integer4, 13, 38, mutableIntBoundingBox);
                }
            }
        }
    }
    
    public static class Entry extends r
    {
        public Entry(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, direction, v, 1, 1, 1);
        }
        
        public Entry(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 2, 3, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 3, 0, 7, 3, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 2, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 2, 0, 7, 2, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 7, 7, 3, 7, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 0, 2, 3, 0, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 1, 0, 6, 3, 0, Entry.PRISMARINE_BRICKS, Entry.PRISMARINE_BRICKS, false);
            if (this.l.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 7, 4, 2, 7);
            }
            if (this.l.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 3, 1, 2, 4);
            }
            if (this.l.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 6, 1, 3, 7, 2, 4);
            }
            return true;
        }
    }
    
    public static class SimpleRoom extends r
    {
        private int p;
        
        public SimpleRoom(final Direction direction, final v v, final Random random) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, direction, v, 1, 1, 1);
            this.p = random.nextInt(3);
        }
        
        public SimpleRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 0, 0, this.l.c[Direction.DOWN.getId()]);
            }
            if (this.l.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 4, 1, 6, 4, 6, SimpleRoom.PRISMARINE);
            }
            final boolean boolean5 = this.p != 0 && random.nextBoolean() && !this.l.c[Direction.DOWN.getId()] && !this.l.c[Direction.UP.getId()] && this.l.c() > 1;
            if (this.p == 0) {
                this.fillWithOutline(world, boundingBox, 0, 1, 0, 2, 1, 2, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 3, 0, 2, 3, 2, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 2, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 1, 2, 0, 2, 2, 0, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 1, 2, 1, boundingBox);
                this.fillWithOutline(world, boundingBox, 5, 1, 0, 7, 1, 2, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 5, 3, 0, 7, 3, 2, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 2, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 2, 0, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 6, 2, 1, boundingBox);
                this.fillWithOutline(world, boundingBox, 0, 1, 5, 2, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 3, 5, 2, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 2, 7, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 1, 2, 7, 2, 2, 7, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 1, 2, 6, boundingBox);
                this.fillWithOutline(world, boundingBox, 5, 1, 5, 7, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 5, 3, 5, 7, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 2, 5, 7, 2, 7, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 5, 2, 7, 6, 2, 7, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 6, 2, 6, boundingBox);
                if (this.l.c[Direction.SOUTH.getId()]) {
                    this.fillWithOutline(world, boundingBox, 3, 3, 0, 4, 3, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, 3, 3, 0, 4, 3, 1, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 3, 2, 0, 4, 2, 0, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 1, 1, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                if (this.l.c[Direction.NORTH.getId()]) {
                    this.fillWithOutline(world, boundingBox, 3, 3, 7, 4, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, 3, 3, 6, 4, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 3, 2, 7, 4, 2, 7, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 3, 1, 6, 4, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                if (this.l.c[Direction.WEST.getId()]) {
                    this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 3, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, 0, 3, 3, 1, 3, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 0, 2, 3, 0, 2, 4, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 0, 1, 3, 1, 1, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                if (this.l.c[Direction.EAST.getId()]) {
                    this.fillWithOutline(world, boundingBox, 7, 3, 3, 7, 3, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, 6, 3, 3, 7, 3, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 7, 2, 3, 7, 2, 4, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 6, 1, 3, 7, 1, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
            }
            else if (this.p == 1) {
                this.fillWithOutline(world, boundingBox, 2, 1, 2, 2, 3, 2, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 2, 1, 5, 2, 3, 5, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 5, 1, 5, 5, 3, 5, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 5, 1, 2, 5, 3, 2, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 2, 2, 2, boundingBox);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 2, 2, 5, boundingBox);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 5, 2, 5, boundingBox);
                this.addBlock(world, SimpleRoom.SEA_LANTERN, 5, 2, 2, boundingBox);
                this.fillWithOutline(world, boundingBox, 0, 1, 0, 1, 3, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 1, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 1, 7, 1, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 1, 6, 0, 3, 6, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 1, 7, 7, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 1, 6, 7, 3, 6, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 1, 0, 7, 3, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 1, 1, 7, 3, 1, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.addBlock(world, SimpleRoom.PRISMARINE, 1, 2, 0, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 0, 2, 1, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 1, 2, 7, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 0, 2, 6, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 6, 2, 7, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 7, 2, 6, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 6, 2, 0, boundingBox);
                this.addBlock(world, SimpleRoom.PRISMARINE, 7, 2, 1, boundingBox);
                if (!this.l.c[Direction.SOUTH.getId()]) {
                    this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                if (!this.l.c[Direction.NORTH.getId()]) {
                    this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                if (!this.l.c[Direction.WEST.getId()]) {
                    this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 3, 6, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 2, 6, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 1, 6, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
                if (!this.l.c[Direction.EAST.getId()]) {
                    this.fillWithOutline(world, boundingBox, 7, 3, 1, 7, 3, 6, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 7, 2, 1, 7, 2, 6, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                    this.fillWithOutline(world, boundingBox, 7, 1, 1, 7, 1, 6, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                }
            }
            else if (this.p == 2) {
                this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 7, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 0, 1, 3, 0, 2, 4, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 7, 1, 3, 7, 2, 4, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 2, 0, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 3, 1, 7, 4, 2, 7, SimpleRoom.DARK_PRISMARINE, SimpleRoom.DARK_PRISMARINE, false);
                if (this.l.c[Direction.SOUTH.getId()]) {
                    this.a(world, boundingBox, 3, 1, 0, 4, 2, 0);
                }
                if (this.l.c[Direction.NORTH.getId()]) {
                    this.a(world, boundingBox, 3, 1, 7, 4, 2, 7);
                }
                if (this.l.c[Direction.WEST.getId()]) {
                    this.a(world, boundingBox, 0, 1, 3, 0, 2, 4);
                }
                if (this.l.c[Direction.EAST.getId()]) {
                    this.a(world, boundingBox, 7, 1, 3, 7, 2, 4);
                }
            }
            if (boolean5) {
                this.fillWithOutline(world, boundingBox, 3, 1, 3, 4, 1, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 3, 2, 3, 4, 2, 4, SimpleRoom.PRISMARINE, SimpleRoom.PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 3, 3, 3, 4, 3, 4, SimpleRoom.PRISMARINE_BRICKS, SimpleRoom.PRISMARINE_BRICKS, false);
            }
            return true;
        }
    }
    
    public static class SimpleRoomTop extends r
    {
        public SimpleRoomTop(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, direction, v, 1, 1, 1);
        }
        
        public SimpleRoomTop(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 0, 0, this.l.c[Direction.DOWN.getId()]);
            }
            if (this.l.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 4, 1, 6, 4, 6, SimpleRoomTop.PRISMARINE);
            }
            for (int integer5 = 1; integer5 <= 6; ++integer5) {
                for (int integer6 = 1; integer6 <= 6; ++integer6) {
                    if (random.nextInt(3) != 0) {
                        final int integer7 = 2 + ((random.nextInt(4) != 0) ? 1 : 0);
                        final BlockState blockState8 = Blocks.an.getDefaultState();
                        this.fillWithOutline(world, boundingBox, integer5, integer7, integer6, integer5, 3, integer6, blockState8, blockState8, false);
                    }
                }
            }
            this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 7, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 7, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, SimpleRoomTop.PRISMARINE_BRICKS, SimpleRoomTop.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 3, 0, 2, 4, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 7, 1, 3, 7, 2, 4, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 2, 0, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 3, 1, 7, 4, 2, 7, SimpleRoomTop.DARK_PRISMARINE, SimpleRoomTop.DARK_PRISMARINE, false);
            if (this.l.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            return true;
        }
    }
    
    public static class DoubleYRoom extends r
    {
        public DoubleYRoom(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, direction, v, 1, 2, 1);
        }
        
        public DoubleYRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 0, 0, this.l.c[Direction.DOWN.getId()]);
            }
            final v v5 = this.l.b[Direction.UP.getId()];
            if (v5.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 8, 1, 6, 8, 6, DoubleYRoom.PRISMARINE);
            }
            this.fillWithOutline(world, boundingBox, 0, 4, 0, 0, 4, 7, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 7, 4, 0, 7, 4, 7, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 4, 0, 6, 4, 0, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 4, 7, 6, 4, 7, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 4, 1, 2, 4, 2, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 4, 2, 1, 4, 2, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 4, 1, 5, 4, 2, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 4, 2, 6, 4, 2, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 4, 5, 2, 4, 6, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 4, 5, 1, 4, 5, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 4, 5, 5, 4, 6, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 4, 5, 6, 4, 5, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
            v v6 = this.l;
            for (int integer7 = 1; integer7 <= 5; integer7 += 4) {
                int integer8 = 0;
                if (v6.c[Direction.SOUTH.getId()]) {
                    this.fillWithOutline(world, boundingBox, 2, integer7, integer8, 2, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 5, integer7, integer8, 5, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 3, integer7 + 2, integer8, 4, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, 0, integer7, integer8, 7, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 0, integer7 + 1, integer8, 7, integer7 + 1, integer8, DoubleYRoom.PRISMARINE, DoubleYRoom.PRISMARINE, false);
                }
                integer8 = 7;
                if (v6.c[Direction.NORTH.getId()]) {
                    this.fillWithOutline(world, boundingBox, 2, integer7, integer8, 2, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 5, integer7, integer8, 5, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 3, integer7 + 2, integer8, 4, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, 0, integer7, integer8, 7, integer7 + 2, integer8, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, 0, integer7 + 1, integer8, 7, integer7 + 1, integer8, DoubleYRoom.PRISMARINE, DoubleYRoom.PRISMARINE, false);
                }
                int integer9 = 0;
                if (v6.c[Direction.WEST.getId()]) {
                    this.fillWithOutline(world, boundingBox, integer9, integer7, 2, integer9, integer7 + 2, 2, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer9, integer7, 5, integer9, integer7 + 2, 5, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer9, integer7 + 2, 3, integer9, integer7 + 2, 4, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, integer9, integer7, 0, integer9, integer7 + 2, 7, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer9, integer7 + 1, 0, integer9, integer7 + 1, 7, DoubleYRoom.PRISMARINE, DoubleYRoom.PRISMARINE, false);
                }
                integer9 = 7;
                if (v6.c[Direction.EAST.getId()]) {
                    this.fillWithOutline(world, boundingBox, integer9, integer7, 2, integer9, integer7 + 2, 2, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer9, integer7, 5, integer9, integer7 + 2, 5, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer9, integer7 + 2, 3, integer9, integer7 + 2, 4, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                }
                else {
                    this.fillWithOutline(world, boundingBox, integer9, integer7, 0, integer9, integer7 + 2, 7, DoubleYRoom.PRISMARINE_BRICKS, DoubleYRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer9, integer7 + 1, 0, integer9, integer7 + 1, 7, DoubleYRoom.PRISMARINE, DoubleYRoom.PRISMARINE, false);
                }
                v6 = v5;
            }
            return true;
        }
    }
    
    public static class DoubleXRoom extends r
    {
        public DoubleXRoom(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, direction, v, 2, 1, 1);
        }
        
        public DoubleXRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final v v5 = this.l.b[Direction.EAST.getId()];
            final v v6 = this.l;
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 8, 0, v5.c[Direction.DOWN.getId()]);
                this.a(world, boundingBox, 0, 0, v6.c[Direction.DOWN.getId()]);
            }
            if (v6.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 4, 1, 7, 4, 6, DoubleXRoom.PRISMARINE);
            }
            if (v5.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 8, 4, 1, 14, 4, 6, DoubleXRoom.PRISMARINE);
            }
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 15, 3, 0, 15, 3, 7, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 0, 15, 3, 0, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 7, 14, 3, 7, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, DoubleXRoom.PRISMARINE, DoubleXRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 15, 2, 0, 15, 2, 7, DoubleXRoom.PRISMARINE, DoubleXRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 0, 15, 2, 0, DoubleXRoom.PRISMARINE, DoubleXRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 7, 14, 2, 7, DoubleXRoom.PRISMARINE, DoubleXRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 15, 1, 0, 15, 1, 7, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 0, 15, 1, 0, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 7, 14, 1, 7, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 1, 0, 10, 1, 4, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 2, 0, 9, 2, 3, DoubleXRoom.PRISMARINE, DoubleXRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 5, 3, 0, 10, 3, 4, DoubleXRoom.PRISMARINE_BRICKS, DoubleXRoom.PRISMARINE_BRICKS, false);
            this.addBlock(world, DoubleXRoom.SEA_LANTERN, 6, 2, 3, boundingBox);
            this.addBlock(world, DoubleXRoom.SEA_LANTERN, 9, 2, 3, boundingBox);
            if (v6.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (v6.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 7, 4, 2, 7);
            }
            if (v6.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 3, 0, 2, 4);
            }
            if (v5.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 11, 1, 0, 12, 2, 0);
            }
            if (v5.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 11, 1, 7, 12, 2, 7);
            }
            if (v5.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 15, 1, 3, 15, 2, 4);
            }
            return true;
        }
    }
    
    public static class DoubleZRoom extends r
    {
        public DoubleZRoom(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, direction, v, 1, 1, 2);
        }
        
        public DoubleZRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final v v5 = this.l.b[Direction.NORTH.getId()];
            final v v6 = this.l;
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 0, 8, v5.c[Direction.DOWN.getId()]);
                this.a(world, boundingBox, 0, 0, v6.c[Direction.DOWN.getId()]);
            }
            if (v6.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 4, 1, 6, 4, 7, DoubleZRoom.PRISMARINE);
            }
            if (v5.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 4, 8, 6, 4, 14, DoubleZRoom.PRISMARINE);
            }
            this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 15, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 15, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 0, 7, 3, 0, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 15, 6, 3, 15, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 15, DoubleZRoom.PRISMARINE, DoubleZRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 15, DoubleZRoom.PRISMARINE, DoubleZRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 0, 7, 2, 0, DoubleZRoom.PRISMARINE, DoubleZRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 1, 2, 15, 6, 2, 15, DoubleZRoom.PRISMARINE, DoubleZRoom.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 15, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 15, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 0, 7, 1, 0, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 15, 6, 1, 15, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 1, 1, 1, 2, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 1, 1, 6, 1, 2, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 1, 1, 3, 2, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 3, 1, 6, 3, 2, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 1, 13, 1, 1, 14, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 1, 13, 6, 1, 14, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 3, 13, 1, 3, 14, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 3, 13, 6, 3, 14, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 1, 6, 2, 3, 6, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 1, 6, 5, 3, 6, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 1, 9, 2, 3, 9, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 1, 9, 5, 3, 9, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 3, 2, 6, 4, 2, 6, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 3, 2, 9, 4, 2, 9, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 2, 7, 2, 2, 8, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 2, 7, 5, 2, 8, DoubleZRoom.PRISMARINE_BRICKS, DoubleZRoom.PRISMARINE_BRICKS, false);
            this.addBlock(world, DoubleZRoom.SEA_LANTERN, 2, 2, 5, boundingBox);
            this.addBlock(world, DoubleZRoom.SEA_LANTERN, 5, 2, 5, boundingBox);
            this.addBlock(world, DoubleZRoom.SEA_LANTERN, 2, 2, 10, boundingBox);
            this.addBlock(world, DoubleZRoom.SEA_LANTERN, 5, 2, 10, boundingBox);
            this.addBlock(world, DoubleZRoom.PRISMARINE_BRICKS, 2, 3, 5, boundingBox);
            this.addBlock(world, DoubleZRoom.PRISMARINE_BRICKS, 5, 3, 5, boundingBox);
            this.addBlock(world, DoubleZRoom.PRISMARINE_BRICKS, 2, 3, 10, boundingBox);
            this.addBlock(world, DoubleZRoom.PRISMARINE_BRICKS, 5, 3, 10, boundingBox);
            if (v6.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (v6.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 7, 1, 3, 7, 2, 4);
            }
            if (v6.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 3, 0, 2, 4);
            }
            if (v5.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 15, 4, 2, 15);
            }
            if (v5.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 11, 0, 2, 12);
            }
            if (v5.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 7, 1, 11, 7, 2, 12);
            }
            return true;
        }
    }
    
    public static class DoubleXYRoom extends r
    {
        public DoubleXYRoom(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, 1, direction, v, 2, 2, 1);
        }
        
        public DoubleXYRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final v v5 = this.l.b[Direction.EAST.getId()];
            final v v6 = this.l;
            final v v7 = v6.b[Direction.UP.getId()];
            final v v8 = v5.b[Direction.UP.getId()];
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 8, 0, v5.c[Direction.DOWN.getId()]);
                this.a(world, boundingBox, 0, 0, v6.c[Direction.DOWN.getId()]);
            }
            if (v7.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 8, 1, 7, 8, 6, DoubleXYRoom.PRISMARINE);
            }
            if (v8.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 8, 8, 1, 14, 8, 6, DoubleXYRoom.PRISMARINE);
            }
            for (int integer9 = 1; integer9 <= 7; ++integer9) {
                BlockState blockState10 = DoubleXYRoom.PRISMARINE_BRICKS;
                if (integer9 == 2 || integer9 == 6) {
                    blockState10 = DoubleXYRoom.PRISMARINE;
                }
                this.fillWithOutline(world, boundingBox, 0, integer9, 0, 0, integer9, 7, blockState10, blockState10, false);
                this.fillWithOutline(world, boundingBox, 15, integer9, 0, 15, integer9, 7, blockState10, blockState10, false);
                this.fillWithOutline(world, boundingBox, 1, integer9, 0, 15, integer9, 0, blockState10, blockState10, false);
                this.fillWithOutline(world, boundingBox, 1, integer9, 7, 14, integer9, 7, blockState10, blockState10, false);
            }
            this.fillWithOutline(world, boundingBox, 2, 1, 3, 2, 7, 4, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 3, 1, 2, 4, 7, 2, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 7, 5, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 13, 1, 3, 13, 7, 4, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 11, 1, 2, 12, 7, 2, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 11, 1, 5, 12, 7, 5, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 1, 3, 5, 3, 4, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 1, 3, 10, 3, 4, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 7, 2, 10, 7, 5, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 5, 2, 5, 7, 2, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 5, 2, 10, 7, 2, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 5, 5, 5, 7, 5, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 5, 5, 10, 7, 5, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.addBlock(world, DoubleXYRoom.PRISMARINE_BRICKS, 6, 6, 2, boundingBox);
            this.addBlock(world, DoubleXYRoom.PRISMARINE_BRICKS, 9, 6, 2, boundingBox);
            this.addBlock(world, DoubleXYRoom.PRISMARINE_BRICKS, 6, 6, 5, boundingBox);
            this.addBlock(world, DoubleXYRoom.PRISMARINE_BRICKS, 9, 6, 5, boundingBox);
            this.fillWithOutline(world, boundingBox, 5, 4, 3, 6, 4, 4, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 9, 4, 3, 10, 4, 4, DoubleXYRoom.PRISMARINE_BRICKS, DoubleXYRoom.PRISMARINE_BRICKS, false);
            this.addBlock(world, DoubleXYRoom.SEA_LANTERN, 5, 4, 2, boundingBox);
            this.addBlock(world, DoubleXYRoom.SEA_LANTERN, 5, 4, 5, boundingBox);
            this.addBlock(world, DoubleXYRoom.SEA_LANTERN, 10, 4, 2, boundingBox);
            this.addBlock(world, DoubleXYRoom.SEA_LANTERN, 10, 4, 5, boundingBox);
            if (v6.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (v6.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 7, 4, 2, 7);
            }
            if (v6.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 3, 0, 2, 4);
            }
            if (v5.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 11, 1, 0, 12, 2, 0);
            }
            if (v5.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 11, 1, 7, 12, 2, 7);
            }
            if (v5.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 15, 1, 3, 15, 2, 4);
            }
            if (v7.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 5, 0, 4, 6, 0);
            }
            if (v7.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 5, 7, 4, 6, 7);
            }
            if (v7.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 5, 3, 0, 6, 4);
            }
            if (v8.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 11, 5, 0, 12, 6, 0);
            }
            if (v8.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 11, 5, 7, 12, 6, 7);
            }
            if (v8.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 15, 5, 3, 15, 6, 4);
            }
            return true;
        }
    }
    
    public static class DoubleYZRoom extends r
    {
        public DoubleYZRoom(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, 1, direction, v, 1, 2, 2);
        }
        
        public DoubleYZRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final v v5 = this.l.b[Direction.NORTH.getId()];
            final v v6 = this.l;
            final v v7 = v5.b[Direction.UP.getId()];
            final v v8 = v6.b[Direction.UP.getId()];
            if (this.l.a / 25 > 0) {
                this.a(world, boundingBox, 0, 8, v5.c[Direction.DOWN.getId()]);
                this.a(world, boundingBox, 0, 0, v6.c[Direction.DOWN.getId()]);
            }
            if (v8.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 8, 1, 6, 8, 7, DoubleYZRoom.PRISMARINE);
            }
            if (v7.b[Direction.UP.getId()] == null) {
                this.a(world, boundingBox, 1, 8, 8, 6, 8, 14, DoubleYZRoom.PRISMARINE);
            }
            for (int integer9 = 1; integer9 <= 7; ++integer9) {
                BlockState blockState10 = DoubleYZRoom.PRISMARINE_BRICKS;
                if (integer9 == 2 || integer9 == 6) {
                    blockState10 = DoubleYZRoom.PRISMARINE;
                }
                this.fillWithOutline(world, boundingBox, 0, integer9, 0, 0, integer9, 15, blockState10, blockState10, false);
                this.fillWithOutline(world, boundingBox, 7, integer9, 0, 7, integer9, 15, blockState10, blockState10, false);
                this.fillWithOutline(world, boundingBox, 1, integer9, 0, 6, integer9, 0, blockState10, blockState10, false);
                this.fillWithOutline(world, boundingBox, 1, integer9, 15, 6, integer9, 15, blockState10, blockState10, false);
            }
            for (int integer9 = 1; integer9 <= 7; ++integer9) {
                BlockState blockState10 = DoubleYZRoom.DARK_PRISMARINE;
                if (integer9 == 2 || integer9 == 6) {
                    blockState10 = DoubleYZRoom.SEA_LANTERN;
                }
                this.fillWithOutline(world, boundingBox, 3, integer9, 7, 4, integer9, 8, blockState10, blockState10, false);
            }
            if (v6.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (v6.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 7, 1, 3, 7, 2, 4);
            }
            if (v6.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 3, 0, 2, 4);
            }
            if (v5.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 1, 15, 4, 2, 15);
            }
            if (v5.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 1, 11, 0, 2, 12);
            }
            if (v5.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 7, 1, 11, 7, 2, 12);
            }
            if (v8.c[Direction.SOUTH.getId()]) {
                this.a(world, boundingBox, 3, 5, 0, 4, 6, 0);
            }
            if (v8.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 7, 5, 3, 7, 6, 4);
                this.fillWithOutline(world, boundingBox, 5, 4, 2, 6, 4, 5, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 1, 2, 6, 3, 2, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 1, 5, 6, 3, 5, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
            }
            if (v8.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 5, 3, 0, 6, 4);
                this.fillWithOutline(world, boundingBox, 1, 4, 2, 2, 4, 5, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 1, 2, 1, 3, 2, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 1, 5, 1, 3, 5, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
            }
            if (v7.c[Direction.NORTH.getId()]) {
                this.a(world, boundingBox, 3, 5, 15, 4, 6, 15);
            }
            if (v7.c[Direction.WEST.getId()]) {
                this.a(world, boundingBox, 0, 5, 11, 0, 6, 12);
                this.fillWithOutline(world, boundingBox, 1, 4, 10, 2, 4, 13, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 1, 10, 1, 3, 10, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 1, 1, 13, 1, 3, 13, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
            }
            if (v7.c[Direction.EAST.getId()]) {
                this.a(world, boundingBox, 7, 5, 11, 7, 6, 12);
                this.fillWithOutline(world, boundingBox, 5, 4, 10, 6, 4, 13, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 1, 10, 6, 3, 10, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 1, 13, 6, 3, 13, DoubleYZRoom.PRISMARINE_BRICKS, DoubleYZRoom.PRISMARINE_BRICKS, false);
            }
            return true;
        }
    }
    
    public static class CoreRoom extends r
    {
        public CoreRoom(final Direction direction, final v v) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, direction, v, 2, 2, 2);
        }
        
        public CoreRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.a(world, boundingBox, 1, 8, 0, 14, 8, 14, CoreRoom.PRISMARINE);
            int integer5 = 7;
            BlockState blockState6 = CoreRoom.PRISMARINE_BRICKS;
            this.fillWithOutline(world, boundingBox, 0, 7, 0, 0, 7, 15, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 15, 7, 0, 15, 7, 15, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 1, 7, 0, 15, 7, 0, blockState6, blockState6, false);
            this.fillWithOutline(world, boundingBox, 1, 7, 15, 14, 7, 15, blockState6, blockState6, false);
            for (integer5 = 1; integer5 <= 6; ++integer5) {
                blockState6 = CoreRoom.PRISMARINE_BRICKS;
                if (integer5 == 2 || integer5 == 6) {
                    blockState6 = CoreRoom.PRISMARINE;
                }
                for (int integer6 = 0; integer6 <= 15; integer6 += 15) {
                    this.fillWithOutline(world, boundingBox, integer6, integer5, 0, integer6, integer5, 1, blockState6, blockState6, false);
                    this.fillWithOutline(world, boundingBox, integer6, integer5, 6, integer6, integer5, 9, blockState6, blockState6, false);
                    this.fillWithOutline(world, boundingBox, integer6, integer5, 14, integer6, integer5, 15, blockState6, blockState6, false);
                }
                this.fillWithOutline(world, boundingBox, 1, integer5, 0, 1, integer5, 0, blockState6, blockState6, false);
                this.fillWithOutline(world, boundingBox, 6, integer5, 0, 9, integer5, 0, blockState6, blockState6, false);
                this.fillWithOutline(world, boundingBox, 14, integer5, 0, 14, integer5, 0, blockState6, blockState6, false);
                this.fillWithOutline(world, boundingBox, 1, integer5, 15, 14, integer5, 15, blockState6, blockState6, false);
            }
            this.fillWithOutline(world, boundingBox, 6, 3, 6, 9, 6, 9, CoreRoom.DARK_PRISMARINE, CoreRoom.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 7, 4, 7, 8, 5, 8, Blocks.bD.getDefaultState(), Blocks.bD.getDefaultState(), false);
            for (integer5 = 3; integer5 <= 6; integer5 += 3) {
                for (int integer7 = 6; integer7 <= 9; integer7 += 3) {
                    this.addBlock(world, CoreRoom.SEA_LANTERN, integer7, integer5, 6, boundingBox);
                    this.addBlock(world, CoreRoom.SEA_LANTERN, integer7, integer5, 9, boundingBox);
                }
            }
            this.fillWithOutline(world, boundingBox, 5, 1, 6, 5, 2, 6, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 1, 9, 5, 2, 9, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 1, 6, 10, 2, 6, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 1, 9, 10, 2, 9, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 1, 5, 6, 2, 5, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 9, 1, 5, 9, 2, 5, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, 1, 10, 6, 2, 10, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 9, 1, 10, 9, 2, 10, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 2, 5, 5, 6, 5, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 2, 10, 5, 6, 10, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 2, 5, 10, 6, 5, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 2, 10, 10, 6, 10, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 7, 1, 5, 7, 6, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 7, 1, 10, 7, 6, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 5, 7, 9, 5, 7, 14, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 10, 7, 9, 10, 7, 14, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 7, 5, 6, 7, 5, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 7, 10, 6, 7, 10, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 9, 7, 5, 14, 7, 5, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 9, 7, 10, 14, 7, 10, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 1, 2, 2, 1, 3, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 3, 1, 2, 3, 1, 2, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 13, 1, 2, 13, 1, 3, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 12, 1, 2, 12, 1, 2, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 2, 1, 12, 2, 1, 13, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 3, 1, 13, 3, 1, 13, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 13, 1, 12, 13, 1, 13, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 12, 1, 13, 12, 1, 13, CoreRoom.PRISMARINE_BRICKS, CoreRoom.PRISMARINE_BRICKS, false);
            return true;
        }
    }
    
    public static class WingRoom extends r
    {
        private int p;
        
        public WingRoom(final Direction direction, final MutableIntBoundingBox mutableIntBoundingBox, final int integer) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, direction, mutableIntBoundingBox);
            this.p = (integer & 0x1);
        }
        
        public WingRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.p == 0) {
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.fillWithOutline(world, boundingBox, 10 - integer5, 3 - integer5, 20 - integer5, 12 + integer5, 3 - integer5, 20, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(world, boundingBox, 7, 0, 6, 15, 0, 16, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 6, 0, 6, 6, 3, 20, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 16, 0, 6, 16, 3, 20, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 1, 7, 7, 1, 20, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 15, 1, 7, 15, 1, 20, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 7, 1, 6, 9, 3, 6, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 13, 1, 6, 15, 3, 6, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 8, 1, 7, 9, 1, 7, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 13, 1, 7, 14, 1, 7, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 9, 0, 5, 13, 0, 5, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 10, 0, 7, 12, 0, 7, WingRoom.DARK_PRISMARINE, WingRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 8, 0, 10, 8, 0, 12, WingRoom.DARK_PRISMARINE, WingRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 14, 0, 10, 14, 0, 12, WingRoom.DARK_PRISMARINE, WingRoom.DARK_PRISMARINE, false);
                for (int integer5 = 18; integer5 >= 7; integer5 -= 3) {
                    this.addBlock(world, WingRoom.SEA_LANTERN, 6, 3, integer5, boundingBox);
                    this.addBlock(world, WingRoom.SEA_LANTERN, 16, 3, integer5, boundingBox);
                }
                this.addBlock(world, WingRoom.SEA_LANTERN, 10, 0, 10, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 12, 0, 10, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 10, 0, 12, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 12, 0, 12, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 8, 3, 6, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 14, 3, 6, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 4, 2, 4, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 4, 1, 4, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 4, 0, 4, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 18, 2, 4, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 18, 1, 4, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 18, 0, 4, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 4, 2, 18, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 4, 1, 18, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 4, 0, 18, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 18, 2, 18, boundingBox);
                this.addBlock(world, WingRoom.SEA_LANTERN, 18, 1, 18, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 18, 0, 18, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 9, 7, 20, boundingBox);
                this.addBlock(world, WingRoom.PRISMARINE_BRICKS, 13, 7, 20, boundingBox);
                this.fillWithOutline(world, boundingBox, 6, 0, 21, 7, 4, 21, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 15, 0, 21, 16, 4, 21, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.a(world, boundingBox, 11, 2, 16);
            }
            else if (this.p == 1) {
                this.fillWithOutline(world, boundingBox, 9, 3, 18, 13, 3, 20, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 9, 0, 18, 9, 2, 18, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, boundingBox, 13, 0, 18, 13, 2, 18, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                int integer5 = 9;
                final int integer6 = 20;
                final int integer7 = 5;
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    this.addBlock(world, WingRoom.PRISMARINE_BRICKS, integer5, 6, 20, boundingBox);
                    this.addBlock(world, WingRoom.SEA_LANTERN, integer5, 5, 20, boundingBox);
                    this.addBlock(world, WingRoom.PRISMARINE_BRICKS, integer5, 4, 20, boundingBox);
                    integer5 = 13;
                }
                this.fillWithOutline(world, boundingBox, 7, 3, 7, 15, 3, 14, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                integer5 = 10;
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    this.fillWithOutline(world, boundingBox, integer5, 0, 10, integer5, 6, 10, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer5, 0, 12, integer5, 6, 12, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                    this.addBlock(world, WingRoom.SEA_LANTERN, integer5, 0, 10, boundingBox);
                    this.addBlock(world, WingRoom.SEA_LANTERN, integer5, 0, 12, boundingBox);
                    this.addBlock(world, WingRoom.SEA_LANTERN, integer5, 4, 10, boundingBox);
                    this.addBlock(world, WingRoom.SEA_LANTERN, integer5, 4, 12, boundingBox);
                    integer5 = 12;
                }
                integer5 = 8;
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    this.fillWithOutline(world, boundingBox, integer5, 0, 7, integer5, 2, 7, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, boundingBox, integer5, 0, 14, integer5, 2, 14, WingRoom.PRISMARINE_BRICKS, WingRoom.PRISMARINE_BRICKS, false);
                    integer5 = 14;
                }
                this.fillWithOutline(world, boundingBox, 8, 3, 8, 8, 3, 13, WingRoom.DARK_PRISMARINE, WingRoom.DARK_PRISMARINE, false);
                this.fillWithOutline(world, boundingBox, 14, 3, 8, 14, 3, 13, WingRoom.DARK_PRISMARINE, WingRoom.DARK_PRISMARINE, false);
                this.a(world, boundingBox, 11, 5, 13);
            }
            return true;
        }
    }
    
    public static class Penthouse extends r
    {
        public Penthouse(final Direction direction, final MutableIntBoundingBox mutableIntBoundingBox) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, direction, mutableIntBoundingBox);
        }
        
        public Penthouse(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, compoundTag);
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.fillWithOutline(world, boundingBox, 2, -1, 2, 11, -1, 11, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 0, -1, 0, 1, -1, 11, Penthouse.PRISMARINE, Penthouse.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 12, -1, 0, 13, -1, 11, Penthouse.PRISMARINE, Penthouse.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 2, -1, 0, 11, -1, 1, Penthouse.PRISMARINE, Penthouse.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 2, -1, 12, 11, -1, 13, Penthouse.PRISMARINE, Penthouse.PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 0, 13, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 13, 0, 0, 13, 0, 13, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 0, 0, 12, 0, 0, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 1, 0, 13, 12, 0, 13, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            for (int integer5 = 2; integer5 <= 11; integer5 += 3) {
                this.addBlock(world, Penthouse.SEA_LANTERN, 0, 0, integer5, boundingBox);
                this.addBlock(world, Penthouse.SEA_LANTERN, 13, 0, integer5, boundingBox);
                this.addBlock(world, Penthouse.SEA_LANTERN, integer5, 0, 0, boundingBox);
            }
            this.fillWithOutline(world, boundingBox, 2, 0, 3, 4, 0, 9, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 9, 0, 3, 11, 0, 9, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 4, 0, 9, 9, 0, 11, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.addBlock(world, Penthouse.PRISMARINE_BRICKS, 5, 0, 8, boundingBox);
            this.addBlock(world, Penthouse.PRISMARINE_BRICKS, 8, 0, 8, boundingBox);
            this.addBlock(world, Penthouse.PRISMARINE_BRICKS, 10, 0, 10, boundingBox);
            this.addBlock(world, Penthouse.PRISMARINE_BRICKS, 3, 0, 10, boundingBox);
            this.fillWithOutline(world, boundingBox, 3, 0, 3, 3, 0, 7, Penthouse.DARK_PRISMARINE, Penthouse.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 10, 0, 3, 10, 0, 7, Penthouse.DARK_PRISMARINE, Penthouse.DARK_PRISMARINE, false);
            this.fillWithOutline(world, boundingBox, 6, 0, 10, 7, 0, 10, Penthouse.DARK_PRISMARINE, Penthouse.DARK_PRISMARINE, false);
            int integer5 = 3;
            for (int integer6 = 0; integer6 < 2; ++integer6) {
                for (int integer7 = 2; integer7 <= 8; integer7 += 3) {
                    this.fillWithOutline(world, boundingBox, integer5, 0, integer7, integer5, 2, integer7, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
                }
                integer5 = 10;
            }
            this.fillWithOutline(world, boundingBox, 5, 0, 10, 5, 2, 10, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 8, 0, 10, 8, 2, 10, Penthouse.PRISMARINE_BRICKS, Penthouse.PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, boundingBox, 6, -1, 7, 7, -1, 8, Penthouse.DARK_PRISMARINE, Penthouse.DARK_PRISMARINE, false);
            this.a(world, boundingBox, 6, -1, 3, 7, -1, 4);
            this.a(world, boundingBox, 6, 1, 6);
            return true;
        }
    }
    
    static class v
    {
        private final int a;
        private final v[] b;
        private final boolean[] c;
        private boolean d;
        private boolean e;
        private int f;
        
        public v(final int integer) {
            this.b = new v[6];
            this.c = new boolean[6];
            this.a = integer;
        }
        
        public void a(final Direction direction, final v v) {
            this.b[direction.getId()] = v;
            v.b[direction.getOpposite().getId()] = this;
        }
        
        public void a() {
            for (int integer1 = 0; integer1 < 6; ++integer1) {
                this.c[integer1] = (this.b[integer1] != null);
            }
        }
        
        public boolean a(final int integer) {
            if (this.e) {
                return true;
            }
            this.f = integer;
            for (int integer2 = 0; integer2 < 6; ++integer2) {
                if (this.b[integer2] != null && this.c[integer2] && this.b[integer2].f != integer && this.b[integer2].a(integer)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean b() {
            return this.a >= 75;
        }
        
        public int c() {
            int integer1 = 0;
            for (int integer2 = 0; integer2 < 6; ++integer2) {
                if (this.c[integer2]) {
                    ++integer1;
                }
            }
            return integer1;
        }
    }
    
    static class f implements i
    {
        private f() {
        }
        
        @Override
        public boolean a(final v v) {
            return true;
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v.d = true;
            return new SimpleRoom(direction, v, random);
        }
    }
    
    static class g implements i
    {
        private g() {
        }
        
        @Override
        public boolean a(final v v) {
            return !v.c[Direction.WEST.getId()] && !v.c[Direction.EAST.getId()] && !v.c[Direction.NORTH.getId()] && !v.c[Direction.SOUTH.getId()] && !v.c[Direction.UP.getId()];
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v.d = true;
            return new SimpleRoomTop(direction, v);
        }
    }
    
    static class c implements i
    {
        private c() {
        }
        
        @Override
        public boolean a(final v v) {
            return v.c[Direction.UP.getId()] && !v.b[Direction.UP.getId()].d;
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v.d = true;
            v.b[Direction.UP.getId()].d = true;
            return new DoubleYRoom(direction, v);
        }
    }
    
    static class a implements i
    {
        private a() {
        }
        
        @Override
        public boolean a(final v v) {
            return v.c[Direction.EAST.getId()] && !v.b[Direction.EAST.getId()].d;
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v.d = true;
            v.b[Direction.EAST.getId()].d = true;
            return new DoubleXRoom(direction, v);
        }
    }
    
    static class e implements i
    {
        private e() {
        }
        
        @Override
        public boolean a(final v v) {
            return v.c[Direction.NORTH.getId()] && !v.b[Direction.NORTH.getId()].d;
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v v2 = v;
            if (!v.c[Direction.NORTH.getId()] || v.b[Direction.NORTH.getId()].d) {
                v2 = v.b[Direction.SOUTH.getId()];
            }
            v2.d = true;
            v2.b[Direction.NORTH.getId()].d = true;
            return new DoubleZRoom(direction, v2);
        }
    }
    
    static class b implements i
    {
        private b() {
        }
        
        @Override
        public boolean a(final v v) {
            if (v.c[Direction.EAST.getId()] && !v.b[Direction.EAST.getId()].d && v.c[Direction.UP.getId()] && !v.b[Direction.UP.getId()].d) {
                final v v2 = v.b[Direction.EAST.getId()];
                return v2.c[Direction.UP.getId()] && !v2.b[Direction.UP.getId()].d;
            }
            return false;
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v.d = true;
            v.b[Direction.EAST.getId()].d = true;
            v.b[Direction.UP.getId()].d = true;
            v.b[Direction.EAST.getId()].b[Direction.UP.getId()].d = true;
            return new DoubleXYRoom(direction, v);
        }
    }
    
    static class d implements i
    {
        private d() {
        }
        
        @Override
        public boolean a(final v v) {
            if (v.c[Direction.NORTH.getId()] && !v.b[Direction.NORTH.getId()].d && v.c[Direction.UP.getId()] && !v.b[Direction.UP.getId()].d) {
                final v v2 = v.b[Direction.NORTH.getId()];
                return v2.c[Direction.UP.getId()] && !v2.b[Direction.UP.getId()].d;
            }
            return false;
        }
        
        @Override
        public r a(final Direction direction, final v v, final Random random) {
            v.d = true;
            v.b[Direction.NORTH.getId()].d = true;
            v.b[Direction.UP.getId()].d = true;
            v.b[Direction.NORTH.getId()].b[Direction.UP.getId()].d = true;
            return new DoubleYZRoom(direction, v);
        }
    }
    
    interface i
    {
        boolean a(final v arg1);
        
        r a(final Direction arg1, final v arg2, final Random arg3);
    }
}
