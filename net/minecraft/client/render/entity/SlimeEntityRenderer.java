package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;

@Environment(EnvType.CLIENT)
public class SlimeEntityRenderer extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>>
{
    private static final Identifier SKIN;
    
    public SlimeEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SlimeEntityModel(16), 0.25f);
        this.addFeature((FeatureRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>>)new SlimeOverlayFeatureRenderer((FeatureRendererContext<Entity, SlimeEntityModel<Entity>>)this));
    }
    
    @Override
    public void render(final SlimeEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        this.c = 0.25f * entity.getSize();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected void scale(final SlimeEntity entity, final float tickDelta) {
        final float float3 = 0.999f;
        GlStateManager.scalef(0.999f, 0.999f, 0.999f);
        final float float4 = (float)entity.getSize();
        final float float5 = MathHelper.lerp(tickDelta, entity.lastStretch, entity.stretch) / (float4 * 0.5f + 1.0f);
        final float float6 = 1.0f / (float5 + 1.0f);
        GlStateManager.scalef(float6 * float4, 1.0f / float6 * float4, float6 * float4);
    }
    
    protected Identifier getTexture(final SlimeEntity slimeEntity) {
        return SlimeEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/slime/slime.png");
    }
}
