package net.minecraft.client.texture;

import java.util.concurrent.Executor;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import net.minecraft.resource.ResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Texture
{
    void pushFilter(final boolean arg1, final boolean arg2);
    
    void popFilter();
    
    void load(final ResourceManager arg1) throws IOException;
    
    int getGlId();
    
    default void bindTexture() {
        GlStateManager.bindTexture(this.getGlId());
    }
    
    default void registerTexture(final TextureManager textureManager, final ResourceManager resourceManager, final Identifier identifier, final Executor executor) {
        textureManager.registerTexture(identifier, this);
    }
}
