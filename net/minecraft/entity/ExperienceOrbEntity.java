package net.minecraft.entity;

import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.Packet;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tag.FluidTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class ExperienceOrbEntity extends Entity
{
    public int renderTicks;
    public int orbAge;
    public int pickupDelay;
    private int health;
    private int amount;
    private PlayerEntity target;
    private int ar;
    
    public ExperienceOrbEntity(final World world, final double x, final double y, final double z, final int amount) {
        this(EntityType.EXPERIENCE_ORB, world);
        this.setPosition(x, y, z);
        this.yaw = (float)(this.random.nextDouble() * 360.0);
        this.setVelocity((this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0);
        this.amount = amount;
    }
    
    public ExperienceOrbEntity(final EntityType<? extends ExperienceOrbEntity> type, final World world) {
        super(type, world);
        this.health = 5;
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        float float1 = 0.5f;
        float1 = MathHelper.clamp(float1, 0.0f, 1.0f);
        final int integer2 = super.getLightmapCoordinates();
        int integer3 = integer2 & 0xFF;
        final int integer4 = integer2 >> 16 & 0xFF;
        integer3 += (int)(float1 * 15.0f * 16.0f);
        if (integer3 > 240) {
            integer3 = 240;
        }
        return integer3 | integer4 << 16;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.pickupDelay > 0) {
            --this.pickupDelay;
        }
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        if (this.isInFluid(FluidTags.a)) {
            this.applyWaterMovement();
        }
        else if (!this.isUnaffectedByGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
        }
        if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.b)) {
            this.setVelocity((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.20000000298023224, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            this.playSound(SoundEvents.dF, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }
        if (!this.world.doesNotCollide(this.getBoundingBox())) {
            this.pushOutOfBlocks(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
        }
        final double double1 = 8.0;
        if (this.ar < this.renderTicks - 20 + this.getEntityId() % 100) {
            if (this.target == null || this.target.squaredDistanceTo(this) > 64.0) {
                this.target = this.world.getClosestPlayer(this, 8.0);
            }
            this.ar = this.renderTicks;
        }
        if (this.target != null && this.target.isSpectator()) {
            this.target = null;
        }
        if (this.target != null) {
            final Vec3d vec3d3 = new Vec3d(this.target.x - this.x, this.target.y + this.target.getStandingEyeHeight() / 2.0 - this.y, this.target.z - this.z);
            final double double2 = vec3d3.lengthSquared();
            if (double2 < 64.0) {
                final double double3 = 1.0 - Math.sqrt(double2) / 8.0;
                this.setVelocity(this.getVelocity().add(vec3d3.normalize().multiply(double3 * double3 * 0.1)));
            }
        }
        this.move(MovementType.a, this.getVelocity());
        float float3 = 0.98f;
        if (this.onGround) {
            float3 = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.98f;
        }
        this.setVelocity(this.getVelocity().multiply(float3, 0.98, float3));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
        }
        ++this.renderTicks;
        ++this.orbAge;
        if (this.orbAge >= 6000) {
            this.remove();
        }
    }
    
    private void applyWaterMovement() {
        final Vec3d vec3d1 = this.getVelocity();
        this.setVelocity(vec3d1.x * 0.9900000095367432, Math.min(vec3d1.y + 5.000000237487257E-4, 0.05999999865889549), vec3d1.z * 0.9900000095367432);
    }
    
    @Override
    protected void onSwimmingStart() {
    }
    
    @Override
    protected void burn(final int integer) {
        this.damage(DamageSource.IN_FIRE, (float)integer);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        this.health -= (int)amount;
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putShort("Health", (short)this.health);
        tag.putShort("Age", (short)this.orbAge);
        tag.putShort("Value", (short)this.amount);
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.health = tag.getShort("Health");
        this.orbAge = tag.getShort("Age");
        this.amount = tag.getShort("Value");
    }
    
    @Override
    public void onPlayerCollision(final PlayerEntity playerEntity) {
        if (this.world.isClient) {
            return;
        }
        if (this.pickupDelay == 0 && playerEntity.experienceOrbPickupDelay == 0) {
            playerEntity.experienceOrbPickupDelay = 2;
            playerEntity.sendPickup(this, 1);
            final Map.Entry<EquipmentSlot, ItemStack> entry2 = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.J, playerEntity);
            if (entry2 != null) {
                final ItemStack itemStack3 = entry2.getValue();
                if (!itemStack3.isEmpty() && itemStack3.isDamaged()) {
                    final int integer4 = Math.min(this.c(this.amount), itemStack3.getDamage());
                    this.amount -= this.b(integer4);
                    itemStack3.setDamage(itemStack3.getDamage() - integer4);
                }
            }
            if (this.amount > 0) {
                playerEntity.addExperience(this.amount);
            }
            this.remove();
        }
    }
    
    private int b(final int integer) {
        return integer / 2;
    }
    
    private int c(final int integer) {
        return integer * 2;
    }
    
    public int getExperienceAmount() {
        return this.amount;
    }
    
    @Environment(EnvType.CLIENT)
    public int getOrbSize() {
        if (this.amount >= 2477) {
            return 10;
        }
        if (this.amount >= 1237) {
            return 9;
        }
        if (this.amount >= 617) {
            return 8;
        }
        if (this.amount >= 307) {
            return 7;
        }
        if (this.amount >= 149) {
            return 6;
        }
        if (this.amount >= 73) {
            return 5;
        }
        if (this.amount >= 37) {
            return 4;
        }
        if (this.amount >= 17) {
            return 3;
        }
        if (this.amount >= 7) {
            return 2;
        }
        if (this.amount >= 3) {
            return 1;
        }
        return 0;
    }
    
    public static int roundToOrbSize(final int value) {
        if (value >= 2477) {
            return 2477;
        }
        if (value >= 1237) {
            return 1237;
        }
        if (value >= 617) {
            return 617;
        }
        if (value >= 307) {
            return 307;
        }
        if (value >= 149) {
            return 149;
        }
        if (value >= 73) {
            return 73;
        }
        if (value >= 37) {
            return 37;
        }
        if (value >= 17) {
            return 17;
        }
        if (value >= 7) {
            return 7;
        }
        if (value >= 3) {
            return 3;
        }
        return 1;
    }
    
    @Override
    public boolean canPlayerAttack() {
        return false;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new ExperienceOrbSpawnS2CPacket(this);
    }
}
