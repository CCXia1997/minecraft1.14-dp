package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface WindowEventHandler
{
    void onWindowFocusChanged(final boolean arg1);
    
    void updateDisplay(final boolean arg1);
    
    void onResolutionChanged();
}
