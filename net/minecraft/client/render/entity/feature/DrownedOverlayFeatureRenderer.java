package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends ZombieEntity> extends FeatureRenderer<T, DrownedEntityModel<T>>
{
    private static final Identifier a;
    private final DrownedEntityModel<T> b;
    
    public DrownedOverlayFeatureRenderer(final FeatureRendererContext<T, DrownedEntityModel<T>> context) {
        super(context);
        this.b = new DrownedEntityModel<T>(0.25f, 0.0f, 64, 64);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.isInvisible()) {
            return;
        }
        this.getModel().setAttributes(this.b);
        this.b.animateModel(entity, float2, float3, float4);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(DrownedOverlayFeatureRenderer.a);
        this.b.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
    
    static {
        a = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
    }
}
