package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface JumpingMount
{
    @Environment(EnvType.CLIENT)
    void setJumpStrength(final int arg1);
    
    boolean canJump();
    
    void startJumping(final int arg1);
    
    void stopJumping();
}
