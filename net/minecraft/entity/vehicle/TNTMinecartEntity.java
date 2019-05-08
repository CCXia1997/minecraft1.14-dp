package net.minecraft.entity.vehicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.BlockTags;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class TNTMinecartEntity extends AbstractMinecartEntity
{
    private int fuseTicks;
    
    public TNTMinecartEntity(final EntityType<? extends TNTMinecartEntity> type, final World world) {
        super(type, world);
        this.fuseTicks = -1;
    }
    
    public TNTMinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.TNT_MINECART, world, double2, double4, double6);
        this.fuseTicks = -1;
    }
    
    @Override
    public Type getMinecartType() {
        return Type.d;
    }
    
    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.bG.getDefaultState();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.fuseTicks > 0) {
            --this.fuseTicks;
            this.world.addParticle(ParticleTypes.Q, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        }
        else if (this.fuseTicks == 0) {
            this.explode(Entity.squaredHorizontalLength(this.getVelocity()));
        }
        if (this.horizontalCollision) {
            final double double1 = Entity.squaredHorizontalLength(this.getVelocity());
            if (double1 >= 0.009999999776482582) {
                this.explode(double1);
            }
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        final Entity entity3 = source.getSource();
        if (entity3 instanceof ProjectileEntity) {
            final ProjectileEntity projectileEntity4 = (ProjectileEntity)entity3;
            if (projectileEntity4.isOnFire()) {
                this.explode(projectileEntity4.getVelocity().lengthSquared());
            }
        }
        return super.damage(source, amount);
    }
    
    @Override
    public void dropItems(final DamageSource damageSource) {
        final double double2 = Entity.squaredHorizontalLength(this.getVelocity());
        if (damageSource.isFire() || damageSource.isExplosive() || double2 >= 0.009999999776482582) {
            if (this.fuseTicks < 0) {
                this.prime();
                this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
            }
            return;
        }
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItem(Blocks.bG);
        }
    }
    
    protected void explode(final double double1) {
        if (!this.world.isClient) {
            double double2 = Math.sqrt(double1);
            if (double2 > 5.0) {
                double2 = 5.0;
            }
            this.world.createExplosion(this, this.x, this.y, this.z, (float)(4.0 + this.random.nextDouble() * 1.5 * double2), Explosion.DestructionType.b);
            this.remove();
        }
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        if (fallDistance >= 3.0f) {
            final float float3 = fallDistance / 10.0f;
            this.explode(float3 * float3);
        }
        super.handleFallDamage(fallDistance, damageMultiplier);
    }
    
    @Override
    public void onActivatorRail(final int x, final int y, final int z, final boolean boolean4) {
        if (boolean4 && this.fuseTicks < 0) {
            this.prime();
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 10) {
            this.prime();
        }
        else {
            super.handleStatus(status);
        }
    }
    
    public void prime() {
        this.fuseTicks = 80;
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)10);
            if (!this.isSilent()) {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.lD, SoundCategory.e, 1.0f, 1.0f);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public int getFuseTicks() {
        return this.fuseTicks;
    }
    
    public boolean isPrimed() {
        return this.fuseTicks > -1;
    }
    
    @Override
    public float getEffectiveExplosionResistance(final Explosion explosion, final BlockView world, final BlockPos pos, final BlockState blockState, final FluidState state, final float float6) {
        if (this.isPrimed() && (blockState.matches(BlockTags.B) || world.getBlockState(pos.up()).matches(BlockTags.B))) {
            return 0.0f;
        }
        return super.getEffectiveExplosionResistance(explosion, world, pos, blockState, state, float6);
    }
    
    @Override
    public boolean canExplosionDestroyBlock(final Explosion explosion, final BlockView world, final BlockPos pos, final BlockState state, final float float5) {
        return (!this.isPrimed() || (!state.matches(BlockTags.B) && !world.getBlockState(pos.up()).matches(BlockTags.B))) && super.canExplosionDestroyBlock(explosion, world, pos, state, float5);
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("TNTFuse", 99)) {
            this.fuseTicks = tag.getInt("TNTFuse");
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("TNTFuse", this.fuseTicks);
    }
}
