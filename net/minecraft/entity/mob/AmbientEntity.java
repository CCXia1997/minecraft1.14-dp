package net.minecraft.entity.mob;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public abstract class AmbientEntity extends MobEntity
{
    protected AmbientEntity(final EntityType<? extends AmbientEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return false;
    }
}
