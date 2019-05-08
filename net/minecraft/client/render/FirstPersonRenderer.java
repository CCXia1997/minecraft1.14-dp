package net.minecraft.client.render;

import java.util.Objects;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.block.BlockRenderType;
import net.minecraft.world.BlockView;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import com.google.common.base.MoreObjects;
import net.minecraft.util.Hand;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import net.minecraft.item.FilledMapItem;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Item;
import net.minecraft.block.BlockRenderLayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FirstPersonRenderer
{
    private static final Identifier MAP_BACKGROUND_TEX;
    private static final Identifier UNDERWATER_TEX;
    private final MinecraftClient client;
    private ItemStack mainHand;
    private ItemStack offHand;
    private float equipProgressMainHand;
    private float prevEquipProgressMainHand;
    private float equipProgressOffHand;
    private float prevEquipProgressOffHand;
    private final EntityRenderDispatcher renderManager;
    private final ItemRenderer itemRenderer;
    
    public FirstPersonRenderer(final MinecraftClient minecraftClient) {
        this.mainHand = ItemStack.EMPTY;
        this.offHand = ItemStack.EMPTY;
        this.client = minecraftClient;
        this.renderManager = minecraftClient.getEntityRenderManager();
        this.itemRenderer = minecraftClient.getItemRenderer();
    }
    
    public void renderItem(final LivingEntity holder, final ItemStack stack, final ModelTransformation.Type type) {
        this.renderItemFromSide(holder, stack, type, false);
    }
    
    public void renderItemFromSide(final LivingEntity holder, final ItemStack stack, final ModelTransformation.Type transformation, final boolean boolean4) {
        if (stack.isEmpty()) {
            return;
        }
        final Item item5 = stack.getItem();
        final Block block6 = Block.getBlockFromItem(item5);
        GlStateManager.pushMatrix();
        final boolean boolean5 = this.itemRenderer.hasDepthInGui(stack) && block6.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
        if (boolean5) {
            GlStateManager.depthMask(false);
        }
        this.itemRenderer.renderHeldItem(stack, holder, transformation, boolean4);
        if (boolean5) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    private void rotate(final float pitch, final float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(yaw, 0.0f, 1.0f, 0.0f);
        GuiLighting.enable();
        GlStateManager.popMatrix();
    }
    
    private void applyLightmap() {
        final AbstractClientPlayerEntity abstractClientPlayerEntity1 = this.client.player;
        final int integer2 = this.client.world.getLightmapIndex(new BlockPos(abstractClientPlayerEntity1.x, abstractClientPlayerEntity1.y + abstractClientPlayerEntity1.getStandingEyeHeight(), abstractClientPlayerEntity1.z), 0);
        final float float3 = (float)(integer2 & 0xFFFF);
        final float float4 = (float)(integer2 >> 16);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, float3, float4);
    }
    
    private void applyCameraAngles(final float tickDelta) {
        final ClientPlayerEntity clientPlayerEntity2 = this.client.player;
        final float float3 = MathHelper.lerp(tickDelta, clientPlayerEntity2.lastRenderPitch, clientPlayerEntity2.renderPitch);
        final float float4 = MathHelper.lerp(tickDelta, clientPlayerEntity2.lastRenderYaw, clientPlayerEntity2.renderYaw);
        GlStateManager.rotatef((clientPlayerEntity2.getPitch(tickDelta) - float3) * 0.1f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef((clientPlayerEntity2.getYaw(tickDelta) - float4) * 0.1f, 0.0f, 1.0f, 0.0f);
    }
    
    private float getMapAngle(final float float1) {
        float float2 = 1.0f - float1 / 45.0f + 0.1f;
        float2 = MathHelper.clamp(float2, 0.0f, 1.0f);
        float2 = -MathHelper.cos(float2 * 3.1415927f) * 0.5f + 0.5f;
        return float2;
    }
    
    private void renderArms() {
        if (this.client.player.isInvisible()) {
            return;
        }
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.renderArm(AbsoluteHand.b);
        this.renderArm(AbsoluteHand.a);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }
    
    private void renderArm(final AbsoluteHand absoluteHand) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        final EntityRenderer<AbstractClientPlayerEntity> entityRenderer2 = this.renderManager.<ClientPlayerEntity, EntityRenderer<AbstractClientPlayerEntity>>getRenderer(this.client.player);
        final PlayerEntityRenderer playerEntityRenderer3 = (PlayerEntityRenderer)entityRenderer2;
        GlStateManager.pushMatrix();
        final float float4 = (absoluteHand == AbsoluteHand.b) ? 1.0f : -1.0f;
        GlStateManager.rotatef(92.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float4 * -41.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(float4 * 0.3f, -1.1f, 0.45f);
        if (absoluteHand == AbsoluteHand.b) {
            playerEntityRenderer3.renderRightArm(this.client.player);
        }
        else {
            playerEntityRenderer3.renderLeftArm(this.client.player);
        }
        GlStateManager.popMatrix();
    }
    
    private void renderMapInOneHand(final float equipProgress, final AbsoluteHand hand, final float float3, final ItemStack item) {
        final float float4 = (hand == AbsoluteHand.b) ? 1.0f : -1.0f;
        GlStateManager.translatef(float4 * 0.125f, -0.125f, 0.0f);
        if (!this.client.player.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(float4 * 10.0f, 0.0f, 0.0f, 1.0f);
            this.renderArmHoldingItem(equipProgress, float3, hand);
            GlStateManager.popMatrix();
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(float4 * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
        final float float5 = MathHelper.sqrt(float3);
        final float float6 = MathHelper.sin(float5 * 3.1415927f);
        final float float7 = -0.5f * float6;
        final float float8 = 0.4f * MathHelper.sin(float5 * 6.2831855f);
        final float float9 = -0.3f * MathHelper.sin(float3 * 3.1415927f);
        GlStateManager.translatef(float4 * float7, float8 - 0.3f * float6, float9);
        GlStateManager.rotatef(float6 * -45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float4 * float6 * -30.0f, 0.0f, 1.0f, 0.0f);
        this.renderFirstPersonMap(item);
        GlStateManager.popMatrix();
    }
    
    private void renderMapInBothHands(final float pitch, final float equipProgress, final float float3) {
        final float float4 = MathHelper.sqrt(float3);
        final float float5 = -0.2f * MathHelper.sin(float3 * 3.1415927f);
        final float float6 = -0.4f * MathHelper.sin(float4 * 3.1415927f);
        GlStateManager.translatef(0.0f, -float5 / 2.0f, float6);
        final float float7 = this.getMapAngle(pitch);
        GlStateManager.translatef(0.0f, 0.04f + equipProgress * -1.2f + float7 * -0.5f, -0.72f);
        GlStateManager.rotatef(float7 * -85.0f, 1.0f, 0.0f, 0.0f);
        this.renderArms();
        final float float8 = MathHelper.sin(float4 * 3.1415927f);
        GlStateManager.rotatef(float8 * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(this.mainHand);
    }
    
    private void renderFirstPersonMap(final ItemStack map) {
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.scalef(0.38f, 0.38f, 0.38f);
        GlStateManager.disableLighting();
        this.client.getTextureManager().bindTexture(FirstPersonRenderer.MAP_BACKGROUND_TEX);
        final Tessellator tessellator2 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder3 = tessellator2.getBufferBuilder();
        GlStateManager.translatef(-0.5f, -0.5f, 0.0f);
        GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f);
        bufferBuilder3.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder3.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
        bufferBuilder3.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
        bufferBuilder3.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
        bufferBuilder3.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
        tessellator2.draw();
        final MapState mapState4 = FilledMapItem.getOrCreateMapState(map, this.client.world);
        if (mapState4 != null) {
            this.client.gameRenderer.getMapRenderer().draw(mapState4, false);
        }
        GlStateManager.enableLighting();
    }
    
    private void renderArmHoldingItem(final float float1, final float float2, final AbsoluteHand arm) {
        final boolean boolean4 = arm != AbsoluteHand.a;
        final float float3 = boolean4 ? 1.0f : -1.0f;
        final float float4 = MathHelper.sqrt(float2);
        final float float5 = -0.3f * MathHelper.sin(float4 * 3.1415927f);
        final float float6 = 0.4f * MathHelper.sin(float4 * 6.2831855f);
        final float float7 = -0.4f * MathHelper.sin(float2 * 3.1415927f);
        GlStateManager.translatef(float3 * (float5 + 0.64000005f), float6 - 0.6f + float1 * -0.6f, float7 - 0.71999997f);
        GlStateManager.rotatef(float3 * 45.0f, 0.0f, 1.0f, 0.0f);
        final float float8 = MathHelper.sin(float2 * float2 * 3.1415927f);
        final float float9 = MathHelper.sin(float4 * 3.1415927f);
        GlStateManager.rotatef(float3 * float9 * 70.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float3 * float8 * -20.0f, 0.0f, 0.0f, 1.0f);
        final AbstractClientPlayerEntity abstractClientPlayerEntity12 = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity12.getSkinTexture());
        GlStateManager.translatef(float3 * -1.0f, 3.6f, 3.5f);
        GlStateManager.rotatef(float3 * 120.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(200.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float3 * -135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(float3 * 5.6f, 0.0f, 0.0f);
        final PlayerEntityRenderer playerEntityRenderer13 = this.renderManager.<AbstractClientPlayerEntity, PlayerEntityRenderer>getRenderer(abstractClientPlayerEntity12);
        GlStateManager.disableCull();
        if (boolean4) {
            playerEntityRenderer13.renderRightArm(abstractClientPlayerEntity12);
        }
        else {
            playerEntityRenderer13.renderLeftArm(abstractClientPlayerEntity12);
        }
        GlStateManager.enableCull();
    }
    
    private void applyEatOrDrinkTransformation(final float tickDelta, final AbsoluteHand hand, final ItemStack item) {
        final float float4 = this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f;
        final float float5 = float4 / item.getMaxUseTime();
        if (float5 < 0.8f) {
            final float float6 = MathHelper.abs(MathHelper.cos(float4 / 4.0f * 3.1415927f) * 0.1f);
            GlStateManager.translatef(0.0f, float6, 0.0f);
        }
        final float float6 = 1.0f - (float)Math.pow(float5, 27.0);
        final int integer7 = (hand == AbsoluteHand.b) ? 1 : -1;
        GlStateManager.translatef(float6 * 0.6f * integer7, float6 * -0.5f, float6 * 0.0f);
        GlStateManager.rotatef(integer7 * float6 * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float6 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(integer7 * float6 * 30.0f, 0.0f, 0.0f, 1.0f);
    }
    
    private void a(final AbsoluteHand absoluteHand, final float float2) {
        final int integer3 = (absoluteHand == AbsoluteHand.b) ? 1 : -1;
        final float float3 = MathHelper.sin(float2 * float2 * 3.1415927f);
        GlStateManager.rotatef(integer3 * (45.0f + float3 * -20.0f), 0.0f, 1.0f, 0.0f);
        final float float4 = MathHelper.sin(MathHelper.sqrt(float2) * 3.1415927f);
        GlStateManager.rotatef(integer3 * float4 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(float4 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(integer3 * -45.0f, 0.0f, 1.0f, 0.0f);
    }
    
    private void applyHandOffset(final AbsoluteHand hand, final float float2) {
        final int integer3 = (hand == AbsoluteHand.b) ? 1 : -1;
        GlStateManager.translatef(integer3 * 0.56f, -0.52f + float2 * -0.6f, -0.72f);
    }
    
    public void renderFirstPersonItem(final float tickDelta) {
        final AbstractClientPlayerEntity abstractClientPlayerEntity2 = this.client.player;
        final float float3 = abstractClientPlayerEntity2.getHandSwingProgress(tickDelta);
        final Hand hand4 = MoreObjects.<Hand>firstNonNull(abstractClientPlayerEntity2.preferredHand, Hand.a);
        final float float4 = MathHelper.lerp(tickDelta, abstractClientPlayerEntity2.prevPitch, abstractClientPlayerEntity2.pitch);
        final float float5 = MathHelper.lerp(tickDelta, abstractClientPlayerEntity2.prevYaw, abstractClientPlayerEntity2.yaw);
        boolean boolean7 = true;
        boolean boolean8 = true;
        if (abstractClientPlayerEntity2.isUsingItem()) {
            final ItemStack itemStack9 = abstractClientPlayerEntity2.getActiveItem();
            if (itemStack9.getItem() == Items.jf || itemStack9.getItem() == Items.py) {
                boolean7 = (abstractClientPlayerEntity2.getActiveHand() == Hand.a);
                boolean8 = !boolean7;
            }
            final Hand hand5 = abstractClientPlayerEntity2.getActiveHand();
            if (hand5 == Hand.a) {
                final ItemStack itemStack10 = abstractClientPlayerEntity2.getOffHandStack();
                if (itemStack10.getItem() == Items.py && CrossbowItem.isCharged(itemStack10)) {
                    boolean8 = false;
                }
            }
        }
        else {
            final ItemStack itemStack9 = abstractClientPlayerEntity2.getMainHandStack();
            final ItemStack itemStack11 = abstractClientPlayerEntity2.getOffHandStack();
            if (itemStack9.getItem() == Items.py && CrossbowItem.isCharged(itemStack9)) {
                boolean8 = !boolean7;
            }
            if (itemStack11.getItem() == Items.py && CrossbowItem.isCharged(itemStack11)) {
                boolean7 = !itemStack9.isEmpty();
                boolean8 = !boolean7;
            }
        }
        this.rotate(float4, float5);
        this.applyLightmap();
        this.applyCameraAngles(tickDelta);
        GlStateManager.enableRescaleNormal();
        if (boolean7) {
            final float float6 = (hand4 == Hand.a) ? float3 : 0.0f;
            final float float7 = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(abstractClientPlayerEntity2, tickDelta, float4, Hand.a, float6, this.mainHand, float7);
        }
        if (boolean8) {
            final float float6 = (hand4 == Hand.b) ? float3 : 0.0f;
            final float float7 = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(abstractClientPlayerEntity2, tickDelta, float4, Hand.b, float6, this.offHand, float7);
        }
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
    }
    
    public void renderFirstPersonItem(final AbstractClientPlayerEntity player, final float tickDelta, final float pitch, final Hand hand, final float float5, final ItemStack item, final float equipProgress) {
        final boolean boolean8 = hand == Hand.a;
        final AbsoluteHand absoluteHand9 = boolean8 ? player.getMainHand() : player.getMainHand().getOpposite();
        GlStateManager.pushMatrix();
        if (item.isEmpty()) {
            if (boolean8 && !player.isInvisible()) {
                this.renderArmHoldingItem(equipProgress, float5, absoluteHand9);
            }
        }
        else if (item.getItem() == Items.lV) {
            if (boolean8 && this.offHand.isEmpty()) {
                this.renderMapInBothHands(pitch, equipProgress, float5);
            }
            else {
                this.renderMapInOneHand(equipProgress, absoluteHand9, float5, item);
            }
        }
        else if (item.getItem() == Items.py) {
            final boolean boolean9 = CrossbowItem.isCharged(item);
            final boolean boolean10 = absoluteHand9 == AbsoluteHand.b;
            final int integer12 = boolean10 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                this.applyHandOffset(absoluteHand9, equipProgress);
                GlStateManager.translatef(integer12 * -0.4785682f, -0.094387f, 0.05731531f);
                GlStateManager.rotatef(-11.935f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(integer12 * 65.3f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(integer12 * -9.785f, 0.0f, 0.0f, 1.0f);
                final float float6 = item.getMaxUseTime() - (this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                float float7 = float6 / CrossbowItem.getPullTime(item);
                if (float7 > 1.0f) {
                    float7 = 1.0f;
                }
                if (float7 > 0.1f) {
                    final float float8 = MathHelper.sin((float6 - 0.1f) * 1.3f);
                    final float float9 = float7 - 0.1f;
                    final float float10 = float8 * float9;
                    GlStateManager.translatef(float10 * 0.0f, float10 * 0.004f, float10 * 0.0f);
                }
                GlStateManager.translatef(float7 * 0.0f, float7 * 0.0f, float7 * 0.04f);
                GlStateManager.scalef(1.0f, 1.0f, 1.0f + float7 * 0.2f);
                GlStateManager.rotatef(integer12 * 45.0f, 0.0f, -1.0f, 0.0f);
            }
            else {
                final float float6 = -0.4f * MathHelper.sin(MathHelper.sqrt(float5) * 3.1415927f);
                final float float7 = 0.2f * MathHelper.sin(MathHelper.sqrt(float5) * 6.2831855f);
                final float float8 = -0.2f * MathHelper.sin(float5 * 3.1415927f);
                GlStateManager.translatef(integer12 * float6, float7, float8);
                this.applyHandOffset(absoluteHand9, equipProgress);
                this.a(absoluteHand9, float5);
                if (boolean9 && float5 < 0.001f) {
                    GlStateManager.translatef(integer12 * -0.641864f, 0.0f, 0.0f);
                    GlStateManager.rotatef(integer12 * 10.0f, 0.0f, 1.0f, 0.0f);
                }
            }
            this.renderItemFromSide(player, item, boolean10 ? ModelTransformation.Type.e : ModelTransformation.Type.d, !boolean10);
        }
        else {
            final boolean boolean9 = absoluteHand9 == AbsoluteHand.b;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                final int integer13 = boolean9 ? 1 : -1;
                switch (item.getUseAction()) {
                    case a: {
                        this.applyHandOffset(absoluteHand9, equipProgress);
                        break;
                    }
                    case b:
                    case c: {
                        this.applyEatOrDrinkTransformation(tickDelta, absoluteHand9, item);
                        this.applyHandOffset(absoluteHand9, equipProgress);
                        break;
                    }
                    case d: {
                        this.applyHandOffset(absoluteHand9, equipProgress);
                        break;
                    }
                    case e: {
                        this.applyHandOffset(absoluteHand9, equipProgress);
                        GlStateManager.translatef(integer13 * -0.2785682f, 0.18344387f, 0.15731531f);
                        GlStateManager.rotatef(-13.935f, 1.0f, 0.0f, 0.0f);
                        GlStateManager.rotatef(integer13 * 35.3f, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotatef(integer13 * -9.785f, 0.0f, 0.0f, 1.0f);
                        final float float11 = item.getMaxUseTime() - (this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float float6 = float11 / 20.0f;
                        float6 = (float6 * float6 + float6 * 2.0f) / 3.0f;
                        if (float6 > 1.0f) {
                            float6 = 1.0f;
                        }
                        if (float6 > 0.1f) {
                            final float float7 = MathHelper.sin((float11 - 0.1f) * 1.3f);
                            final float float8 = float6 - 0.1f;
                            final float float9 = float7 * float8;
                            GlStateManager.translatef(float9 * 0.0f, float9 * 0.004f, float9 * 0.0f);
                        }
                        GlStateManager.translatef(float6 * 0.0f, float6 * 0.0f, float6 * 0.04f);
                        GlStateManager.scalef(1.0f, 1.0f, 1.0f + float6 * 0.2f);
                        GlStateManager.rotatef(integer13 * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                    case f: {
                        this.applyHandOffset(absoluteHand9, equipProgress);
                        GlStateManager.translatef(integer13 * -0.5f, 0.7f, 0.1f);
                        GlStateManager.rotatef(-55.0f, 1.0f, 0.0f, 0.0f);
                        GlStateManager.rotatef(integer13 * 35.3f, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotatef(integer13 * -9.785f, 0.0f, 0.0f, 1.0f);
                        final float float11 = item.getMaxUseTime() - (this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float float6 = float11 / 10.0f;
                        if (float6 > 1.0f) {
                            float6 = 1.0f;
                        }
                        if (float6 > 0.1f) {
                            final float float7 = MathHelper.sin((float11 - 0.1f) * 1.3f);
                            final float float8 = float6 - 0.1f;
                            final float float9 = float7 * float8;
                            GlStateManager.translatef(float9 * 0.0f, float9 * 0.004f, float9 * 0.0f);
                        }
                        GlStateManager.translatef(0.0f, 0.0f, float6 * 0.2f);
                        GlStateManager.scalef(1.0f, 1.0f, 1.0f + float6 * 0.2f);
                        GlStateManager.rotatef(integer13 * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                }
            }
            else if (player.isUsingRiptide()) {
                this.applyHandOffset(absoluteHand9, equipProgress);
                final int integer13 = boolean9 ? 1 : -1;
                GlStateManager.translatef(integer13 * -0.4f, 0.8f, 0.3f);
                GlStateManager.rotatef(integer13 * 65.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(integer13 * -85.0f, 0.0f, 0.0f, 1.0f);
            }
            else {
                final float float12 = -0.4f * MathHelper.sin(MathHelper.sqrt(float5) * 3.1415927f);
                final float float11 = 0.2f * MathHelper.sin(MathHelper.sqrt(float5) * 6.2831855f);
                final float float6 = -0.2f * MathHelper.sin(float5 * 3.1415927f);
                final int integer14 = boolean9 ? 1 : -1;
                GlStateManager.translatef(integer14 * float12, float11, float6);
                this.applyHandOffset(absoluteHand9, equipProgress);
                this.a(absoluteHand9, float5);
            }
            this.renderItemFromSide(player, item, boolean9 ? ModelTransformation.Type.e : ModelTransformation.Type.d, !boolean9);
        }
        GlStateManager.popMatrix();
    }
    
    public void renderOverlays(final float float1) {
        GlStateManager.disableAlphaTest();
        if (this.client.player.isInsideWall()) {
            BlockState blockState2 = this.client.world.getBlockState(new BlockPos(this.client.player));
            final PlayerEntity playerEntity3 = this.client.player;
            for (int integer4 = 0; integer4 < 8; ++integer4) {
                final double double5 = playerEntity3.x + ((integer4 >> 0) % 2 - 0.5f) * playerEntity3.getWidth() * 0.8f;
                final double double6 = playerEntity3.y + ((integer4 >> 1) % 2 - 0.5f) * 0.1f;
                final double double7 = playerEntity3.z + ((integer4 >> 2) % 2 - 0.5f) * playerEntity3.getWidth() * 0.8f;
                final BlockPos blockPos11 = new BlockPos(double5, double6 + playerEntity3.getStandingEyeHeight(), double7);
                final BlockState blockState3 = this.client.world.getBlockState(blockPos11);
                if (blockState3.canSuffocate(this.client.world, blockPos11)) {
                    blockState2 = blockState3;
                }
            }
            if (blockState2.getRenderType() != BlockRenderType.a) {
                this.renderBlock(this.client.getBlockRenderManager().getModels().getSprite(blockState2));
            }
        }
        if (!this.client.player.isSpectator()) {
            if (this.client.player.isInFluid(FluidTags.a)) {
                this.renderWaterOverlay(float1);
            }
            if (this.client.player.isOnFire()) {
                this.renderFireOverlay();
            }
        }
        GlStateManager.enableAlphaTest();
    }
    
    private void renderBlock(final Sprite sprite) {
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        final Tessellator tessellator2 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder3 = tessellator2.getBufferBuilder();
        final float float4 = 0.1f;
        GlStateManager.color4f(0.1f, 0.1f, 0.1f, 0.5f);
        GlStateManager.pushMatrix();
        final float float5 = -1.0f;
        final float float6 = 1.0f;
        final float float7 = -1.0f;
        final float float8 = 1.0f;
        final float float9 = -0.5f;
        final float float10 = sprite.getMinU();
        final float float11 = sprite.getMaxU();
        final float float12 = sprite.getMinV();
        final float float13 = sprite.getMaxV();
        bufferBuilder3.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder3.vertex(-1.0, -1.0, -0.5).texture(float11, float13).next();
        bufferBuilder3.vertex(1.0, -1.0, -0.5).texture(float10, float13).next();
        bufferBuilder3.vertex(1.0, 1.0, -0.5).texture(float10, float12).next();
        bufferBuilder3.vertex(-1.0, 1.0, -0.5).texture(float11, float12).next();
        tessellator2.draw();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderWaterOverlay(final float float1) {
        this.client.getTextureManager().bindTexture(FirstPersonRenderer.UNDERWATER_TEX);
        final Tessellator tessellator2 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder3 = tessellator2.getBufferBuilder();
        final float float2 = this.client.player.getBrightnessAtEyes();
        GlStateManager.color4f(float2, float2, float2, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final float float3 = 4.0f;
        final float float4 = -1.0f;
        final float float5 = 1.0f;
        final float float6 = -1.0f;
        final float float7 = 1.0f;
        final float float8 = -0.5f;
        final float float9 = -this.client.player.yaw / 64.0f;
        final float float10 = this.client.player.pitch / 64.0f;
        bufferBuilder3.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder3.vertex(-1.0, -1.0, -0.5).texture(4.0f + float9, 4.0f + float10).next();
        bufferBuilder3.vertex(1.0, -1.0, -0.5).texture(0.0f + float9, 4.0f + float10).next();
        bufferBuilder3.vertex(1.0, 1.0, -0.5).texture(0.0f + float9, 0.0f + float10).next();
        bufferBuilder3.vertex(-1.0, 1.0, -0.5).texture(4.0f + float9, 0.0f + float10).next();
        tessellator2.draw();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
    }
    
    private void renderFireOverlay() {
        final Tessellator tessellator1 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder2 = tessellator1.getBufferBuilder();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.9f);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final float float3 = 1.0f;
        for (int integer4 = 0; integer4 < 2; ++integer4) {
            GlStateManager.pushMatrix();
            final Sprite sprite5 = this.client.getSpriteAtlas().getSprite(ModelLoader.FIRE_1);
            this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            final float float4 = sprite5.getMinU();
            final float float5 = sprite5.getMaxU();
            final float float6 = sprite5.getMinV();
            final float float7 = sprite5.getMaxV();
            final float float8 = -0.5f;
            final float float9 = 0.5f;
            final float float10 = -0.5f;
            final float float11 = 0.5f;
            final float float12 = -0.5f;
            GlStateManager.translatef(-(integer4 * 2 - 1) * 0.24f, -0.3f, 0.0f);
            GlStateManager.rotatef((integer4 * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            bufferBuilder2.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder2.vertex(-0.5, -0.5, -0.5).texture(float5, float7).next();
            bufferBuilder2.vertex(0.5, -0.5, -0.5).texture(float4, float7).next();
            bufferBuilder2.vertex(0.5, 0.5, -0.5).texture(float4, float6).next();
            bufferBuilder2.vertex(-0.5, 0.5, -0.5).texture(float5, float6).next();
            tessellator1.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }
    
    public void updateHeldItems() {
        this.prevEquipProgressMainHand = this.equipProgressMainHand;
        this.prevEquipProgressOffHand = this.equipProgressOffHand;
        final ClientPlayerEntity clientPlayerEntity1 = this.client.player;
        final ItemStack itemStack2 = clientPlayerEntity1.getMainHandStack();
        final ItemStack itemStack3 = clientPlayerEntity1.getOffHandStack();
        if (clientPlayerEntity1.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4f, 0.0f, 1.0f);
        }
        else {
            final float float4 = clientPlayerEntity1.s(1.0f);
            this.equipProgressMainHand += MathHelper.clamp((Objects.equals(this.mainHand, itemStack2) ? (float4 * float4 * float4) : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(Objects.equals(this.offHand, itemStack3) ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
        }
        if (this.equipProgressMainHand < 0.1f) {
            this.mainHand = itemStack2;
        }
        if (this.equipProgressOffHand < 0.1f) {
            this.offHand = itemStack3;
        }
    }
    
    public void resetEquipProgress(final Hand hand) {
        if (hand == Hand.a) {
            this.equipProgressMainHand = 0.0f;
        }
        else {
            this.equipProgressOffHand = 0.0f;
        }
    }
    
    static {
        MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
        UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");
    }
}
