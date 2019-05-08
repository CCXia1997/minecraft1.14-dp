package net.minecraft.client.particle;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.render.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ParticleTextureSheet
{
    public static final ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet() {
        @Override
        public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
            GuiLighting.disable();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
        }
        
        @Override
        public void draw(final Tessellator tessellator) {
            tessellator.draw();
        }
        
        @Override
        public String toString() {
            return "TERRAIN_SHEET";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet() {
        @Override
        public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
            GuiLighting.disable();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
        }
        
        @Override
        public void draw(final Tessellator tessellator) {
            tessellator.draw();
        }
        
        @Override
        public String toString() {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet() {
        @Override
        public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
            GuiLighting.disable();
            GlStateManager.depthMask(false);
            textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569f);
            bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
        }
        
        @Override
        public void draw(final Tessellator tessellator) {
            tessellator.draw();
        }
        
        @Override
        public String toString() {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_LIT = new ParticleTextureSheet() {
        @Override
        public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            GuiLighting.disable();
            bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
        }
        
        @Override
        public void draw(final Tessellator tessellator) {
            tessellator.draw();
        }
        
        @Override
        public String toString() {
            return "PARTICLE_SHEET_LIT";
        }
    };
    public static final ParticleTextureSheet CUSTOM = new ParticleTextureSheet() {
        @Override
        public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }
        
        @Override
        public void draw(final Tessellator tessellator) {
        }
        
        @Override
        public String toString() {
            return "CUSTOM";
        }
    };
    public static final ParticleTextureSheet NO_RENDER = new ParticleTextureSheet() {
        @Override
        public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
        }
        
        @Override
        public void draw(final Tessellator tessellator) {
        }
        
        @Override
        public String toString() {
            return "NO_RENDER";
        }
    };
    
    void begin(final BufferBuilder arg1, final TextureManager arg2);
    
    void draw(final Tessellator arg1);
}
