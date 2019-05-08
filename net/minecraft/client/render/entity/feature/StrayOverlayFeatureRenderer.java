package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;

@Environment(EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends MobEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>
{
    private static final Identifier SKIN;
    private final StrayEntityModel<T> model;
    
    public StrayOverlayFeatureRenderer(final FeatureRendererContext<T, M> context) {
        super(context);
        this.model = new StrayEntityModel<T>(0.25f, true);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.getModel().copyStateTo(this.model);
        this.model.animateModel(entity, float2, float3, float4);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(StrayOverlayFeatureRenderer.SKIN);
        this.model.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
    
    static {
        SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
    }
}
