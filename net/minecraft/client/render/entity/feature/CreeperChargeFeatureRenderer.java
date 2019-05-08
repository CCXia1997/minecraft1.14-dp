package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;

@Environment(EnvType.CLIENT)
public class CreeperChargeFeatureRenderer extends FeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>>
{
    private static final Identifier SKIN;
    private final CreeperEntityModel<CreeperEntity> b;
    
    public CreeperChargeFeatureRenderer(final FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> context) {
        super(context);
        this.b = new CreeperEntityModel<CreeperEntity>(2.0f);
    }
    
    @Override
    public void render(final CreeperEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.isCharged()) {
            return;
        }
        final boolean boolean9 = entity.isInvisible();
        GlStateManager.depthMask(!boolean9);
        this.bindTexture(CreeperChargeFeatureRenderer.SKIN);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float float9 = entity.age + float4;
        GlStateManager.translatef(float9 * 0.01f, float9 * 0.01f, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        final float float10 = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        ((FeatureRenderer<T, CreeperEntityModel<CreeperEntity>>)this).getModel().copyStateTo(this.b);
        final GameRenderer gameRenderer12 = MinecraftClient.getInstance().gameRenderer;
        gameRenderer12.setFogBlack(true);
        this.b.render(entity, float2, float3, float5, float6, float7, float8);
        gameRenderer12.setFogBlack(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    static {
        SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    }
}
