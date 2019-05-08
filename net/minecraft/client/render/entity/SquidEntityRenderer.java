package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.SquidEntity;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderer extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>>
{
    private static final Identifier SKIN;
    
    public SquidEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SquidEntityModel(), 0.7f);
    }
    
    protected Identifier getTexture(final SquidEntity squidEntity) {
        return SquidEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final SquidEntity entity, final float pitch, final float yaw, final float delta) {
        final float float5 = MathHelper.lerp(delta, entity.c, entity.b);
        final float float6 = MathHelper.lerp(delta, entity.bz, entity.d);
        GlStateManager.translatef(0.0f, 0.5f, 0.0f);
        GlStateManager.rotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float5, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float6, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.2f, 0.0f);
    }
    
    @Override
    protected float getAge(final SquidEntity entity, final float tickDelta) {
        return MathHelper.lerp(tickDelta, entity.bD, entity.bC);
    }
    
    static {
        SKIN = new Identifier("textures/entity/squid.png");
    }
}
