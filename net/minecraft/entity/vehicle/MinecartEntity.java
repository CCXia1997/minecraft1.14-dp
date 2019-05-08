package net.minecraft.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class MinecartEntity extends AbstractMinecartEntity
{
    public MinecartEntity(final EntityType<?> type, final World world) {
        super(type, world);
    }
    
    public MinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.MINECART, world, double2, double4, double6);
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        if (player.isSneaking()) {
            return false;
        }
        if (this.hasPassengers()) {
            return true;
        }
        if (!this.world.isClient) {
            player.startRiding(this);
        }
        return true;
    }
    
    @Override
    public void onActivatorRail(final int x, final int y, final int z, final boolean boolean4) {
        if (boolean4) {
            if (this.hasPassengers()) {
                this.removeAllPassengers();
            }
            if (this.m() == 0) {
                this.d(-this.n());
                this.c(10);
                this.a(50.0f);
                this.scheduleVelocityUpdate();
            }
        }
    }
    
    @Override
    public Type getMinecartType() {
        return Type.a;
    }
}
