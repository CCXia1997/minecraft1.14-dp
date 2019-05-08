package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinRemappingImageFilter implements ImageFilter
{
    @Override
    public NativeImage filterImage(NativeImage nativeImage) {
        final boolean boolean2 = nativeImage.getHeight() == 32;
        if (boolean2) {
            final NativeImage nativeImage2 = new NativeImage(64, 64, true);
            nativeImage2.copyFrom(nativeImage);
            nativeImage.close();
            nativeImage = nativeImage2;
            nativeImage.fillRGBA(0, 32, 64, 32, 0);
            nativeImage.a(4, 16, 16, 32, 4, 4, true, false);
            nativeImage.a(8, 16, 16, 32, 4, 4, true, false);
            nativeImage.a(0, 20, 24, 32, 4, 12, true, false);
            nativeImage.a(4, 20, 16, 32, 4, 12, true, false);
            nativeImage.a(8, 20, 8, 32, 4, 12, true, false);
            nativeImage.a(12, 20, 16, 32, 4, 12, true, false);
            nativeImage.a(44, 16, -8, 32, 4, 4, true, false);
            nativeImage.a(48, 16, -8, 32, 4, 4, true, false);
            nativeImage.a(40, 20, 0, 32, 4, 12, true, false);
            nativeImage.a(44, 20, -8, 32, 4, 12, true, false);
            nativeImage.a(48, 20, -16, 32, 4, 12, true, false);
            nativeImage.a(52, 20, -8, 32, 4, 12, true, false);
        }
        b(nativeImage, 0, 0, 32, 16);
        if (boolean2) {
            a(nativeImage, 32, 0, 64, 32);
        }
        b(nativeImage, 0, 16, 64, 32);
        b(nativeImage, 16, 48, 48, 64);
        return nativeImage;
    }
    
    @Override
    public void a() {
    }
    
    private static void a(final NativeImage nativeImage, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = integer2; integer6 < integer4; ++integer6) {
            for (int integer7 = integer3; integer7 < integer5; ++integer7) {
                final int integer8 = nativeImage.getPixelRGBA(integer6, integer7);
                if ((integer8 >> 24 & 0xFF) < 128) {
                    return;
                }
            }
        }
        for (int integer6 = integer2; integer6 < integer4; ++integer6) {
            for (int integer7 = integer3; integer7 < integer5; ++integer7) {
                nativeImage.setPixelRGBA(integer6, integer7, nativeImage.getPixelRGBA(integer6, integer7) & 0xFFFFFF);
            }
        }
    }
    
    private static void b(final NativeImage nativeImage, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = integer2; integer6 < integer4; ++integer6) {
            for (int integer7 = integer3; integer7 < integer5; ++integer7) {
                nativeImage.setPixelRGBA(integer6, integer7, nativeImage.getPixelRGBA(integer6, integer7) | 0xFF000000);
            }
        }
    }
}
