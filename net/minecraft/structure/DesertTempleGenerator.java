package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import net.minecraft.world.loot.LootTables;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import java.util.Random;

public class DesertTempleGenerator extends StructurePieceWithDimensions
{
    private final boolean[] hasPlacedChest;
    
    public DesertTempleGenerator(final Random random, final int x, final int z) {
        super(StructurePieceType.DESERT_TEMPLE, random, x, 64, z, 21, 15, 21);
        this.hasPlacedChest = new boolean[4];
    }
    
    public DesertTempleGenerator(final StructureManager manager, final CompoundTag tag) {
        super(StructurePieceType.DESERT_TEMPLE, tag);
        (this.hasPlacedChest = new boolean[4])[0] = tag.getBoolean("hasPlacedChest0");
        this.hasPlacedChest[1] = tag.getBoolean("hasPlacedChest1");
        this.hasPlacedChest[2] = tag.getBoolean("hasPlacedChest2");
        this.hasPlacedChest[3] = tag.getBoolean("hasPlacedChest3");
    }
    
    @Override
    protected void toNbt(final CompoundTag tag) {
        super.toNbt(tag);
        tag.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
        tag.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
        tag.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
        tag.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
    }
    
    @Override
    public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
        this.fillWithOutline(world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        for (int integer5 = 1; integer5 <= 9; ++integer5) {
            this.fillWithOutline(world, boundingBox, integer5, integer5, integer5, this.width - 1 - integer5, integer5, this.depth - 1 - integer5, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
            this.fillWithOutline(world, boundingBox, integer5 + 1, integer5, integer5 + 1, this.width - 2 - integer5, integer5, this.depth - 2 - integer5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        }
        for (int integer5 = 0; integer5 < this.width; ++integer5) {
            for (int integer6 = 0; integer6 < this.depth; ++integer6) {
                final int integer7 = -5;
                this.b(world, Blocks.as.getDefaultState(), integer5, -5, integer6, boundingBox);
            }
        }
        final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.ea.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.NORTH);
        final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)Blocks.ea.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.SOUTH);
        final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)Blocks.ea.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.EAST);
        final BlockState blockState8 = ((AbstractPropertyContainer<O, BlockState>)Blocks.ea.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.WEST);
        this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.as.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.addBlock(world, blockState5, 2, 10, 0, boundingBox);
        this.addBlock(world, blockState6, 2, 10, 4, boundingBox);
        this.addBlock(world, blockState7, 0, 10, 2, boundingBox);
        this.addBlock(world, blockState8, 4, 10, 2, boundingBox);
        this.fillWithOutline(world, boundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.as.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.addBlock(world, blockState5, this.width - 3, 10, 0, boundingBox);
        this.addBlock(world, blockState6, this.width - 3, 10, 4, boundingBox);
        this.addBlock(world, blockState7, this.width - 5, 10, 2, boundingBox);
        this.addBlock(world, blockState8, this.width - 1, 10, 2, boundingBox);
        this.fillWithOutline(world, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.as.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(world, Blocks.au.getDefaultState(), 9, 1, 1, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 9, 2, 1, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 9, 3, 1, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 10, 3, 1, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 11, 3, 1, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 11, 2, 1, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 11, 1, 1, boundingBox);
        this.fillWithOutline(world, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.as.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.as.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 8, 1, 8, 8, 3, 8, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 12, 1, 8, 12, 3, 8, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 8, 1, 12, 8, 3, 12, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 12, 1, 12, 12, 3, 12, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 5, 5, 9, 5, 7, 11, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 5, 5, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 5, 6, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 6, 6, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, boundingBox);
        this.fillWithOutline(world, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(world, blockState5, 2, 4, 5, boundingBox);
        this.addBlock(world, blockState5, 2, 3, 4, boundingBox);
        this.addBlock(world, blockState5, this.width - 3, 4, 5, boundingBox);
        this.addBlock(world, blockState5, this.width - 3, 3, 4, boundingBox);
        this.fillWithOutline(world, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.addBlock(world, Blocks.as.getDefaultState(), 1, 1, 2, boundingBox);
        this.addBlock(world, Blocks.as.getDefaultState(), this.width - 2, 1, 2, boundingBox);
        this.addBlock(world, Blocks.hK.getDefaultState(), 1, 2, 2, boundingBox);
        this.addBlock(world, Blocks.hK.getDefaultState(), this.width - 2, 2, 2, boundingBox);
        this.addBlock(world, blockState8, 2, 1, 2, boundingBox);
        this.addBlock(world, blockState7, this.width - 3, 1, 2, boundingBox);
        this.fillWithOutline(world, boundingBox, 4, 3, 5, 4, 3, 17, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        for (int integer8 = 5; integer8 <= 17; integer8 += 2) {
            this.addBlock(world, Blocks.au.getDefaultState(), 4, 1, integer8, boundingBox);
            this.addBlock(world, Blocks.at.getDefaultState(), 4, 2, integer8, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), this.width - 5, 1, integer8, boundingBox);
            this.addBlock(world, Blocks.at.getDefaultState(), this.width - 5, 2, integer8, boundingBox);
        }
        this.addBlock(world, Blocks.fy.getDefaultState(), 10, 0, 7, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 10, 0, 8, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 9, 0, 9, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 11, 0, 9, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 8, 0, 10, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 12, 0, 10, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 7, 0, 10, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 13, 0, 10, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 9, 0, 11, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 11, 0, 11, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 10, 0, 12, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 10, 0, 13, boundingBox);
        this.addBlock(world, Blocks.fI.getDefaultState(), 10, 0, 10, boundingBox);
        for (int integer8 = 0; integer8 <= this.width - 1; integer8 += this.width - 1) {
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 2, 1, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 2, 2, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 2, 3, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 3, 1, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 3, 2, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 3, 3, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 4, 1, boundingBox);
            this.addBlock(world, Blocks.at.getDefaultState(), integer8, 4, 2, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 4, 3, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 5, 1, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 5, 2, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 5, 3, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 6, 1, boundingBox);
            this.addBlock(world, Blocks.at.getDefaultState(), integer8, 6, 2, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 6, 3, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 7, 1, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 7, 2, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 7, 3, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 8, 1, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 8, 2, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 8, 3, boundingBox);
        }
        for (int integer8 = 2; integer8 <= this.width - 3; integer8 += this.width - 3 - 2) {
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 - 1, 2, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 2, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 + 1, 2, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 - 1, 3, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 3, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 + 1, 3, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8 - 1, 4, 0, boundingBox);
            this.addBlock(world, Blocks.at.getDefaultState(), integer8, 4, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8 + 1, 4, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 - 1, 5, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 5, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 + 1, 5, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8 - 1, 6, 0, boundingBox);
            this.addBlock(world, Blocks.at.getDefaultState(), integer8, 6, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8 + 1, 6, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8 - 1, 7, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8, 7, 0, boundingBox);
            this.addBlock(world, Blocks.fy.getDefaultState(), integer8 + 1, 7, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 - 1, 8, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8, 8, 0, boundingBox);
            this.addBlock(world, Blocks.au.getDefaultState(), integer8 + 1, 8, 0, boundingBox);
        }
        this.fillWithOutline(world, boundingBox, 8, 4, 0, 12, 6, 0, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 8, 6, 0, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 12, 6, 0, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 9, 5, 0, boundingBox);
        this.addBlock(world, Blocks.at.getDefaultState(), 10, 5, 0, boundingBox);
        this.addBlock(world, Blocks.fy.getDefaultState(), 11, 5, 0, boundingBox);
        this.fillWithOutline(world, boundingBox, 8, -14, 8, 12, -11, 12, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 8, -10, 8, 12, -10, 12, Blocks.at.getDefaultState(), Blocks.at.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 8, -9, 8, 12, -9, 12, Blocks.au.getDefaultState(), Blocks.au.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.as.getDefaultState(), Blocks.as.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(world, Blocks.co.getDefaultState(), 10, -11, 10, boundingBox);
        this.fillWithOutline(world, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.bG.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 8, -11, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 8, -10, 10, boundingBox);
        this.addBlock(world, Blocks.at.getDefaultState(), 7, -10, 10, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 7, -11, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 12, -11, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 12, -10, 10, boundingBox);
        this.addBlock(world, Blocks.at.getDefaultState(), 13, -10, 10, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 13, -11, 10, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -11, 8, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -10, 8, boundingBox);
        this.addBlock(world, Blocks.at.getDefaultState(), 10, -10, 7, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 10, -11, 7, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -11, 12, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -10, 12, boundingBox);
        this.addBlock(world, Blocks.at.getDefaultState(), 10, -10, 13, boundingBox);
        this.addBlock(world, Blocks.au.getDefaultState(), 10, -11, 13, boundingBox);
        for (final Direction direction10 : Direction.Type.HORIZONTAL) {
            if (!this.hasPlacedChest[direction10.getHorizontal()]) {
                final int integer9 = direction10.getOffsetX() * 2;
                final int integer10 = direction10.getOffsetZ() * 2;
                this.hasPlacedChest[direction10.getHorizontal()] = this.addChest(world, boundingBox, random, 10 + integer9, -11, 10 + integer10, LootTables.CHEST_DESERT_PYRAMID);
            }
        }
        return true;
    }
}
