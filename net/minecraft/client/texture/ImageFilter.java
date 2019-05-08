package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ImageFilter
{
    NativeImage filterImage(final NativeImage arg1);
    
    void a();
}
