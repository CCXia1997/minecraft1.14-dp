package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Direction;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.util.GlAllocationUtils;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;

@Environment(EnvType.CLIENT)
public class EndPortalBlockEntityRenderer extends BlockEntityRenderer<EndPortalBlockEntity>
{
    private static final Identifier SKY_TEX;
    private static final Identifier PORTAL_TEX;
    private static final Random RANDOM;
    private static final FloatBuffer f;
    private static final FloatBuffer g;
    private final FloatBuffer h;
    
    public EndPortalBlockEntityRenderer() {
        this.h = GlAllocationUtils.allocateFloatBuffer(16);
    }
    
    @Override
    public void render(final EndPortalBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        GlStateManager.disableLighting();
        EndPortalBlockEntityRenderer.RANDOM.setSeed(31100L);
        GlStateManager.getMatrix(2982, EndPortalBlockEntityRenderer.f);
        GlStateManager.getMatrix(2983, EndPortalBlockEntityRenderer.g);
        final double double10 = xOffset * xOffset + yOffset * yOffset + zOffset * zOffset;
        final int integer12 = this.a(double10);
        final float float13 = this.c();
        boolean boolean14 = false;
        final GameRenderer gameRenderer15 = MinecraftClient.getInstance().gameRenderer;
        for (int integer13 = 0; integer13 < integer12; ++integer13) {
            GlStateManager.pushMatrix();
            float float14 = 2.0f / (18 - integer13);
            if (integer13 == 0) {
                this.bindTexture(EndPortalBlockEntityRenderer.SKY_TEX);
                float14 = 0.15f;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            if (integer13 >= 1) {
                this.bindTexture(EndPortalBlockEntityRenderer.PORTAL_TEX);
                boolean14 = true;
                gameRenderer15.setFogBlack(true);
            }
            if (integer13 == 1) {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            }
            GlStateManager.texGenMode(GlStateManager.TexCoord.a, 9216);
            GlStateManager.texGenMode(GlStateManager.TexCoord.b, 9216);
            GlStateManager.texGenMode(GlStateManager.TexCoord.c, 9216);
            GlStateManager.texGenParam(GlStateManager.TexCoord.a, 9474, this.a(1.0f, 0.0f, 0.0f, 0.0f));
            GlStateManager.texGenParam(GlStateManager.TexCoord.b, 9474, this.a(0.0f, 1.0f, 0.0f, 0.0f));
            GlStateManager.texGenParam(GlStateManager.TexCoord.c, 9474, this.a(0.0f, 0.0f, 1.0f, 0.0f));
            GlStateManager.enableTexGen(GlStateManager.TexCoord.a);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.b);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.c);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.5f, 0.5f, 0.0f);
            GlStateManager.scalef(0.5f, 0.5f, 1.0f);
            final float float15 = (float)(integer13 + 1);
            GlStateManager.translatef(17.0f / float15, (2.0f + float15 / 1.5f) * (SystemUtil.getMeasuringTimeMs() % 800000L / 800000.0f), 0.0f);
            GlStateManager.rotatef((float15 * float15 * 4321.0f + float15 * 9.0f) * 2.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.scalef(4.5f - float15 / 4.0f, 4.5f - float15 / 4.0f, 1.0f);
            GlStateManager.multMatrix(EndPortalBlockEntityRenderer.g);
            GlStateManager.multMatrix(EndPortalBlockEntityRenderer.f);
            final Tessellator tessellator19 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder20 = tessellator19.getBufferBuilder();
            bufferBuilder20.begin(7, VertexFormats.POSITION_COLOR);
            final float float16 = (EndPortalBlockEntityRenderer.RANDOM.nextFloat() * 0.5f + 0.1f) * float14;
            final float float17 = (EndPortalBlockEntityRenderer.RANDOM.nextFloat() * 0.5f + 0.4f) * float14;
            final float float18 = (EndPortalBlockEntityRenderer.RANDOM.nextFloat() * 0.5f + 0.5f) * float14;
            if (entity.shouldDrawSide(Direction.SOUTH)) {
                bufferBuilder20.vertex(xOffset, yOffset, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset + 1.0, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset + 1.0, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
            }
            if (entity.shouldDrawSide(Direction.NORTH)) {
                bufferBuilder20.vertex(xOffset, yOffset + 1.0, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset + 1.0, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset, zOffset).color(float16, float17, float18, 1.0f).next();
            }
            if (entity.shouldDrawSide(Direction.EAST)) {
                bufferBuilder20.vertex(xOffset + 1.0, yOffset + 1.0, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset + 1.0, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset, zOffset).color(float16, float17, float18, 1.0f).next();
            }
            if (entity.shouldDrawSide(Direction.WEST)) {
                bufferBuilder20.vertex(xOffset, yOffset, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset + 1.0, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset + 1.0, zOffset).color(float16, float17, float18, 1.0f).next();
            }
            if (entity.shouldDrawSide(Direction.DOWN)) {
                bufferBuilder20.vertex(xOffset, yOffset, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
            }
            if (entity.shouldDrawSide(Direction.UP)) {
                bufferBuilder20.vertex(xOffset, yOffset + float13, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset + float13, zOffset + 1.0).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset + 1.0, yOffset + float13, zOffset).color(float16, float17, float18, 1.0f).next();
                bufferBuilder20.vertex(xOffset, yOffset + float13, zOffset).color(float16, float17, float18, 1.0f).next();
            }
            tessellator19.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            this.bindTexture(EndPortalBlockEntityRenderer.SKY_TEX);
        }
        GlStateManager.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexCoord.a);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.b);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.c);
        GlStateManager.enableLighting();
        if (boolean14) {
            gameRenderer15.setFogBlack(false);
        }
    }
    
    protected int a(final double double1) {
        int integer3;
        if (double1 > 36864.0) {
            integer3 = 1;
        }
        else if (double1 > 25600.0) {
            integer3 = 3;
        }
        else if (double1 > 16384.0) {
            integer3 = 5;
        }
        else if (double1 > 9216.0) {
            integer3 = 7;
        }
        else if (double1 > 4096.0) {
            integer3 = 9;
        }
        else if (double1 > 1024.0) {
            integer3 = 11;
        }
        else if (double1 > 576.0) {
            integer3 = 13;
        }
        else if (double1 > 256.0) {
            integer3 = 14;
        }
        else {
            integer3 = 15;
        }
        return integer3;
    }
    
    protected float c() {
        return 0.75f;
    }
    
    private FloatBuffer a(final float float1, final float float2, final float float3, final float float4) {
        this.h.clear();
        this.h.put(float1).put(float2).put(float3).put(float4);
        this.h.flip();
        return this.h;
    }
    
    static {
        SKY_TEX = new Identifier("textures/environment/end_sky.png");
        PORTAL_TEX = new Identifier("textures/entity/end_portal.png");
        RANDOM = new Random(31100L);
        f = GlAllocationUtils.allocateFloatBuffer(16);
        g = GlAllocationUtils.allocateFloatBuffer(16);
    }
}
