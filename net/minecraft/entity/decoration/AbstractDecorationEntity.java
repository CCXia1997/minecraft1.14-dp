package net.minecraft.entity.decoration;

import net.minecraft.entity.LightningEntity;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BoundingBox;
import org.apache.commons.lang3.Validate;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;

public abstract class AbstractDecorationEntity extends Entity
{
    protected static final Predicate<Entity> PREDICATE;
    private int e;
    protected BlockPos blockPos;
    @Nullable
    public Direction facing;
    
    protected AbstractDecorationEntity(final EntityType<? extends AbstractDecorationEntity> type, final World world) {
        super(type, world);
    }
    
    protected AbstractDecorationEntity(final EntityType<? extends AbstractDecorationEntity> world, final World world, final BlockPos blockPos) {
        this(world, world);
        this.blockPos = blockPos;
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    protected void setFacing(final Direction direction) {
        Validate.notNull(direction);
        Validate.isTrue(direction.getAxis().isHorizontal());
        this.facing = direction;
        this.yaw = (float)(this.facing.getHorizontal() * 90);
        this.prevYaw = this.yaw;
        this.f();
    }
    
    protected void f() {
        if (this.facing == null) {
            return;
        }
        double double1 = this.blockPos.getX() + 0.5;
        double double2 = this.blockPos.getY() + 0.5;
        double double3 = this.blockPos.getZ() + 0.5;
        final double double4 = 0.46875;
        final double double5 = this.a(this.getWidthPixels());
        final double double6 = this.a(this.getHeightPixels());
        double1 -= this.facing.getOffsetX() * 0.46875;
        double3 -= this.facing.getOffsetZ() * 0.46875;
        double2 += double6;
        final Direction direction13 = this.facing.rotateYCounterclockwise();
        double1 += double5 * direction13.getOffsetX();
        double3 += double5 * direction13.getOffsetZ();
        this.x = double1;
        this.y = double2;
        this.z = double3;
        double double7 = this.getWidthPixels();
        double double8 = this.getHeightPixels();
        double double9 = this.getWidthPixels();
        if (this.facing.getAxis() == Direction.Axis.Z) {
            double9 = 1.0;
        }
        else {
            double7 = 1.0;
        }
        double7 /= 32.0;
        double8 /= 32.0;
        double9 /= 32.0;
        this.setBoundingBox(new BoundingBox(double1 - double7, double2 - double8, double3 - double9, double1 + double7, double2 + double8, double3 + double9));
    }
    
    private double a(final int integer) {
        return (integer % 32 == 0) ? 0.5 : 0.0;
    }
    
    @Override
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        if (this.e++ == 100 && !this.world.isClient) {
            this.e = 0;
            if (!this.removed && !this.i()) {
                this.remove();
                this.onBreak(null);
            }
        }
    }
    
    public boolean i() {
        if (!this.world.doesNotCollide(this)) {
            return false;
        }
        final int integer1 = Math.max(1, this.getWidthPixels() / 16);
        final int integer2 = Math.max(1, this.getHeightPixels() / 16);
        final BlockPos blockPos3 = this.blockPos.offset(this.facing.getOpposite());
        final Direction direction4 = this.facing.rotateYCounterclockwise();
        final BlockPos.Mutable mutable5 = new BlockPos.Mutable();
        for (int integer3 = 0; integer3 < integer1; ++integer3) {
            for (int integer4 = 0; integer4 < integer2; ++integer4) {
                final int integer5 = (integer1 - 1) / -2;
                final int integer6 = (integer2 - 1) / -2;
                mutable5.set(blockPos3).setOffset(direction4, integer3 + integer5).setOffset(Direction.UP, integer4 + integer6);
                final BlockState blockState10 = this.world.getBlockState(mutable5);
                if (!blockState10.getMaterial().isSolid() && !AbstractRedstoneGateBlock.isRedstoneGate(blockState10)) {
                    return false;
                }
            }
        }
        return this.world.getEntities(this, this.getBoundingBox(), AbstractDecorationEntity.PREDICATE).isEmpty();
    }
    
    @Override
    public boolean collides() {
        return true;
    }
    
    @Override
    public boolean handlePlayerAttack(final Entity entity) {
        return entity instanceof PlayerEntity && this.damage(DamageSource.player((PlayerEntity)entity), 0.0f);
    }
    
    @Override
    public Direction getHorizontalFacing() {
        return this.facing;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.removed && !this.world.isClient) {
            this.remove();
            this.scheduleVelocityUpdate();
            this.onBreak(source.getAttacker());
        }
        return true;
    }
    
    @Override
    public void move(final MovementType type, final Vec3d offset) {
        if (!this.world.isClient && !this.removed && offset.lengthSquared() > 0.0) {
            this.remove();
            this.onBreak(null);
        }
    }
    
    @Override
    public void addVelocity(final double deltaX, final double deltaY, final double deltaZ) {
        if (!this.world.isClient && !this.removed && deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 0.0) {
            this.remove();
            this.onBreak(null);
        }
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putByte("Facing", (byte)this.facing.getHorizontal());
        final BlockPos blockPos2 = this.getDecorationBlockPos();
        tag.putInt("TileX", blockPos2.getX());
        tag.putInt("TileY", blockPos2.getY());
        tag.putInt("TileZ", blockPos2.getZ());
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.blockPos = new BlockPos(tag.getInt("TileX"), tag.getInt("TileY"), tag.getInt("TileZ"));
        this.setFacing(Direction.fromHorizontal(tag.getByte("Facing")));
    }
    
    public abstract int getWidthPixels();
    
    public abstract int getHeightPixels();
    
    public abstract void onBreak(@Nullable final Entity arg1);
    
    public abstract void onPlace();
    
    @Override
    public ItemEntity dropStack(final ItemStack stack, final float float2) {
        final ItemEntity itemEntity3 = new ItemEntity(this.world, this.x + this.facing.getOffsetX() * 0.15f, this.y + float2, this.z + this.facing.getOffsetZ() * 0.15f, stack);
        itemEntity3.setToDefaultPickupDelay();
        this.world.spawnEntity(itemEntity3);
        return itemEntity3;
    }
    
    @Override
    protected boolean shouldSetPositionOnLoad() {
        return false;
    }
    
    @Override
    public void setPosition(final double x, final double y, final double z) {
        this.blockPos = new BlockPos(x, y, z);
        this.f();
        this.velocityDirty = true;
    }
    
    public BlockPos getDecorationBlockPos() {
        return this.blockPos;
    }
    
    @Override
    public float applyRotation(final BlockRotation blockRotation) {
        if (this.facing != null && this.facing.getAxis() != Direction.Axis.Y) {
            switch (blockRotation) {
                case ROT_180: {
                    this.facing = this.facing.getOpposite();
                    break;
                }
                case ROT_270: {
                    this.facing = this.facing.rotateYCounterclockwise();
                    break;
                }
                case ROT_90: {
                    this.facing = this.facing.rotateYClockwise();
                    break;
                }
            }
        }
        final float float2 = MathHelper.wrapDegrees(this.yaw);
        switch (blockRotation) {
            case ROT_180: {
                return float2 + 180.0f;
            }
            case ROT_270: {
                return float2 + 90.0f;
            }
            case ROT_90: {
                return float2 + 270.0f;
            }
            default: {
                return float2;
            }
        }
    }
    
    @Override
    public float applyMirror(final BlockMirror blockMirror) {
        return this.applyRotation(blockMirror.getRotation(this.facing));
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
    }
    
    @Override
    public void refreshSize() {
    }
    
    static {
        PREDICATE = (entity -> entity instanceof AbstractDecorationEntity);
    }
}
