package net.minecraft.client.render.entity.model;

import net.minecraft.entity.mob.HostileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T>
{
    public ZombieEntityModel() {
        this(0.0f, false);
    }
    
    public ZombieEntityModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, boolean2 ? 32 : 64);
    }
    
    protected ZombieEntityModel(final float scale, final float float2, final int textureWidth, final int integer4) {
        super(scale, float2, textureWidth, integer4);
    }
    
    @Override
    public boolean a(final T zombieEntity) {
        return zombieEntity.isAttacking();
    }
}
