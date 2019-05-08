package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

@Environment(EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T>
{
    private static final Identifier SKIN;
    protected final EntityModel<T> model;
    
    public MinecartEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new MinecartEntityModel<T>();
        this.c = 0.7f;
    }
    
    @Override
    public void render(final T entity, double x, double y, double z, float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        long long10 = entity.getEntityId() * 493286711L;
        long10 = long10 * long10 * 4392167121L + long10 * 98761L;
        final float float12 = (((long10 >> 16 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float float13 = (((long10 >> 20 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float float14 = (((long10 >> 24 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        GlStateManager.translatef(float12, float13, float14);
        final double double15 = MathHelper.lerp(tickDelta, entity.prevRenderX, entity.x);
        final double double16 = MathHelper.lerp(tickDelta, entity.prevRenderY, entity.y);
        final double double17 = MathHelper.lerp(tickDelta, entity.prevRenderZ, entity.z);
        final double double18 = 0.30000001192092896;
        final Vec3d vec3d23 = entity.k(double15, double16, double17);
        float float15 = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        if (vec3d23 != null) {
            Vec3d vec3d24 = entity.a(double15, double16, double17, 0.30000001192092896);
            Vec3d vec3d25 = entity.a(double15, double16, double17, -0.30000001192092896);
            if (vec3d24 == null) {
                vec3d24 = vec3d23;
            }
            if (vec3d25 == null) {
                vec3d25 = vec3d23;
            }
            x += vec3d23.x - double15;
            y += (vec3d24.y + vec3d25.y) / 2.0 - double16;
            z += vec3d23.z - double17;
            Vec3d vec3d26 = vec3d25.add(-vec3d24.x, -vec3d24.y, -vec3d24.z);
            if (vec3d26.length() != 0.0) {
                vec3d26 = vec3d26.normalize();
                yaw = (float)(Math.atan2(vec3d26.z, vec3d26.x) * 180.0 / 3.141592653589793);
                float15 = (float)(Math.atan(vec3d26.y) * 73.0);
            }
        }
        GlStateManager.translatef((float)x, (float)y + 0.375f, (float)z);
        GlStateManager.rotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-float15, 0.0f, 0.0f, 1.0f);
        final float float16 = entity.m() - tickDelta;
        float float17 = entity.l() - tickDelta;
        if (float17 < 0.0f) {
            float17 = 0.0f;
        }
        if (float16 > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(float16) * float16 * float17 / 10.0f * entity.n(), 1.0f, 0.0f, 0.0f);
        }
        final int integer27 = entity.getBlockOffset();
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        final BlockState blockState28 = entity.getContainedBlock();
        if (blockState28.getRenderType() != BlockRenderType.a) {
            GlStateManager.pushMatrix();
            this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            final float float18 = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(-0.5f, (integer27 - 8) / 16.0f, 0.5f);
            this.renderBlock(entity, tickDelta, blockState28);
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.bindEntityTexture(entity);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.model.render(entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final T abstractMinecartEntity) {
        return MinecartEntityRenderer.SKIN;
    }
    
    protected void renderBlock(final T entity, final float delta, final BlockState state) {
        GlStateManager.pushMatrix();
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(state, entity.getBrightnessAtEyes());
        GlStateManager.popMatrix();
    }
    
    static {
        SKIN = new Identifier("textures/entity/minecart.png");
    }
}
