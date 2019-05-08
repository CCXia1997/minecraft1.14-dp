package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.state.property.Property;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import java.util.Iterator;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.IWorld;
import java.util.Random;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Block;
import java.util.Set;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BlockMirror;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.block.BlockState;

public abstract class StructurePiece
{
    protected static final BlockState AIR;
    protected MutableIntBoundingBox boundingBox;
    @Nullable
    private Direction facing;
    private BlockMirror mirror;
    private BlockRotation rotation;
    protected int o;
    private final StructurePieceType type;
    private static final Set<Block> BLOCKS_NEEDING_POST_PROCESSING;
    
    protected StructurePiece(final StructurePieceType type, final int integer) {
        this.type = type;
        this.o = integer;
    }
    
    public StructurePiece(final StructurePieceType type, final CompoundTag tag) {
        this(type, tag.getInt("GD"));
        if (tag.containsKey("BB")) {
            this.boundingBox = new MutableIntBoundingBox(tag.getIntArray("BB"));
        }
        final int integer3 = tag.getInt("O");
        this.setOrientation((integer3 == -1) ? null : Direction.fromHorizontal(integer3));
    }
    
    public final CompoundTag getTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.putString("id", Registry.STRUCTURE_PIECE.getId(this.getType()).toString());
        compoundTag1.put("BB", this.boundingBox.toNbt());
        final Direction direction2 = this.getFacing();
        compoundTag1.putInt("O", (direction2 == null) ? -1 : direction2.getHorizontal());
        compoundTag1.putInt("GD", this.o);
        this.toNbt(compoundTag1);
        return compoundTag1;
    }
    
    protected abstract void toNbt(final CompoundTag arg1);
    
    public void a(final StructurePiece structurePiece, final List<StructurePiece> list, final Random random) {
    }
    
    public abstract boolean generate(final IWorld arg1, final Random arg2, final MutableIntBoundingBox arg3, final ChunkPos arg4);
    
    public MutableIntBoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public int h() {
        return this.o;
    }
    
    public boolean a(final ChunkPos chunkPos, final int integer) {
        final int integer2 = chunkPos.x << 4;
        final int integer3 = chunkPos.z << 4;
        return this.boundingBox.intersectsXZ(integer2 - integer, integer3 - integer, integer2 + 15 + integer, integer3 + 15 + integer);
    }
    
    public static StructurePiece a(final List<StructurePiece> list, final MutableIntBoundingBox mutableIntBoundingBox) {
        for (final StructurePiece structurePiece4 : list) {
            if (structurePiece4.getBoundingBox() != null && structurePiece4.getBoundingBox().intersects(mutableIntBoundingBox)) {
                return structurePiece4;
            }
        }
        return null;
    }
    
    protected boolean a(final BlockView blockView, final MutableIntBoundingBox mutableIntBoundingBox) {
        final int integer3 = Math.max(this.boundingBox.minX - 1, mutableIntBoundingBox.minX);
        final int integer4 = Math.max(this.boundingBox.minY - 1, mutableIntBoundingBox.minY);
        final int integer5 = Math.max(this.boundingBox.minZ - 1, mutableIntBoundingBox.minZ);
        final int integer6 = Math.min(this.boundingBox.maxX + 1, mutableIntBoundingBox.maxX);
        final int integer7 = Math.min(this.boundingBox.maxY + 1, mutableIntBoundingBox.maxY);
        final int integer8 = Math.min(this.boundingBox.maxZ + 1, mutableIntBoundingBox.maxZ);
        final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
        for (int integer9 = integer3; integer9 <= integer6; ++integer9) {
            for (int integer10 = integer5; integer10 <= integer8; ++integer10) {
                if (blockView.getBlockState(mutable9.set(integer9, integer4, integer10)).getMaterial().isLiquid()) {
                    return true;
                }
                if (blockView.getBlockState(mutable9.set(integer9, integer7, integer10)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        for (int integer9 = integer3; integer9 <= integer6; ++integer9) {
            for (int integer10 = integer4; integer10 <= integer7; ++integer10) {
                if (blockView.getBlockState(mutable9.set(integer9, integer10, integer5)).getMaterial().isLiquid()) {
                    return true;
                }
                if (blockView.getBlockState(mutable9.set(integer9, integer10, integer8)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        for (int integer9 = integer5; integer9 <= integer8; ++integer9) {
            for (int integer10 = integer4; integer10 <= integer7; ++integer10) {
                if (blockView.getBlockState(mutable9.set(integer3, integer10, integer9)).getMaterial().isLiquid()) {
                    return true;
                }
                if (blockView.getBlockState(mutable9.set(integer6, integer10, integer9)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected int applyXTransform(final int integer1, final int integer2) {
        final Direction direction3 = this.getFacing();
        if (direction3 == null) {
            return integer1;
        }
        switch (direction3) {
            case NORTH:
            case SOUTH: {
                return this.boundingBox.minX + integer1;
            }
            case WEST: {
                return this.boundingBox.maxX - integer2;
            }
            case EAST: {
                return this.boundingBox.minX + integer2;
            }
            default: {
                return integer1;
            }
        }
    }
    
    protected int applyYTransform(final int integer) {
        if (this.getFacing() == null) {
            return integer;
        }
        return integer + this.boundingBox.minY;
    }
    
    protected int applyZTransform(final int integer1, final int integer2) {
        final Direction direction3 = this.getFacing();
        if (direction3 == null) {
            return integer2;
        }
        switch (direction3) {
            case NORTH: {
                return this.boundingBox.maxZ - integer2;
            }
            case SOUTH: {
                return this.boundingBox.minZ + integer2;
            }
            case WEST:
            case EAST: {
                return this.boundingBox.minZ + integer1;
            }
            default: {
                return integer2;
            }
        }
    }
    
    protected void addBlock(final IWorld world, BlockState block, final int x, final int y, final int z, final MutableIntBoundingBox mutableIntBoundingBox) {
        final BlockPos blockPos7 = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        if (!mutableIntBoundingBox.contains(blockPos7)) {
            return;
        }
        if (this.mirror != BlockMirror.NONE) {
            block = block.mirror(this.mirror);
        }
        if (this.rotation != BlockRotation.ROT_0) {
            block = block.rotate(this.rotation);
        }
        world.setBlockState(blockPos7, block, 2);
        final FluidState fluidState8 = world.getFluidState(blockPos7);
        if (!fluidState8.isEmpty()) {
            world.getFluidTickScheduler().schedule(blockPos7, fluidState8.getFluid(), 0);
        }
        if (StructurePiece.BLOCKS_NEEDING_POST_PROCESSING.contains(block.getBlock())) {
            world.getChunk(blockPos7).markBlockForPostProcessing(blockPos7);
        }
    }
    
    protected BlockState getBlockAt(final BlockView blockView, final int x, final int y, final int z, final MutableIntBoundingBox mutableIntBoundingBox) {
        final int integer6 = this.applyXTransform(x, z);
        final int integer7 = this.applyYTransform(y);
        final int integer8 = this.applyZTransform(x, z);
        final BlockPos blockPos9 = new BlockPos(integer6, integer7, integer8);
        if (!mutableIntBoundingBox.contains(blockPos9)) {
            return Blocks.AIR.getDefaultState();
        }
        return blockView.getBlockState(blockPos9);
    }
    
    protected boolean isUnderSeaLevel(final ViewableWorld viewableWorld, final int x, final int z, final int y, final MutableIntBoundingBox mutableIntBoundingBox) {
        final int integer6 = this.applyXTransform(x, y);
        final int integer7 = this.applyYTransform(z + 1);
        final int integer8 = this.applyZTransform(x, y);
        final BlockPos blockPos9 = new BlockPos(integer6, integer7, integer8);
        return mutableIntBoundingBox.contains(blockPos9) && integer7 < viewableWorld.getTop(Heightmap.Type.c, integer6, integer8);
    }
    
    protected void fill(final IWorld world, final MutableIntBoundingBox bounds, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int integer8) {
        for (int integer9 = minY; integer9 <= maxY; ++integer9) {
            for (int integer10 = minX; integer10 <= maxX; ++integer10) {
                for (int integer11 = minZ; integer11 <= integer8; ++integer11) {
                    this.addBlock(world, Blocks.AIR.getDefaultState(), integer10, integer9, integer11, bounds);
                }
            }
        }
    }
    
    protected void fillWithOutline(final IWorld world, final MutableIntBoundingBox mutableIntBoundingBox, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final BlockState blockState9, final BlockState inside, final boolean boolean11) {
        for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
            for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                    if (!boolean11 || !this.getBlockAt(world, integer10, integer9, integer11, mutableIntBoundingBox).isAir()) {
                        if (integer9 == integer4 || integer9 == integer7 || integer10 == integer3 || integer10 == integer6 || integer11 == integer5 || integer11 == integer8) {
                            this.addBlock(world, blockState9, integer10, integer9, integer11, mutableIntBoundingBox);
                        }
                        else {
                            this.addBlock(world, inside, integer10, integer9, integer11, mutableIntBoundingBox);
                        }
                    }
                }
            }
        }
    }
    
    protected void fillWithOutline(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ, final boolean replaceBlocks, final Random random, final BlockRandomizer blockRandomizer) {
        for (int integer12 = minY; integer12 <= maxY; ++integer12) {
            for (int integer13 = minX; integer13 <= maxX; ++integer13) {
                for (int integer14 = minZ; integer14 <= maxZ; ++integer14) {
                    if (!replaceBlocks || !this.getBlockAt(iWorld, integer13, integer12, integer14, mutableIntBoundingBox).isAir()) {
                        blockRandomizer.setBlock(random, integer13, integer12, integer14, integer12 == minY || integer12 == maxY || integer13 == minX || integer13 == maxX || integer14 == minZ || integer14 == maxZ);
                        this.addBlock(iWorld, blockRandomizer.getBlock(), integer13, integer12, integer14, mutableIntBoundingBox);
                    }
                }
            }
        }
    }
    
    protected void fillWithOutlineUnderSealevel(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox, final Random random, final float float4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final int integer10, final BlockState blockState11, final BlockState blockState12, final boolean boolean13, final boolean boolean14) {
        for (int integer11 = integer6; integer11 <= integer9; ++integer11) {
            for (int integer12 = integer5; integer12 <= integer8; ++integer12) {
                for (int integer13 = integer7; integer13 <= integer10; ++integer13) {
                    if (random.nextFloat() <= float4) {
                        if (!boolean13 || !this.getBlockAt(iWorld, integer12, integer11, integer13, mutableIntBoundingBox).isAir()) {
                            if (!boolean14 || this.isUnderSeaLevel(iWorld, integer12, integer11, integer13, mutableIntBoundingBox)) {
                                if (integer11 == integer6 || integer11 == integer9 || integer12 == integer5 || integer12 == integer8 || integer13 == integer7 || integer13 == integer10) {
                                    this.addBlock(iWorld, blockState11, integer12, integer11, integer13, mutableIntBoundingBox);
                                }
                                else {
                                    this.addBlock(iWorld, blockState12, integer12, integer11, integer13, mutableIntBoundingBox);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void addBlockWithRandomThreshold(final IWorld world, final MutableIntBoundingBox bounds, final Random random, final float threshold, final int x, final int y, final int z, final BlockState blockState) {
        if (random.nextFloat() < threshold) {
            this.addBlock(world, blockState, x, y, z, bounds);
        }
    }
    
    protected void a(final IWorld world, final MutableIntBoundingBox bounds, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ, final BlockState block, final boolean boolean10) {
        final float float11 = (float)(maxX - minX + 1);
        final float float12 = (float)(maxY - minY + 1);
        final float float13 = (float)(maxZ - minZ + 1);
        final float float14 = minX + float11 / 2.0f;
        final float float15 = minZ + float13 / 2.0f;
        for (int integer16 = minY; integer16 <= maxY; ++integer16) {
            final float float16 = (integer16 - minY) / float12;
            for (int integer17 = minX; integer17 <= maxX; ++integer17) {
                final float float17 = (integer17 - float14) / (float11 * 0.5f);
                for (int integer18 = minZ; integer18 <= maxZ; ++integer18) {
                    final float float18 = (integer18 - float15) / (float13 * 0.5f);
                    if (!boolean10 || !this.getBlockAt(world, integer17, integer16, integer18, bounds).isAir()) {
                        final float float19 = float17 * float17 + float16 * float16 + float18 * float18;
                        if (float19 <= 1.05f) {
                            this.addBlock(world, block, integer17, integer16, integer18, bounds);
                        }
                    }
                }
            }
        }
    }
    
    protected void b(final IWorld world, final BlockState blockState, final int x, final int y, final int z, final MutableIntBoundingBox mutableIntBoundingBox) {
        final int integer7 = this.applyXTransform(x, z);
        int integer8 = this.applyYTransform(y);
        final int integer9 = this.applyZTransform(x, z);
        if (!mutableIntBoundingBox.contains(new BlockPos(integer7, integer8, integer9))) {
            return;
        }
        while ((world.isAir(new BlockPos(integer7, integer8, integer9)) || world.getBlockState(new BlockPos(integer7, integer8, integer9)).getMaterial().isLiquid()) && integer8 > 1) {
            world.setBlockState(new BlockPos(integer7, integer8, integer9), blockState, 2);
            --integer8;
        }
    }
    
    protected boolean addChest(final IWorld world, final MutableIntBoundingBox boundingBox, final Random random, final int x, final int y, final int z, final Identifier lootTableId) {
        final BlockPos blockPos8 = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        return this.addChest(world, boundingBox, random, blockPos8, lootTableId, null);
    }
    
    public static BlockState a(final BlockView blockView, final BlockPos blockPos, final BlockState blockState) {
        Direction direction4 = null;
        for (final Direction direction5 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos2 = blockPos.offset(direction5);
            final BlockState blockState2 = blockView.getBlockState(blockPos2);
            if (blockState2.getBlock() == Blocks.bP) {
                return blockState;
            }
            if (!blockState2.isFullOpaque(blockView, blockPos2)) {
                continue;
            }
            if (direction4 != null) {
                direction4 = null;
                break;
            }
            direction4 = direction5;
        }
        if (direction4 != null) {
            return ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Direction>with((Property<Comparable>)HorizontalFacingBlock.FACING, direction4.getOpposite());
        }
        Direction direction6 = blockState.<Direction>get((Property<Direction>)HorizontalFacingBlock.FACING);
        BlockPos blockPos3 = blockPos.offset(direction6);
        if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
            direction6 = direction6.getOpposite();
            blockPos3 = blockPos.offset(direction6);
        }
        if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
            direction6 = direction6.rotateYClockwise();
            blockPos3 = blockPos.offset(direction6);
        }
        if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
            direction6 = direction6.getOpposite();
            blockPos3 = blockPos.offset(direction6);
        }
        return ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Direction>with((Property<Comparable>)HorizontalFacingBlock.FACING, direction6);
    }
    
    protected boolean addChest(final IWorld world, final MutableIntBoundingBox boundingBox, final Random random, final BlockPos pos, final Identifier lootTableId, @Nullable BlockState block) {
        if (!boundingBox.contains(pos) || world.getBlockState(pos).getBlock() == Blocks.bP) {
            return false;
        }
        if (block == null) {
            block = a(world, pos, Blocks.bP.getDefaultState());
        }
        world.setBlockState(pos, block, 2);
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity7).setLootTable(lootTableId, random.nextLong());
        }
        return true;
    }
    
    protected boolean addDispenser(final IWorld world, final MutableIntBoundingBox boundingBox, final Random random, final int x, final int y, final int z, final Direction facing, final Identifier lootTbaleId) {
        final BlockPos blockPos9 = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        if (boundingBox.contains(blockPos9) && world.getBlockState(blockPos9).getBlock() != Blocks.ar) {
            this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.ar.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)DispenserBlock.FACING, facing), x, y, z, boundingBox);
            final BlockEntity blockEntity10 = world.getBlockEntity(blockPos9);
            if (blockEntity10 instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)blockEntity10).setLootTable(lootTbaleId, random.nextLong());
            }
            return true;
        }
        return false;
    }
    
    public void translate(final int x, final int y, final int z) {
        this.boundingBox.translate(x, y, z);
    }
    
    @Nullable
    public Direction getFacing() {
        return this.facing;
    }
    
    public void setOrientation(@Nullable final Direction orientation) {
        this.facing = orientation;
        if (orientation == null) {
            this.rotation = BlockRotation.ROT_0;
            this.mirror = BlockMirror.NONE;
        }
        else {
            switch (orientation) {
                case SOUTH: {
                    this.mirror = BlockMirror.LEFT_RIGHT;
                    this.rotation = BlockRotation.ROT_0;
                    break;
                }
                case WEST: {
                    this.mirror = BlockMirror.LEFT_RIGHT;
                    this.rotation = BlockRotation.ROT_90;
                    break;
                }
                case EAST: {
                    this.mirror = BlockMirror.NONE;
                    this.rotation = BlockRotation.ROT_90;
                    break;
                }
                default: {
                    this.mirror = BlockMirror.NONE;
                    this.rotation = BlockRotation.ROT_0;
                    break;
                }
            }
        }
    }
    
    public BlockRotation getRotation() {
        return this.rotation;
    }
    
    public StructurePieceType getType() {
        return this.type;
    }
    
    static {
        AIR = Blocks.kT.getDefaultState();
        BLOCKS_NEEDING_POST_PROCESSING = ImmutableSet.<Block>builder().add(Blocks.dO).add(Blocks.bK).add(Blocks.bL).add(Blocks.cH).add(Blocks.ie).add(Blocks.ii).add(Blocks.ih).add(Blocks.if_).add(Blocks.ig).add(Blocks.ce).add(Blocks.dA).build();
    }
    
    public abstract static class BlockRandomizer
    {
        protected BlockState block;
        
        protected BlockRandomizer() {
            this.block = Blocks.AIR.getDefaultState();
        }
        
        public abstract void setBlock(final Random arg1, final int arg2, final int arg3, final int arg4, final boolean arg5);
        
        public BlockState getBlock() {
            return this.block;
        }
    }
}
