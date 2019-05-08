package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum AchievementObtainedStatus
{
    a(0), 
    b(1);
    
    private final int spriteIndex;
    
    private AchievementObtainedStatus(final int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }
    
    public int getSpriteIndex() {
        return this.spriteIndex;
    }
}
