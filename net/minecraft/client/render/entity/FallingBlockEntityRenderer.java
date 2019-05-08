package net.minecraft.client.render.entity;

import net.minecraft.util.Identifier;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.world.ExtendedBlockView;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.FallingBlockEntity;

@Environment(EnvType.CLIENT)
public class FallingBlockEntityRenderer extends EntityRenderer<FallingBlockEntity>
{
    public FallingBlockEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.c = 0.5f;
    }
    
    @Override
    public void render(final FallingBlockEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final BlockState blockState10 = entity.getBlockState();
        if (blockState10.getRenderType() != BlockRenderType.c) {
            return;
        }
        final World world11 = entity.getWorldClient();
        if (blockState10 == world11.getBlockState(new BlockPos(entity)) || blockState10.getRenderType() == BlockRenderType.a) {
            return;
        }
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        final Tessellator tessellator12 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder13 = tessellator12.getBufferBuilder();
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        bufferBuilder13.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
        final BlockPos blockPos14 = new BlockPos(entity.x, entity.getBoundingBox().maxY, entity.z);
        GlStateManager.translatef((float)(x - blockPos14.getX() - 0.5), (float)(y - blockPos14.getY()), (float)(z - blockPos14.getZ() - 0.5));
        final BlockRenderManager blockRenderManager15 = MinecraftClient.getInstance().getBlockRenderManager();
        blockRenderManager15.getModelRenderer().tesselate(world11, blockRenderManager15.getModel(blockState10), blockState10, blockPos14, bufferBuilder13, false, new Random(), blockState10.getRenderingSeed(entity.getFallingBlockPos()));
        tessellator12.draw();
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final FallingBlockEntity fallingBlockEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
