package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import javax.annotation.Nullable;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.Cuboid;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.block.BannerBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BannerBlockEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer extends BlockEntityRenderer<BannerBlockEntity>
{
    private final BannerBlockEntityModel model;
    
    public BannerBlockEntityRenderer() {
        this.model = new BannerBlockEntityModel();
    }
    
    @Override
    public void render(final BannerBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final float float10 = 0.6666667f;
        final boolean boolean11 = entity.getWorld() == null;
        GlStateManager.pushMatrix();
        final Cuboid cuboid14 = this.model.b();
        long long12;
        if (boolean11) {
            long12 = 0L;
            GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
            cuboid14.visible = true;
        }
        else {
            long12 = entity.getWorld().getTime();
            final BlockState blockState15 = entity.getCachedState();
            if (blockState15.getBlock() instanceof BannerBlock) {
                GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                GlStateManager.rotatef(-blockState15.<Integer>get((Property<Integer>)BannerBlock.ROTATION) * 360 / 16.0f, 0.0f, 1.0f, 0.0f);
                cuboid14.visible = true;
            }
            else {
                GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset - 0.16666667f, (float)zOffset + 0.5f);
                GlStateManager.rotatef(-blockState15.<Direction>get((Property<Direction>)WallBannerBlock.FACING).asRotation(), 0.0f, 1.0f, 0.0f);
                GlStateManager.translatef(0.0f, -0.3125f, -0.4375f);
                cuboid14.visible = false;
            }
        }
        final BlockPos blockPos15 = entity.getPos();
        final float float11 = blockPos15.getX() * 7 + blockPos15.getY() * 9 + blockPos15.getZ() * 13 + long12 + tickDelta;
        this.model.c().pitch = (-0.0125f + 0.01f * MathHelper.cos(float11 * 3.1415927f * 0.02f)) * 3.1415927f;
        GlStateManager.enableRescaleNormal();
        final Identifier identifier17 = this.getTextureId(entity);
        if (identifier17 != null) {
            this.bindTexture(identifier17);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6666667f, -0.6666667f, -0.6666667f);
            this.model.a();
            GlStateManager.popMatrix();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    @Nullable
    private Identifier getTextureId(final BannerBlockEntity bannerBlockEntity) {
        return TextureCache.BANNER.get(bannerBlockEntity.getPatternCacheKey(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getPatternColors());
    }
}
