package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Environment(EnvType.CLIENT)
public class EnderDragonDeathFeatureRenderer extends FeatureRenderer<EnderDragonEntity, DragonEntityModel>
{
    public EnderDragonDeathFeatureRenderer(final FeatureRendererContext<EnderDragonEntity, DragonEntityModel> context) {
        super(context);
    }
    
    @Override
    public void render(final EnderDragonEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.bL <= 0) {
            return;
        }
        final Tessellator tessellator9 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder10 = tessellator9.getBufferBuilder();
        GuiLighting.disable();
        final float float9 = (entity.bL + float4) / 200.0f;
        float float10 = 0.0f;
        if (float9 > 0.8f) {
            float10 = (float9 - 0.8f) / 0.2f;
        }
        final Random random13 = new Random(432L);
        GlStateManager.disableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, -1.0f, -2.0f);
        for (int integer14 = 0; integer14 < (float9 + float9 * float9) / 2.0f * 60.0f; ++integer14) {
            GlStateManager.rotatef(random13.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(random13.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(random13.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(random13.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(random13.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(random13.nextFloat() * 360.0f + float9 * 90.0f, 0.0f, 0.0f, 1.0f);
            final float float11 = random13.nextFloat() * 20.0f + 5.0f + float10 * 10.0f;
            final float float12 = random13.nextFloat() * 2.0f + 1.0f + float10 * 2.0f;
            bufferBuilder10.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder10.vertex(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0f * (1.0f - float10))).next();
            bufferBuilder10.vertex(-0.866 * float12, float11, -0.5f * float12).color(255, 0, 255, 0).next();
            bufferBuilder10.vertex(0.866 * float12, float11, -0.5f * float12).color(255, 0, 255, 0).next();
            bufferBuilder10.vertex(0.0, float11, 1.0f * float12).color(255, 0, 255, 0).next();
            bufferBuilder10.vertex(-0.866 * float12, float11, -0.5f * float12).color(255, 0, 255, 0).next();
            tessellator9.draw();
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture();
        GlStateManager.enableAlphaTest();
        GuiLighting.enable();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
