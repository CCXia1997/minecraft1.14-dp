package net.minecraft.client.render.entity.feature;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Box;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import java.util.Random;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>
{
    private final EntityRenderDispatcher a;
    
    public StuckArrowsFeatureRenderer(final LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
        this.a = livingEntityRenderer.getRenderManager();
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final int integer9 = entity.getStuckArrows();
        if (integer9 <= 0) {
            return;
        }
        final Entity entity2 = new ArrowEntity(entity.world, entity.x, entity.y, entity.z);
        final Random random11 = new Random(entity.getEntityId());
        GuiLighting.disable();
        for (int integer10 = 0; integer10 < integer9; ++integer10) {
            GlStateManager.pushMatrix();
            final Cuboid cuboid13 = this.getModel().getRandomCuboid(random11);
            final Box box14 = cuboid13.boxes.get(random11.nextInt(cuboid13.boxes.size()));
            cuboid13.applyTransform(0.0625f);
            float float9 = random11.nextFloat();
            float float10 = random11.nextFloat();
            float float11 = random11.nextFloat();
            final float float12 = MathHelper.lerp(float9, box14.xMin, box14.xMax) / 16.0f;
            final float float13 = MathHelper.lerp(float10, box14.yMin, box14.yMax) / 16.0f;
            final float float14 = MathHelper.lerp(float11, box14.zMin, box14.zMax) / 16.0f;
            GlStateManager.translatef(float12, float13, float14);
            float9 = float9 * 2.0f - 1.0f;
            float10 = float10 * 2.0f - 1.0f;
            float11 = float11 * 2.0f - 1.0f;
            float9 *= -1.0f;
            float10 *= -1.0f;
            float11 *= -1.0f;
            final float float15 = MathHelper.sqrt(float9 * float9 + float11 * float11);
            entity2.yaw = (float)(Math.atan2(float9, float11) * 57.2957763671875);
            entity2.pitch = (float)(Math.atan2(float10, float15) * 57.2957763671875);
            entity2.prevYaw = entity2.yaw;
            entity2.prevPitch = entity2.pitch;
            final double double22 = 0.0;
            final double double23 = 0.0;
            final double double24 = 0.0;
            this.a.render(entity2, 0.0, 0.0, 0.0, 0.0f, float4, false);
            GlStateManager.popMatrix();
        }
        GuiLighting.enable();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
