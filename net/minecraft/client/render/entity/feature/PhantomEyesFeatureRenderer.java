package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class PhantomEyesFeatureRenderer<T extends Entity> extends FeatureRenderer<T, PhantomEntityModel<T>>
{
    private static final Identifier a;
    
    public PhantomEyesFeatureRenderer(final FeatureRendererContext<T, PhantomEntityModel<T>> context) {
        super(context);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.bindTexture(PhantomEyesFeatureRenderer.a);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!entity.isInvisible());
        final int integer9 = 61680;
        final int integer10 = 61680;
        final int integer11 = 0;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final GameRenderer gameRenderer12 = MinecraftClient.getInstance().gameRenderer;
        gameRenderer12.setFogBlack(true);
        this.getModel().render(entity, float2, float3, float5, float6, float7, float8);
        gameRenderer12.setFogBlack(false);
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
        a = new Identifier("textures/entity/phantom_eyes.png");
    }
}
