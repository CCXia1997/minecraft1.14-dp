package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SpiderEyesFeatureRenderer<T extends Entity, M extends SpiderEntityModel<T>> extends FeatureRenderer<T, M>
{
    private static final Identifier SKIN;
    
    public SpiderEyesFeatureRenderer(final FeatureRendererContext<T, M> context) {
        super(context);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.bindTexture(SpiderEyesFeatureRenderer.SKIN);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        if (entity.isInvisible()) {
            GlStateManager.depthMask(false);
        }
        else {
            GlStateManager.depthMask(true);
        }
        int integer9 = 61680;
        int integer10 = integer9 % 65536;
        int integer11 = integer9 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer10, (float)integer11);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final GameRenderer gameRenderer12 = MinecraftClient.getInstance().gameRenderer;
        gameRenderer12.setFogBlack(true);
        this.getModel().render(entity, float2, float3, float5, float6, float7, float8);
        gameRenderer12.setFogBlack(false);
        integer9 = entity.getLightmapCoordinates();
        integer10 = integer9 % 65536;
        integer11 = integer9 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer10, (float)integer11);
        this.applyLightmapCoordinates(entity);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    static {
        SKIN = new Identifier("textures/entity/spider_eyes.png");
    }
}
