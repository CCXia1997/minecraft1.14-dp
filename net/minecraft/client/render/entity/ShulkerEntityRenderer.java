package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ShulkerSomethingFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>>
{
    public static final Identifier SKIN;
    public static final Identifier[] SKIN_COLOR;
    
    public ShulkerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ShulkerEntityModel(), 0.0f);
        this.addFeature(new ShulkerSomethingFeatureRenderer(this));
    }
    
    @Override
    public void render(final ShulkerEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final int integer10 = entity.dY();
        if (integer10 > 0 && entity.ea()) {
            final BlockPos blockPos11 = entity.getAttachedBlock();
            final BlockPos blockPos12 = entity.dZ();
            double double13 = (integer10 - tickDelta) / 6.0;
            double13 *= double13;
            final double double14 = (blockPos11.getX() - blockPos12.getX()) * double13;
            final double double15 = (blockPos11.getY() - blockPos12.getY()) * double13;
            final double double16 = (blockPos11.getZ() - blockPos12.getZ()) * double13;
            super.render(entity, x - double14, y - double15, z - double16, yaw, tickDelta);
        }
        else {
            super.render(entity, x, y, z, yaw, tickDelta);
        }
    }
    
    @Override
    public boolean isVisible(final ShulkerEntity shulkerEntity, final VisibleRegion visibleRegion, final double double3, final double double5, final double double7) {
        if (super.isVisible(shulkerEntity, visibleRegion, double3, double5, double7)) {
            return true;
        }
        if (shulkerEntity.dY() > 0 && shulkerEntity.ea()) {
            final BlockPos blockPos9 = shulkerEntity.dZ();
            final BlockPos blockPos10 = shulkerEntity.getAttachedBlock();
            final Vec3d vec3d11 = new Vec3d(blockPos10.getX(), blockPos10.getY(), blockPos10.getZ());
            final Vec3d vec3d12 = new Vec3d(blockPos9.getX(), blockPos9.getY(), blockPos9.getZ());
            if (visibleRegion.intersects(new BoundingBox(vec3d12.x, vec3d12.y, vec3d12.z, vec3d11.x, vec3d11.y, vec3d11.z))) {
                return true;
            }
        }
        return false;
    }
    
    protected Identifier getTexture(final ShulkerEntity shulkerEntity) {
        if (shulkerEntity.getColor() == null) {
            return ShulkerEntityRenderer.SKIN;
        }
        return ShulkerEntityRenderer.SKIN_COLOR[shulkerEntity.getColor().getId()];
    }
    
    @Override
    protected void setupTransforms(final ShulkerEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        switch (entity.getAttachedFace()) {
            case EAST: {
                GlStateManager.translatef(0.5f, 0.5f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case WEST: {
                GlStateManager.translatef(-0.5f, 0.5f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case NORTH: {
                GlStateManager.translatef(0.0f, 0.5f, -0.5f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.translatef(0.0f, 0.5f, 0.5f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case UP: {
                GlStateManager.translatef(0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
        }
    }
    
    @Override
    protected void scale(final ShulkerEntity entity, final float tickDelta) {
        final float float3 = 0.999f;
        GlStateManager.scalef(0.999f, 0.999f, 0.999f);
    }
    
    static {
        SKIN = new Identifier("textures/entity/shulker/shulker.png");
        SKIN_COLOR = new Identifier[] { new Identifier("textures/entity/shulker/shulker_white.png"), new Identifier("textures/entity/shulker/shulker_orange.png"), new Identifier("textures/entity/shulker/shulker_magenta.png"), new Identifier("textures/entity/shulker/shulker_light_blue.png"), new Identifier("textures/entity/shulker/shulker_yellow.png"), new Identifier("textures/entity/shulker/shulker_lime.png"), new Identifier("textures/entity/shulker/shulker_pink.png"), new Identifier("textures/entity/shulker/shulker_gray.png"), new Identifier("textures/entity/shulker/shulker_light_gray.png"), new Identifier("textures/entity/shulker/shulker_cyan.png"), new Identifier("textures/entity/shulker/shulker_purple.png"), new Identifier("textures/entity/shulker/shulker_blue.png"), new Identifier("textures/entity/shulker/shulker_brown.png"), new Identifier("textures/entity/shulker/shulker_green.png"), new Identifier("textures/entity/shulker/shulker_red.png"), new Identifier("textures/entity/shulker/shulker_black.png") };
    }
}
