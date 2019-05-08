package net.minecraft.client.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.resource.Resource;
import java.io.IOException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DyeColor;
import java.util.List;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BannerTexture extends AbstractTexture
{
    private static final Logger LOGGER;
    private final Identifier filename;
    private final List<String> patternNames;
    private final List<DyeColor> dyes;
    
    public BannerTexture(final Identifier filename, final List<String> patternNames, final List<DyeColor> list3) {
        this.filename = filename;
        this.patternNames = patternNames;
        this.dyes = list3;
    }
    
    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
        try (final Resource resource2 = resourceManager.getResource(this.filename);
             final NativeImage nativeImage4 = NativeImage.fromInputStream(resource2.getInputStream());
             final NativeImage nativeImage5 = new NativeImage(nativeImage4.getWidth(), nativeImage4.getHeight(), false)) {
            nativeImage5.copyFrom(nativeImage4);
            for (int integer8 = 0; integer8 < 17 && integer8 < this.patternNames.size() && integer8 < this.dyes.size(); ++integer8) {
                final String string9 = this.patternNames.get(integer8);
                if (string9 != null) {
                    try (final Resource resource3 = resourceManager.getResource(new Identifier(string9));
                         final NativeImage nativeImage6 = NativeImage.fromInputStream(resource3.getInputStream())) {
                        final int integer9 = this.dyes.get(integer8).getColorSwapped();
                        if (nativeImage6.getWidth() != nativeImage5.getWidth() || nativeImage6.getHeight() != nativeImage5.getHeight()) {
                            continue;
                        }
                        for (int integer10 = 0; integer10 < nativeImage6.getHeight(); ++integer10) {
                            for (int integer11 = 0; integer11 < nativeImage6.getWidth(); ++integer11) {
                                final int integer12 = nativeImage6.getPixelRGBA(integer11, integer10);
                                if ((integer12 & 0xFF000000) != 0x0) {
                                    final int integer13 = (integer12 & 0xFF) << 24 & 0xFF000000;
                                    final int integer14 = nativeImage4.getPixelRGBA(integer11, integer10);
                                    final int integer15 = MathHelper.multiplyColors(integer14, integer9) & 0xFFFFFF;
                                    nativeImage5.blendPixel(integer11, integer10, integer13 | integer15);
                                }
                            }
                        }
                    }
                }
            }
            TextureUtil.prepareImage(this.getGlId(), nativeImage5.getWidth(), nativeImage5.getHeight());
            GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
            nativeImage5.upload(0, 0, 0, false);
            GlStateManager.pixelTransfer(3357, 0.0f);
        }
        catch (IOException iOException2) {
            BannerTexture.LOGGER.error("Couldn't load layered color mask image", (Throwable)iOException2);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
