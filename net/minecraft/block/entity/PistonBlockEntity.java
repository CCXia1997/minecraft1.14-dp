package net.minecraft.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.world.IWorld;
import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShapes;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tickable;

public class PistonBlockEntity extends BlockEntity implements Tickable
{
    private BlockState pushedBlock;
    private Direction facing;
    private boolean extending;
    private boolean source;
    private static final ThreadLocal<Direction> h;
    private float nextProgress;
    private float progress;
    private long savedWorldTime;
    
    public PistonBlockEntity() {
        super(BlockEntityType.PISTON);
    }
    
    public PistonBlockEntity(final BlockState pushedBlock, final Direction facing, final boolean extending, final boolean boolean4) {
        this();
        this.pushedBlock = pushedBlock;
        this.facing = facing;
        this.extending = extending;
        this.source = boolean4;
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    public boolean isExtending() {
        return this.extending;
    }
    
    public Direction getFacing() {
        return this.facing;
    }
    
    public boolean isSource() {
        return this.source;
    }
    
    public float getProgress(float float1) {
        if (float1 > 1.0f) {
            float1 = 1.0f;
        }
        return MathHelper.lerp(float1, this.progress, this.nextProgress);
    }
    
    @Environment(EnvType.CLIENT)
    public float getRenderOffsetX(final float float1) {
        return this.facing.getOffsetX() * this.e(this.getProgress(float1));
    }
    
    @Environment(EnvType.CLIENT)
    public float getRenderOffsetY(final float float1) {
        return this.facing.getOffsetY() * this.e(this.getProgress(float1));
    }
    
    @Environment(EnvType.CLIENT)
    public float getRenderOffsetZ(final float float1) {
        return this.facing.getOffsetZ() * this.e(this.getProgress(float1));
    }
    
    private float e(final float float1) {
        return this.extending ? (float1 - 1.0f) : (1.0f - float1);
    }
    
    private BlockState v() {
        if (!this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock) {
            return (((AbstractPropertyContainer<O, BlockState>)Blocks.aW.getDefaultState()).with(PistonHeadBlock.TYPE, (this.pushedBlock.getBlock() == Blocks.aO) ? PistonType.b : PistonType.a)).<Comparable, Comparable>with((Property<Comparable>)PistonHeadBlock.FACING, (Comparable)this.pushedBlock.<V>get((Property<V>)PistonBlock.FACING));
        }
        return this.pushedBlock;
    }
    
    private void f(final float float1) {
        final Direction direction2 = this.r();
        final double double3 = float1 - this.nextProgress;
        final VoxelShape voxelShape5 = this.v().getCollisionShape(this.world, this.getPos());
        if (voxelShape5.isEmpty()) {
            return;
        }
        final List<BoundingBox> list6 = voxelShape5.getBoundingBoxes();
        final BoundingBox boundingBox7 = this.a(this.a(list6));
        final List<Entity> list7 = this.world.getEntities((Entity)null, this.a(boundingBox7, direction2, double3).union(boundingBox7));
        if (list7.isEmpty()) {
            return;
        }
        final boolean boolean9 = this.pushedBlock.getBlock() == Blocks.gf;
        for (int integer10 = 0; integer10 < list7.size(); ++integer10) {
            final Entity entity11 = list7.get(integer10);
            if (entity11.getPistonBehavior() != PistonBehavior.d) {
                if (boolean9) {
                    final Vec3d vec3d12 = entity11.getVelocity();
                    double double4 = vec3d12.x;
                    double double5 = vec3d12.y;
                    double double6 = vec3d12.z;
                    switch (direction2.getAxis()) {
                        case X: {
                            double4 = direction2.getOffsetX();
                            break;
                        }
                        case Y: {
                            double5 = direction2.getOffsetY();
                            break;
                        }
                        case Z: {
                            double6 = direction2.getOffsetZ();
                            break;
                        }
                    }
                    entity11.setVelocity(double4, double5, double6);
                }
                double double7 = 0.0;
                for (int integer11 = 0; integer11 < list6.size(); ++integer11) {
                    final BoundingBox boundingBox8 = this.a(this.a(list6.get(integer11)), direction2, double3);
                    final BoundingBox boundingBox9 = entity11.getBoundingBox();
                    if (boundingBox8.intersects(boundingBox9)) {
                        double7 = Math.max(double7, this.a(boundingBox8, direction2, boundingBox9));
                        if (double7 >= double3) {
                            break;
                        }
                    }
                }
                if (double7 > 0.0) {
                    double7 = Math.min(double7, double3) + 0.01;
                    PistonBlockEntity.h.set(direction2);
                    entity11.move(MovementType.c, new Vec3d(double7 * direction2.getOffsetX(), double7 * direction2.getOffsetY(), double7 * direction2.getOffsetZ()));
                    PistonBlockEntity.h.set(null);
                    if (!this.extending && this.source) {
                        this.a(entity11, direction2, double3);
                    }
                }
            }
        }
    }
    
    public Direction r() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }
    
