package net.minecraft.client.render.entity;

import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.client.network.ClientPlayerEntity;
import java.util.Iterator;
import net.minecraft.text.TextFormat;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import java.util.List;
import java.nio.FloatBuffer;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>
{
    private static final Logger LOGGER;
    private static final NativeImageBackedTexture colorOverlayTexture;
    protected M model;
    protected final FloatBuffer colorOverlayBuffer;
    protected final List<FeatureRenderer<T, M>> features;
    protected boolean disableOutlineRender;
    
    public LivingEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final M entityModel, final float float3) {
        super(entityRenderDispatcher);
        this.colorOverlayBuffer = GlAllocationUtils.allocateFloatBuffer(4);
        this.features = Lists.newArrayList();
        this.model = entityModel;
        this.c = float3;
    }
    
    protected final boolean addFeature(final FeatureRenderer<T, M> featureRenderer) {
        return this.features.add(featureRenderer);
    }
    
    @Override
    public M getModel() {
        return this.model;
    }
    
    @Override
    public void render(final T entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.model.handSwingProgress = this.getHandSwingProgress(entity, tickDelta);
        this.model.isRiding = entity.hasVehicle();
        this.model.isChild = entity.isChild();
        try {
            float float10 = MathHelper.lerpAngleDegrees(tickDelta, entity.aL, entity.aK);
            final float float11 = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw);
            float float12 = float11 - float10;
            if (entity.hasVehicle() && entity.getVehicle() instanceof LivingEntity) {
                final LivingEntity livingEntity13 = (LivingEntity)entity.getVehicle();
                float10 = MathHelper.lerpAngleDegrees(tickDelta, livingEntity13.aL, livingEntity13.aK);
                float12 = float11 - float10;
                float float13 = MathHelper.wrapDegrees(float12);
                if (float13 < -85.0f) {
                    float13 = -85.0f;
                }
                if (float13 >= 85.0f) {
                    float13 = 85.0f;
                }
                float10 = float11 - float13;
                if (float13 * float13 > 2500.0f) {
                    float10 += float13 * 0.2f;
                }
                float12 = float11 - float10;
            }
            final float float14 = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
            this.renderLabelIfPresent(entity, x, y, z);
            float float13 = this.getAge(entity, tickDelta);
            this.setupTransforms(entity, float13, float10, tickDelta);
            final float float15 = this.scaleAndTranslate(entity, tickDelta);
            float float16 = 0.0f;
            float float17 = 0.0f;
            if (!entity.hasVehicle() && entity.isAlive()) {
                float16 = MathHelper.lerp(tickDelta, entity.lastLimbDistance, entity.limbDistance);
                float17 = entity.limbAngle - entity.limbDistance * (1.0f - tickDelta);
                if (entity.isChild()) {
                    float17 *= 3.0f;
                }
                if (float16 > 1.0f) {
                    float16 = 1.0f;
                }
            }
            GlStateManager.enableAlphaTest();
            this.model.animateModel(entity, float17, float16, tickDelta);
            this.model.setAngles(entity, float17, float16, float13, float12, float14, float15);
            if (this.e) {
                final boolean boolean18 = this.beforeOutlineRender(entity);
                GlStateManager.enableColorMaterial();
                GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
                if (!this.disableOutlineRender) {
                    this.render(entity, float17, float16, float13, float12, float14, float15);
                }
                if (!entity.isSpectator()) {
                    this.renderFeatures(entity, float17, float16, tickDelta, float13, float12, float14, float15);
                }
                GlStateManager.tearDownSolidRenderingTextureCombine();
                GlStateManager.disableColorMaterial();
                if (boolean18) {
                    this.afterOutlineRender();
                }
            }
            else {
                final boolean boolean18 = this.applyOverlayColor(entity, tickDelta);
                this.render(entity, float17, float16, float13, float12, float14, float15);
                if (boolean18) {
                    this.disableOverlayColor();
                }
                GlStateManager.depthMask(true);
                if (!entity.isSpectator()) {
                    this.renderFeatures(entity, float17, float16, tickDelta, float13, float12, float14, float15);
                }
            }
            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception10) {
            LivingEntityRenderer.LOGGER.error("Couldn't render entity", (Throwable)exception10);
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    public float scaleAndTranslate(final T entity, final float tickDelta) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.scale(entity, tickDelta);
        final float float3 = 0.0625f;
        GlStateManager.translatef(0.0f, -1.501f, 0.0f);
        return 0.0625f;
    }
    
    protected boolean beforeOutlineRender(final T livingEntity) {
        GlStateManager.disableLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        return true;
    }
    
    protected void afterOutlineRender() {
        GlStateManager.enableLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    protected void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final boolean boolean8 = this.c(entity);
        final boolean boolean9 = !boolean8 && !entity.canSeePlayer(MinecraftClient.getInstance().player);
        if (boolean8 || boolean9) {
            if (!this.bindEntityTexture(entity)) {
                return;
            }
            if (boolean9) {
                GlStateManager.setProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
            }
            this.model.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
            if (boolean9) {
                GlStateManager.unsetProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
            }
        }
    }
    
    protected boolean c(final T livingEntity) {
        return !livingEntity.isInvisible() || this.e;
    }
    
    protected boolean applyOverlayColor(final T livingEntity, final float float2) {
        return this.applyOverlayColor(livingEntity, float2, true);
    }
    
    protected boolean applyOverlayColor(final T entity, final float tickDelta, final boolean hasHurtOverlay) {
        final float float4 = entity.getBrightnessAtEyes();
        final int integer5 = this.getOverlayColor(entity, float4, tickDelta);
        final boolean boolean6 = (integer5 >> 24 & 0xFF) > 0;
        final boolean boolean7 = entity.hurtTime > 0 || entity.deathTime > 0;
        if (!boolean6 && !boolean7) {
            return false;
        }
        if (!boolean6 && !hasHurtOverlay) {
            return false;
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        this.colorOverlayBuffer.position(0);
        if (boolean7) {
            this.colorOverlayBuffer.put(1.0f);
            this.colorOverlayBuffer.put(0.0f);
            this.colorOverlayBuffer.put(0.0f);
            this.colorOverlayBuffer.put(0.3f);
        }
        else {
            final float float5 = (integer5 >> 24 & 0xFF) / 255.0f;
            final float float6 = (integer5 >> 16 & 0xFF) / 255.0f;
            final float float7 = (integer5 >> 8 & 0xFF) / 255.0f;
            final float float8 = (integer5 & 0xFF) / 255.0f;
            this.colorOverlayBuffer.put(float6);
            this.colorOverlayBuffer.put(float7);
            this.colorOverlayBuffer.put(float8);
            this.colorOverlayBuffer.put(1.0f - float5);
        }
        this.colorOverlayBuffer.flip();
        GlStateManager.texEnv(8960, 8705, this.colorOverlayBuffer);
        GlStateManager.activeTexture(GLX.GL_TEXTURE2);
        GlStateManager.enableTexture();
        GlStateManager.bindTexture(LivingEntityRenderer.colorOverlayTexture.getGlId());
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        return true;
    }
    
    protected void disableOverlayColor() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.activeTexture(GLX.GL_TEXTURE2);
        GlStateManager.disableTexture();
        GlStateManager.bindTexture(0);
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    @Override
    protected void renderLabelIfPresent(final T entity, final double x, final double y, final double z) {
        if (entity.getPose() == EntityPose.c) {
            final Direction direction8 = entity.getSleepingDirection();
            if (direction8 != null) {
                final float float9 = entity.getEyeHeight(EntityPose.a) - 0.1f;
                GlStateManager.translatef((float)x - direction8.getOffsetX() * float9, (float)y, (float)z - direction8.getOffsetZ() * float9);
                return;
            }
        }
        GlStateManager.translatef((float)x, (float)y, (float)z);
    }
    
    private static float a(final Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    protected void setupTransforms(final T entity, final float pitch, final float yaw, final float delta) {
        final EntityPose entityPose5 = entity.getPose();
        if (entityPose5 != EntityPose.c) {
            GlStateManager.rotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        }
        if (entity.deathTime > 0) {
            float float6 = (entity.deathTime + delta - 1.0f) / 20.0f * 1.6f;
            float6 = MathHelper.sqrt(float6);
            if (float6 > 1.0f) {
                float6 = 1.0f;
            }
            GlStateManager.rotatef(float6 * this.getLyingAngle(entity), 0.0f, 0.0f, 1.0f);
        }
        else if (entity.isUsingRiptide()) {
            GlStateManager.rotatef(-90.0f - entity.pitch, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef((entity.age + delta) * -75.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (entityPose5 == EntityPose.c) {
            final Direction direction6 = entity.getSleepingDirection();
            GlStateManager.rotatef((direction6 != null) ? a(direction6) : yaw, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(this.getLyingAngle(entity), 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(270.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (entity.hasCustomName() || entity instanceof PlayerEntity) {
            final String string6 = TextFormat.stripFormatting(entity.getName().getString());
            if (string6 != null && ("Dinnerbone".equals(string6) || "Grumm".equals(string6)) && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isSkinOverlayVisible(PlayerModelPart.CAPE))) {
                GlStateManager.translatef(0.0f, entity.getHeight() + 0.1f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }
    
    protected float getHandSwingProgress(final T entity, final float tickDelta) {
        return entity.getHandSwingProgress(tickDelta);
    }
    
    protected float getAge(final T entity, final float tickDelta) {
        return entity.age + tickDelta;
    }
    
    protected void renderFeatures(final T entity, final float limbAngle, final float limbDistance, final float tickDelta, final float age, final float headYaw, final float headPitch, final float scale) {
        for (final FeatureRenderer<T, M> featureRenderer10 : this.features) {
            final boolean boolean11 = this.applyOverlayColor(entity, tickDelta, featureRenderer10.hasHurtOverlay());
            featureRenderer10.render(entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
            if (boolean11) {
                this.disableOverlayColor();
            }
        }
    }
    
    protected float getLyingAngle(final T livingEntity) {
        return 90.0f;
    }
    
    protected int getOverlayColor(final T livingEntity, final float float2, final float float3) {
        return 0;
    }
    
    protected void scale(final T entity, final float tickDelta) {
    }
    
    public void renderLabelIfPresent(final T entity, final double x, final double y, final double z) {
        if (!this.hasLabel(entity)) {
            return;
        }
        final double double8 = entity.squaredDistanceTo(this.renderManager.camera.getPos());
        final float float10 = entity.isInSneakingPose() ? 32.0f : 64.0f;
        if (double8 >= float10 * float10) {
            return;
        }
        final String string11 = entity.getDisplayName().getFormattedText();
        GlStateManager.alphaFunc(516, 0.1f);
        this.renderLabel(entity, x, y, z, string11, double8);
    }
    
    @Override
    protected boolean hasLabel(final T entity) {
        final ClientPlayerEntity clientPlayerEntity2 = MinecraftClient.getInstance().player;
        final boolean boolean3 = !entity.canSeePlayer(clientPlayerEntity2);
        if (entity != clientPlayerEntity2) {
            final AbstractTeam abstractTeam4 = entity.getScoreboardTeam();
            final AbstractTeam abstractTeam5 = clientPlayerEntity2.getScoreboardTeam();
            if (abstractTeam4 != null) {
                final AbstractTeam.VisibilityRule visibilityRule6 = abstractTeam4.getNameTagVisibilityRule();
                switch (visibilityRule6) {
                    case ALWAYS: {
                        return boolean3;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDDEN_FOR_OTHER_TEAMS: {
                        return (abstractTeam5 == null) ? boolean3 : (abstractTeam4.isEqual(abstractTeam5) && (abstractTeam4.shouldShowFriendlyInvisibles() || boolean3));
                    }
                    case HIDDEN_FOR_TEAM: {
                        return (abstractTeam5 == null) ? boolean3 : (!abstractTeam4.isEqual(abstractTeam5) && boolean3);
                    }
                    default: {
                        return true;
                    }
                }
            }
        }
        return MinecraftClient.isHudEnabled() && entity != this.renderManager.camera.getFocusedEntity() && boolean3 && !entity.hasPassengers();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        int integer2;
        int integer3;
        colorOverlayTexture = SystemUtil.<NativeImageBackedTexture>consume(new NativeImageBackedTexture(16, 16, false), nativeImageBackedTexture -> {
            nativeImageBackedTexture.getImage().untrack();
            for (integer2 = 0; integer2 < 16; ++integer2) {
                for (integer3 = 0; integer3 < 16; ++integer3) {
                    nativeImageBackedTexture.getImage().setPixelRGBA(integer3, integer2, -1);
                }
            }
            nativeImageBackedTexture.upload();
        });
    }
}
