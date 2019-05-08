package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.util.DyeColor;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;

@Environment(EnvType.CLIENT)
public class ShulkerSomethingFeatureRenderer extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>>
{
    public ShulkerSomethingFeatureRenderer(final FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final ShulkerEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        GlStateManager.pushMatrix();
        switch (entity.getAttachedFace()) {
            case EAST: {
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(1.0f, -1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case WEST: {
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(-1.0f, -1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case NORTH: {
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -1.0f, -1.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -1.0f, 1.0f);
                break;
            }
            case UP: {
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -2.0f, 0.0f);
                break;
            }
        }
        final Cuboid cuboid9 = ((FeatureRenderer<T, ShulkerEntityModel>)this).getModel().c();
        cuboid9.yaw = float6 * 0.017453292f;
        cuboid9.pitch = float7 * 0.017453292f;
        final DyeColor dyeColor10 = entity.getColor();
        if (dyeColor10 == null) {
            this.bindTexture(ShulkerEntityRenderer.SKIN);
        }
        else {
            this.bindTexture(ShulkerEntityRenderer.SKIN_COLOR[dyeColor10.getId()]);
        }
        cuboid9.render(float8);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
