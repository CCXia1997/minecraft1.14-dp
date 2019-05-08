package net.minecraft.entity.projectile;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;

public class TridentEntity extends ProjectileEntity
{
    private static final TrackedData<Byte> LOYALTY;
    private ItemStack tridentStack;
    private boolean dealtDamage;
    public int ar;
    
    public TridentEntity(final EntityType<? extends TridentEntity> type, final World world) {
        super(type, world);
        this.tridentStack = new ItemStack(Items.pu);
    }
    
    public TridentEntity(final World world, final LivingEntity livingEntity, final ItemStack itemStack) {
        super(EntityType.TRIDENT, livingEntity, world);
        this.tridentStack = new ItemStack(Items.pu);
        this.tridentStack = itemStack.copy();
        this.dataTracker.<Byte>set(TridentEntity.LOYALTY, (byte)EnchantmentHelper.getLoyalty(itemStack));
    }
    
    @Environment(EnvType.CLIENT)
    public TridentEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.TRIDENT, double2, double4, double6, world);
        this.tridentStack = new ItemStack(Items.pu);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(TridentEntity.LOYALTY, (Byte)0);
    }
    
    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        final Entity entity1 = this.getOwner();
        if ((this.dealtDamage || this.isNoClip()) && entity1 != null) {
            final int integer2 = this.dataTracker.<Byte>get(TridentEntity.LOYALTY);
            if (integer2 > 0 && !this.isOwnerAlive()) {
                if (!this.world.isClient && this.pickupType == PickupType.PICKUP) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.remove();
            }
            else if (integer2 > 0) {
                this.setNoClip(true);
                final Vec3d vec3d3 = new Vec3d(entity1.x - this.x, entity1.y + entity1.getStandingEyeHeight() - this.y, entity1.z - this.z);
                this.y += vec3d3.y * 0.015 * integer2;
                if (this.world.isClient) {
                    this.prevRenderY = this.y;
                }
                final double double4 = 0.05 * integer2;
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d3.normalize().multiply(double4)));
                if (this.ar == 0) {
                    this.playSound(SoundEvents.lH, 10.0f, 1.0f);
                }
                ++this.ar;
            }
        }
        super.tick();
    }
    
    private boolean isOwnerAlive() {
        final Entity entity1 = this.getOwner();
        return entity1 != null && entity1.isAlive() && (!(entity1 instanceof ServerPlayerEntity) || !entity1.isSpectator());
    }
    
    @Override
    protected ItemStack asItemStack() {
        return this.tridentStack.copy();
    }
    
    @Nullable
    @Override
    protected EntityHitResult getEntityCollision(final Vec3d vec3d1, final Vec3d vec3d2) {
        if (this.dealtDamage) {
            return null;
        }
        return super.getEntityCollision(vec3d1, vec3d2);
    }
    
    @Override
    protected void onEntityHit(final EntityHitResult entityHitResult) {
        final Entity entity2 = entityHitResult.getEntity();
        float float3 = 8.0f;
        if (entity2 instanceof LivingEntity) {
            final LivingEntity livingEntity4 = (LivingEntity)entity2;
            float3 += EnchantmentHelper.getAttackDamage(this.tridentStack, livingEntity4.getGroup());
        }
        final Entity entity3 = this.getOwner();
        final DamageSource damageSource5 = DamageSource.trident(this, (entity3 == null) ? this : entity3);
        this.dealtDamage = true;
        SoundEvent soundEvent6 = SoundEvents.lF;
        if (entity2.damage(damageSource5, float3) && entity2 instanceof LivingEntity) {
            final LivingEntity livingEntity5 = (LivingEntity)entity2;
            if (entity3 instanceof LivingEntity) {
                EnchantmentHelper.onUserDamaged(livingEntity5, entity3);
                EnchantmentHelper.onTargetDamaged((LivingEntity)entity3, livingEntity5);
            }
            this.onHit(livingEntity5);
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float float4 = 1.0f;
        if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.tridentStack)) {
            final BlockPos blockPos8 = entity2.getBlockPos();
            if (this.world.isSkyVisible(blockPos8)) {
                final LightningEntity lightningEntity9 = new LightningEntity(this.world, blockPos8.getX() + 0.5, blockPos8.getY(), blockPos8.getZ() + 0.5, false);
                lightningEntity9.setChanneller((entity3 instanceof ServerPlayerEntity) ? ((ServerPlayerEntity)entity3) : null);
                ((ServerWorld)this.world).addLightning(lightningEntity9);
                soundEvent6 = SoundEvents.lM;
                float4 = 5.0f;
            }
        }
        this.playSound(soundEvent6, float4, 1.0f);
    }
    
    @Override
    protected SoundEvent getSound() {
        return SoundEvents.lG;
    }
    
    @Override
    public void onPlayerCollision(final PlayerEntity playerEntity) {
        final Entity entity2 = this.getOwner();
        if (entity2 != null && entity2.getUuid() != playerEntity.getUuid()) {
            return;
        }
        super.onPlayerCollision(playerEntity);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("Trident", 10)) {
            this.tridentStack = ItemStack.fromTag(tag.getCompound("Trident"));
        }
        this.dealtDamage = tag.getBoolean("DealtDamage");
        this.dataTracker.<Byte>set(TridentEntity.LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.put("Trident", this.tridentStack.toTag(new CompoundTag()));
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }
    
    @Override
    protected void age() {
        final int integer1 = this.dataTracker.<Byte>get(TridentEntity.LOYALTY);
        if (this.pickupType != PickupType.PICKUP || integer1 <= 0) {
            super.age();
        }
    }
    
    @Override
    protected float getDragInWater() {
        return 0.99f;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderFrom(final double x, final double y, final double z) {
        return true;
    }
    
    static {
        LOYALTY = DataTracker.<Byte>registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
