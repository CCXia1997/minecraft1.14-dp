package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.nbt.IntArrayTag;
import java.util.AbstractList;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.loot.LootTables;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.RailBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.Tag;
import java.util.Iterator;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.Lists;
import net.minecraft.world.BlockView;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.gen.feature.MineshaftFeature;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import java.util.Random;
import java.util.List;

public class MineshaftGenerator
{
    private static MineshaftPart a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, @Nullable final Direction direction, final int integer7, final MineshaftFeature.Type type) {
        final int integer8 = random.nextInt(100);
        if (integer8 >= 80) {
            final MutableIntBoundingBox mutableIntBoundingBox10 = MineshaftCrossing.a(list, random, integer3, integer4, integer5, direction);
            if (mutableIntBoundingBox10 != null) {
                return new MineshaftCrossing(integer7, mutableIntBoundingBox10, direction, type);
            }
        }
        else if (integer8 >= 70) {
            final MutableIntBoundingBox mutableIntBoundingBox10 = MineshaftStairs.a(list, random, integer3, integer4, integer5, direction);
            if (mutableIntBoundingBox10 != null) {
                return new MineshaftStairs(integer7, mutableIntBoundingBox10, direction, type);
            }
        }
        else {
            final MutableIntBoundingBox mutableIntBoundingBox10 = MineshaftCorridor.a(list, random, integer3, integer4, integer5, direction);
            if (mutableIntBoundingBox10 != null) {
                return new MineshaftCorridor(integer7, random, mutableIntBoundingBox10, direction, type);
            }
        }
        return null;
    }
    
    private static MineshaftPart b(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, final Direction direction, final int integer8) {
        if (integer8 > 8) {
            return null;
        }
        if (Math.abs(integer4 - structurePiece.getBoundingBox().minX) > 80 || Math.abs(integer6 - structurePiece.getBoundingBox().minZ) > 80) {
            return null;
        }
        final MineshaftFeature.Type type9 = ((MineshaftPart)structurePiece).mineshaftType;
        final MineshaftPart mineshaftPart10 = a(list, random, integer4, integer5, integer6, direction, integer8 + 1, type9);
        if (mineshaftPart10 != null) {
            list.add(mineshaftPart10);
            mineshaftPart10.a(structurePiece, list, random);
        }
        return mineshaftPart10;
    }
    
    abstract static class MineshaftPart extends StructurePiece
    {
        protected MineshaftFeature.Type mineshaftType;
        
        public MineshaftPart(final StructurePieceType structurePieceType, final int integer, final MineshaftFeature.Type type) {
            super(structurePieceType, integer);
            this.mineshaftType = type;
        }
        
        public MineshaftPart(final StructurePieceType type, final CompoundTag tag) {
            super(type, tag);
            this.mineshaftType = MineshaftFeature.Type.byIndex(tag.getInt("MST"));
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            tag.putInt("MST", this.mineshaftType.ordinal());
        }
        
        protected BlockState a() {
            switch (this.mineshaftType) {
                default: {
                    return Blocks.n.getDefaultState();
                }
                case MESA: {
                    return Blocks.s.getDefaultState();
                }
            }
        }
        
        protected BlockState b() {
            switch (this.mineshaftType) {
                default: {
                    return Blocks.cH.getDefaultState();
                }
                case MESA: {
                    return Blocks.ii.getDefaultState();
                }
            }
        }
        
        protected boolean a(final BlockView blockView, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5, final int integer6) {
            for (int integer7 = integer3; integer7 <= integer4; ++integer7) {
                if (this.getBlockAt(blockView, integer7, integer5 + 1, integer6, mutableIntBoundingBox).isAir()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static class MineshaftRoom extends MineshaftPart
    {
        private final List<MutableIntBoundingBox> entrances;
        
        public MineshaftRoom(final int integer1, final Random random, final int integer3, final int integer4, final MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_ROOM, integer1, type);
            this.entrances = Lists.newLinkedList();
            this.mineshaftType = type;
            this.boundingBox = new MutableIntBoundingBox(integer3, 50, integer4, integer3 + 7 + random.nextInt(6), 54 + random.nextInt(6), integer4 + 7 + random.nextInt(6));
        }
        
        public MineshaftRoom(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_ROOM, compoundTag);
            this.entrances = Lists.newLinkedList();
            final ListTag listTag3 = compoundTag.getList("Entrances", 11);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                this.entrances.add(new MutableIntBoundingBox(listTag3.getIntArray(integer4)));
            }
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            final int integer4 = this.h();
            int integer5 = this.boundingBox.getBlockCountY() - 3 - 1;
            if (integer5 <= 0) {
                integer5 = 1;
            }
            for (int integer6 = 0; integer6 < this.boundingBox.getBlockCountX(); integer6 += 4) {
                integer6 += random.nextInt(this.boundingBox.getBlockCountX());
                if (integer6 + 3 > this.boundingBox.getBlockCountX()) {
                    break;
                }
                final MineshaftPart mineshaftPart7 = b(structurePiece, list, random, this.boundingBox.minX + integer6, this.boundingBox.minY + random.nextInt(integer5) + 1, this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                if (mineshaftPart7 != null) {
                    final MutableIntBoundingBox mutableIntBoundingBox8 = mineshaftPart7.getBoundingBox();
                    this.entrances.add(new MutableIntBoundingBox(mutableIntBoundingBox8.minX, mutableIntBoundingBox8.minY, this.boundingBox.minZ, mutableIntBoundingBox8.maxX, mutableIntBoundingBox8.maxY, this.boundingBox.minZ + 1));
                }
            }
            for (int integer6 = 0; integer6 < this.boundingBox.getBlockCountX(); integer6 += 4) {
                integer6 += random.nextInt(this.boundingBox.getBlockCountX());
                if (integer6 + 3 > this.boundingBox.getBlockCountX()) {
                    break;
                }
                final MineshaftPart mineshaftPart7 = b(structurePiece, list, random, this.boundingBox.minX + integer6, this.boundingBox.minY + random.nextInt(integer5) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                if (mineshaftPart7 != null) {
                    final MutableIntBoundingBox mutableIntBoundingBox8 = mineshaftPart7.getBoundingBox();
                    this.entrances.add(new MutableIntBoundingBox(mutableIntBoundingBox8.minX, mutableIntBoundingBox8.minY, this.boundingBox.maxZ - 1, mutableIntBoundingBox8.maxX, mutableIntBoundingBox8.maxY, this.boundingBox.maxZ));
                }
            }
            for (int integer6 = 0; integer6 < this.boundingBox.getBlockCountZ(); integer6 += 4) {
                integer6 += random.nextInt(this.boundingBox.getBlockCountZ());
                if (integer6 + 3 > this.boundingBox.getBlockCountZ()) {
                    break;
                }
                final MineshaftPart mineshaftPart7 = b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(integer5) + 1, this.boundingBox.minZ + integer6, Direction.WEST, integer4);
                if (mineshaftPart7 != null) {
                    final MutableIntBoundingBox mutableIntBoundingBox8 = mineshaftPart7.getBoundingBox();
                    this.entrances.add(new MutableIntBoundingBox(this.boundingBox.minX, mutableIntBoundingBox8.minY, mutableIntBoundingBox8.minZ, this.boundingBox.minX + 1, mutableIntBoundingBox8.maxY, mutableIntBoundingBox8.maxZ));
                }
            }
            for (int integer6 = 0; integer6 < this.boundingBox.getBlockCountZ(); integer6 += 4) {
                integer6 += random.nextInt(this.boundingBox.getBlockCountZ());
                if (integer6 + 3 > this.boundingBox.getBlockCountZ()) {
                    break;
                }
                final StructurePiece structurePiece2 = b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(integer5) + 1, this.boundingBox.minZ + integer6, Direction.EAST, integer4);
                if (structurePiece2 != null) {
                    final MutableIntBoundingBox mutableIntBoundingBox8 = structurePiece2.getBoundingBox();
                    this.entrances.add(new MutableIntBoundingBox(this.boundingBox.maxX - 1, mutableIntBoundingBox8.minY, mutableIntBoundingBox8.minZ, this.boundingBox.maxX, mutableIntBoundingBox8.maxY, mutableIntBoundingBox8.maxZ));
                }
            }
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.a(world, boundingBox)) {
                return false;
            }
            this.fillWithOutline(world, boundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.j.getDefaultState(), MineshaftRoom.AIR, true);
            this.fillWithOutline(world, boundingBox, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, MineshaftRoom.AIR, MineshaftRoom.AIR, false);
            for (final MutableIntBoundingBox mutableIntBoundingBox6 : this.entrances) {
                this.fillWithOutline(world, boundingBox, mutableIntBoundingBox6.minX, mutableIntBoundingBox6.maxY - 2, mutableIntBoundingBox6.minZ, mutableIntBoundingBox6.maxX, mutableIntBoundingBox6.maxY, mutableIntBoundingBox6.maxZ, MineshaftRoom.AIR, MineshaftRoom.AIR, false);
            }
            this.a(world, boundingBox, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, MineshaftRoom.AIR, false);
            return true;
        }
        
        @Override
        public void translate(final int x, final int y, final int z) {
            super.translate(x, y, z);
            for (final MutableIntBoundingBox mutableIntBoundingBox5 : this.entrances) {
                mutableIntBoundingBox5.translate(x, y, z);
            }
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            final ListTag listTag2 = new ListTag();
            for (final MutableIntBoundingBox mutableIntBoundingBox4 : this.entrances) {
                ((AbstractList<IntArrayTag>)listTag2).add(mutableIntBoundingBox4.toNbt());
            }
            tag.put("Entrances", listTag2);
        }
    }
    
    public static class MineshaftCorridor extends MineshaftPart
    {
        private final boolean hasRails;
        private final boolean hasCobwebs;
        private boolean hasSpawner;
        private final int length;
        
        public MineshaftCorridor(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, compoundTag);
            this.hasRails = compoundTag.getBoolean("hr");
            this.hasCobwebs = compoundTag.getBoolean("sc");
            this.hasSpawner = compoundTag.getBoolean("hps");
            this.length = compoundTag.getInt("Num");
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("hr", this.hasRails);
            tag.putBoolean("sc", this.hasCobwebs);
            tag.putBoolean("hps", this.hasSpawner);
            tag.putInt("Num", this.length);
        }
        
        public MineshaftCorridor(final int integer, final Random random, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction, final MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, integer, type);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.hasRails = (random.nextInt(3) == 0);
            this.hasCobwebs = (!this.hasRails && random.nextInt(23) == 0);
            if (this.getFacing().getAxis() == Direction.Axis.Z) {
                this.length = mutableIntBoundingBox.getBlockCountZ() / 5;
            }
            else {
                this.length = mutableIntBoundingBox.getBlockCountX() / 5;
            }
        }
        
        public static MutableIntBoundingBox a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = new MutableIntBoundingBox(integer3, integer4, integer5, integer3, integer4 + 3 - 1, integer5);
            int integer6;
            for (integer6 = random.nextInt(3) + 2; integer6 > 0; --integer6) {
                final int integer7 = integer6 * 5;
                switch (direction) {
                    default: {
                        mutableIntBoundingBox7.maxX = integer3 + 3 - 1;
                        mutableIntBoundingBox7.minZ = integer5 - (integer7 - 1);
                        break;
                    }
                    case SOUTH: {
                        mutableIntBoundingBox7.maxX = integer3 + 3 - 1;
                        mutableIntBoundingBox7.maxZ = integer5 + integer7 - 1;
                        break;
                    }
                    case WEST: {
                        mutableIntBoundingBox7.minX = integer3 - (integer7 - 1);
                        mutableIntBoundingBox7.maxZ = integer5 + 3 - 1;
                        break;
                    }
                    case EAST: {
                        mutableIntBoundingBox7.maxX = integer3 + integer7 - 1;
                        mutableIntBoundingBox7.maxZ = integer5 + 3 - 1;
                        break;
                    }
                }
                if (StructurePiece.a(list, mutableIntBoundingBox7) == null) {
                    break;
                }
            }
            if (integer6 > 0) {
                return mutableIntBoundingBox7;
            }
            return null;
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            final int integer4 = this.h();
            final int integer5 = random.nextInt(4);
            final Direction direction6 = this.getFacing();
            if (direction6 != null) {
                switch (direction6) {
                    default: {
                        if (integer5 <= 1) {
                            b(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, direction6, integer4);
                            break;
                        }
                        if (integer5 == 2) {
                            b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.WEST, integer4);
                            break;
                        }
                        b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.EAST, integer4);
                        break;
                    }
                    case SOUTH: {
                        if (integer5 <= 1) {
                            b(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, direction6, integer4);
                            break;
                        }
                        if (integer5 == 2) {
                            b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, integer4);
                            break;
                        }
                        b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, integer4);
                        break;
                    }
                    case WEST: {
                        if (integer5 <= 1) {
                            b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction6, integer4);
                            break;
                        }
                        if (integer5 == 2) {
                            b(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                            break;
                        }
                        b(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                        break;
                    }
                    case EAST: {
                        if (integer5 <= 1) {
                            b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction6, integer4);
                            break;
                        }
                        if (integer5 == 2) {
                            b(structurePiece, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                            break;
                        }
                        b(structurePiece, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                        break;
                    }
                }
            }
            if (integer4 < 8) {
                if (direction6 == Direction.NORTH || direction6 == Direction.SOUTH) {
                    for (int integer6 = this.boundingBox.minZ + 3; integer6 + 3 <= this.boundingBox.maxZ; integer6 += 5) {
                        final int integer7 = random.nextInt(5);
                        if (integer7 == 0) {
                            b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, integer6, Direction.WEST, integer4 + 1);
                        }
                        else if (integer7 == 1) {
                            b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, integer6, Direction.EAST, integer4 + 1);
                        }
                    }
                }
                else {
                    for (int integer6 = this.boundingBox.minX + 3; integer6 + 3 <= this.boundingBox.maxX; integer6 += 5) {
                        final int integer7 = random.nextInt(5);
                        if (integer7 == 0) {
                            b(structurePiece, list, random, integer6, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, integer4 + 1);
                        }
                        else if (integer7 == 1) {
                            b(structurePiece, list, random, integer6, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4 + 1);
                        }
                    }
                }
            }
        }
        
        @Override
        protected boolean addChest(final IWorld world, final MutableIntBoundingBox boundingBox, final Random random, final int x, final int y, final int z, final Identifier lootTableId) {
            final BlockPos blockPos8 = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
            if (boundingBox.contains(blockPos8) && world.getBlockState(blockPos8).isAir() && !world.getBlockState(blockPos8.down()).isAir()) {
                final BlockState blockState9 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cf.getDefaultState()).<RailShape, RailShape>with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.a : RailShape.b);
                this.addBlock(world, blockState9, x, y, z, boundingBox);
                final ChestMinecartEntity chestMinecartEntity10 = new ChestMinecartEntity(world.getWorld(), blockPos8.getX() + 0.5f, blockPos8.getY() + 0.5f, blockPos8.getZ() + 0.5f);
                chestMinecartEntity10.setLootTable(lootTableId, random.nextLong());
                world.spawnEntity(chestMinecartEntity10);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.a(world, boundingBox)) {
                return false;
            }
            final int integer5 = 0;
            final int integer6 = 2;
            final int integer7 = 0;
            final int integer8 = 2;
            final int integer9 = this.length * 5 - 1;
            final BlockState blockState10 = this.a();
            this.fillWithOutline(world, boundingBox, 0, 0, 0, 2, 1, integer9, MineshaftCorridor.AIR, MineshaftCorridor.AIR, false);
            this.fillWithOutlineUnderSealevel(world, boundingBox, random, 0.8f, 0, 2, 0, 2, 2, integer9, MineshaftCorridor.AIR, MineshaftCorridor.AIR, false, false);
            if (this.hasCobwebs) {
                this.fillWithOutlineUnderSealevel(world, boundingBox, random, 0.6f, 0, 0, 0, 2, 1, integer9, Blocks.aP.getDefaultState(), MineshaftCorridor.AIR, false, true);
            }
            for (int integer10 = 0; integer10 < this.length; ++integer10) {
                final int integer11 = 2 + integer10 * 5;
                this.a(world, boundingBox, 0, 0, integer11, 2, 2, random);
                this.a(world, boundingBox, random, 0.1f, 0, 2, integer11 - 1);
                this.a(world, boundingBox, random, 0.1f, 2, 2, integer11 - 1);
                this.a(world, boundingBox, random, 0.1f, 0, 2, integer11 + 1);
                this.a(world, boundingBox, random, 0.1f, 2, 2, integer11 + 1);
                this.a(world, boundingBox, random, 0.05f, 0, 2, integer11 - 2);
                this.a(world, boundingBox, random, 0.05f, 2, 2, integer11 - 2);
                this.a(world, boundingBox, random, 0.05f, 0, 2, integer11 + 2);
                this.a(world, boundingBox, random, 0.05f, 2, 2, integer11 + 2);
                if (random.nextInt(100) == 0) {
                    this.addChest(world, boundingBox, random, 2, 0, integer11 - 1, LootTables.CHEST_ABANDONED_MINESHAFT);
                }
                if (random.nextInt(100) == 0) {
                    this.addChest(world, boundingBox, random, 0, 0, integer11 + 1, LootTables.CHEST_ABANDONED_MINESHAFT);
                }
                if (this.hasCobwebs && !this.hasSpawner) {
                    final int integer12 = this.applyYTransform(0);
                    final int integer13 = integer11 - 1 + random.nextInt(3);
                    final int integer14 = this.applyXTransform(1, integer13);
                    final int integer15 = this.applyZTransform(1, integer13);
                    final BlockPos blockPos17 = new BlockPos(integer14, integer12, integer15);
                    if (boundingBox.contains(blockPos17) && this.isUnderSeaLevel(world, 1, 0, integer13, boundingBox)) {
                        this.hasSpawner = true;
                        world.setBlockState(blockPos17, Blocks.bN.getDefaultState(), 2);
                        final BlockEntity blockEntity18 = world.getBlockEntity(blockPos17);
                        if (blockEntity18 instanceof MobSpawnerBlockEntity) {
                            ((MobSpawnerBlockEntity)blockEntity18).getLogic().setEntityId(EntityType.CAVE_SPIDER);
                        }
                    }
                }
            }
            for (int integer10 = 0; integer10 <= 2; ++integer10) {
                for (int integer11 = 0; integer11 <= integer9; ++integer11) {
                    final int integer12 = -1;
                    final BlockState blockState11 = this.getBlockAt(world, integer10, -1, integer11, boundingBox);
                    if (blockState11.isAir() && this.isUnderSeaLevel(world, integer10, -1, integer11, boundingBox)) {
                        final int integer14 = -1;
                        this.addBlock(world, blockState10, integer10, -1, integer11, boundingBox);
                    }
                }
            }
            if (this.hasRails) {
                final BlockState blockState12 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cf.getDefaultState()).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.a);
                for (int integer11 = 0; integer11 <= integer9; ++integer11) {
                    final BlockState blockState13 = this.getBlockAt(world, 1, -1, integer11, boundingBox);
                    if (!blockState13.isAir() && blockState13.isFullOpaque(world, new BlockPos(this.applyXTransform(1, integer11), this.applyYTransform(-1), this.applyZTransform(1, integer11)))) {
                        final float float14 = this.isUnderSeaLevel(world, 1, 0, integer11, boundingBox) ? 0.7f : 0.9f;
                        this.addBlockWithRandomThreshold(world, boundingBox, random, float14, 1, 0, integer11, blockState12);
                    }
                }
            }
            return true;
        }
        
        private void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final Random random) {
            if (!this.a(iWorld, mutableIntBoundingBox, integer3, integer7, integer6, integer5)) {
                return;
            }
            final BlockState blockState9 = this.a();
            final BlockState blockState10 = this.b();
            this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3, integer4, integer5, integer3, integer6 - 1, integer5, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WEST, true), MineshaftCorridor.AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, integer7, integer4, integer5, integer7, integer6 - 1, integer5, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.EAST, true), MineshaftCorridor.AIR, false);
            if (random.nextInt(4) == 0) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3, integer6, integer5, integer3, integer6, integer5, blockState9, MineshaftCorridor.AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer7, integer6, integer5, integer7, integer6, integer5, blockState9, MineshaftCorridor.AIR, false);
            }
            else {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3, integer6, integer5, integer7, integer6, integer5, blockState9, MineshaftCorridor.AIR, false);
                this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.05f, integer3 + 1, integer6, integer5 - 1, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.NORTH));
                this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.05f, integer3 + 1, integer6, integer5 + 1, ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.SOUTH));
            }
        }
        
        private void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final Random random, final float float4, final int integer5, final int integer6, final int integer7) {
            if (this.isUnderSeaLevel(iWorld, integer5, integer6, integer7, mutableIntBoundingBox)) {
                this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, float4, integer5, integer6, integer7, Blocks.aP.getDefaultState());
            }
        }
    }
    
    public static class MineshaftCrossing extends MineshaftPart
    {
        private final Direction direction;
        private final boolean twoFloors;
        
        public MineshaftCrossing(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_CROSSING, compoundTag);
            this.twoFloors = compoundTag.getBoolean("tf");
            this.direction = Direction.fromHorizontal(compoundTag.getInt("D"));
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("tf", this.twoFloors);
            tag.putInt("D", this.direction.getHorizontal());
        }
        
        public MineshaftCrossing(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, @Nullable final Direction direction, final MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_CROSSING, integer, type);
            this.direction = direction;
            this.boundingBox = mutableIntBoundingBox;
            this.twoFloors = (mutableIntBoundingBox.getBlockCountY() > 3);
        }
        
        public static MutableIntBoundingBox a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction facing) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = new MutableIntBoundingBox(integer3, integer4, integer5, integer3, integer4 + 3 - 1, integer5);
            if (random.nextInt(4) == 0) {
                final MutableIntBoundingBox mutableIntBoundingBox8 = mutableIntBoundingBox7;
                mutableIntBoundingBox8.maxY += 4;
            }
            switch (facing) {
                default: {
                    mutableIntBoundingBox7.minX = integer3 - 1;
                    mutableIntBoundingBox7.maxX = integer3 + 3;
                    mutableIntBoundingBox7.minZ = integer5 - 4;
                    break;
                }
                case SOUTH: {
                    mutableIntBoundingBox7.minX = integer3 - 1;
                    mutableIntBoundingBox7.maxX = integer3 + 3;
                    mutableIntBoundingBox7.maxZ = integer5 + 3 + 1;
                    break;
                }
                case WEST: {
                    mutableIntBoundingBox7.minX = integer3 - 4;
                    mutableIntBoundingBox7.minZ = integer5 - 1;
                    mutableIntBoundingBox7.maxZ = integer5 + 3;
                    break;
                }
                case EAST: {
                    mutableIntBoundingBox7.maxX = integer3 + 3 + 1;
                    mutableIntBoundingBox7.minZ = integer5 - 1;
                    mutableIntBoundingBox7.maxZ = integer5 + 3;
                    break;
                }
            }
            if (StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return mutableIntBoundingBox7;
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            final int integer4 = this.h();
            switch (this.direction) {
                default: {
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                    b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, integer4);
                    b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, integer4);
                    break;
                }
                case SOUTH: {
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                    b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, integer4);
                    b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, integer4);
                    break;
                }
                case WEST: {
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                    b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, integer4);
                    break;
                }
                case EAST: {
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                    b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, integer4);
                    break;
                }
            }
            if (this.twoFloors) {
                if (random.nextBoolean()) {
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                }
                if (random.nextBoolean()) {
                    b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, integer4);
                }
                if (random.nextBoolean()) {
                    b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, integer4);
                }
                if (random.nextBoolean()) {
                    b(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                }
            }
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.a(world, boundingBox)) {
                return false;
            }
            final BlockState blockState5 = this.a();
            if (this.twoFloors) {
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
            }
            else {
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
                this.fillWithOutline(world, boundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, MineshaftCrossing.AIR, MineshaftCrossing.AIR, false);
            }
            this.a(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.a(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            this.a(world, boundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.a(world, boundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            for (int integer6 = this.boundingBox.minX; integer6 <= this.boundingBox.maxX; ++integer6) {
                for (int integer7 = this.boundingBox.minZ; integer7 <= this.boundingBox.maxZ; ++integer7) {
                    if (this.getBlockAt(world, integer6, this.boundingBox.minY - 1, integer7, boundingBox).isAir() && this.isUnderSeaLevel(world, integer6, this.boundingBox.minY - 1, integer7, boundingBox)) {
                        this.addBlock(world, blockState5, integer6, this.boundingBox.minY - 1, integer7, boundingBox);
                    }
                }
            }
            return true;
        }
        
        private void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5, final int integer6) {
            if (!this.getBlockAt(iWorld, integer3, integer6 + 1, integer5, mutableIntBoundingBox).isAir()) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, integer3, integer4, integer5, integer3, integer6, integer5, this.a(), MineshaftCrossing.AIR, false);
            }
        }
    }
    
    public static class MineshaftStairs extends MineshaftPart
    {
        public MineshaftStairs(final int integer, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction, final MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_STAIRS, integer, type);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }
        
        public MineshaftStairs(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_STAIRS, compoundTag);
        }
        
        public static MutableIntBoundingBox a(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction direction) {
            final MutableIntBoundingBox mutableIntBoundingBox7 = new MutableIntBoundingBox(integer3, integer4 - 5, integer5, integer3, integer4 + 3 - 1, integer5);
            switch (direction) {
                default: {
                    mutableIntBoundingBox7.maxX = integer3 + 3 - 1;
                    mutableIntBoundingBox7.minZ = integer5 - 8;
                    break;
                }
                case SOUTH: {
                    mutableIntBoundingBox7.maxX = integer3 + 3 - 1;
                    mutableIntBoundingBox7.maxZ = integer5 + 8;
                    break;
                }
                case WEST: {
                    mutableIntBoundingBox7.minX = integer3 - 8;
                    mutableIntBoundingBox7.maxZ = integer5 + 3 - 1;
                    break;
                }
                case EAST: {
                    mutableIntBoundingBox7.maxX = integer3 + 8;
                    mutableIntBoundingBox7.maxZ = integer5 + 3 - 1;
                    break;
                }
            }
            if (StructurePiece.a(list, mutableIntBoundingBox7) != null) {
                return null;
            }
            return mutableIntBoundingBox7;
        }
        
        @Override
        public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
            final int integer4 = this.h();
            final Direction direction5 = this.getFacing();
            if (direction5 != null) {
                switch (direction5) {
                    default: {
                        b(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, integer4);
                        break;
                    }
                    case SOUTH: {
                        b(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, integer4);
                        break;
                    }
                    case WEST: {
                        b(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, integer4);
                        break;
                    }
                    case EAST: {
                        b(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, integer4);
                        break;
                    }
                }
            }
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (this.a(world, boundingBox)) {
                return false;
            }
            this.fillWithOutline(world, boundingBox, 0, 5, 0, 2, 7, 1, MineshaftStairs.AIR, MineshaftStairs.AIR, false);
            this.fillWithOutline(world, boundingBox, 0, 0, 7, 2, 2, 8, MineshaftStairs.AIR, MineshaftStairs.AIR, false);
            for (int integer5 = 0; integer5 < 5; ++integer5) {
                this.fillWithOutline(world, boundingBox, 0, 5 - integer5 - ((integer5 < 4) ? 1 : 0), 2 + integer5, 2, 7 - integer5, 2 + integer5, MineshaftStairs.AIR, MineshaftStairs.AIR, false);
            }
            return true;
        }
    }
}
