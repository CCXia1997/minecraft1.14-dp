package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;

@Environment(EnvType.CLIENT)
public class WitherArmorFeatureRenderer extends FeatureRenderer<WitherEntity, WitherEntityModel<WitherEntity>>
{
    private static final Identifier SKIN;
    private final WitherEntityModel<WitherEntity> b;
    
    public WitherArmorFeatureRenderer(final FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> context) {
        super(context);
        this.b = new WitherEntityModel<WitherEntity>(0.5f);
    }
    
    @Override
    public void render(final WitherEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.isAtHalfHealth()) {
            return;
        }
        GlStateManager.depthMask(!entity.isInvisible());
        this.bindTexture(WitherArmorFeatureRenderer.SKIN);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float float9 = entity.age + float4;
        final float float10 = MathHelper.cos(float9 * 0.02f) * 3.0f;
        final float float11 = float9 * 0.01f;
        GlStateManager.translatef(float10, float11, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        final float float12 = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        this.b.animateModel(entity, float2, float3, float4);
        ((FeatureRenderer<T, WitherEntityModel<WitherEntity>>)this).getModel().copyStateTo(this.b);
        final GameRenderer gameRenderer13 = MinecraftClient.getInstance().gameRenderer;
        gameRenderer13.setFogBlack(true);
        this.b.render(entity, float2, float3, float5, float6, float7, float8);
        gameRenderer13.setFogBlack(false);
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
        SKIN = new Identifier("textures/entity/wither/wither_armor.png");
    }
}
