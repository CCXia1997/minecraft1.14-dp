package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractTexture implements Texture
{
    protected int glId;
    protected boolean bilinear;
    protected boolean mipmap;
    protected boolean oldBilinear;
    protected boolean oldMipmap;
    
    public AbstractTexture() {
        this.glId = -1;
    }
    
    public void setFilter(final boolean bilinear, final boolean boolean2) {
        this.bilinear = bilinear;
        this.mipmap = boolean2;
        int integer3;
        int integer4;
        if (bilinear) {
            integer3 = (boolean2 ? 9987 : 9729);
            integer4 = 9729;
        }
        else {
            integer3 = (boolean2 ? 9986 : 9728);
            integer4 = 9728;
        }
        GlStateManager.texParameter(3553, 10241, integer3);
        GlStateManager.texParameter(3553, 10240, integer4);
    }
    
    @Override
    public void pushFilter(final boolean bilinear, final boolean boolean2) {
        this.oldBilinear = this.bilinear;
        this.oldMipmap = this.mipmap;
        this.setFilter(bilinear, boolean2);
    }
    
    @Override
    public void popFilter() {
        this.setFilter(this.oldBilinear, this.oldMipmap);
    }
    
    @Override
    public int getGlId() {
        if (this.glId == -1) {
            this.glId = TextureUtil.generateTextureId();
        }
        return this.glId;
    }
    
    public void clearGlId() {
        if (this.glId != -1) {
            TextureUtil.releaseTextureId(this.glId);
            this.glId = -1;
        }
    }
}
