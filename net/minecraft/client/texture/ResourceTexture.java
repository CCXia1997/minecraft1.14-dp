package net.minecraft.client.texture;

import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import javax.annotation.Nullable;
import java.io.Closeable;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResourceTexture extends AbstractTexture
{
    private static final Logger LOGGER;
    protected final Identifier location;
    
    public ResourceTexture(final Identifier identifier) {
        this.location = identifier;
    }
    
    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
        try (final TextureData textureData2 = this.loadTextureData(resourceManager)) {
            boolean boolean4 = false;
            boolean boolean5 = false;
            textureData2.checkException();
            final TextureResourceMetadata textureResourceMetadata6 = textureData2.getMetadata();
            if (textureResourceMetadata6 != null) {
                boolean4 = textureResourceMetadata6.shouldBlur();
                boolean5 = textureResourceMetadata6.shouldClamp();
            }
            this.bindTexture();
            TextureUtil.prepareImage(this.getGlId(), 0, textureData2.getImage().getWidth(), textureData2.getImage().getHeight());
            textureData2.getImage().upload(0, 0, 0, 0, 0, textureData2.getImage().getWidth(), textureData2.getImage().getHeight(), boolean4, boolean5, false);
        }
    }
    
    protected TextureData loadTextureData(final ResourceManager resourceManager) {
        return TextureData.load(resourceManager, this.location);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public static class TextureData implements Closeable
    {
        private final TextureResourceMetadata metadata;
        private final NativeImage image;
        private final IOException exception;
        
        public TextureData(final IOException exception) {
            this.exception = exception;
            this.metadata = null;
            this.image = null;
        }
        
        public TextureData(@Nullable final TextureResourceMetadata metadata, final NativeImage image) {
            this.exception = null;
            this.metadata = metadata;
            this.image = image;
        }
        
        public static TextureData load(final ResourceManager resourceManager, final Identifier identifier) {
            try (final Resource resource3 = resourceManager.getResource(identifier)) {
                final NativeImage nativeImage5 = NativeImage.fromInputStream(resource3.getInputStream());
                TextureResourceMetadata textureResourceMetadata6 = null;
                try {
                    textureResourceMetadata6 = resource3.<TextureResourceMetadata>getMetadata((ResourceMetadataReader<TextureResourceMetadata>)TextureResourceMetadata.READER);
                }
                catch (RuntimeException runtimeException7) {
                    ResourceTexture.LOGGER.warn("Failed reading metadata of: {}", identifier, runtimeException7);
                }
                return new TextureData(textureResourceMetadata6, nativeImage5);
            }
            catch (IOException iOException3) {
                return new TextureData(iOException3);
            }
        }
        
        @Nullable
        public TextureResourceMetadata getMetadata() {
            return this.metadata;
        }
        
        public NativeImage getImage() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
            return this.image;
        }
        
        @Override
        public void close() {
            if (this.image != null) {
                this.image.close();
            }
        }
        
        public void checkException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}
