package net.minecraft.entity.passive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public abstract class TameableShoulderEntity extends TameableEntity
{
    private int ticks;
    
    protected TameableShoulderEntity(final EntityType<? extends TameableShoulderEntity> type, final World world) {
        super(type, world);
    }
    
    public boolean mountOnto(final ServerPlayerEntity serverPlayerEntity) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("id", this.getSavedEntityId());
        this.toTag(compoundTag2);
        if (serverPlayerEntity.addShoulderEntity(compoundTag2)) {
            this.remove();
            return true;
        }
        return false;
    }
    
    @Override
    public void tick() {
        ++this.ticks;
        super.tick();
    }
    
    public boolean isReadyToSitOnPlayer() {
        return this.ticks > 100;
    }
}
