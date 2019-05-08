package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.enums.StairShape;
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

public class SwampHutGenerator extends StructurePieceWithDimensions
{
    private boolean hasWitch;
    private boolean hasCat;
    
    public SwampHutGenerator(final Random random, final int integer2, final int integer3) {
        super(StructurePieceType.SWAMP_HUT, random, integer2, 64, integer3, 7, 7, 9);
    }
    
    public SwampHutGenerator(final StructureManager structureManager, final CompoundTag compoundTag) {
        super(StructurePieceType.SWAMP_HUT, compoundTag);
        this.hasWitch = compoundTag.getBoolean("Witch");
        this.hasCat = compoundTag.getBoolean("Cat");
    }
    
    @Override
    protected void toNbt(final CompoundTag tag) {
        super.toNbt(tag);
        tag.putBoolean("Witch", this.hasWitch);
        tag.putBoolean("Cat", this.hasCat);
    }
    
    @Override
    public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
        if (!this.a(world, boundingBox, 0)) {
            return false;
        }
        this.fillWithOutline(world, boundingBox, 1, 1, 1, 5, 1, 7, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 1, 4, 2, 5, 4, 7, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 2, 1, 0, 4, 1, 0, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 2, 2, 2, 3, 3, 2, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 1, 2, 3, 1, 3, 6, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 5, 2, 3, 5, 3, 6, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 2, 2, 7, 4, 3, 7, Blocks.o.getDefaultState(), Blocks.o.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 1, 0, 2, 1, 3, 2, Blocks.I.getDefaultState(), Blocks.I.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 5, 0, 2, 5, 3, 2, Blocks.I.getDefaultState(), Blocks.I.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 1, 0, 7, 1, 3, 7, Blocks.I.getDefaultState(), Blocks.I.getDefaultState(), false);
        this.fillWithOutline(world, boundingBox, 5, 0, 7, 5, 3, 7, Blocks.I.getDefaultState(), Blocks.I.getDefaultState(), false);
        this.addBlock(world, Blocks.cH.getDefaultState(), 2, 3, 2, boundingBox);
        this.addBlock(world, Blocks.cH.getDefaultState(), 3, 3, 7, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 1, 3, 4, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 5, 3, 4, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 5, 3, 5, boundingBox);
        this.addBlock(world, Blocks.eI.getDefaultState(), 1, 3, 5, boundingBox);
        this.addBlock(world, Blocks.bT.getDefaultState(), 3, 2, 6, boundingBox);
        this.addBlock(world, Blocks.dT.getDefaultState(), 4, 2, 6, boundingBox);
        this.addBlock(world, Blocks.cH.getDefaultState(), 1, 2, 1, boundingBox);
        this.addBlock(world, Blocks.cH.getDefaultState(), 5, 2, 1, boundingBox);
        final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.eg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.NORTH);
        final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)Blocks.eg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.EAST);
        final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)Blocks.eg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.WEST);
        final BlockState blockState8 = ((AbstractPropertyContainer<O, BlockState>)Blocks.eg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.SOUTH);
        this.fillWithOutline(world, boundingBox, 0, 4, 1, 6, 4, 1, blockState5, blockState5, false);
        this.fillWithOutline(world, boundingBox, 0, 4, 2, 0, 4, 7, blockState6, blockState6, false);
        this.fillWithOutline(world, boundingBox, 6, 4, 2, 6, 4, 7, blockState7, blockState7, false);
        this.fillWithOutline(world, boundingBox, 0, 4, 8, 6, 4, 8, blockState8, blockState8, false);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState5).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.e), 0, 4, 1, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState5).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.d), 6, 4, 1, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState8).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.d), 0, 4, 8, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)blockState8).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.e), 6, 4, 8, boundingBox);
        for (int integer9 = 2; integer9 <= 7; integer9 += 5) {
            for (int integer10 = 1; integer10 <= 5; integer10 += 4) {
                this.b(world, Blocks.I.getDefaultState(), integer10, -1, integer9, boundingBox);
            }
        }
        if (!this.hasWitch) {
            final int integer9 = this.applyXTransform(2, 5);
            final int integer10 = this.applyYTransform(2);
            final int integer11 = this.applyZTransform(2, 5);
            if (boundingBox.contains(new BlockPos(integer9, integer10, integer11))) {
                this.hasWitch = true;
                final WitchEntity witchEntity12 = EntityType.WITCH.create(world.getWorld());
                witchEntity12.setPersistent();
                witchEntity12.setPositionAndAngles(integer9 + 0.5, integer10, integer11 + 0.5, 0.0f, 0.0f);
                witchEntity12.initialize(world, world.getLocalDifficulty(new BlockPos(integer9, integer10, integer11)), SpawnType.d, null, null);
                world.spawnEntity(witchEntity12);
            }
        }
        this.a(world, boundingBox);
        return true;
    }
    
    private void a(final IWorld iWorld, final MutableIntBoundingBox mutableIntBoundingBox) {
        if (!this.hasCat) {
            final int integer3 = this.applyXTransform(2, 5);
            final int integer4 = this.applyYTransform(2);
            final int integer5 = this.applyZTransform(2, 5);
            if (mutableIntBoundingBox.contains(new BlockPos(integer3, integer4, integer5))) {
                this.hasCat = true;
                final CatEntity catEntity6 = EntityType.CAT.create(iWorld.getWorld());
                catEntity6.setPersistent();
                catEntity6.setPositionAndAngles(integer3 + 0.5, integer4, integer5 + 0.5, 0.0f, 0.0f);
                catEntity6.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(integer3, integer4, integer5)), SpawnType.d, null, null);
                iWorld.spawnEntity(catEntity6);
            }
        }
    }
}
