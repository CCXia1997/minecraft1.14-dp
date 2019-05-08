package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;

public class PlayerAbilities
{
    public boolean invulnerable;
    public boolean flying;
    public boolean allowFlying;
    public boolean creativeMode;
    public boolean allowModifyWorld;
    private float flySpeed;
    private float walkSpeed;
    
    public PlayerAbilities() {
        this.allowModifyWorld = true;
        this.flySpeed = 0.05f;
        this.walkSpeed = 0.1f;
    }
    
    public void serialize(final CompoundTag compoundTag) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putBoolean("invulnerable", this.invulnerable);
        compoundTag2.putBoolean("flying", this.flying);
        compoundTag2.putBoolean("mayfly", this.allowFlying);
        compoundTag2.putBoolean("instabuild", this.creativeMode);
        compoundTag2.putBoolean("mayBuild", this.allowModifyWorld);
        compoundTag2.putFloat("flySpeed", this.flySpeed);
        compoundTag2.putFloat("walkSpeed", this.walkSpeed);
        compoundTag.put("abilities", compoundTag2);
    }
    
    public void deserialize(final CompoundTag compoundTag) {
        if (compoundTag.containsKey("abilities", 10)) {
            final CompoundTag compoundTag2 = compoundTag.getCompound("abilities");
            this.invulnerable = compoundTag2.getBoolean("invulnerable");
            this.flying = compoundTag2.getBoolean("flying");
            this.allowFlying = compoundTag2.getBoolean("mayfly");
            this.creativeMode = compoundTag2.getBoolean("instabuild");
            if (compoundTag2.containsKey("flySpeed", 99)) {
                this.flySpeed = compoundTag2.getFloat("flySpeed");
                this.walkSpeed = compoundTag2.getFloat("walkSpeed");
            }
            if (compoundTag2.containsKey("mayBuild", 1)) {
                this.allowModifyWorld = compoundTag2.getBoolean("mayBuild");
            }
        }
    }
    
    public float getFlySpeed() {
        return this.flySpeed;
    }
    
    @Environment(EnvType.CLIENT)
    public void setFlySpeed(final float float1) {
        this.flySpeed = float1;
    }
    
    public float getWalkSpeed() {
        return this.walkSpeed;
    }
    
    @Environment(EnvType.CLIENT)
    public void setWalkSpeed(final float float1) {
        this.walkSpeed = float1;
    }
}
