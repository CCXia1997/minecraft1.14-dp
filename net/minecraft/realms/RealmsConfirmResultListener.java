package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RealmsConfirmResultListener
{
    void confirmResult(final boolean arg1, final int arg2);
}
