package net.minecraft.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Lazy;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MissingSprite extends Sprite
{
    private static final Identifier MISSINGNO;
    @Nullable
    private static NativeImageBackedTexture TEXTURE;
    private static final Lazy<NativeImage> IMAGE;
    
    private MissingSprite() {
        super(MissingSprite.MISSINGNO, 16, 16);
        this.images = new NativeImage[] { MissingSprite.IMAGE.get() };
    }
    
    public static MissingSprite getMissingSprite() {
        return new MissingSprite();
    }
    
    public static Identifier getMissingSpriteId() {
        return MissingSprite.MISSINGNO;
    }
    
    @Override
    public void destroy() {
        for (int integer1 = 1; integer1 < this.images.length; ++integer1) {
            this.images[integer1].close();
        }
        this.images = new NativeImage[] { MissingSprite.IMAGE.get() };
    }
    
    public static NativeImageBackedTexture getMissingSpriteTexture() {
        if (MissingSprite.TEXTURE == null) {
            MissingSprite.TEXTURE = new NativeImageBackedTexture(MissingSprite.IMAGE.get());
            MinecraftClient.getInstance().getTextureManager().registerTexture(MissingSprite.MISSINGNO, MissingSprite.TEXTURE);
        }
        return MissingSprite.TEXTURE;
    }
    
    static {
        MISSINGNO = new Identifier("missingno");
        final NativeImage nativeImage1;
        final int integer2;
        final int integer3;
        int integer4;
        int integer5;
        IMAGE = new Lazy<NativeImage>(() -> {
            nativeImage1 = new NativeImage(16, 16, false);
            integer2 = -16777216;
            integer3 = -524040;
            for (integer4 = 0; integer4 < 16; ++integer4) {
                for (integer5 = 0; integer5 < 16; ++integer5) {
                    if (integer4 < 8 ^ integer5 < 8) {
                        nativeImage1.setPixelRGBA(integer5, integer4, -524040);
                    }
                    else {
                        nativeImage1.setPixelRGBA(integer5, integer4, -16777216);
                    }
                }
            }
            nativeImage1.untrack();
            return nativeImage1;
        });
    }
}
