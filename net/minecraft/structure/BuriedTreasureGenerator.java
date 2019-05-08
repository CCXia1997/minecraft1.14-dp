package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.BlockPos;

public class BuriedTreasureGenerator
{
    public static class Piece extends StructurePiece
    {
        public Piece(final BlockPos pos) {
            super(StructurePieceType.BURIED_TREASURE, 0);
            this.boundingBox = new MutableIntBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
        
        public Piece(final StructureManager manager, final CompoundTag tag) {
            super(StructurePieceType.BURIED_TREASURE, tag);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final int integer5 = world.getTop(Heightmap.Type.c, this.boundingBox.minX, this.boundingBox.minZ);
            final BlockPos.Mutable mutable6 = new BlockPos.Mutable(this.boundingBox.minX, integer5, this.boundingBox.minZ);
            while (mutable6.getY() > 0) {
                final BlockState blockState7 = world.getBlockState(mutable6);
                final BlockState blockState8 = world.getBlockState(mutable6.down());
                if (blockState8 == Blocks.as.getDefaultState() || blockState8 == Blocks.b.getDefaultState() || blockState8 == Blocks.g.getDefaultState() || blockState8 == Blocks.c.getDefaultState() || blockState8 == Blocks.e.getDefaultState()) {
                    final BlockState blockState9 = (blockState7.isAir() || this.isLiquid(blockState7)) ? Blocks.C.getDefaultState() : blockState7;
                    for (final Direction direction13 : Direction.values()) {
                        final BlockPos blockPos14 = mutable6.offset(direction13);
                        final BlockState blockState10 = world.getBlockState(blockPos14);
                        if (blockState10.isAir() || this.isLiquid(blockState10)) {
                            final BlockPos blockPos15 = blockPos14.down();
                            final BlockState blockState11 = world.getBlockState(blockPos15);
                            if ((blockState11.isAir() || this.isLiquid(blockState11)) && direction13 != Direction.UP) {
                                world.setBlockState(blockPos14, blockState8, 3);
                            }
                            else {
                                world.setBlockState(blockPos14, blockState9, 3);
                            }
                        }
                    }
                    return this.addChest(world, boundingBox, random, new BlockPos(this.boundingBox.minX, mutable6.getY(), this.boundingBox.minZ), LootTables.G, null);
                }
                mutable6.setOffset(0, -1, 0);
            }
            return false;
        }
        
        private boolean isLiquid(final BlockState state) {
            return state == Blocks.A.getDefaultState() || state == Blocks.B.getDefaultState();
        }
    }
}
