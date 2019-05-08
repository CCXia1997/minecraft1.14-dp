package net.minecraft.entity.decoration;

import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.tag.BlockTags;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class LeadKnotEntity extends AbstractDecorationEntity
{
    public LeadKnotEntity(final EntityType<? extends LeadKnotEntity> type, final World world) {
        super(type, world);
    }
    
    public LeadKnotEntity(final World world, final BlockPos blockPos) {
        super(EntityType.LEASH_KNOT, world, blockPos);
        this.setPosition(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        final float float3 = 0.125f;
        final float float4 = 0.1875f;
        final float float5 = 0.25f;
        this.setBoundingBox(new BoundingBox(this.x - 0.1875, this.y - 0.25 + 0.125, this.z - 0.1875, this.x + 0.1875, this.y + 0.25 + 0.125, this.z + 0.1875));
        this.teleporting = true;
    }
    
    @Override
    public void setPosition(final double x, final double y, final double z) {
        super.setPosition(MathHelper.floor(x) + 0.5, MathHelper.floor(y) + 0.5, MathHelper.floor(z) + 0.5);
    }
    
    @Override
    protected void f() {
        this.x = this.blockPos.getX() + 0.5;
        this.y = this.blockPos.getY() + 0.5;
        this.z = this.blockPos.getZ() + 0.5;
    }
    
    public void setFacing(final Direction direction) {
    }
    
    @Override
    public int getWidthPixels() {
        return 9;
    }
    
    @Override
    public int getHeightPixels() {
        return 9;
    }
    
    @Override
    protected float getEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return -0.0625f;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        return distance < 1024.0;
    }
    
    @Override
    public void onBreak(@Nullable final Entity entity) {
        this.playSound(SoundEvents.fT, 1.0f, 1.0f);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        if (this.world.isClient) {
            return true;
        }
        boolean boolean3 = false;
        final double double4 = 7.0;
        final List<MobEntity> list6 = this.world.<MobEntity>getEntities(MobEntity.class, new BoundingBox(this.x - 7.0, this.y - 7.0, this.z - 7.0, this.x + 7.0, this.y + 7.0, this.z + 7.0));
        for (final MobEntity mobEntity8 : list6) {
            if (mobEntity8.getHoldingEntity() == player) {
                mobEntity8.attachLeash(this, true);
                boolean3 = true;
            }
        }
        if (!boolean3) {
            this.remove();
            if (player.abilities.creativeMode) {
                for (final MobEntity mobEntity8 : list6) {
                    if (mobEntity8.isLeashed() && mobEntity8.getHoldingEntity() == this) {
                        mobEntity8.detachLeash(true, false);
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean i() {
        return this.world.getBlockState(this.blockPos).getBlock().matches(BlockTags.G);
    }
    
    public static LeadKnotEntity getOrCreate(final World world, final BlockPos pos) {
        final int integer3 = pos.getX();
        final int integer4 = pos.getY();
        final int integer5 = pos.getZ();
        final List<LeadKnotEntity> list6 = world.<LeadKnotEntity>getEntities(LeadKnotEntity.class, new BoundingBox(integer3 - 1.0, integer4 - 1.0, integer5 - 1.0, integer3 + 1.0, integer4 + 1.0, integer5 + 1.0));
        for (final LeadKnotEntity leadKnotEntity8 : list6) {
            if (leadKnotEntity8.getDecorationBlockPos().equals(pos)) {
                return leadKnotEntity8;
            }
        }
        final LeadKnotEntity leadKnotEntity9 = new LeadKnotEntity(world, pos);
        world.spawnEntity(leadKnotEntity9);
        leadKnotEntity9.onPlace();
        return leadKnotEntity9;
    }
    
    @Override
    public void onPlace() {
        this.playSound(SoundEvents.fU, 1.0f, 1.0f);
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), 0, this.getDecorationBlockPos());
    }
}
