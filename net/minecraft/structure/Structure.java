package net.minecraft.structure;

import java.util.AbstractList;
import net.minecraft.util.IdList;
import net.minecraft.nbt.IntTag;
import java.util.Comparator;
import net.minecraft.SharedConstants;
import net.minecraft.util.TagHelper;
import net.minecraft.entity.EntityType;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BlockMirror;
import net.minecraft.world.ViewableWorld;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.math.Direction;
import net.minecraft.block.FluidFillable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.util.Clearable;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.world.BlockView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public class Structure
{
    private final List<List<StructureBlockInfo>> blocks;
    private final List<StructureEntityInfo> entities;
    private BlockPos size;
    private String author;
    
    public Structure() {
        this.blocks = Lists.newArrayList();
        this.entities = Lists.newArrayList();
        this.size = BlockPos.ORIGIN;
        this.author = "?";
    }
    
    public BlockPos getSize() {
        return this.size;
    }
    
    public void setAuthor(final String name) {
        this.author = name;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void a(final World world, final BlockPos blockPos2, final BlockPos blockPos3, final boolean boolean4, @Nullable final Block block) {
        if (blockPos3.getX() < 1 || blockPos3.getY() < 1 || blockPos3.getZ() < 1) {
            return;
        }
        final BlockPos blockPos4 = blockPos2.add(blockPos3).add(-1, -1, -1);
        final List<StructureBlockInfo> list7 = Lists.newArrayList();
        final List<StructureBlockInfo> list8 = Lists.newArrayList();
        final List<StructureBlockInfo> list9 = Lists.newArrayList();
        final BlockPos blockPos5 = new BlockPos(Math.min(blockPos2.getX(), blockPos4.getX()), Math.min(blockPos2.getY(), blockPos4.getY()), Math.min(blockPos2.getZ(), blockPos4.getZ()));
        final BlockPos blockPos6 = new BlockPos(Math.max(blockPos2.getX(), blockPos4.getX()), Math.max(blockPos2.getY(), blockPos4.getY()), Math.max(blockPos2.getZ(), blockPos4.getZ()));
        this.size = blockPos3;
        for (final BlockPos blockPos7 : BlockPos.iterate(blockPos5, blockPos6)) {
            final BlockPos blockPos8 = blockPos7.subtract(blockPos5);
            final BlockState blockState15 = world.getBlockState(blockPos7);
            if (block != null && block == blockState15.getBlock()) {
                continue;
            }
            final BlockEntity blockEntity16 = world.getBlockEntity(blockPos7);
            if (blockEntity16 != null) {
                final CompoundTag compoundTag17 = blockEntity16.toTag(new CompoundTag());
                compoundTag17.remove("x");
                compoundTag17.remove("y");
                compoundTag17.remove("z");
                list8.add(new StructureBlockInfo(blockPos8, blockState15, compoundTag17));
            }
            else if (blockState15.isFullOpaque(world, blockPos7) || Block.isShapeFullCube(blockState15.getCollisionShape(world, blockPos7))) {
                list7.add(new StructureBlockInfo(blockPos8, blockState15, null));
            }
            else {
                list9.add(new StructureBlockInfo(blockPos8, blockState15, null));
            }
        }
        final List<StructureBlockInfo> list10 = Lists.newArrayList();
        list10.addAll(list7);
        list10.addAll(list8);
        list10.addAll(list9);
        this.blocks.clear();
        this.blocks.add(list10);
        if (boolean4) {
            this.a(world, blockPos5, blockPos6.add(1, 1, 1));
        }
        else {
            this.entities.clear();
        }
    }
    
    private void a(final World world, final BlockPos blockPos2, final BlockPos blockPos3) {
        final List<Entity> list4 = world.<Entity>getEntities(Entity.class, new BoundingBox(blockPos2, blockPos3), entity -> !(entity instanceof PlayerEntity));
        this.entities.clear();
        for (final Entity entity2 : list4) {
            final Vec3d vec3d7 = new Vec3d(entity2.x - blockPos2.getX(), entity2.y - blockPos2.getY(), entity2.z - blockPos2.getZ());
            final CompoundTag compoundTag8 = new CompoundTag();
            entity2.saveToTag(compoundTag8);
            BlockPos blockPos4;
            if (entity2 instanceof PaintingEntity) {
                blockPos4 = ((PaintingEntity)entity2).getDecorationBlockPos().subtract(blockPos2);
            }
            else {
                blockPos4 = new BlockPos(vec3d7);
            }
            this.entities.add(new StructureEntityInfo(vec3d7, blockPos4, compoundTag8));
        }
    }
    
    public List<StructureBlockInfo> a(final BlockPos blockPos, final StructurePlacementData structurePlacementData, final Block block) {
        return this.a(blockPos, structurePlacementData, block, true);
    }
    
    public List<StructureBlockInfo> a(final BlockPos blockPos, final StructurePlacementData structurePlacementData, final Block block, final boolean boolean4) {
        final List<StructureBlockInfo> list5 = Lists.newArrayList();
        final MutableIntBoundingBox mutableIntBoundingBox6 = structurePlacementData.h();
        for (final StructureBlockInfo structureBlockInfo8 : structurePlacementData.a(this.blocks, blockPos)) {
            final BlockPos blockPos2 = boolean4 ? a(structurePlacementData, structureBlockInfo8.pos).add(blockPos) : structureBlockInfo8.pos;
            if (mutableIntBoundingBox6 != null && !mutableIntBoundingBox6.contains(blockPos2)) {
                continue;
            }
            final BlockState blockState10 = structureBlockInfo8.state;
            if (blockState10.getBlock() != block) {
                continue;
            }
            list5.add(new StructureBlockInfo(blockPos2, blockState10.rotate(structurePlacementData.getRotation()), structureBlockInfo8.tag));
        }
        return list5;
    }
    
    public BlockPos a(final StructurePlacementData structurePlacementData1, final BlockPos blockPos2, final StructurePlacementData structurePlacementData3, final BlockPos blockPos4) {
        final BlockPos blockPos5 = a(structurePlacementData1, blockPos2);
        final BlockPos blockPos6 = a(structurePlacementData3, blockPos4);
        return blockPos5.subtract(blockPos6);
    }
    
    public static BlockPos a(final StructurePlacementData structurePlacementData, final BlockPos blockPos) {
        return a(blockPos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition());
    }
    
    public void place(final IWorld iWorld, final BlockPos blockPos, final StructurePlacementData structurePlacementData) {
        structurePlacementData.k();
        this.b(iWorld, blockPos, structurePlacementData);
    }
    
    public void b(final IWorld iWorld, final BlockPos blockPos, final StructurePlacementData structurePlacementData) {
        this.a(iWorld, blockPos, structurePlacementData, 2);
    }
    
    public boolean a(final IWorld iWorld, final BlockPos blockPos, final StructurePlacementData structurePlacementData, final int integer) {
        if (this.blocks.isEmpty()) {
            return false;
        }
        final List<StructureBlockInfo> list5 = structurePlacementData.a(this.blocks, blockPos);
        if ((list5.isEmpty() && (structurePlacementData.shouldIgnoreEntities() || this.entities.isEmpty())) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        final MutableIntBoundingBox mutableIntBoundingBox6 = structurePlacementData.h();
        final List<BlockPos> list6 = Lists.newArrayListWithCapacity(structurePlacementData.shouldPlaceFluids() ? list5.size() : 0);
        final List<Pair<BlockPos, CompoundTag>> list7 = Lists.newArrayListWithCapacity(list5.size());
        int integer10 = Integer.MAX_VALUE;
        int integer11 = Integer.MAX_VALUE;
        int integer12 = Integer.MAX_VALUE;
        int integer13 = Integer.MIN_VALUE;
        int integer14 = Integer.MIN_VALUE;
        int integer15 = Integer.MIN_VALUE;
        final List<StructureBlockInfo> list8 = process(iWorld, blockPos, structurePlacementData, list5);
        for (final StructureBlockInfo structureBlockInfo17 : list8) {
            final BlockPos blockPos2 = structureBlockInfo17.pos;
            if (mutableIntBoundingBox6 != null && !mutableIntBoundingBox6.contains(blockPos2)) {
                continue;
            }
            final FluidState fluidState19 = structurePlacementData.shouldPlaceFluids() ? iWorld.getFluidState(blockPos2) : null;
            final BlockState blockState20 = structureBlockInfo17.state.mirror(structurePlacementData.getMirror()).rotate(structurePlacementData.getRotation());
            if (structureBlockInfo17.tag != null) {
                final BlockEntity blockEntity21 = iWorld.getBlockEntity(blockPos2);
                Clearable.clear(blockEntity21);
                iWorld.setBlockState(blockPos2, Blocks.gg.getDefaultState(), 4);
            }
            if (!iWorld.setBlockState(blockPos2, blockState20, integer)) {
                continue;
            }
            integer10 = Math.min(integer10, blockPos2.getX());
            integer11 = Math.min(integer11, blockPos2.getY());
            integer12 = Math.min(integer12, blockPos2.getZ());
            integer13 = Math.max(integer13, blockPos2.getX());
            integer14 = Math.max(integer14, blockPos2.getY());
            integer15 = Math.max(integer15, blockPos2.getZ());
            list7.add((Pair<BlockPos, CompoundTag>)Pair.of(blockPos2, structureBlockInfo17.tag));
            if (structureBlockInfo17.tag != null) {
                final BlockEntity blockEntity21 = iWorld.getBlockEntity(blockPos2);
                if (blockEntity21 != null) {
                    structureBlockInfo17.tag.putInt("x", blockPos2.getX());
                    structureBlockInfo17.tag.putInt("y", blockPos2.getY());
                    structureBlockInfo17.tag.putInt("z", blockPos2.getZ());
                    blockEntity21.fromTag(structureBlockInfo17.tag);
                    blockEntity21.applyMirror(structurePlacementData.getMirror());
                    blockEntity21.applyRotation(structurePlacementData.getRotation());
                }
            }
            if (fluidState19 == null || !(blockState20.getBlock() instanceof FluidFillable)) {
                continue;
            }
            ((FluidFillable)blockState20.getBlock()).tryFillWithFluid(iWorld, blockPos2, blockState20, fluidState19);
            if (fluidState19.isStill()) {
                continue;
            }
            list6.add(blockPos2);
        }
        boolean boolean16 = true;
        final Direction[] arr17 = { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
        while (boolean16 && !list6.isEmpty()) {
            boolean16 = false;
            final Iterator<BlockPos> iterator18 = list6.iterator();
            while (iterator18.hasNext()) {
                BlockPos blockPos4;
                final BlockPos blockPos3 = blockPos4 = iterator18.next();
                FluidState fluidState20 = iWorld.getFluidState(blockPos4);
                for (int integer16 = 0; integer16 < arr17.length && !fluidState20.isStill(); ++integer16) {
                    final BlockPos blockPos5 = blockPos4.offset(arr17[integer16]);
                    final FluidState fluidState21 = iWorld.getFluidState(blockPos5);
                    if (fluidState21.getHeight(iWorld, blockPos5) > fluidState20.getHeight(iWorld, blockPos4) || (fluidState21.isStill() && !fluidState20.isStill())) {
                        fluidState20 = fluidState21;
                        blockPos4 = blockPos5;
                    }
                }
                if (fluidState20.isStill()) {
                    final BlockState blockState21 = iWorld.getBlockState(blockPos3);
                    final Block block23 = blockState21.getBlock();
                    if (!(block23 instanceof FluidFillable)) {
                        continue;
                    }
                    ((FluidFillable)block23).tryFillWithFluid(iWorld, blockPos3, blockState21, fluidState20);
                    boolean16 = true;
                    iterator18.remove();
                }
            }
        }
        if (integer10 <= integer13) {
            if (!structurePlacementData.i()) {
                final VoxelSet voxelSet18 = new BitSetVoxelSet(integer13 - integer10 + 1, integer14 - integer11 + 1, integer15 - integer12 + 1);
                final int integer17 = integer10;
                final int integer18 = integer11;
                final int integer19 = integer12;
                for (final Pair<BlockPos, CompoundTag> pair23 : list7) {
                    final BlockPos blockPos6 = (BlockPos)pair23.getFirst();
                    voxelSet18.set(blockPos6.getX() - integer17, blockPos6.getY() - integer18, blockPos6.getZ() - integer19, true, true);
                }
                final BlockPos blockPos7;
                final BlockPos blockPos8;
                final BlockState blockState22;
                final BlockState blockState23;
                final BlockState blockState24;
                final BlockState blockState25;
                voxelSet18.a((direction, integer7, integer8, integer9) -> {
                    blockPos7 = new BlockPos(integer17 + integer7, integer18 + integer8, integer19 + integer9);
                    blockPos8 = blockPos7.offset(direction);
                    blockState22 = iWorld.getBlockState(blockPos7);
                    blockState23 = iWorld.getBlockState(blockPos8);
                    blockState24 = blockState22.getStateForNeighborUpdate(direction, blockState23, iWorld, blockPos7, blockPos8);
                    if (blockState22 != blockState24) {
                        iWorld.setBlockState(blockPos7, blockState24, (integer & 0xFFFFFFFE) | 0x10);
                    }
                    blockState25 = blockState23.getStateForNeighborUpdate(direction.getOpposite(), blockState24, iWorld, blockPos8, blockPos7);
                    if (blockState23 != blockState25) {
                        iWorld.setBlockState(blockPos8, blockState25, (integer & 0xFFFFFFFE) | 0x10);
                    }
                    return;
                });
            }
            for (final Pair<BlockPos, CompoundTag> pair24 : list7) {
                final BlockPos blockPos4 = (BlockPos)pair24.getFirst();
                if (!structurePlacementData.i()) {
                    final BlockState blockState26 = iWorld.getBlockState(blockPos4);
                    final BlockState blockState21 = Block.getRenderingState(blockState26, iWorld, blockPos4);
                    if (blockState26 != blockState21) {
                        iWorld.setBlockState(blockPos4, blockState21, (integer & 0xFFFFFFFE) | 0x10);
                    }
                    iWorld.updateNeighbors(blockPos4, blockState21.getBlock());
                }
                if (pair24.getSecond() != null) {
                    final BlockEntity blockEntity21 = iWorld.getBlockEntity(blockPos4);
                    if (blockEntity21 == null) {
                        continue;
                    }
                    blockEntity21.markDirty();
                }
            }
        }
        if (!structurePlacementData.shouldIgnoreEntities()) {
            this.a(iWorld, blockPos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition(), mutableIntBoundingBox6);
        }
        return true;
    }
    
    public static List<StructureBlockInfo> process(final IWorld world, final BlockPos pos, final StructurePlacementData placementData, final List<StructureBlockInfo> blockInfos) {
        final List<StructureBlockInfo> list5 = Lists.newArrayList();
        for (final StructureBlockInfo structureBlockInfo7 : blockInfos) {
            final BlockPos blockPos8 = a(placementData, structureBlockInfo7.pos).add(pos);
            StructureBlockInfo structureBlockInfo8 = new StructureBlockInfo(blockPos8, structureBlockInfo7.state, structureBlockInfo7.tag);
            for (Iterator<StructureProcessor> iterator10 = placementData.getProcessors().iterator(); structureBlockInfo8 != null && iterator10.hasNext(); structureBlockInfo8 = iterator10.next().process(world, pos, structureBlockInfo7, structureBlockInfo8, placementData)) {}
            if (structureBlockInfo8 != null) {
                list5.add(structureBlockInfo8);
            }
        }
        return list5;
    }
    
    private void a(final IWorld iWorld, final BlockPos blockPos2, final BlockMirror blockMirror, final BlockRotation blockRotation, final BlockPos blockPos5, @Nullable final MutableIntBoundingBox mutableIntBoundingBox) {
        for (final StructureEntityInfo structureEntityInfo8 : this.entities) {
            final BlockPos blockPos6 = a(structureEntityInfo8.blockPos, blockMirror, blockRotation, blockPos5).add(blockPos2);
            if (mutableIntBoundingBox != null && !mutableIntBoundingBox.contains(blockPos6)) {
                continue;
            }
            final CompoundTag compoundTag10 = structureEntityInfo8.tag;
            final Vec3d vec3d11 = a(structureEntityInfo8.pos, blockMirror, blockRotation, blockPos5);
            final Vec3d vec3d12 = vec3d11.add(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            final ListTag listTag13 = new ListTag();
            ((AbstractList<DoubleTag>)listTag13).add(new DoubleTag(vec3d12.x));
            ((AbstractList<DoubleTag>)listTag13).add(new DoubleTag(vec3d12.y));
            ((AbstractList<DoubleTag>)listTag13).add(new DoubleTag(vec3d12.z));
            compoundTag10.put("Pos", listTag13);
            compoundTag10.remove("UUIDMost");
            compoundTag10.remove("UUIDLeast");
            final float float6;
            final float float7;
            final Vec3d vec3d13;
            a(iWorld, compoundTag10).ifPresent(entity -> {
                float6 = entity.applyMirror(blockMirror);
                float7 = float6 + (entity.yaw - entity.applyRotation(blockRotation));
                entity.setPositionAndAngles(vec3d13.x, vec3d13.y, vec3d13.z, float7, entity.pitch);
                iWorld.spawnEntity(entity);
            });
        }
    }
    
    private static Optional<Entity> a(final IWorld iWorld, final CompoundTag compoundTag) {
        try {
            return EntityType.getEntityFromTag(compoundTag, iWorld.getWorld());
        }
        catch (Exception exception3) {
            return Optional.<Entity>empty();
        }
    }
    
    public BlockPos a(final BlockRotation blockRotation) {
        switch (blockRotation) {
            case ROT_270:
            case ROT_90: {
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
            }
            default: {
                return this.size;
            }
        }
    }
    
    public static BlockPos a(final BlockPos blockPos1, final BlockMirror blockMirror, final BlockRotation blockRotation, final BlockPos blockPos4) {
        int integer5 = blockPos1.getX();
        final int integer6 = blockPos1.getY();
        int integer7 = blockPos1.getZ();
        boolean boolean8 = true;
        switch (blockMirror) {
            case LEFT_RIGHT: {
                integer7 = -integer7;
                break;
            }
            case FRONT_BACK: {
                integer5 = -integer5;
                break;
            }
            default: {
                boolean8 = false;
                break;
            }
        }
        final int integer8 = blockPos4.getX();
        final int integer9 = blockPos4.getZ();
        switch (blockRotation) {
            case ROT_180: {
                return new BlockPos(integer8 + integer8 - integer5, integer6, integer9 + integer9 - integer7);
            }
            case ROT_270: {
                return new BlockPos(integer8 - integer9 + integer7, integer6, integer8 + integer9 - integer5);
            }
            case ROT_90: {
                return new BlockPos(integer8 + integer9 - integer7, integer6, integer9 - integer8 + integer5);
            }
            default: {
                return boolean8 ? new BlockPos(integer5, integer6, integer7) : blockPos1;
            }
        }
    }
    
    private static Vec3d a(final Vec3d vec3d, final BlockMirror blockMirror, final BlockRotation blockRotation, final BlockPos blockPos) {
        double double5 = vec3d.x;
        final double double6 = vec3d.y;
        double double7 = vec3d.z;
        boolean boolean11 = true;
        switch (blockMirror) {
            case LEFT_RIGHT: {
                double7 = 1.0 - double7;
                break;
            }
            case FRONT_BACK: {
                double5 = 1.0 - double5;
                break;
            }
            default: {
                boolean11 = false;
                break;
            }
        }
        final int integer12 = blockPos.getX();
        final int integer13 = blockPos.getZ();
        switch (blockRotation) {
            case ROT_180: {
                return new Vec3d(integer12 + integer12 + 1 - double5, double6, integer13 + integer13 + 1 - double7);
            }
            case ROT_270: {
                return new Vec3d(integer12 - integer13 + double7, double6, integer12 + integer13 + 1 - double5);
            }
            case ROT_90: {
                return new Vec3d(integer12 + integer13 + 1 - double7, double6, integer13 - integer12 + double5);
            }
            default: {
                return boolean11 ? new Vec3d(double5, double6, double7) : vec3d;
            }
        }
    }
    
    public BlockPos a(final BlockPos blockPos, final BlockMirror blockMirror, final BlockRotation blockRotation) {
        return a(blockPos, blockMirror, blockRotation, this.getSize().getX(), this.getSize().getZ());
    }
    
    public static BlockPos a(final BlockPos blockPos, final BlockMirror blockMirror, final BlockRotation blockRotation, int integer4, int integer5) {
        --integer4;
        --integer5;
        final int integer6 = (blockMirror == BlockMirror.FRONT_BACK) ? integer4 : 0;
        final int integer7 = (blockMirror == BlockMirror.LEFT_RIGHT) ? integer5 : 0;
        BlockPos blockPos2 = blockPos;
        switch (blockRotation) {
            case ROT_0: {
                blockPos2 = blockPos.add(integer6, 0, integer7);
                break;
            }
            case ROT_90: {
                blockPos2 = blockPos.add(integer5 - integer7, 0, integer6);
                break;
            }
            case ROT_180: {
                blockPos2 = blockPos.add(integer4 - integer6, 0, integer5 - integer7);
                break;
            }
            case ROT_270: {
                blockPos2 = blockPos.add(integer7, 0, integer4 - integer6);
                break;
            }
        }
        return blockPos2;
    }
    
    public MutableIntBoundingBox calculateBoundingBox(final StructurePlacementData placementData, final BlockPos pos) {
        final BlockRotation blockRotation3 = placementData.getRotation();
        final BlockPos blockPos4 = placementData.getPosition();
        final BlockPos blockPos5 = this.a(blockRotation3);
        final BlockMirror blockMirror6 = placementData.getMirror();
        final int integer7 = blockPos4.getX();
        final int integer8 = blockPos4.getZ();
        final int integer9 = blockPos5.getX() - 1;
        final int integer10 = blockPos5.getY() - 1;
        final int integer11 = blockPos5.getZ() - 1;
        MutableIntBoundingBox mutableIntBoundingBox12 = new MutableIntBoundingBox(0, 0, 0, 0, 0, 0);
        switch (blockRotation3) {
            case ROT_0: {
                mutableIntBoundingBox12 = new MutableIntBoundingBox(0, 0, 0, integer9, integer10, integer11);
                break;
            }
            case ROT_180: {
                mutableIntBoundingBox12 = new MutableIntBoundingBox(integer7 + integer7 - integer9, 0, integer8 + integer8 - integer11, integer7 + integer7, integer10, integer8 + integer8);
                break;
            }
            case ROT_270: {
                mutableIntBoundingBox12 = new MutableIntBoundingBox(integer7 - integer8, 0, integer7 + integer8 - integer11, integer7 - integer8 + integer9, integer10, integer7 + integer8);
                break;
            }
            case ROT_90: {
                mutableIntBoundingBox12 = new MutableIntBoundingBox(integer7 + integer8 - integer9, 0, integer8 - integer7, integer7 + integer8, integer10, integer8 - integer7 + integer11);
                break;
            }
        }
        switch (blockMirror6) {
            case FRONT_BACK: {
                this.a(blockRotation3, integer9, integer11, mutableIntBoundingBox12, Direction.WEST, Direction.EAST);
                break;
            }
            case LEFT_RIGHT: {
                this.a(blockRotation3, integer11, integer9, mutableIntBoundingBox12, Direction.NORTH, Direction.SOUTH);
                break;
            }
        }
        mutableIntBoundingBox12.translate(pos.getX(), pos.getY(), pos.getZ());
        return mutableIntBoundingBox12;
    }
    
    private void a(final BlockRotation blockRotation, final int integer2, final int integer3, final MutableIntBoundingBox mutableIntBoundingBox, final Direction direction5, final Direction direction6) {
        BlockPos blockPos7 = BlockPos.ORIGIN;
        if (blockRotation == BlockRotation.ROT_90 || blockRotation == BlockRotation.ROT_270) {
            blockPos7 = blockPos7.offset(blockRotation.rotate(direction5), integer3);
        }
        else if (blockRotation == BlockRotation.ROT_180) {
            blockPos7 = blockPos7.offset(direction6, integer2);
        }
        else {
            blockPos7 = blockPos7.offset(direction5, integer2);
        }
        mutableIntBoundingBox.translate(blockPos7.getX(), 0, blockPos7.getZ());
    }
    
    public CompoundTag toTag(final CompoundTag tag) {
        if (this.blocks.isEmpty()) {
            tag.put("blocks", new ListTag());
            tag.put("palette", new ListTag());
        }
        else {
            final List<a> list2 = Lists.newArrayList();
            final a a3 = new a();
            list2.add(a3);
            for (int integer4 = 1; integer4 < this.blocks.size(); ++integer4) {
                list2.add(new a());
            }
            final ListTag listTag4 = new ListTag();
            final List<StructureBlockInfo> list3 = this.blocks.get(0);
            for (int integer5 = 0; integer5 < list3.size(); ++integer5) {
                final StructureBlockInfo structureBlockInfo7 = list3.get(integer5);
                final CompoundTag compoundTag8 = new CompoundTag();
                compoundTag8.put("pos", this.createIntListTag(structureBlockInfo7.pos.getX(), structureBlockInfo7.pos.getY(), structureBlockInfo7.pos.getZ()));
                final int integer6 = a3.a(structureBlockInfo7.state);
                compoundTag8.putInt("state", integer6);
                if (structureBlockInfo7.tag != null) {
                    compoundTag8.put("nbt", structureBlockInfo7.tag);
                }
                ((AbstractList<CompoundTag>)listTag4).add(compoundTag8);
                for (int integer7 = 1; integer7 < this.blocks.size(); ++integer7) {
                    final a a4 = list2.get(integer7);
                    a4.a(this.blocks.get(integer7).get(integer5).state, integer6);
                }
            }
            tag.put("blocks", listTag4);
            if (list2.size() == 1) {
                final ListTag listTag5 = new ListTag();
                for (final BlockState blockState8 : a3) {
                    ((AbstractList<CompoundTag>)listTag5).add(TagHelper.serializeBlockState(blockState8));
                }
                tag.put("palette", listTag5);
            }
            else {
                final ListTag listTag5 = new ListTag();
                for (final a a5 : list2) {
                    final ListTag listTag6 = new ListTag();
                    for (final BlockState blockState9 : a5) {
                        ((AbstractList<CompoundTag>)listTag6).add(TagHelper.serializeBlockState(blockState9));
                    }
                    ((AbstractList<ListTag>)listTag5).add(listTag6);
                }
                tag.put("palettes", listTag5);
            }
        }
        final ListTag listTag7 = new ListTag();
        for (final StructureEntityInfo structureEntityInfo4 : this.entities) {
            final CompoundTag compoundTag9 = new CompoundTag();
            compoundTag9.put("pos", this.createDoubleListTag(structureEntityInfo4.pos.x, structureEntityInfo4.pos.y, structureEntityInfo4.pos.z));
            compoundTag9.put("blockPos", this.createIntListTag(structureEntityInfo4.blockPos.getX(), structureEntityInfo4.blockPos.getY(), structureEntityInfo4.blockPos.getZ()));
            if (structureEntityInfo4.tag != null) {
                compoundTag9.put("nbt", structureEntityInfo4.tag);
            }
            ((AbstractList<CompoundTag>)listTag7).add(compoundTag9);
        }
        tag.put("entities", listTag7);
        tag.put("size", this.createIntListTag(this.size.getX(), this.size.getY(), this.size.getZ()));
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return tag;
    }
    
    public void fromTag(final CompoundTag tag) {
        this.blocks.clear();
        this.entities.clear();
        final ListTag listTag2 = tag.getList("size", 3);
        this.size = new BlockPos(listTag2.getInt(0), listTag2.getInt(1), listTag2.getInt(2));
        final ListTag listTag3 = tag.getList("blocks", 10);
        if (tag.containsKey("palettes", 9)) {
            final ListTag listTag4 = tag.getList("palettes", 9);
            for (int integer5 = 0; integer5 < listTag4.size(); ++integer5) {
                this.a(listTag4.getListTag(integer5), listTag3);
            }
        }
        else {
            this.a(tag.getList("palette", 10), listTag3);
        }
        final ListTag listTag4 = tag.getList("entities", 10);
        for (int integer5 = 0; integer5 < listTag4.size(); ++integer5) {
            final CompoundTag compoundTag6 = listTag4.getCompoundTag(integer5);
            final ListTag listTag5 = compoundTag6.getList("pos", 6);
            final Vec3d vec3d8 = new Vec3d(listTag5.getDouble(0), listTag5.getDouble(1), listTag5.getDouble(2));
            final ListTag listTag6 = compoundTag6.getList("blockPos", 3);
            final BlockPos blockPos10 = new BlockPos(listTag6.getInt(0), listTag6.getInt(1), listTag6.getInt(2));
            if (compoundTag6.containsKey("nbt")) {
                final CompoundTag compoundTag7 = compoundTag6.getCompound("nbt");
                this.entities.add(new StructureEntityInfo(vec3d8, blockPos10, compoundTag7));
            }
        }
    }
    
    private void a(final ListTag listTag1, final ListTag listTag2) {
        final a a3 = new a();
        final List<StructureBlockInfo> list4 = Lists.newArrayList();
        for (int integer5 = 0; integer5 < listTag1.size(); ++integer5) {
            a3.a(TagHelper.deserializeBlockState(listTag1.getCompoundTag(integer5)), integer5);
        }
        for (int integer5 = 0; integer5 < listTag2.size(); ++integer5) {
            final CompoundTag compoundTag6 = listTag2.getCompoundTag(integer5);
            final ListTag listTag3 = compoundTag6.getList("pos", 3);
            final BlockPos blockPos8 = new BlockPos(listTag3.getInt(0), listTag3.getInt(1), listTag3.getInt(2));
            final BlockState blockState9 = a3.a(compoundTag6.getInt("state"));
            CompoundTag compoundTag7;
            if (compoundTag6.containsKey("nbt")) {
                compoundTag7 = compoundTag6.getCompound("nbt");
            }
            else {
                compoundTag7 = null;
            }
            list4.add(new StructureBlockInfo(blockPos8, blockState9, compoundTag7));
        }
        list4.sort(Comparator.comparingInt(structureBlockInfo -> structureBlockInfo.pos.getY()));
        this.blocks.add(list4);
    }
    
    private ListTag createIntListTag(final int... arr) {
        final ListTag listTag2 = new ListTag();
        for (final int integer6 : arr) {
            ((AbstractList<IntTag>)listTag2).add(new IntTag(integer6));
        }
        return listTag2;
    }
    
    private ListTag createDoubleListTag(final double... arr) {
        final ListTag listTag2 = new ListTag();
        for (final double double6 : arr) {
            ((AbstractList<DoubleTag>)listTag2).add(new DoubleTag(double6));
        }
        return listTag2;
    }
    
    static class a implements Iterable<BlockState>
    {
        public static final BlockState a;
        private final IdList<BlockState> b;
        private int c;
        
        private a() {
            this.b = new IdList<BlockState>(16);
        }
        
        public int a(final BlockState blockState) {
            int integer2 = this.b.getId(blockState);
            if (integer2 == -1) {
                integer2 = this.c++;
                this.b.set(blockState, integer2);
            }
            return integer2;
        }
        
        @Nullable
        public BlockState a(final int integer) {
            final BlockState blockState2 = this.b.get(integer);
            return (blockState2 == null) ? Structure.a.a : blockState2;
        }
        
        @Override
        public Iterator<BlockState> iterator() {
            return this.b.iterator();
        }
        
        public void a(final BlockState blockState, final int integer) {
            this.b.set(blockState, integer);
        }
        
        static {
            a = Blocks.AIR.getDefaultState();
        }
    }
    
    public static class StructureBlockInfo
    {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag tag;
        
        public StructureBlockInfo(final BlockPos pos, final BlockState state, @Nullable final CompoundTag tag) {
            this.pos = pos;
            this.state = state;
            this.tag = tag;
        }
        
        @Override
        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.tag);
        }
    }
    
    public static class StructureEntityInfo
    {
        public final Vec3d pos;
        public final BlockPos blockPos;
        public final CompoundTag tag;
        
        public StructureEntityInfo(final Vec3d pos, final BlockPos blockPos, final CompoundTag tag) {
            this.pos = pos;
            this.blockPos = blockPos;
            this.tag = tag;
        }
    }
}
