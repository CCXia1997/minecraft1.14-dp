package net.minecraft.client.sortme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Snooper
{
    void setFixedData(final String arg1, final Object arg2);
}
