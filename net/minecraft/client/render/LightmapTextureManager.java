package net.minecraft.client.render;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LightmapTextureManager implements AutoCloseable
{
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean isDirty;
    private float prevFlicker;
    private float flicker;
    private final GameRenderer worldRenderer;
    private final MinecraftClient client;
    
    public LightmapTextureManager(final GameRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.client = worldRenderer.getClient();
        this.texture = new NativeImageBackedTexture(16, 16, false);
        this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
        this.image = this.texture.getImage();
    }
    
    @Override
    public void close() {
        this.texture.close();
    }
    
    public void tick() {
        this.flicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random());
        this.flicker *= (float)0.9;
        this.prevFlicker += this.flicker - this.prevFlicker;
        this.isDirty = true;
    }
    
    public void disable() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    public void enable() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float float1 = 0.00390625f;
        GlStateManager.scalef(0.00390625f, 0.00390625f, 0.00390625f);
        GlStateManager.translatef(8.0f, 8.0f, 8.0f);
        GlStateManager.matrixMode(5888);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        GlStateManager.texParameter(3553, 10241, 9729);
        GlStateManager.texParameter(3553, 10240, 9729);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    public void update(final float delta) {
        if (!this.isDirty) {
            return;
        }
        this.client.getProfiler().push("lightTex");
        final World world2 = this.client.world;
        if (world2 == null) {
            return;
        }
        final float float3 = world2.getAmbientLight(1.0f);
        final float float4 = float3 * 0.95f + 0.05f;
        final float float5 = this.client.player.L();
        float float6;
        if (this.client.player.hasStatusEffect(StatusEffects.p)) {
            float6 = this.worldRenderer.getNightVisionStrength(this.client.player, delta);
        }
        else if (float5 > 0.0f && this.client.player.hasStatusEffect(StatusEffects.C)) {
            float6 = float5;
        }
        else {
            float6 = 0.0f;
        }
        for (int integer7 = 0; integer7 < 16; ++integer7) {
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                float float7 = world2.dimension.getLightLevelToBrightness()[integer7] * float4;
                final float float8 = world2.dimension.getLightLevelToBrightness()[integer8] * (this.prevFlicker * 0.1f + 1.5f);
                if (world2.getTicksSinceLightning() > 0) {
                    float7 = world2.dimension.getLightLevelToBrightness()[integer7];
                }
                final float float9 = float7 * (float3 * 0.65f + 0.35f);
                final float float10 = float7 * (float3 * 0.65f + 0.35f);
                final float float11 = float7;
                final float float12 = float8;
                final float float13 = float8 * ((float8 * 0.6f + 0.4f) * 0.6f + 0.4f);
                final float float14 = float8 * (float8 * float8 * 0.6f + 0.4f);
                float float15 = float9 + float12;
                float float16 = float10 + float13;
                float float17 = float11 + float14;
                float15 = float15 * 0.96f + 0.03f;
                float16 = float16 * 0.96f + 0.03f;
                float17 = float17 * 0.96f + 0.03f;
                if (this.worldRenderer.getSkyDarkness(delta) > 0.0f) {
                    final float float18 = this.worldRenderer.getSkyDarkness(delta);
                    float15 = float15 * (1.0f - float18) + float15 * 0.7f * float18;
                    float16 = float16 * (1.0f - float18) + float16 * 0.6f * float18;
                    float17 = float17 * (1.0f - float18) + float17 * 0.6f * float18;
                }
                if (world2.dimension.getType() == DimensionType.c) {
                    float15 = 0.22f + float12 * 0.75f;
                    float16 = 0.28f + float13 * 0.75f;
                    float17 = 0.25f + float14 * 0.75f;
                }
                if (float6 > 0.0f) {
                    float float18 = 1.0f / float15;
                    if (float18 > 1.0f / float16) {
                        float18 = 1.0f / float16;
                    }
                    if (float18 > 1.0f / float17) {
                        float18 = 1.0f / float17;
                    }
                    float15 = float15 * (1.0f - float6) + float15 * float18 * float6;
                    float16 = float16 * (1.0f - float6) + float16 * float18 * float6;
                    float17 = float17 * (1.0f - float6) + float17 * float18 * float6;
                }
                if (float15 > 1.0f) {
                    float15 = 1.0f;
                }
                if (float16 > 1.0f) {
                    float16 = 1.0f;
                }
                if (float17 > 1.0f) {
                    float17 = 1.0f;
                }
                float float18 = (float)this.client.options.gamma;
                float float19 = 1.0f - float15;
                float float20 = 1.0f - float16;
                float float21 = 1.0f - float17;
                float19 = 1.0f - float19 * float19 * float19 * float19;
                float20 = 1.0f - float20 * float20 * float20 * float20;
                float21 = 1.0f - float21 * float21 * float21 * float21;
                float15 = float15 * (1.0f - float18) + float19 * float18;
                float16 = float16 * (1.0f - float18) + float20 * float18;
                float17 = float17 * (1.0f - float18) + float21 * float18;
                float15 = float15 * 0.96f + 0.03f;
                float16 = float16 * 0.96f + 0.03f;
                float17 = float17 * 0.96f + 0.03f;
                if (float15 > 1.0f) {
                    float15 = 1.0f;
                }
                if (float16 > 1.0f) {
                    float16 = 1.0f;
                }
                if (float17 > 1.0f) {
                    float17 = 1.0f;
                }
                if (float15 < 0.0f) {
                    float15 = 0.0f;
                }
                if (float16 < 0.0f) {
                    float16 = 0.0f;
                }
                if (float17 < 0.0f) {
                    float17 = 0.0f;
                }
                final int integer9 = 255;
                final int integer10 = (int)(float15 * 255.0f);
                final int integer11 = (int)(float16 * 255.0f);
                final int integer12 = (int)(float17 * 255.0f);
                this.image.setPixelRGBA(integer8, integer7, 0xFF000000 | integer12 << 16 | integer11 << 8 | integer10);
            }
        }
        this.texture.upload();
        this.isDirty = false;
        this.client.getProfiler().pop();
    }
}
