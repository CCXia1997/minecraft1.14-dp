package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ShulkerBulletEntity;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity>
{
    private static final Identifier SKIN;
    private final ShulkerBulletEntityModel<ShulkerBulletEntity> model;
    
    public ShulkerBulletEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new ShulkerBulletEntityModel<ShulkerBulletEntity>();
    }
    
    private float a(final float float1, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - float1; float4 < -180.0f; float4 += 360.0f) {}
        while (float4 >= 180.0f) {
            float4 -= 360.0f;
        }
        return float1 + float3 * float4;
    }
    
    @Override
    public void render(final ShulkerBulletEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        final float float10 = this.a(entity.prevYaw, entity.yaw, tickDelta);
        final float float11 = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        final float float12 = entity.age + tickDelta;
        GlStateManager.translatef((float)x, (float)y + 0.15f, (float)z);
        GlStateManager.rotatef(MathHelper.sin(float12 * 0.1f) * 180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.cos(float12 * 0.1f) * 180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.sin(float12 * 0.15f) * 360.0f, 0.0f, 0.0f, 1.0f);
        final float float13 = 0.03125f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.bindEntityTexture(entity);
        this.model.render(entity, 0.0f, 0.0f, 0.0f, float10, float11, 0.03125f);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.scalef(1.5f, 1.5f, 1.5f);
        this.model.render(entity, 0.0f, 0.0f, 0.0f, float10, float11, 0.03125f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final ShulkerBulletEntity shulkerBulletEntity) {
        return ShulkerBulletEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/shulker/spark.png");
    }
}
