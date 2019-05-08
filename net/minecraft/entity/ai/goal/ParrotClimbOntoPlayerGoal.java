package net.minecraft.entity.ai.goal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;

public class ParrotClimbOntoPlayerGoal extends Goal
{
    private final TameableShoulderEntity parrot;
    private ServerPlayerEntity parrotOwner;
    private boolean mounted;
    
    public ParrotClimbOntoPlayerGoal(final TameableShoulderEntity parrot) {
        this.parrot = parrot;
    }
    
    @Override
    public boolean canStart() {
        final ServerPlayerEntity serverPlayerEntity1 = (ServerPlayerEntity)this.parrot.getOwner();
        final boolean boolean2 = serverPlayerEntity1 != null && !serverPlayerEntity1.isSpectator() && !serverPlayerEntity1.abilities.flying && !serverPlayerEntity1.isInsideWater();
        return !this.parrot.isSitting() && boolean2 && this.parrot.isReadyToSitOnPlayer();
    }
    
    @Override
    public boolean canStop() {
        return !this.mounted;
    }
    
    @Override
    public void start() {
        this.parrotOwner = (ServerPlayerEntity)this.parrot.getOwner();
        this.mounted = false;
    }
    
    @Override
    public void tick() {
        if (this.mounted || this.parrot.isSitting() || this.parrot.isLeashed()) {
            return;
        }
        if (this.parrot.getBoundingBox().intersects(this.parrotOwner.getBoundingBox())) {
            this.mounted = this.parrot.mountOnto(this.parrotOwner);
        }
    }
}
