package net.minecraft.entity.thrown;

import javax.annotation.Nullable;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class ThrownEnderpearlEntity extends ThrownItemEntity
{
    private LivingEntity owner;
    
    public ThrownEnderpearlEntity(final EntityType<? extends ThrownEnderpearlEntity> type, final World world) {
        super(type, world);
    }
    
    public ThrownEnderpearlEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.ENDER_PEARL, livingEntity, world);
        this.owner = livingEntity;
    }
    
    @Environment(EnvType.CLIENT)
    public ThrownEnderpearlEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.ENDER_PEARL, double2, double4, double6, world);
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.mg;
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        final LivingEntity livingEntity2 = this.getOwner();
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            final Entity entity3 = ((EntityHitResult)hitResult).getEntity();
            if (entity3 == this.owner) {
                return;
            }
            entity3.damage(DamageSource.thrownProjectile(this, livingEntity2), 0.0f);
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos3 = ((BlockHitResult)hitResult).getBlockPos();
            final BlockEntity blockEntity4 = this.world.getBlockEntity(blockPos3);
            if (blockEntity4 instanceof EndGatewayBlockEntity) {
                final EndGatewayBlockEntity endGatewayBlockEntity5 = (EndGatewayBlockEntity)blockEntity4;
                if (livingEntity2 != null) {
                    if (livingEntity2 instanceof ServerPlayerEntity) {
                        Criterions.ENTER_BLOCK.handle((ServerPlayerEntity)livingEntity2, this.world.getBlockState(blockPos3));
                    }
                    endGatewayBlockEntity5.tryTeleportingEntity(livingEntity2);
                    this.remove();
                    return;
                }
                endGatewayBlockEntity5.tryTeleportingEntity(this);
                return;
            }
        }
        for (int integer3 = 0; integer3 < 32; ++integer3) {
            this.world.addParticle(ParticleTypes.O, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
        if (!this.world.isClient) {
            if (livingEntity2 instanceof ServerPlayerEntity) {
                final ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)livingEntity2;
                if (serverPlayerEntity3.networkHandler.getConnection().isOpen() && serverPlayerEntity3.world == this.world && !serverPlayerEntity3.isSleeping()) {
                    if (this.random.nextFloat() < 0.05f && this.world.getGameRules().getBoolean("doMobSpawning")) {
                        final EndermiteEntity endermiteEntity4 = EntityType.ENDERMITE.create(this.world);
                        endermiteEntity4.setPlayerSpawned(true);
                        endermiteEntity4.setPositionAndAngles(livingEntity2.x, livingEntity2.y, livingEntity2.z, livingEntity2.yaw, livingEntity2.pitch);
                        this.world.spawnEntity(endermiteEntity4);
                    }
                    if (livingEntity2.hasVehicle()) {
                        livingEntity2.stopRiding();
                    }
                    livingEntity2.requestTeleport(this.x, this.y, this.z);
                    livingEntity2.fallDistance = 0.0f;
                    livingEntity2.damage(DamageSource.FALL, 5.0f);
                }
            }
            else if (livingEntity2 != null) {
                livingEntity2.requestTeleport(this.x, this.y, this.z);
                livingEntity2.fallDistance = 0.0f;
            }
            this.remove();
        }
    }
    
    @Override
    public void tick() {
        final LivingEntity livingEntity1 = this.getOwner();
        if (livingEntity1 != null && livingEntity1 instanceof PlayerEntity && !livingEntity1.isAlive()) {
            this.remove();
        }
        else {
            super.tick();
        }
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType newDimension) {
        if (this.owner.dimension != newDimension) {
            this.owner = null;
        }
        return super.changeDimension(newDimension);
    }
}
