package net.minecraft.realms;

import net.minecraft.client.gui.Element;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class RealmsGuiEventListener
{
    public boolean mouseClicked(final double double1, final double double3, final int integer5) {
        return false;
    }
    
    public boolean mouseReleased(final double double1, final double double3, final int integer5) {
        return false;
    }
    
    public boolean mouseDragged(final double double1, final double double3, final int integer, final double double6, final double double8) {
        return false;
    }
    
    public boolean mouseScrolled(final double double1, final double double3, final double double5) {
        return false;
    }
    
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return false;
    }
    
    public boolean keyReleased(final int integer1, final int integer2, final int integer3) {
        return false;
    }
    
    public boolean charTyped(final char character, final int integer) {
        return false;
    }
    
    public abstract Element getProxy();
}
