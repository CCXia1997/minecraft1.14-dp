package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;

@Environment(EnvType.CLIENT)
public class ExplodingWitherSkullEntityRenderer extends EntityRenderer<ExplodingWitherSkullEntity>
{
    private static final Identifier INVINCIBLE_SKIN;
    private static final Identifier SKIN;
    private final SkullEntityModel model;
    
    public ExplodingWitherSkullEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new SkullEntityModel();
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
    public void render(final ExplodingWitherSkullEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        final float float10 = this.a(entity.prevYaw, entity.yaw, tickDelta);
        final float float11 = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        GlStateManager.translatef((float)x, (float)y, (float)z);
        final float float12 = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.bindEntityTexture(entity);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.model.setRotationAngles(0.0f, 0.0f, 0.0f, float10, float11, 0.0625f);
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final ExplodingWitherSkullEntity explodingWitherSkullEntity) {
        return explodingWitherSkullEntity.isCharged() ? ExplodingWitherSkullEntityRenderer.INVINCIBLE_SKIN : ExplodingWitherSkullEntityRenderer.SKIN;
    }
    
    static {
        INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
        SKIN = new Identifier("textures/entity/wither/wither.png");
    }
}
