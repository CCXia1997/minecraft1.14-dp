package net.minecraft.client.gui.hud.spectator;

import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommand
{
    void use(final SpectatorMenu arg1);
    
    TextComponent getName();
    
    void renderIcon(final float arg1, final int arg2);
    
    boolean enabled();
}
