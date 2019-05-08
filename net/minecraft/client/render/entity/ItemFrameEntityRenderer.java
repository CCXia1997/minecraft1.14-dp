package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.item.map.MapState;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.FilledMapItem;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.item.Items;
import net.minecraft.client.texture.SpriteAtlasTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ItemFrameEntity;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderer extends EntityRenderer<ItemFrameEntity>
{
    private static final Identifier MAP_BACKGROUND_TEX;
    private static final ModelIdentifier NORMAL_FRAME;
    private static final ModelIdentifier MAP_FRAME;
    private final MinecraftClient client;
    private final ItemRenderer itemRenderer;
    
    public ItemFrameEntityRenderer(final EntityRenderDispatcher renderManager, final ItemRenderer itemRenderer) {
        super(renderManager);
        this.client = MinecraftClient.getInstance();
        this.itemRenderer = itemRenderer;
    }
    
    @Override
    public void render(final ItemFrameEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        final BlockPos blockPos10 = entity.getDecorationBlockPos();
        final double double11 = blockPos10.getX() - entity.x + x;
        final double double12 = blockPos10.getY() - entity.y + y;
        final double double13 = blockPos10.getZ() - entity.z + z;
        GlStateManager.translated(double11 + 0.5, double12 + 0.5, double13 + 0.5);
        GlStateManager.rotatef(entity.pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f - entity.yaw, 0.0f, 1.0f, 0.0f);
        this.renderManager.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        final BlockRenderManager blockRenderManager17 = this.client.getBlockRenderManager();
        final BakedModelManager bakedModelManager18 = blockRenderManager17.getModels().getModelManager();
        final ModelIdentifier modelIdentifier19 = (entity.getHeldItemStack().getItem() == Items.lV) ? ItemFrameEntityRenderer.MAP_FRAME : ItemFrameEntityRenderer.NORMAL_FRAME;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        blockRenderManager17.getModelRenderer().render(bakedModelManager18.getModel(modelIdentifier19), 1.0f, 1.0f, 1.0f, 1.0f);
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        if (entity.getHeldItemStack().getItem() == Items.lV) {
            GlStateManager.pushLightingAttributes();
            GuiLighting.enable();
        }
        GlStateManager.translatef(0.0f, 0.0f, 0.4375f);
        this.renderItem(entity);
        if (entity.getHeldItemStack().getItem() == Items.lV) {
            GuiLighting.disable();
            GlStateManager.popAttributes();
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        this.renderLabelIfPresent(entity, x + entity.facing.getOffsetX() * 0.3f, y - 0.25, z + entity.facing.getOffsetZ() * 0.3f);
    }
    
    @Nullable
    @Override
    protected Identifier getTexture(final ItemFrameEntity itemFrameEntity) {
        return null;
    }
    
    private void renderItem(final ItemFrameEntity itemFrameEntity) {
        final ItemStack itemStack2 = itemFrameEntity.getHeldItemStack();
        if (itemStack2.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        final boolean boolean3 = itemStack2.getItem() == Items.lV;
        final int integer4 = boolean3 ? (itemFrameEntity.getRotation() % 4 * 2) : itemFrameEntity.getRotation();
        GlStateManager.rotatef(integer4 * 360.0f / 8.0f, 0.0f, 0.0f, 1.0f);
        if (boolean3) {
            GlStateManager.disableLighting();
            this.renderManager.textureManager.bindTexture(ItemFrameEntityRenderer.MAP_BACKGROUND_TEX);
            GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            final float float5 = 0.0078125f;
            GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f);
            GlStateManager.translatef(-64.0f, -64.0f, 0.0f);
            final MapState mapState6 = FilledMapItem.getOrCreateMapState(itemStack2, itemFrameEntity.world);
            GlStateManager.translatef(0.0f, 0.0f, -1.0f);
            if (mapState6 != null) {
                this.client.gameRenderer.getMapRenderer().draw(mapState6, true);
            }
        }
        else {
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderItem(itemStack2, ModelTransformation.Type.FIXED);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    protected void renderLabelIfPresent(final ItemFrameEntity entity, final double x, final double y, final double z) {
        if (!MinecraftClient.isHudEnabled() || entity.getHeldItemStack().isEmpty() || !entity.getHeldItemStack().hasDisplayName() || this.renderManager.targetedEntity != entity) {
            return;
        }
        final double double8 = entity.squaredDistanceTo(this.renderManager.camera.getPos());
        final float float10 = entity.isInSneakingPose() ? 32.0f : 64.0f;
        if (double8 >= float10 * float10) {
            return;
        }
        final String string11 = entity.getHeldItemStack().getDisplayName().getFormattedText();
        this.renderLabel(entity, string11, x, y, z, 64);
    }
    
    static {
        MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
        NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
        MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
    }
}
