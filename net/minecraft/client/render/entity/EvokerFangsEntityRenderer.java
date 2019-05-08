package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.EvokerFangsEntity;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity>
{
    private static final Identifier SKIN;
    private final EvokerFangsEntityModel<EvokerFangsEntity> model;
    
    public EvokerFangsEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new EvokerFangsEntityModel<EvokerFangsEntity>();
    }
    
    @Override
    public void render(final EvokerFangsEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final float float10 = entity.getAnimationProgress(tickDelta);
        if (float10 == 0.0f) {
            return;
        }
        float float11 = 2.0f;
        if (float10 > 0.9f) {
            float11 *= (float)((1.0 - float10) / 0.10000000149011612);
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableAlphaTest();
        this.bindEntityTexture(entity);
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.rotatef(90.0f - entity.yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(-float11, -float11, float11);
        final float float12 = 0.03125f;
        GlStateManager.translatef(0.0f, -0.626f, 0.0f);
        this.model.render(entity, float10, 0.0f, 0.0f, entity.yaw, entity.pitch, 0.03125f);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final EvokerFangsEntity evokerFangsEntity) {
        return EvokerFangsEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
    }
}
