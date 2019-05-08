package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RenderableGlyph extends Glyph
{
    int getWidth();
    
    int getHeight();
    
    void upload(final int arg1, final int arg2);
    
    boolean hasColor();
    
    float getOversample();
    
    default float getXMin() {
        return this.getBearingX();
    }
    
    default float getXMax() {
        return this.getXMin() + this.getWidth() / this.getOversample();
    }
    
    default float getYMin() {
        return this.getAscent();
    }
    
    default float getYMax() {
        return this.getYMin() + this.getHeight() / this.getOversample();
    }
    
    default float getAscent() {
        return 3.0f;
    }
}
