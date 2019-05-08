package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

public interface ProgressListener
{
    void a(final TextComponent arg1);
    
    @Environment(EnvType.CLIENT)
    void b(final TextComponent arg1);
    
    void c(final TextComponent arg1);
    
    void progressStagePercentage(final int arg1);
    
    @Environment(EnvType.CLIENT)
    void setDone();
}
