package net.minecraft.realms.pluginapi;

import net.minecraft.realms.RealmsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface LoadedRealmsPlugin
{
    RealmsScreen getMainScreen(final RealmsScreen arg1);
    
    RealmsScreen getNotificationsScreen(final RealmsScreen arg1);
}
