package net.minecraft.client.texture;

import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.resource.ResourceManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NativeImageBackedTexture extends AbstractTexture implements AutoCloseable
{
    private NativeImage image;
    
    public NativeImageBackedTexture(final NativeImage nativeImage) {
        this.image = nativeImage;
        TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
        this.upload();
    }
    
    public NativeImageBackedTexture(final int integer1, final int integer2, final boolean boolean3) {
        this.image = new NativeImage(integer1, integer2, boolean3);
        TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
    }
    
    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
    }
    
    public void upload() {
        this.bindTexture();
        this.image.upload(0, 0, 0, false);
    }
    
    @Nullable
    public NativeImage getImage() {
        return this.image;
    }
    
    public void setImage(final NativeImage nativeImage) throws Exception {
        this.image.close();
        this.image = nativeImage;
    }
    
    @Override
    public void close() {
        this.image.close();
        this.clearGlId();
        this.image = null;
    }
}
