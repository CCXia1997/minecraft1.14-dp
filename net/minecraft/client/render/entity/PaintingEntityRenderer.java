package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingEntity;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity>
{
    public PaintingEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    public void render(final PaintingEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.rotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);
        final PaintingMotive paintingMotive10 = entity.motive;
        final float float11 = 0.0625f;
        GlStateManager.scalef(0.0625f, 0.0625f, 0.0625f);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        final PaintingManager paintingManager12 = MinecraftClient.getInstance().getPaintingManager();
        this.a(entity, paintingMotive10.getWidth(), paintingMotive10.getHeight(), paintingManager12.getPaintingSprite(paintingMotive10), paintingManager12.getBackSprite());
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final PaintingEntity paintingEntity) {
        return SpriteAtlasTexture.PAINTING_ATLAS_TEX;
    }
    
    private void a(final PaintingEntity paintingEntity, final int integer2, final int integer3, final Sprite sprite4, final Sprite sprite5) {
        final float float6 = -integer2 / 2.0f;
        final float float7 = -integer3 / 2.0f;
        final float float8 = 0.5f;
        final float float9 = sprite5.getMinU();
        final float float10 = sprite5.getMaxU();
        final float float11 = sprite5.getMinV();
        final float float12 = sprite5.getMaxV();
        final float float13 = sprite5.getMinU();
        final float float14 = sprite5.getMaxU();
        final float float15 = sprite5.getMinV();
        final float float16 = sprite5.getV(1.0);
        final float float17 = sprite5.getMinU();
        final float float18 = sprite5.getU(1.0);
        final float float19 = sprite5.getMinV();
        final float float20 = sprite5.getMaxV();
        final int integer4 = integer2 / 16;
        final int integer5 = integer3 / 16;
        final double double23 = 16.0 / integer4;
        final double double24 = 16.0 / integer5;
        for (int integer6 = 0; integer6 < integer4; ++integer6) {
            for (int integer7 = 0; integer7 < integer5; ++integer7) {
                final float float21 = float6 + (integer6 + 1) * 16;
                final float float22 = float6 + integer6 * 16;
                final float float23 = float7 + (integer7 + 1) * 16;
                final float float24 = float7 + integer7 * 16;
                this.a(paintingEntity, (float21 + float22) / 2.0f, (float23 + float24) / 2.0f);
                final float float25 = sprite4.getU(double23 * (integer4 - integer6));
                final float float26 = sprite4.getU(double23 * (integer4 - (integer6 + 1)));
                final float float27 = sprite4.getV(double24 * (integer5 - integer7));
                final float float28 = sprite4.getV(double24 * (integer5 - (integer7 + 1)));
                final Tessellator tessellator37 = Tessellator.getInstance();
                final BufferBuilder bufferBuilder38 = tessellator37.getBufferBuilder();
                bufferBuilder38.begin(7, VertexFormats.POSITION_UV_NORMAL);
                bufferBuilder38.vertex(float21, float24, -0.5).texture(float26, float27).normal(0.0f, 0.0f, -1.0f).next();
                bufferBuilder38.vertex(float22, float24, -0.5).texture(float25, float27).normal(0.0f, 0.0f, -1.0f).next();
                bufferBuilder38.vertex(float22, float23, -0.5).texture(float25, float28).normal(0.0f, 0.0f, -1.0f).next();
                bufferBuilder38.vertex(float21, float23, -0.5).texture(float26, float28).normal(0.0f, 0.0f, -1.0f).next();
                bufferBuilder38.vertex(float21, float23, 0.5).texture(float9, float11).normal(0.0f, 0.0f, 1.0f).next();
                bufferBuilder38.vertex(float22, float23, 0.5).texture(float10, float11).normal(0.0f, 0.0f, 1.0f).next();
                bufferBuilder38.vertex(float22, float24, 0.5).texture(float10, float12).normal(0.0f, 0.0f, 1.0f).next();
                bufferBuilder38.vertex(float21, float24, 0.5).texture(float9, float12).normal(0.0f, 0.0f, 1.0f).next();
                bufferBuilder38.vertex(float21, float23, -0.5).texture(float13, float15).normal(0.0f, 1.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float23, -0.5).texture(float14, float15).normal(0.0f, 1.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float23, 0.5).texture(float14, float16).normal(0.0f, 1.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float23, 0.5).texture(float13, float16).normal(0.0f, 1.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float24, 0.5).texture(float13, float15).normal(0.0f, -1.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float24, 0.5).texture(float14, float15).normal(0.0f, -1.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float24, -0.5).texture(float14, float16).normal(0.0f, -1.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float24, -0.5).texture(float13, float16).normal(0.0f, -1.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float23, 0.5).texture(float18, float19).normal(-1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float24, 0.5).texture(float18, float20).normal(-1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float24, -0.5).texture(float17, float20).normal(-1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float21, float23, -0.5).texture(float17, float19).normal(-1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float23, -0.5).texture(float18, float19).normal(1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float24, -0.5).texture(float18, float20).normal(1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float24, 0.5).texture(float17, float20).normal(1.0f, 0.0f, 0.0f).next();
                bufferBuilder38.vertex(float22, float23, 0.5).texture(float17, float19).normal(1.0f, 0.0f, 0.0f).next();
                tessellator37.draw();
            }
        }
    }
    
    private void a(final PaintingEntity paintingEntity, final float float2, final float float3) {
        int integer4 = MathHelper.floor(paintingEntity.x);
        final int integer5 = MathHelper.floor(paintingEntity.y + float3 / 16.0f);
        int integer6 = MathHelper.floor(paintingEntity.z);
        final Direction direction7 = paintingEntity.facing;
        if (direction7 == Direction.NORTH) {
            integer4 = MathHelper.floor(paintingEntity.x + float2 / 16.0f);
        }
        if (direction7 == Direction.WEST) {
            integer6 = MathHelper.floor(paintingEntity.z - float2 / 16.0f);
        }
        if (direction7 == Direction.SOUTH) {
            integer4 = MathHelper.floor(paintingEntity.x - float2 / 16.0f);
        }
        if (direction7 == Direction.EAST) {
            integer6 = MathHelper.floor(paintingEntity.z + float2 / 16.0f);
        }
        final int integer7 = this.renderManager.world.getLightmapIndex(new BlockPos(integer4, integer5, integer6), 0);
        final int integer8 = integer7 % 65536;
        final int integer9 = integer7 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer8, (float)integer9);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
    }
}
