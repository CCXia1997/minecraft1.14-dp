package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.state.property.Property;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;

@Environment(EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity>
{
    private final ShulkerEntityModel<?> model;
    
    public ShulkerBoxBlockEntityRenderer(final ShulkerEntityModel<?> shulkerEntityModel) {
        this.model = shulkerEntityModel;
    }
    
    @Override
    public void render(final ShulkerBoxBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        Direction direction10 = Direction.UP;
        if (entity.hasWorld()) {
            final BlockState blockState11 = this.getWorld().getBlockState(entity.getPos());
            if (blockState11.getBlock() instanceof ShulkerBoxBlock) {
                direction10 = blockState11.<Direction>get(ShulkerBoxBlock.FACING);
            }
        }
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        if (blockBreakStage >= 0) {
            this.bindTexture(ShulkerBoxBlockEntityRenderer.DESTROY_STAGE_TEXTURES[blockBreakStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            final DyeColor dyeColor11 = entity.getColor();
            if (dyeColor11 == null) {
                this.bindTexture(ShulkerEntityRenderer.SKIN);
            }
            else {
                this.bindTexture(ShulkerEntityRenderer.SKIN_COLOR[dyeColor11.getId()]);
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        if (blockBreakStage < 0) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 1.5f, (float)zOffset + 0.5f);
        GlStateManager.scalef(1.0f, -1.0f, -1.0f);
        GlStateManager.translatef(0.0f, 1.0f, 0.0f);
        final float float11 = 0.9995f;
        GlStateManager.scalef(0.9995f, 0.9995f, 0.9995f);
        GlStateManager.translatef(0.0f, -1.0f, 0.0f);
        switch (direction10) {
            case DOWN: {
                GlStateManager.translatef(0.0f, 2.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
            case NORTH: {
                GlStateManager.translatef(0.0f, 1.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.translatef(0.0f, 1.0f, -1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case WEST: {
                GlStateManager.translatef(-1.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case EAST: {
                GlStateManager.translatef(1.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
        }
        this.model.a().render(0.0625f);
        GlStateManager.translatef(0.0f, -entity.getAnimationProgress(tickDelta) * 0.5f, 0.0f);
        GlStateManager.rotatef(270.0f * entity.getAnimationProgress(tickDelta), 0.0f, 1.0f, 0.0f);
        this.model.b().render(0.0625f);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (blockBreakStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
