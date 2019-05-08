package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.Block;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity>
{
    private static final Identifier SHADOW_TEX;
    protected final EntityRenderDispatcher renderManager;
    protected float c;
    protected float d;
    protected boolean e;
    
    protected EntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        this.d = 1.0f;
        this.renderManager = entityRenderDispatcher;
    }
    
    public void a(final boolean boolean1) {
        this.e = boolean1;
    }
    
    public boolean isVisible(final T entity, final VisibleRegion visibleRegion, final double double3, final double double5, final double double7) {
        if (!entity.shouldRenderFrom(double3, double5, double7)) {
            return false;
        }
        if (entity.ignoreCameraFrustum) {
            return true;
        }
        BoundingBox boundingBox9 = entity.getVisibilityBoundingBox().expand(0.5);
        if (boundingBox9.isValid() || boundingBox9.averageDimension() == 0.0) {
            boundingBox9 = new BoundingBox(entity.x - 2.0, entity.y - 2.0, entity.z - 2.0, entity.x + 2.0, entity.y + 2.0, entity.z + 2.0);
        }
        return visibleRegion.intersects(boundingBox9);
    }
    
    public void render(final T entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (!this.e) {
            this.renderLabelIfPresent(entity, x, y, z);
        }
    }
    
    protected int getOutlineColor(final T entity) {
        final Team team2 = (Team)entity.getScoreboardTeam();
        if (team2 != null && team2.getColor().getColor() != null) {
            return team2.getColor().getColor();
        }
        return 16777215;
    }
    
    protected void renderLabelIfPresent(final T entity, final double x, final double y, final double z) {
        if (!this.hasLabel(entity)) {
            return;
        }
        this.renderLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
    }
    
    protected boolean hasLabel(final T entity) {
        return entity.shouldRenderName() && entity.hasCustomName();
    }
    
    protected void renderLabel(final T entity, final double x, final double y, final double z, final String text, final double double9) {
        this.renderLabel(entity, text, x, y, z, 64);
    }
    
    @Nullable
    protected abstract Identifier getTexture(final T arg1);
    
    protected boolean bindEntityTexture(final T entity) {
        final Identifier identifier2 = this.getTexture(entity);
        if (identifier2 == null) {
            return false;
        }
        this.bindTexture(identifier2);
        return true;
    }
    
    public void bindTexture(final Identifier identifier) {
        this.renderManager.textureManager.bindTexture(identifier);
    }
    
    private void renderEntityOnFire(final Entity entity, final double x, final double y, final double double6, final float float8) {
        GlStateManager.disableLighting();
        final SpriteAtlasTexture spriteAtlasTexture9 = MinecraftClient.getInstance().getSpriteAtlas();
        final Sprite sprite10 = spriteAtlasTexture9.getSprite(ModelLoader.FIRE_0);
        final Sprite sprite11 = spriteAtlasTexture9.getSprite(ModelLoader.FIRE_1);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)double6);
        final float float9 = entity.getWidth() * 1.4f;
        GlStateManager.scalef(float9, float9, float9);
        final Tessellator tessellator13 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder14 = tessellator13.getBufferBuilder();
        float float10 = 0.5f;
        final float float11 = 0.0f;
        float float12 = entity.getHeight() / float9;
        float float13 = (float)(entity.y - entity.getBoundingBox().minY);
        GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, -0.3f + (int)float12 * 0.02f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float float14 = 0.0f;
        int integer20 = 0;
        bufferBuilder14.begin(7, VertexFormats.POSITION_UV);
        while (float12 > 0.0f) {
            final Sprite sprite12 = (integer20 % 2 == 0) ? sprite10 : sprite11;
            this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            float float15 = sprite12.getMinU();
            final float float16 = sprite12.getMinV();
            float float17 = sprite12.getMaxU();
            final float float18 = sprite12.getMaxV();
            if (integer20 / 2 % 2 == 0) {
                final float float19 = float17;
                float17 = float15;
                float15 = float19;
            }
            bufferBuilder14.vertex(float10 - 0.0f, 0.0f - float13, float14).texture(float17, float18).next();
            bufferBuilder14.vertex(-float10 - 0.0f, 0.0f - float13, float14).texture(float15, float18).next();
            bufferBuilder14.vertex(-float10 - 0.0f, 1.4f - float13, float14).texture(float15, float16).next();
            bufferBuilder14.vertex(float10 - 0.0f, 1.4f - float13, float14).texture(float17, float16).next();
            float12 -= 0.45f;
            float13 -= 0.45f;
            float10 *= 0.9f;
            float14 += 0.03f;
            ++integer20;
        }
        tessellator13.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }
    
    private void renderShadow(final Entity entity, final double double2, final double double4, final double double6, final float float8, final float float9) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.renderManager.textureManager.bindTexture(EntityRenderer.SHADOW_TEX);
        final ViewableWorld viewableWorld10 = this.getWorld();
        GlStateManager.depthMask(false);
        float float10 = this.c;
        if (entity instanceof MobEntity) {
            final MobEntity mobEntity12 = (MobEntity)entity;
            if (mobEntity12.isChild()) {
                float10 *= 0.5f;
            }
        }
        final double double7 = MathHelper.lerp(float9, entity.prevRenderX, entity.x);
        final double double8 = MathHelper.lerp(float9, entity.prevRenderY, entity.y);
        final double double9 = MathHelper.lerp(float9, entity.prevRenderZ, entity.z);
        final int integer18 = MathHelper.floor(double7 - float10);
        final int integer19 = MathHelper.floor(double7 + float10);
        final int integer20 = MathHelper.floor(double8 - float10);
        final int integer21 = MathHelper.floor(double8);
        final int integer22 = MathHelper.floor(double9 - float10);
        final int integer23 = MathHelper.floor(double9 + float10);
        final double double10 = double2 - double7;
        final double double11 = double4 - double8;
        final double double12 = double6 - double9;
        final Tessellator tessellator30 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder31 = tessellator30.getBufferBuilder();
        bufferBuilder31.begin(7, VertexFormats.POSITION_UV_COLOR);
        for (final BlockPos blockPos33 : BlockPos.iterate(new BlockPos(integer18, integer20, integer22), new BlockPos(integer19, integer21, integer23))) {
            final BlockPos blockPos34 = blockPos33.down();
            final BlockState blockState35 = viewableWorld10.getBlockState(blockPos34);
            if (blockState35.getRenderType() != BlockRenderType.a && viewableWorld10.getLightLevel(blockPos33) > 3) {
                this.projectShadow(blockState35, viewableWorld10, blockPos34, double2, double4, double6, blockPos33, float8, float10, double10, double11, double12);
            }
        }
        tessellator30.draw();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    private ViewableWorld getWorld() {
        return this.renderManager.world;
    }
    
    private void projectShadow(final BlockState blockState, final ViewableWorld viewableWorld, final BlockPos blockPos3, final double double4, final double double6, final double double8, final BlockPos blockPos10, final float float11, final float float12, final double double13, final double double15, final double double17) {
        if (!Block.isShapeFullCube(blockState.getCollisionShape(viewableWorld, blockPos3))) {
            return;
        }
        final VoxelShape voxelShape19 = blockState.getOutlineShape(this.getWorld(), blockPos10.down());
        if (voxelShape19.isEmpty()) {
            return;
        }
        final Tessellator tessellator20 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder21 = tessellator20.getBufferBuilder();
        double double18 = (float11 - (double6 - (blockPos10.getY() + double15)) / 2.0) * 0.5 * this.getWorld().getBrightness(blockPos10);
        if (double18 < 0.0) {
            return;
        }
        if (double18 > 1.0) {
            double18 = 1.0;
        }
        final BoundingBox boundingBox24 = voxelShape19.getBoundingBox();
        final double double19 = blockPos10.getX() + boundingBox24.minX + double13;
        final double double20 = blockPos10.getX() + boundingBox24.maxX + double13;
        final double double21 = blockPos10.getY() + boundingBox24.minY + double15 + 0.015625;
        final double double22 = blockPos10.getZ() + boundingBox24.minZ + double17;
        final double double23 = blockPos10.getZ() + boundingBox24.maxZ + double17;
        final float float13 = (float)((double4 - double19) / 2.0 / float12 + 0.5);
        final float float14 = (float)((double4 - double20) / 2.0 / float12 + 0.5);
        final float float15 = (float)((double8 - double22) / 2.0 / float12 + 0.5);
        final float float16 = (float)((double8 - double23) / 2.0 / float12 + 0.5);
        bufferBuilder21.vertex(double19, double21, double22).texture(float13, float15).color(1.0f, 1.0f, 1.0f, (float)double18).next();
        bufferBuilder21.vertex(double19, double21, double23).texture(float13, float16).color(1.0f, 1.0f, 1.0f, (float)double18).next();
        bufferBuilder21.vertex(double20, double21, double23).texture(float14, float16).color(1.0f, 1.0f, 1.0f, (float)double18).next();
        bufferBuilder21.vertex(double20, double21, double22).texture(float14, float15).color(1.0f, 1.0f, 1.0f, (float)double18).next();
    }
    
    public static void renderBox(final BoundingBox boundingBox, final double double2, final double double4, final double double6) {
        GlStateManager.disableTexture();
        final Tessellator tessellator8 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder9 = tessellator8.getBufferBuilder();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder9.setOffset(double2, double4, double6);
        bufferBuilder9.begin(7, VertexFormats.POSITION_NORMAL);
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder9.vertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0f, 0.0f, 0.0f).next();
        tessellator8.draw();
        bufferBuilder9.setOffset(0.0, 0.0, 0.0);
        GlStateManager.enableTexture();
    }
    
    public void postRender(final Entity entity, final double double2, final double double4, final double double6, final float float8, final float float9) {
        if (this.renderManager.gameOptions == null) {
            return;
        }
        if (this.renderManager.gameOptions.entityShadows && this.c > 0.0f && !entity.isInvisible() && this.renderManager.shouldRenderShadows()) {
            final double double7 = this.renderManager.squaredDistanceToCamera(entity.x, entity.y, entity.z);
            final float float10 = (float)((1.0 - double7 / 256.0) * this.d);
            if (float10 > 0.0f) {
                this.renderShadow(entity, double2, double4, double6, float10, float9);
            }
        }
        if (entity.doesRenderOnFire() && !entity.isSpectator()) {
            this.renderEntityOnFire(entity, double2, double4, double6, float9);
        }
    }
    
    public TextRenderer getFontRenderer() {
        return this.renderManager.getTextRenderer();
    }
    
    protected void renderLabel(final T entity, final String text, final double x, final double y, final double z, final int maxDistance) {
        final double double10 = entity.squaredDistanceTo(this.renderManager.camera.getPos());
        if (double10 > maxDistance * maxDistance) {
            return;
        }
        final boolean boolean12 = entity.isInSneakingPose();
        final float float13 = this.renderManager.cameraYaw;
        final float float14 = this.renderManager.cameraPitch;
        final float float15 = entity.getHeight() + 0.5f - (boolean12 ? 0.25f : 0.0f);
        final int integer16 = "deadmau5".equals(text) ? -10 : 0;
        GameRenderer.renderFloatingText(this.getFontRenderer(), text, (float)x, (float)y + float15, (float)z, integer16, float13, float14, boolean12);
    }
    
    public EntityRenderDispatcher getRenderManager() {
        return this.renderManager;
    }
    
    public boolean hasSecondPass() {
        return false;
    }
    
    public void renderSecondPass(final T entity, final double double2, final double double4, final double double6, final float float8, final float float9) {
    }
    
    public void applyLightmapCoordinates(final T entity) {
        final int integer2 = entity.getLightmapCoordinates();
        final int integer3 = integer2 % 65536;
        final int integer4 = integer2 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer3, (float)integer4);
    }
    
    static {
        SHADOW_TEX = new Identifier("textures/misc/shadow.png");
    }
}
