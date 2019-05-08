package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Glyph
{
    float getAdvance();
    
    default float getAdvance(final boolean isBold) {
        return this.getAdvance() + (isBold ? this.getBoldOffset() : 0.0f);
    }
    
    default float getBearingX() {
        return 0.0f;
    }
    
    default float getBoldOffset() {
        return 1.0f;
    }
    
    default float getShadowOffset() {
        return 1.0f;
    }
}
