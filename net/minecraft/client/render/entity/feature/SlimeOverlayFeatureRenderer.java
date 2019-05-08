package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer<T extends Entity> extends FeatureRenderer<T, SlimeEntityModel<T>>
{
    private final EntityModel<T> model;
    
    public SlimeOverlayFeatureRenderer(final FeatureRendererContext<T, SlimeEntityModel<T>> context) {
        super(context);
        this.model = new SlimeEntityModel<T>(0);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.isInvisible()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.getModel().copyStateTo(this.model);
        this.model.render(entity, float2, float3, float5, float6, float7, float8);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}
