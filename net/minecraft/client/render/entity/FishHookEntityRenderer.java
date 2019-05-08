package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Items;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.FishHookEntity;

@Environment(EnvType.CLIENT)
public class FishHookEntityRenderer extends EntityRenderer<FishHookEntity>
{
    private static final Identifier SKIN;
    
    public FishHookEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    public void render(final FishHookEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final PlayerEntity playerEntity10 = entity.getOwner();
        if (playerEntity10 == null || this.e) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        this.bindEntityTexture(entity);
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        final float float13 = 1.0f;
        final float float14 = 0.5f;
        final float float15 = 0.5f;
        GlStateManager.rotatef(180.0f - this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.renderManager.gameOptions.perspective == 2) ? -1 : 1) * -this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        bufferBuilder12.begin(7, VertexFormats.POSITION_UV_NORMAL);
        bufferBuilder12.vertex(-0.5, -0.5, 0.0).texture(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder12.vertex(0.5, -0.5, 0.0).texture(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder12.vertex(0.5, 0.5, 0.0).texture(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder12.vertex(-0.5, 0.5, 0.0).texture(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        tessellator11.draw();
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        int integer16 = (playerEntity10.getMainHand() == AbsoluteHand.b) ? 1 : -1;
        final ItemStack itemStack17 = playerEntity10.getMainHandStack();
        if (itemStack17.getItem() != Items.kY) {
            integer16 = -integer16;
        }
        final float float16 = playerEntity10.getHandSwingProgress(tickDelta);
        final float float17 = MathHelper.sin(MathHelper.sqrt(float16) * 3.1415927f);
        final float float18 = MathHelper.lerp(tickDelta, playerEntity10.aL, playerEntity10.aK) * 0.017453292f;
        final double double21 = MathHelper.sin(float18);
        final double double22 = MathHelper.cos(float18);
        final double double23 = integer16 * 0.35;
        final double double24 = 0.8;
        double double25;
        double double26;
        double double27;
        double double28;
        if ((this.renderManager.gameOptions != null && this.renderManager.gameOptions.perspective > 0) || playerEntity10 != MinecraftClient.getInstance().player) {
            double25 = MathHelper.lerp(tickDelta, playerEntity10.prevX, playerEntity10.x) - double22 * double23 - double21 * 0.8;
            double26 = playerEntity10.prevY + playerEntity10.getStandingEyeHeight() + (playerEntity10.y - playerEntity10.prevY) * tickDelta - 0.45;
            double27 = MathHelper.lerp(tickDelta, playerEntity10.prevZ, playerEntity10.z) - double21 * double23 + double22 * 0.8;
            double28 = (playerEntity10.isInSneakingPose() ? -0.1875 : 0.0);
        }
        else {
            double double29 = this.renderManager.gameOptions.fov;
            double29 /= 100.0;
            Vec3d vec3d39 = new Vec3d(integer16 * -0.36 * double29, -0.045 * double29, 0.4);
            vec3d39 = vec3d39.rotateX(-MathHelper.lerp(tickDelta, playerEntity10.prevPitch, playerEntity10.pitch) * 0.017453292f);
            vec3d39 = vec3d39.rotateY(-MathHelper.lerp(tickDelta, playerEntity10.prevYaw, playerEntity10.yaw) * 0.017453292f);
            vec3d39 = vec3d39.rotateY(float17 * 0.5f);
            vec3d39 = vec3d39.rotateX(-float17 * 0.7f);
            double25 = MathHelper.lerp(tickDelta, playerEntity10.prevX, playerEntity10.x) + vec3d39.x;
            double26 = MathHelper.lerp(tickDelta, playerEntity10.prevY, playerEntity10.y) + vec3d39.y;
            double27 = MathHelper.lerp(tickDelta, playerEntity10.prevZ, playerEntity10.z) + vec3d39.z;
            double28 = playerEntity10.getStandingEyeHeight();
        }
        double double29 = MathHelper.lerp(tickDelta, entity.prevX, entity.x);
        final double double30 = MathHelper.lerp(tickDelta, entity.prevY, entity.y) + 0.25;
        final double double31 = MathHelper.lerp(tickDelta, entity.prevZ, entity.z);
        final double double32 = (float)(double25 - double29);
        final double double33 = (float)(double26 - double30) + double28;
        final double double34 = (float)(double27 - double31);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        bufferBuilder12.begin(3, VertexFormats.POSITION_COLOR);
        final int integer17 = 16;
        for (int integer18 = 0; integer18 <= 16; ++integer18) {
            final float float19 = integer18 / 16.0f;
            bufferBuilder12.vertex(x + double32 * float19, y + double33 * (float19 * float19 + float19) * 0.5 + 0.25, z + double34 * float19).color(0, 0, 0, 255).next();
        }
        tessellator11.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final FishHookEntity fishHookEntity) {
        return FishHookEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/fishing_hook.png");
    }
}
