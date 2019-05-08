package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.world.BlockView;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBlockBlockEntity;

@Environment(EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer extends BlockEntityRenderer<StructureBlockBlockEntity>
{
    @Override
    public void render(final StructureBlockBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
            return;
        }
        super.render(entity, xOffset, yOffset, zOffset, tickDelta, blockBreakStage);
        final BlockPos blockPos10 = entity.getOffset();
        final BlockPos blockPos11 = entity.getSize();
        if (blockPos11.getX() < 1 || blockPos11.getY() < 1 || blockPos11.getZ() < 1) {
            return;
        }
        if (entity.getMode() != StructureBlockMode.a && entity.getMode() != StructureBlockMode.b) {
            return;
        }
        final double double12 = 0.01;
        final double double13 = blockPos10.getX();
        final double double14 = blockPos10.getZ();
        final double double15 = yOffset + blockPos10.getY() - 0.01;
        final double double16 = double15 + blockPos11.getY() + 0.02;
        double double17 = 0.0;
        double double18 = 0.0;
        switch (entity.getMirror()) {
            case LEFT_RIGHT: {
                double17 = blockPos11.getX() + 0.02;
                double18 = -(blockPos11.getZ() + 0.02);
                break;
            }
            case FRONT_BACK: {
                double17 = -(blockPos11.getX() + 0.02);
                double18 = blockPos11.getZ() + 0.02;
                break;
            }
            default: {
                double17 = blockPos11.getX() + 0.02;
                double18 = blockPos11.getZ() + 0.02;
                break;
            }
        }
        double double19 = 0.0;
        double double20 = 0.0;
        double double21 = 0.0;
        double double22 = 0.0;
        switch (entity.getRotation()) {
            case ROT_90: {
                double19 = xOffset + ((double18 < 0.0) ? (double13 - 0.01) : (double13 + 1.0 + 0.01));
                double20 = zOffset + ((double17 < 0.0) ? (double14 + 1.0 + 0.01) : (double14 - 0.01));
                double21 = double19 - double18;
                double22 = double20 + double17;
                break;
            }
            case ROT_180: {
                double19 = xOffset + ((double17 < 0.0) ? (double13 - 0.01) : (double13 + 1.0 + 0.01));
                double20 = zOffset + ((double18 < 0.0) ? (double14 - 0.01) : (double14 + 1.0 + 0.01));
                double21 = double19 - double17;
                double22 = double20 - double18;
                break;
            }
            case ROT_270: {
                double19 = xOffset + ((double18 < 0.0) ? (double13 + 1.0 + 0.01) : (double13 - 0.01));
                double20 = zOffset + ((double17 < 0.0) ? (double14 - 0.01) : (double14 + 1.0 + 0.01));
                double21 = double19 + double18;
                double22 = double20 - double17;
                break;
            }
            default: {
                double19 = xOffset + ((double17 < 0.0) ? (double13 + 1.0 + 0.01) : (double13 - 0.01));
                double20 = zOffset + ((double18 < 0.0) ? (double14 + 1.0 + 0.01) : (double14 - 0.01));
                double21 = double19 + double17;
                double22 = double20 + double18;
                break;
            }
        }
        final int integer34 = 255;
        final int integer35 = 223;
        final int integer36 = 127;
        final Tessellator tessellator37 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder38 = tessellator37.getBufferBuilder();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.disableLightmap(true);
        if (entity.getMode() == StructureBlockMode.a || entity.shouldShowBoundingBox()) {
            this.a(tessellator37, bufferBuilder38, double19, double15, double20, double21, double16, double22, 255, 223, 127);
        }
        if (entity.getMode() == StructureBlockMode.a && entity.shouldShowAir()) {
            this.a(entity, xOffset, yOffset, zOffset, blockPos10, tessellator37, bufferBuilder38, true);
            this.a(entity, xOffset, yOffset, zOffset, blockPos10, tessellator37, bufferBuilder38, false);
        }
        this.disableLightmap(false);
        GlStateManager.lineWidth(1.0f);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();
    }
    
    private void a(final StructureBlockBlockEntity structureBlockBlockEntity, final double double2, final double double4, final double double6, final BlockPos blockPos, final Tessellator tessellator9, final BufferBuilder bufferBuilder10, final boolean boolean11) {
        GlStateManager.lineWidth(boolean11 ? 3.0f : 1.0f);
        bufferBuilder10.begin(3, VertexFormats.POSITION_COLOR);
        final BlockView blockView12 = structureBlockBlockEntity.getWorld();
        final BlockPos blockPos2 = structureBlockBlockEntity.getPos();
        final BlockPos blockPos3 = blockPos2.add(blockPos);
        for (final BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(structureBlockBlockEntity.getSize()).add(-1, -1, -1))) {
            final BlockState blockState17 = blockView12.getBlockState(blockPos4);
            final boolean boolean12 = blockState17.isAir();
            final boolean boolean13 = blockState17.getBlock() == Blocks.iF;
            if (boolean12 || boolean13) {
                final float float20 = boolean12 ? 0.05f : 0.0f;
                final double double7 = blockPos4.getX() - blockPos2.getX() + 0.45f + double2 - float20;
                final double double8 = blockPos4.getY() - blockPos2.getY() + 0.45f + double4 - float20;
                final double double9 = blockPos4.getZ() - blockPos2.getZ() + 0.45f + double6 - float20;
                final double double10 = blockPos4.getX() - blockPos2.getX() + 0.55f + double2 + float20;
                final double double11 = blockPos4.getY() - blockPos2.getY() + 0.55f + double4 + float20;
                final double double12 = blockPos4.getZ() - blockPos2.getZ() + 0.55f + double6 + float20;
                if (boolean11) {
                    WorldRenderer.buildBoxOutline(bufferBuilder10, double7, double8, double9, double10, double11, double12, 0.0f, 0.0f, 0.0f, 1.0f);
                }
                else if (boolean12) {
                    WorldRenderer.buildBoxOutline(bufferBuilder10, double7, double8, double9, double10, double11, double12, 0.5f, 0.5f, 1.0f, 1.0f);
                }
                else {
                    WorldRenderer.buildBoxOutline(bufferBuilder10, double7, double8, double9, double10, double11, double12, 1.0f, 0.25f, 0.25f, 1.0f);
                }
            }
        }
        tessellator9.draw();
    }
    
    private void a(final Tessellator tessellator, final BufferBuilder bufferBuilder, final double double3, final double double5, final double double7, final double double9, final double double11, final double double13, final int integer15, final int integer16, final int integer17) {
        GlStateManager.lineWidth(2.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(double3, double5, double7).color((float)integer16, (float)integer16, (float)integer16, 0.0f).next();
        bufferBuilder.vertex(double3, double5, double7).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double5, double7).color(integer16, integer17, integer17, integer15).next();
        bufferBuilder.vertex(double9, double5, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double3, double5, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double3, double5, double7).color(integer17, integer17, integer16, integer15).next();
        bufferBuilder.vertex(double3, double11, double7).color(integer17, integer16, integer17, integer15).next();
        bufferBuilder.vertex(double9, double11, double7).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double11, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double3, double11, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double3, double11, double7).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double3, double11, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double3, double5, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double5, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double11, double13).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double11, double7).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double5, double7).color(integer16, integer16, integer16, integer15).next();
        bufferBuilder.vertex(double9, double5, double7).color((float)integer16, (float)integer16, (float)integer16, 0.0f).next();
        tessellator.draw();
        GlStateManager.lineWidth(1.0f);
    }
    
    @Override
    public boolean a(final StructureBlockBlockEntity structureBlockBlockEntity) {
        return true;
    }
}
