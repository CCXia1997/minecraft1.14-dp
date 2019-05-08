package net.minecraft.client.render.entity;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.util.UseAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.Hand;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Environment(EnvType.CLIENT)
public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
{
    public PlayerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, false);
    }
    
    public PlayerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final boolean boolean2) {
        super(entityRenderDispatcher, new PlayerEntityModel(0.0f, boolean2), 0.5f);
        this.addFeature((FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, new BipedEntityModel(0.5f), new BipedEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature((FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)new ShoulderParrotFeatureRenderer((FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>>)this));
        this.addFeature((FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)new TridentRiptideFeatureRenderer((FeatureRendererContext<LivingEntity, PlayerEntityModel<LivingEntity>>)this));
    }
    
    @Override
    public void render(final AbstractClientPlayerEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (entity.isMainPlayer() && this.renderManager.camera.getFocusedEntity() != entity) {
            return;
        }
        double double10 = y;
        if (entity.isInSneakingPose()) {
            double10 -= 0.125;
        }
        this.setModelPose(entity);
        GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
        super.render(entity, x, double10, z, yaw, tickDelta);
        GlStateManager.unsetProfile(GlStateManager.RenderMode.PLAYER_SKIN);
    }
    
    private void setModelPose(final AbstractClientPlayerEntity abstractClientPlayerEntity) {
        final PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel2 = ((LivingEntityRenderer<T, PlayerEntityModel<AbstractClientPlayerEntity>>)this).getModel();
        if (abstractClientPlayerEntity.isSpectator()) {
            playerEntityModel2.setVisible(false);
            playerEntityModel2.head.visible = true;
            playerEntityModel2.headwear.visible = true;
        }
        else {
            final ItemStack itemStack3 = abstractClientPlayerEntity.getMainHandStack();
            final ItemStack itemStack4 = abstractClientPlayerEntity.getOffHandStack();
            playerEntityModel2.setVisible(true);
            playerEntityModel2.headwear.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.HEAD);
            playerEntityModel2.bodyOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.BODY);
            playerEntityModel2.leftLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.LEFT_LEG);
            playerEntityModel2.rightLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.RIGHT_LEG);
            playerEntityModel2.leftArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.LEFT_ARM);
            playerEntityModel2.rightArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.RIGHT_ARM);
            playerEntityModel2.isSneaking = abstractClientPlayerEntity.isInSneakingPose();
            final BipedEntityModel.ArmPose armPose5 = this.a(abstractClientPlayerEntity, itemStack3, itemStack4, Hand.a);
            final BipedEntityModel.ArmPose armPose6 = this.a(abstractClientPlayerEntity, itemStack3, itemStack4, Hand.b);
            if (abstractClientPlayerEntity.getMainHand() == AbsoluteHand.b) {
                playerEntityModel2.rightArmPose = armPose5;
                playerEntityModel2.leftArmPose = armPose6;
            }
            else {
                playerEntityModel2.rightArmPose = armPose6;
                playerEntityModel2.leftArmPose = armPose5;
            }
        }
    }
    
    private BipedEntityModel.ArmPose a(final AbstractClientPlayerEntity abstractClientPlayerEntity, final ItemStack itemStack2, final ItemStack itemStack3, final Hand hand) {
        BipedEntityModel.ArmPose armPose5 = BipedEntityModel.ArmPose.a;
        final ItemStack itemStack4 = (hand == Hand.a) ? itemStack2 : itemStack3;
        if (!itemStack4.isEmpty()) {
            armPose5 = BipedEntityModel.ArmPose.b;
            if (abstractClientPlayerEntity.getItemUseTimeLeft() > 0) {
                final UseAction useAction7 = itemStack4.getUseAction();
                if (useAction7 == UseAction.d) {
                    armPose5 = BipedEntityModel.ArmPose.c;
                }
                else if (useAction7 == UseAction.e) {
                    armPose5 = BipedEntityModel.ArmPose.d;
                }
                else if (useAction7 == UseAction.f) {
                    armPose5 = BipedEntityModel.ArmPose.e;
                }
                else if (useAction7 == UseAction.g && hand == abstractClientPlayerEntity.getActiveHand()) {
                    armPose5 = BipedEntityModel.ArmPose.f;
                }
            }
            else {
                final boolean boolean7 = itemStack2.getItem() == Items.py;
                final boolean boolean8 = CrossbowItem.isCharged(itemStack2);
                final boolean boolean9 = itemStack3.getItem() == Items.py;
                final boolean boolean10 = CrossbowItem.isCharged(itemStack3);
                if (boolean7 && boolean8) {
                    armPose5 = BipedEntityModel.ArmPose.g;
                }
                if (boolean9 && boolean10 && itemStack2.getItem().getUseAction(itemStack2) == UseAction.a) {
                    armPose5 = BipedEntityModel.ArmPose.g;
                }
            }
        }
        return armPose5;
    }
    
    public Identifier getTexture(final AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return abstractClientPlayerEntity.getSkinTexture();
    }
    
    @Override
    protected void scale(final AbstractClientPlayerEntity entity, final float tickDelta) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
    
    protected void renderLabel(final AbstractClientPlayerEntity abstractClientPlayerEntity, final double x, double y, final double z, final String text, final double double9) {
        if (double9 < 100.0) {
            final Scoreboard scoreboard11 = abstractClientPlayerEntity.getScoreboard();
            final ScoreboardObjective scoreboardObjective12 = scoreboard11.getObjectiveForSlot(2);
            if (scoreboardObjective12 != null) {
                final ScoreboardPlayerScore scoreboardPlayerScore13 = scoreboard11.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective12);
                this.renderLabel((T)abstractClientPlayerEntity, scoreboardPlayerScore13.getScore() + " " + scoreboardObjective12.getDisplayName().getFormattedText(), x, y, z, 64);
                final double n = y;
                this.getFontRenderer().getClass();
                y = n + 9.0f * 1.15f * 0.025f;
            }
        }
        super.renderLabel((T)abstractClientPlayerEntity, x, y, z, text, double9);
    }
    
    public void renderRightArm(final AbstractClientPlayerEntity abstractClientPlayerEntity) {
        final float float2 = 1.0f;
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        final float float3 = 0.0625f;
        final PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel4 = ((LivingEntityRenderer<T, PlayerEntityModel<AbstractClientPlayerEntity>>)this).getModel();
        this.setModelPose(abstractClientPlayerEntity);
        GlStateManager.enableBlend();
        playerEntityModel4.handSwingProgress = 0.0f;
        playerEntityModel4.isSneaking = false;
        playerEntityModel4.setAngles(abstractClientPlayerEntity, playerEntityModel4.p = 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        playerEntityModel4.rightArm.pitch = 0.0f;
        playerEntityModel4.rightArm.render(0.0625f);
        playerEntityModel4.rightArmOverlay.pitch = 0.0f;
        playerEntityModel4.rightArmOverlay.render(0.0625f);
        GlStateManager.disableBlend();
    }
    
    public void renderLeftArm(final AbstractClientPlayerEntity abstractClientPlayerEntity) {
        final float float2 = 1.0f;
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        final float float3 = 0.0625f;
        final PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel4 = ((LivingEntityRenderer<T, PlayerEntityModel<AbstractClientPlayerEntity>>)this).getModel();
        this.setModelPose(abstractClientPlayerEntity);
        GlStateManager.enableBlend();
        playerEntityModel4.isSneaking = false;
        playerEntityModel4.handSwingProgress = 0.0f;
        playerEntityModel4.setAngles(abstractClientPlayerEntity, playerEntityModel4.p = 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        playerEntityModel4.leftArm.pitch = 0.0f;
        playerEntityModel4.leftArm.render(0.0625f);
        playerEntityModel4.leftArmOverlay.pitch = 0.0f;
        playerEntityModel4.leftArmOverlay.render(0.0625f);
        GlStateManager.disableBlend();
    }
    
    @Override
    protected void setupTransforms(final AbstractClientPlayerEntity entity, final float pitch, final float yaw, final float delta) {
        final float float5 = entity.a(delta);
        if (entity.isFallFlying()) {
            super.setupTransforms(entity, pitch, yaw, delta);
            final float float6 = entity.ds() + delta;
            final float float7 = MathHelper.clamp(float6 * float6 / 100.0f, 0.0f, 1.0f);
            if (!entity.isUsingRiptide()) {
                GlStateManager.rotatef(float7 * (-90.0f - entity.pitch), 1.0f, 0.0f, 0.0f);
            }
            final Vec3d vec3d8 = entity.getRotationVec(delta);
            final Vec3d vec3d9 = entity.getVelocity();
            final double double10 = Entity.squaredHorizontalLength(vec3d9);
            final double double11 = Entity.squaredHorizontalLength(vec3d8);
            if (double10 > 0.0 && double11 > 0.0) {
                final double double12 = (vec3d9.x * vec3d8.x + vec3d9.z * vec3d8.z) / (Math.sqrt(double10) * Math.sqrt(double11));
                final double double13 = vec3d9.x * vec3d8.z - vec3d9.z * vec3d8.x;
                GlStateManager.rotatef((float)(Math.signum(double13) * Math.acos(double12)) * 180.0f / 3.1415927f, 0.0f, 1.0f, 0.0f);
            }
        }
        else if (float5 > 0.0f) {
            super.setupTransforms(entity, pitch, yaw, delta);
            final float float6 = entity.isInsideWater() ? (-90.0f - entity.pitch) : -90.0f;
            final float float7 = MathHelper.lerp(float5, 0.0f, float6);
            GlStateManager.rotatef(float7, 1.0f, 0.0f, 0.0f);
            if (entity.isInSwimmingPose()) {
                GlStateManager.translatef(0.0f, -1.0f, 0.3f);
            }
        }
        else {
            super.setupTransforms(entity, pitch, yaw, delta);
        }
    }
}
