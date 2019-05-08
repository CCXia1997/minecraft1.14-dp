package net.minecraft.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.entity.data.TrackedData;

public class PrimedTntEntity extends Entity
{
    private static final TrackedData<Integer> FUSE;
    @Nullable
    private LivingEntity causingEntity;
    private int fuseTimer;
    
    public PrimedTntEntity(final EntityType<? extends PrimedTntEntity> type, final World world) {
        super(type, world);
        this.fuseTimer = 80;
        this.i = true;
    }
    
    public PrimedTntEntity(final World world, final double x, final double y, final double double6, @Nullable final LivingEntity livingEntity8) {
        this(EntityType.TNT, world);
        this.setPosition(x, y, double6);
        final double double7 = world.random.nextDouble() * 6.2831854820251465;
        this.setVelocity(-Math.sin(double7) * 0.02, 0.20000000298023224, -Math.cos(double7) * 0.02);
        this.setFuse(80);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = double6;
        this.causingEntity = livingEntity8;
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<Integer>startTracking(PrimedTntEntity.FUSE, 80);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    public boolean collides() {
        return !this.removed;
    }
    
    @Override
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        if (!this.isUnaffectedByGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.a, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }
        --this.fuseTimer;
        if (this.fuseTimer <= 0) {
            this.remove();
            if (!this.world.isClient) {
                this.explode();
            }
        }
        else {
            this.ax();
            this.world.addParticle(ParticleTypes.Q, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    private void explode() {
        final float float1 = 4.0f;
        this.world.createExplosion(this, this.x, this.y + this.getHeight() / 16.0f, this.z, 4.0f, Explosion.DestructionType.b);
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        tag.putShort("Fuse", (short)this.getFuseTimer());
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        this.setFuse(tag.getShort("Fuse"));
    }
    
    @Nullable
    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }
    
    @Override
    protected float getEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.0f;
    }
    
    public void setFuse(final int integer) {
        this.dataTracker.<Integer>set(PrimedTntEntity.FUSE, integer);
        this.fuseTimer = integer;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (PrimedTntEntity.FUSE.equals(data)) {
            this.fuseTimer = this.getFuse();
        }
    }
    
    public int getFuse() {
        return this.dataTracker.<Integer>get(PrimedTntEntity.FUSE);
    }
    
    public int getFuseTimer() {
        return this.fuseTimer;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    static {
        FUSE = DataTracker.<Integer>registerData(PrimedTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
