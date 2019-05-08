package net.minecraft.client.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.map.MapIcon;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.texture.NativeImageBackedTexture;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.item.map.MapState;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MapRenderer implements AutoCloseable
{
    private static final Identifier MAP_ICONS_TEXTURE;
    private final TextureManager textureManager;
    private final Map<String, MapTexture> mapTextures;
    
    public MapRenderer(final TextureManager textureManager) {
        this.mapTextures = Maps.newHashMap();
        this.textureManager = textureManager;
    }
    
    public void updateTexture(final MapState mapState) {
        this.getMapTexture(mapState).updateTexture();
    }
    
    public void draw(final MapState mapState, final boolean hidePlayerSpecific) {
        this.getMapTexture(mapState).draw(hidePlayerSpecific);
    }
    
    private MapTexture getMapTexture(final MapState mapState) {
        MapTexture mapTexture2 = this.mapTextures.get(mapState.getId());
        if (mapTexture2 == null) {
            mapTexture2 = new MapTexture(mapState);
            this.mapTextures.put(mapState.getId(), mapTexture2);
        }
        return mapTexture2;
    }
    
    @Nullable
    public MapTexture getTexture(final String string) {
        return this.mapTextures.get(string);
    }
    
    public void clearStateTextures() {
        for (final MapTexture mapTexture2 : this.mapTextures.values()) {
            mapTexture2.close();
        }
        this.mapTextures.clear();
    }
    
    @Nullable
    public MapState getState(@Nullable final MapTexture texture) {
        if (texture != null) {
            return texture.mapState;
        }
        return null;
    }
    
    @Override
    public void close() {
        this.clearStateTextures();
    }
    
    static {
        MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
    }
    
    @Environment(EnvType.CLIENT)
    class MapTexture implements AutoCloseable
    {
        private final MapState mapState;
        private final NativeImageBackedTexture texture;
        private final Identifier id;
        
        private MapTexture(final MapState mapState) {
            this.mapState = mapState;
            this.texture = new NativeImageBackedTexture(128, 128, true);
            this.id = MapRenderer.this.textureManager.registerDynamicTexture("map/" + mapState.getId(), this.texture);
        }
        
        private void updateTexture() {
            for (int integer1 = 0; integer1 < 128; ++integer1) {
                for (int integer2 = 0; integer2 < 128; ++integer2) {
                    final int integer3 = integer2 + integer1 * 128;
                    final int integer4 = this.mapState.colors[integer3] & 0xFF;
                    if (integer4 / 4 == 0) {
                        this.texture.getImage().setPixelRGBA(integer2, integer1, (integer3 + integer3 / 128 & 0x1) * 8 + 16 << 24);
                    }
                    else {
                        this.texture.getImage().setPixelRGBA(integer2, integer1, MaterialColor.COLORS[integer4 / 4].getRenderColor(integer4 & 0x3));
                    }
                }
            }
            this.texture.upload();
        }
        
        private void draw(final boolean hidePlayerSpecific) {
            final int integer2 = 0;
            final int integer3 = 0;
            final Tessellator tessellator4 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder5 = tessellator4.getBufferBuilder();
            final float float6 = 0.0f;
            MapRenderer.this.textureManager.bindTexture(this.id);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlphaTest();
            bufferBuilder5.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder5.vertex(0.0, 128.0, -0.009999999776482582).texture(0.0, 1.0).next();
            bufferBuilder5.vertex(128.0, 128.0, -0.009999999776482582).texture(1.0, 1.0).next();
            bufferBuilder5.vertex(128.0, 0.0, -0.009999999776482582).texture(1.0, 0.0).next();
            bufferBuilder5.vertex(0.0, 0.0, -0.009999999776482582).texture(0.0, 0.0).next();
            tessellator4.draw();
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
            int integer4 = 0;
            for (final MapIcon mapIcon9 : this.mapState.icons.values()) {
                if (hidePlayerSpecific && !mapIcon9.renderIfNotHeld()) {
                    continue;
                }
                MapRenderer.this.textureManager.bindTexture(MapRenderer.MAP_ICONS_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0f + mapIcon9.getX() / 2.0f + 64.0f, 0.0f + mapIcon9.getZ() / 2.0f + 64.0f, -0.02f);
                GlStateManager.rotatef(mapIcon9.getAngle() * 360 / 16.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.scalef(4.0f, 4.0f, 3.0f);
                GlStateManager.translatef(-0.125f, 0.125f, 0.0f);
                final byte byte10 = mapIcon9.getTypeId();
                final float float7 = (byte10 % 16 + 0) / 16.0f;
                final float float8 = (byte10 / 16 + 0) / 16.0f;
                final float float9 = (byte10 % 16 + 1) / 16.0f;
                final float float10 = (byte10 / 16 + 1) / 16.0f;
                bufferBuilder5.begin(7, VertexFormats.POSITION_UV);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final float float11 = -0.001f;
                bufferBuilder5.vertex(-1.0, 1.0, integer4 * -0.001f).texture(float7, float8).next();
                bufferBuilder5.vertex(1.0, 1.0, integer4 * -0.001f).texture(float9, float8).next();
                bufferBuilder5.vertex(1.0, -1.0, integer4 * -0.001f).texture(float9, float10).next();
                bufferBuilder5.vertex(-1.0, -1.0, integer4 * -0.001f).texture(float7, float10).next();
                tessellator4.draw();
                GlStateManager.popMatrix();
                if (mapIcon9.getText() != null) {
                    final TextRenderer textRenderer16 = MinecraftClient.getInstance().textRenderer;
                    final String string17 = mapIcon9.getText().getFormattedText();
                    final float float12 = (float)textRenderer16.getStringWidth(string17);
                    final float value = 25.0f / float12;
                    final float min = 0.0f;
                    final float n = 6.0f;
                    textRenderer16.getClass();
                    final float float13 = MathHelper.clamp(value, min, n / 9.0f);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(0.0f + mapIcon9.getX() / 2.0f + 64.0f - float12 * float13 / 2.0f, 0.0f + mapIcon9.getZ() / 2.0f + 64.0f + 4.0f, -0.025f);
                    GlStateManager.scalef(float13, float13, 1.0f);
                    final int left = -1;
                    final int top = -1;
                    final int right = (int)float12;
                    textRenderer16.getClass();
                    DrawableHelper.fill(left, top, right, 9 - 1, Integer.MIN_VALUE);
                    GlStateManager.translatef(0.0f, 0.0f, -0.1f);
                    textRenderer16.draw(string17, 0.0f, 0.0f, -1);
                    GlStateManager.popMatrix();
                }
                ++integer4;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, -0.04f);
            GlStateManager.scalef(1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
        
        @Override
        public void close() {
            this.texture.close();
        }
    }
}
