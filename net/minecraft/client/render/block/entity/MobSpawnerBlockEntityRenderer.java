package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;

@Environment(EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer extends BlockEntityRenderer<MobSpawnerBlockEntity>
{
    @Override
    public void render(final MobSpawnerBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset, (float)zOffset + 0.5f);
        a(entity.getLogic(), xOffset, yOffset, zOffset, tickDelta);
        GlStateManager.popMatrix();
    }
    
    public static void a(final MobSpawnerLogic mobSpawnerLogic, final double double2, final double double4, final double double6, final float float8) {
        final Entity entity9 = mobSpawnerLogic.getRenderedEntity();
        if (entity9 != null) {
            float float9 = 0.53125f;
            final float float10 = Math.max(entity9.getWidth(), entity9.getHeight());
            if (float10 > 1.0) {
                float9 /= float10;
            }
            GlStateManager.translatef(0.0f, 0.4f, 0.0f);
            GlStateManager.rotatef((float)MathHelper.lerp(float8, mobSpawnerLogic.f(), mobSpawnerLogic.e()) * 10.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.2f, 0.0f);
            GlStateManager.rotatef(-30.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.scalef(float9, float9, float9);
            entity9.setPositionAndAngles(double2, double4, double6, 0.0f, 0.0f);
            MinecraftClient.getInstance().getEntityRenderManager().render(entity9, 0.0, 0.0, 0.0, 0.0f, float8, false);
        }
    }
}
