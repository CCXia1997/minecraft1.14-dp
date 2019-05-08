package net.minecraft.client.render.entity.model;

import net.minecraft.util.AbsoluteHand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ModelWithArms
{
    void setArmAngle(final float arg1, final AbsoluteHand arg2);
}
