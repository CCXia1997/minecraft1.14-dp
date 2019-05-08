package net.minecraft.client.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.resource.Resource;
import java.util.Iterator;
import java.io.IOException;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LayeredTexture extends AbstractTexture
{
    private static final Logger LOGGER;
    public final List<String> locations;
    
    public LayeredTexture(final String... arr) {
        this.locations = Lists.<String>newArrayList(arr);
        if (this.locations.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }
    
    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
        final Iterator<String> iterator2 = this.locations.iterator();
        final String string3 = iterator2.next();
        try (final Resource resource4 = resourceManager.getResource(new Identifier(string3));
             final NativeImage nativeImage6 = NativeImage.fromInputStream(resource4.getInputStream())) {
            while (iterator2.hasNext()) {
                final String string4 = iterator2.next();
                if (string4 == null) {
                    continue;
                }
                try (final Resource resource5 = resourceManager.getResource(new Identifier(string4));
                     final NativeImage nativeImage7 = NativeImage.fromInputStream(resource5.getInputStream())) {
                    for (int integer13 = 0; integer13 < nativeImage7.getHeight(); ++integer13) {
                        for (int integer14 = 0; integer14 < nativeImage7.getWidth(); ++integer14) {
                            nativeImage6.blendPixel(integer14, integer13, nativeImage7.getPixelRGBA(integer14, integer13));
                        }
                    }
                }
            }
            TextureUtil.prepareImage(this.getGlId(), nativeImage6.getWidth(), nativeImage6.getHeight());
            nativeImage6.upload(0, 0, 0, false);
        }
        catch (IOException iOException4) {
            LayeredTexture.LOGGER.error("Couldn't load layered image", (Throwable)iOException4);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
