package net.minecraft.client.font;

import net.minecraft.util.SystemUtil;
import net.minecraft.client.texture.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum BlankGlyph implements RenderableGlyph
{
    INSTANCE;
    
    private static final NativeImage IMAGE;
    
    @Override
    public int getWidth() {
        return 5;
    }
    
    @Override
    public int getHeight() {
        return 8;
    }
    
    @Override
    public float getAdvance() {
        return 6.0f;
    }
    
    @Override
    public float getOversample() {
        return 1.0f;
    }
    
    @Override
    public void upload(final int x, final int y) {
        BlankGlyph.IMAGE.upload(0, x, y, false);
    }
    
    @Override
    public boolean hasColor() {
        return true;
    }
    
    static {
        int integer2;
        int integer3;
        boolean boolean4;
        IMAGE = SystemUtil.<NativeImage>consume(new NativeImage(NativeImage.Format.a, 5, 8, false), nativeImage -> {
            for (integer2 = 0; integer2 < 8; ++integer2) {
                for (integer3 = 0; integer3 < 5; ++integer3) {
                    boolean4 = (integer3 == 0 || integer3 + 1 == 5 || integer2 == 0 || integer2 + 1 == 8);
                    nativeImage.setPixelRGBA(integer3, integer2, boolean4 ? -1 : 0);
                }
            }
            nativeImage.untrack();
        });
    }
}
