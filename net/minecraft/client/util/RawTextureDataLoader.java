package net.minecraft.client.util;

import java.io.IOException;
import net.minecraft.resource.Resource;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RawTextureDataLoader
{
    @Deprecated
    public static int[] loadRawTextureData(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
        try (final Resource resource3 = resourceManager.getResource(identifier);
             final NativeImage nativeImage5 = NativeImage.fromInputStream(resource3.getInputStream())) {
            return nativeImage5.makePixelArray();
        }
    }
}
