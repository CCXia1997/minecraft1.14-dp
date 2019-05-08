package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;

@Environment(EnvType.CLIENT)
public class WolfCollarFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>>
{
    private static final Identifier SKIN;
    
    public WolfCollarFeatureRenderer(final FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final WolfEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.isTamed() || entity.isInvisible()) {
            return;
        }
        this.bindTexture(WolfCollarFeatureRenderer.SKIN);
        final float[] arr9 = entity.getCollarColor().getColorComponents();
        GlStateManager.color3f(arr9[0], arr9[1], arr9[2]);
        ((FeatureRenderer<T, WolfEntityModel<WolfEntity>>)this).getModel().render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
    
    static {
        SKIN = new Identifier("textures/entity/wolf/wolf_collar.png");
    }
}
