package net.minecraft.client.render.block.entity;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import java.util.HashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.block.SkullBlock;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SkullBlockEntity;

@Environment(EnvType.CLIENT)
public class SkullBlockEntityRenderer extends BlockEntityRenderer<SkullBlockEntity>
{
    public static SkullBlockEntityRenderer INSTANCE;
    private static final Map<SkullBlock.SkullType, SkullEntityModel> MODELS;
    private static final Map<SkullBlock.SkullType, Identifier> TEXTURES;
    
    @Override
    public void render(final SkullBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final float float10 = entity.getTicksPowered(tickDelta);
        final BlockState blockState11 = entity.getCachedState();
        final boolean boolean12 = blockState11.getBlock() instanceof WallSkullBlock;
        final Direction direction13 = boolean12 ? blockState11.<Direction>get((Property<Direction>)WallSkullBlock.FACING) : null;
        final float float11 = 22.5f * (boolean12 ? ((2 + direction13.getHorizontal()) * 4) : blockState11.<Integer>get((Property<Integer>)SkullBlock.ROTATION));
        this.render((float)xOffset, (float)yOffset, (float)zOffset, direction13, float11, ((AbstractSkullBlock)blockState11.getBlock()).getSkullType(), entity.getOwner(), blockBreakStage, float10);
    }
    
    @Override
    public void setRenderManager(final BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super.setRenderManager(blockEntityRenderDispatcher);
        SkullBlockEntityRenderer.INSTANCE = this;
    }
    
    public void render(final float float1, final float float2, final float float3, @Nullable final Direction direction, final float float5, final SkullBlock.SkullType skullType, @Nullable final GameProfile gameProfile, final int integer, final float float9) {
        final SkullEntityModel skullEntityModel10 = SkullBlockEntityRenderer.MODELS.get(skullType);
        if (integer >= 0) {
            this.bindTexture(SkullBlockEntityRenderer.DESTROY_STAGE_TEXTURES[integer]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 2.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            this.bindTexture(this.a(skullType, gameProfile));
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        if (direction == null) {
            GlStateManager.translatef(float1 + 0.5f, float2, float3 + 0.5f);
        }
        else {
            switch (direction) {
                case NORTH: {
                    GlStateManager.translatef(float1 + 0.5f, float2 + 0.25f, float3 + 0.74f);
                    break;
                }
                case SOUTH: {
                    GlStateManager.translatef(float1 + 0.5f, float2 + 0.25f, float3 + 0.26f);
                    break;
                }
                case WEST: {
                    GlStateManager.translatef(float1 + 0.74f, float2 + 0.25f, float3 + 0.5f);
                    break;
                }
                default: {
                    GlStateManager.translatef(float1 + 0.26f, float2 + 0.25f, float3 + 0.5f);
                    break;
                }
            }
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        if (skullType == SkullBlock.Type.PLAYER) {
            GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
        }
        skullEntityModel10.setRotationAngles(float9, 0.0f, 0.0f, float5, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private Identifier a(final SkullBlock.SkullType skullType, @Nullable final GameProfile gameProfile) {
        Identifier identifier3 = SkullBlockEntityRenderer.TEXTURES.get(skullType);
        if (skullType == SkullBlock.Type.PLAYER && gameProfile != null) {
            final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map5 = minecraftClient4.getSkinProvider().getTextures(gameProfile);
            if (map5.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                identifier3 = minecraftClient4.getSkinProvider().loadSkin(map5.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }
            else {
                identifier3 = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
            }
        }
        return identifier3;
    }
    
    static {
        final SkullEntityModel skullEntityModel2;
        final SkullOverlayEntityModel skullEntityModel3;
        final DragonHeadEntityModel dragonHeadEntityModel4;
        MODELS = SystemUtil.<Map<SkullBlock.SkullType, SkullEntityModel>>consume(Maps.newHashMap(), hashMap -> {
            skullEntityModel2 = new SkullEntityModel(0, 0, 64, 32);
            skullEntityModel3 = new SkullOverlayEntityModel();
            dragonHeadEntityModel4 = new DragonHeadEntityModel(0.0f);
            hashMap.put(SkullBlock.Type.SKELETON, (DragonHeadEntityModel)skullEntityModel2);
            hashMap.put(SkullBlock.Type.WITHER_SKELETON, (DragonHeadEntityModel)skullEntityModel2);
            hashMap.put(SkullBlock.Type.PLAYER, (DragonHeadEntityModel)skullEntityModel3);
            hashMap.put(SkullBlock.Type.ZOMBIE, (DragonHeadEntityModel)skullEntityModel3);
            hashMap.put(SkullBlock.Type.CREEPER, (DragonHeadEntityModel)skullEntityModel2);
            hashMap.put(SkullBlock.Type.DRAGON, dragonHeadEntityModel4);
            return;
        });
        TEXTURES = SystemUtil.<Map<SkullBlock.SkullType, Identifier>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(SkullBlock.Type.SKELETON, new Identifier("textures/entity/skeleton/skeleton.png"));
            hashMap.put(SkullBlock.Type.WITHER_SKELETON, new Identifier("textures/entity/skeleton/wither_skeleton.png"));
            hashMap.put(SkullBlock.Type.ZOMBIE, new Identifier("textures/entity/zombie/zombie.png"));
            hashMap.put(SkullBlock.Type.CREEPER, new Identifier("textures/entity/creeper/creeper.png"));
            hashMap.put(SkullBlock.Type.DRAGON, new Identifier("textures/entity/enderdragon/dragon.png"));
            hashMap.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
        });
    }
}