    private BoundingBox a(final List<BoundingBox> list) {
        double double2 = 0.0;
        double double3 = 0.0;
        double double4 = 0.0;
        double double5 = 1.0;
        double double6 = 1.0;
        double double7 = 1.0;
        for (final BoundingBox boundingBox15 : list) {
            double2 = Math.min(boundingBox15.minX, double2);
            double3 = Math.min(boundingBox15.minY, double3);
            double4 = Math.min(boundingBox15.minZ, double4);
            double5 = Math.max(boundingBox15.maxX, double5);
            double6 = Math.max(boundingBox15.maxY, double6);
            double7 = Math.max(boundingBox15.maxZ, double7);
        }
        return new BoundingBox(double2, double3, double4, double5, double6, double7);
    }
    
    private double a(final BoundingBox boundingBox1, final Direction direction, final BoundingBox boundingBox3) {
        switch (direction.getAxis()) {
            case X: {
                return b(boundingBox1, direction, boundingBox3);
            }
            default: {
                return c(boundingBox1, direction, boundingBox3);
            }
            case Z: {
                return d(boundingBox1, direction, boundingBox3);
            }
        }
    }
    
    private BoundingBox a(final BoundingBox boundingBox) {
        final double double2 = this.e(this.nextProgress);
        return boundingBox.offset(this.pos.getX() + double2 * this.facing.getOffsetX(), this.pos.getY() + double2 * this.facing.getOffsetY(), this.pos.getZ() + double2 * this.facing.getOffsetZ());
    }
    
