package net.minecraft.client.render.entity.model;

import net.minecraft.entity.mob.HostileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.GiantEntity;

@Environment(EnvType.CLIENT)
public class GiantEntityModel extends AbstractZombieModel<GiantEntity>
{
    public GiantEntityModel() {
        this(0.0f, false);
    }
    
    public GiantEntityModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, boolean2 ? 32 : 64);
    }
    
    @Override
    public boolean a(final GiantEntity giantEntity) {
        return false;
    }
}
