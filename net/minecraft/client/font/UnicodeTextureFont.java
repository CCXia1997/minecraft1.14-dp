package net.minecraft.client.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.util.function.Function;
import net.minecraft.resource.Resource;
import java.util.Arrays;
import java.io.IOException;
import com.google.common.collect.Maps;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.resource.ResourceManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UnicodeTextureFont implements Font
{
    private static final Logger LOGGER;
    private final ResourceManager resourceManager;
    private final byte[] sizes;
    private final String template;
    private final Map<Identifier, NativeImage> images;
    
    public UnicodeTextureFont(final ResourceManager resourceManager, final byte[] sizes, final String template) {
        this.images = Maps.newHashMap();
        this.resourceManager = resourceManager;
        this.sizes = sizes;
        this.template = template;
        for (int integer4 = 0; integer4 < 256; ++integer4) {
            final char character5 = (char)(integer4 * 256);
            final Identifier identifier6 = this.getGlyphId(character5);
            try (final Resource resource7 = this.resourceManager.getResource(identifier6);
                 final NativeImage nativeImage9 = NativeImage.fromInputStream(NativeImage.Format.a, resource7.getInputStream())) {
                if (nativeImage9.getWidth() == 256 && nativeImage9.getHeight() == 256) {
                    for (int integer5 = 0; integer5 < 256; ++integer5) {
                        final byte byte12 = sizes[character5 + integer5];
                        if (byte12 != 0 && a(byte12) > b(byte12)) {
                            sizes[character5 + integer5] = 0;
                        }
                    }
                    continue;
                }
            }
            catch (IOException ex) {}
            Arrays.fill(sizes, character5, character5 + '\u0100', (byte)0);
        }
    }
    
    @Override
    public void close() {
        this.images.values().forEach(NativeImage::close);
    }
    
    private Identifier getGlyphId(final char character) {
        final Identifier identifier2 = new Identifier(String.format(this.template, String.format("%02x", character / '\u0100')));
        return new Identifier(identifier2.getNamespace(), "textures/" + identifier2.getPath());
    }
    
    @Nullable
    @Override
    public RenderableGlyph getGlyph(final char character) {
        final byte byte2 = this.sizes[character];
        if (byte2 != 0) {
            final NativeImage nativeImage3 = this.images.computeIfAbsent(this.getGlyphId(character), this::getGlyphImage);
            if (nativeImage3 != null) {
                final int integer4 = a(byte2);
                return new UnicodeTextureGlyph(character % '\u0010' * 16 + integer4, (character & '\u00ff') / 16 * 16, b(byte2) - integer4, 16, nativeImage3);
            }
        }
        return null;
    }
    
    @Nullable
    private NativeImage getGlyphImage(final Identifier glyphId) {
        try (final Resource resource2 = this.resourceManager.getResource(glyphId)) {
            return NativeImage.fromInputStream(NativeImage.Format.a, resource2.getInputStream());
        }
        catch (IOException iOException2) {
            UnicodeTextureFont.LOGGER.error("Couldn't load texture {}", glyphId, iOException2);
            return null;
        }
    }
    
    private static int a(final byte byte1) {
        return byte1 >> 4 & 0xF;
    }
    
    private static int b(final byte byte1) {
        return (byte1 & 0xF) + 1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Loader implements FontLoader
    {
        private final Identifier sizes;
        private final String template;
        
        public Loader(final Identifier sizes, final String template) {
            this.sizes = sizes;
            this.template = template;
        }
        
        public static FontLoader fromJson(final JsonObject jsonObject) {
            return new Loader(new Identifier(JsonHelper.getString(jsonObject, "sizes")), JsonHelper.getString(jsonObject, "template"));
        }
        
        @Nullable
        @Override
        public Font load(final ResourceManager resourceManager) {
            try (final Resource resource2 = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes)) {
                final byte[] arr4 = new byte[65536];
                resource2.getInputStream().read(arr4);
                return new UnicodeTextureFont(resourceManager, arr4, this.template);
            }
            catch (IOException iOException2) {
                UnicodeTextureFont.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.sizes);
                return null;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class UnicodeTextureGlyph implements RenderableGlyph
    {
        private final int width;
        private final int height;
        private final int unpackSkipPixels;
        private final int unpackSkipRows;
        private final NativeImage image;
        
        private UnicodeTextureGlyph(final int unpackSkipPixels, final int unpackSkipRows, final int width, final int height, final NativeImage image) {
            this.width = width;
            this.height = height;
            this.unpackSkipPixels = unpackSkipPixels;
            this.unpackSkipRows = unpackSkipRows;
            this.image = image;
        }
        
        @Override
        public float getOversample() {
            return 2.0f;
        }
        
        @Override
        public int getWidth() {
            return this.width;
        }
        
        @Override
        public int getHeight() {
            return this.height;
        }
        
        @Override
        public float getAdvance() {
            return (float)(this.width / 2 + 1);
        }
        
        @Override
        public void upload(final int x, final int y) {
            this.image.upload(0, x, y, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false);
        }
        
        @Override
        public boolean hasColor() {
            return this.image.getFormat().getBytesPerPixel() > 1;
        }
        
        @Override
        public float getShadowOffset() {
            return 0.5f;
        }
        
        @Override
        public float getBoldOffset() {
            return 0.5f;
        }
    }
}
