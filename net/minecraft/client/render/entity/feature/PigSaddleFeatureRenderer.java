package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;

@Environment(EnvType.CLIENT)
public class PigSaddleFeatureRenderer extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>>
{
    private static final Identifier SKIN;
    private final PigEntityModel<PigEntity> b;
    
    public PigSaddleFeatureRenderer(final FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> context) {
        super(context);
        this.b = new PigEntityModel<PigEntity>(0.5f);
    }
    
    @Override
    public void render(final PigEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.isSaddled()) {
            return;
        }
        this.bindTexture(PigSaddleFeatureRenderer.SKIN);
        ((FeatureRenderer<T, PigEntityModel<PigEntity>>)this).getModel().copyStateTo(this.b);
        this.b.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    static {
        SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
    }
}
