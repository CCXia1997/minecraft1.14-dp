package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.EntityPose;
import net.minecraft.network.Packet;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Entity;

public class EnderDragonPart extends Entity
{
    public final EnderDragonEntity owner;
    public final String name;
    private final EntitySize d;
    
    public EnderDragonPart(final EnderDragonEntity enderDragonEntity, final String name, final float width, final float float4) {
        super(enderDragonEntity.getType(), enderDragonEntity.world);
        this.d = EntitySize.resizeable(width, float4);
        this.refreshSize();
        this.owner = enderDragonEntity;
        this.name = name;
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
    }
    
    @Override
    public boolean collides() {
        return true;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return !this.isInvulnerableTo(source) && this.owner.damagePart(this, source, amount);
    }
    
    @Override
    public boolean isPartOf(final Entity entity) {
        return this == entity || this.owner == entity;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        return this.d;
    }
}
