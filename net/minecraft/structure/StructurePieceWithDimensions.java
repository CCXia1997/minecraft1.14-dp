package net.minecraft.structure;

import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.Direction;
import java.util.Random;

public abstract class StructurePieceWithDimensions extends StructurePiece
{
    protected final int width;
    protected final int height;
    protected final int depth;
    protected int hPos;
    
    protected StructurePieceWithDimensions(final StructurePieceType type, final Random random, final int x, final int y, final int z, final int width, final int height, final int depth) {
        super(type, 0);
        this.hPos = -1;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.setOrientation(Direction.Type.HORIZONTAL.random(random));
        if (this.getFacing().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableIntBoundingBox(x, y, z, x + width - 1, y + height - 1, z + depth - 1);
        }
        else {
            this.boundingBox = new MutableIntBoundingBox(x, y, z, x + depth - 1, y + height - 1, z + width - 1);
        }
    }
    
    protected StructurePieceWithDimensions(final StructurePieceType type, final CompoundTag tag) {
        super(type, tag);
        this.hPos = -1;
        this.width = tag.getInt("Width");
        this.height = tag.getInt("Height");
        this.depth = tag.getInt("Depth");
        this.hPos = tag.getInt("HPos");
    }
    
    @Override
    protected void toNbt(final CompoundTag tag) {
        tag.putInt("Width", this.width);
        tag.putInt("Height", this.height);
        tag.putInt("Depth", this.depth);
        tag.putInt("HPos", this.hPos);
    }
    
    protected boolean a(final IWorld world, final MutableIntBoundingBox boundingBox, final int integer) {
        if (this.hPos >= 0) {
            return true;
        }
        int integer2 = 0;
        int integer3 = 0;
        final BlockPos.Mutable mutable6 = new BlockPos.Mutable();
        for (int integer4 = this.boundingBox.minZ; integer4 <= this.boundingBox.maxZ; ++integer4) {
            for (int integer5 = this.boundingBox.minX; integer5 <= this.boundingBox.maxX; ++integer5) {
                mutable6.set(integer5, 64, integer4);
                if (boundingBox.contains(mutable6)) {
                    integer2 += world.getTopPosition(Heightmap.Type.f, mutable6).getY();
                    ++integer3;
                }
            }
        }
        if (integer3 == 0) {
            return false;
        }
        this.hPos = integer2 / integer3;
        this.boundingBox.translate(0, this.hPos - this.boundingBox.minY + integer, 0);
        return true;
    }
}
