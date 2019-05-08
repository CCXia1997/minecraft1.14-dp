package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;

@Environment(EnvType.CLIENT)
public class CatCollarFeatureRenderer extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>>
{
    private static final Identifier a;
    private final CatEntityModel<CatEntity> b;
    
    public CatCollarFeatureRenderer(final FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> context) {
        super(context);
        this.b = new CatEntityModel<CatEntity>(0.01f);
    }
    
    @Override
    public void render(final CatEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.isTamed() || entity.isInvisible()) {
            return;
        }
        this.bindTexture(CatCollarFeatureRenderer.a);
        final float[] arr9 = entity.getCollarColor().getColorComponents();
        GlStateManager.color3f(arr9[0], arr9[1], arr9[2]);
        ((FeatureRenderer<T, CatEntityModel<CatEntity>>)this).getModel().copyStateTo(this.b);
        this.b.animateModel(entity, float2, float3, float4);
        this.b.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
    
    static {
        a = new Identifier("textures/entity/cat/cat_collar.png");
    }
}