    private BoundingBox a(final BoundingBox boundingBox, final Direction direction, final double double3) {
        final double double4 = double3 * direction.getDirection().offset();
        final double double5 = Math.min(double4, 0.0);
        final double double6 = Math.max(double4, 0.0);
        switch (direction) {
            case WEST: {
                return new BoundingBox(boundingBox.minX + double5, boundingBox.minY, boundingBox.minZ, boundingBox.minX + double6, boundingBox.maxY, boundingBox.maxZ);
            }
            case EAST: {
                return new BoundingBox(boundingBox.maxX + double5, boundingBox.minY, boundingBox.minZ, boundingBox.maxX + double6, boundingBox.maxY, boundingBox.maxZ);
            }
            case DOWN: {
                return new BoundingBox(boundingBox.minX, boundingBox.minY + double5, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + double6, boundingBox.maxZ);
            }
            default: {
                return new BoundingBox(boundingBox.minX, boundingBox.maxY + double5, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY + double6, boundingBox.maxZ);
            }
            case NORTH: {
                return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ + double5, boundingBox.maxX, boundingBox.maxY, boundingBox.minZ + double6);
            }
            case SOUTH: {
                return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.maxZ + double5, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ + double6);
            }
        }
    }
    
    private void a(final Entity entity, final Direction direction, final double double3) {
        final BoundingBox boundingBox5 = entity.getBoundingBox();
        final BoundingBox boundingBox6 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
        if (boundingBox5.intersects(boundingBox6)) {
            final Direction direction2 = direction.getOpposite();
            double double4 = this.a(boundingBox6, direction2, boundingBox5) + 0.01;
            final double double5 = this.a(boundingBox6, direction2, boundingBox5.intersection(boundingBox6)) + 0.01;
            if (Math.abs(double4 - double5) < 0.01) {
                double4 = Math.min(double4, double3) + 0.01;
                PistonBlockEntity.h.set(direction);
                entity.move(MovementType.c, new Vec3d(double4 * direction2.getOffsetX(), double4 * direction2.getOffsetY(), double4 * direction2.getOffsetZ()));
                PistonBlockEntity.h.set(null);
            }
        }
    }
    
    private static double b(final BoundingBox boundingBox1, final Direction direction, final BoundingBox boundingBox3) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return boundingBox1.maxX - boundingBox3.minX;
        }
        return boundingBox3.maxX - boundingBox1.minX;
    }
    
    private static double c(final BoundingBox boundingBox1, final Direction direction, final BoundingBox boundingBox3) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return boundingBox1.maxY - boundingBox3.minY;
        }
        return boundingBox3.maxY - boundingBox1.minY;
    }
    
    private static double d(final BoundingBox boundingBox1, final Direction direction, final BoundingBox boundingBox3) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return boundingBox1.maxZ - boundingBox3.minZ;
        }
        return boundingBox3.maxZ - boundingBox1.minZ;
    }
    
    public BlockState getPushedBlock() {
        return this.pushedBlock;
    }
    
    public void t() {
        if (this.progress < 1.0f && this.world != null) {
            this.nextProgress = 1.0f;
            this.progress = this.nextProgress;
            this.world.removeBlockEntity(this.pos);
            this.invalidate();
            if (this.world.getBlockState(this.pos).getBlock() == Blocks.bn) {
                BlockState blockState1;
                if (this.source) {
                    blockState1 = Blocks.AIR.getDefaultState();
                }
                else {
                    blockState1 = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
                }
                this.world.setBlockState(this.pos, blockState1, 3);
                this.world.updateNeighbor(this.pos, blockState1.getBlock(), this.pos);
            }
        }
    }
    
    @Override
    public void tick() {
        this.savedWorldTime = this.world.getTime();
        this.progress = this.nextProgress;
        if (this.progress >= 1.0f) {
            this.world.removeBlockEntity(this.pos);
            this.invalidate();
            if (this.pushedBlock != null && this.world.getBlockState(this.pos).getBlock() == Blocks.bn) {
                BlockState blockState1 = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
                if (blockState1.isAir()) {
                    this.world.setBlockState(this.pos, this.pushedBlock, 84);
                    Block.replaceBlock(this.pushedBlock, blockState1, this.world, this.pos, 3);
                }
                else {
                    if (blockState1.<Comparable>contains((Property<Comparable>)Properties.WATERLOGGED) && blockState1.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED)) {
                        blockState1 = ((AbstractPropertyContainer<O, BlockState>)blockState1).<Comparable, Boolean>with((Property<Comparable>)Properties.WATERLOGGED, false);
                    }
                    this.world.setBlockState(this.pos, blockState1, 67);
                    this.world.updateNeighbor(this.pos, blockState1.getBlock(), this.pos);
                }
            }
            return;
        }
        final float float1 = this.nextProgress + 0.5f;
        this.f(float1);
        this.nextProgress = float1;
        if (this.nextProgress >= 1.0f) {
            this.nextProgress = 1.0f;
        }
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.pushedBlock = TagHelper.deserializeBlockState(compoundTag.getCompound("blockState"));
        this.facing = Direction.byId(compoundTag.getInt("facing"));
        this.nextProgress = compoundTag.getFloat("progress");
        this.progress = this.nextProgress;
        this.extending = compoundTag.getBoolean("extending");
        this.source = compoundTag.getBoolean("source");
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("blockState", TagHelper.serializeBlockState(this.pushedBlock));
        compoundTag.putInt("facing", this.facing.getId());
        compoundTag.putFloat("progress", this.progress);
        compoundTag.putBoolean("extending", this.extending);
        compoundTag.putBoolean("source", this.source);
        return compoundTag;
    }
    
    public VoxelShape a(final BlockView blockView, final BlockPos blockPos) {
        VoxelShape voxelShape3;
        if (!this.extending && this.source) {
            voxelShape3 = ((AbstractPropertyContainer<O, BlockState>)this.pushedBlock).<Comparable, Boolean>with((Property<Comparable>)PistonBlock.EXTENDED, true).getCollisionShape(blockView, blockPos);
        }
        else {
            voxelShape3 = VoxelShapes.empty();
        }
        final Direction direction4 = PistonBlockEntity.h.get();
        if (this.nextProgress < 1.0 && direction4 == this.r()) {
            return voxelShape3;
        }
        BlockState blockState5;
        if (this.isSource()) {
            blockState5 = (((AbstractPropertyContainer<O, BlockState>)Blocks.aW.getDefaultState()).with((Property<Comparable>)PistonHeadBlock.FACING, this.facing)).<Comparable, Boolean>with((Property<Comparable>)PistonHeadBlock.SHORT, this.extending != 1.0f - this.nextProgress < 4.0f);
        }
        else {
            blockState5 = this.pushedBlock;
        }
        final float float6 = this.e(this.nextProgress);
        final double double7 = this.facing.getOffsetX() * float6;
        final double double8 = this.facing.getOffsetY() * float6;
        final double double9 = this.facing.getOffsetZ() * float6;
        return VoxelShapes.union(voxelShape3, blockState5.getCollisionShape(blockView, blockPos).offset(double7, double8, double9));
    }
    
    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }
    
    static {
        h = new ThreadLocal<Direction>() {
            protected Direction a() {
                return null;
            }
        };
    }
}
