package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.model.BakedModel;
import java.util.Random;
import net.minecraft.client.render.item.ItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ItemEntity;

@Environment(EnvType.CLIENT)
public class ItemEntityRenderer extends EntityRenderer<ItemEntity>
{
    private final ItemRenderer itemRenderer;
    private final Random random;
    
    public ItemEntityRenderer(final EntityRenderDispatcher renderManager, final ItemRenderer itemRenderer) {
        super(renderManager);
        this.random = new Random();
        this.itemRenderer = itemRenderer;
        this.c = 0.15f;
        this.d = 0.75f;
    }
    
    private int a(final ItemEntity itemEntity, final double double2, final double double4, final double double6, final float float8, final BakedModel bakedModel9) {
        final ItemStack itemStack10 = itemEntity.getStack();
        final Item item11 = itemStack10.getItem();
        if (item11 == null) {
            return 0;
        }
        final boolean boolean12 = bakedModel9.hasDepthInGui();
        final int integer13 = this.getRenderedAmount(itemStack10);
        final float float9 = 0.25f;
        final float float10 = MathHelper.sin((itemEntity.getAge() + float8) / 10.0f + itemEntity.hoverHeight) * 0.1f + 0.1f;
        final float float11 = bakedModel9.getTransformation().getTransformation(ModelTransformation.Type.h).scale.y();
        GlStateManager.translatef((float)double2, (float)double4 + float10 + 0.25f * float11, (float)double6);
        if (boolean12 || this.renderManager.gameOptions != null) {
            final float float12 = ((itemEntity.getAge() + float8) / 20.0f + itemEntity.hoverHeight) * 57.295776f;
            GlStateManager.rotatef(float12, 0.0f, 1.0f, 0.0f);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        return integer13;
    }
    
    private int getRenderedAmount(final ItemStack stack) {
        int integer2 = 1;
        if (stack.getAmount() > 48) {
            integer2 = 5;
        }
        else if (stack.getAmount() > 32) {
            integer2 = 4;
        }
        else if (stack.getAmount() > 16) {
            integer2 = 3;
        }
        else if (stack.getAmount() > 1) {
            integer2 = 2;
        }
        return integer2;
    }
    
    @Override
    public void render(final ItemEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final ItemStack itemStack10 = entity.getStack();
        final int integer11 = itemStack10.isEmpty() ? 187 : (Item.getRawIdByItem(itemStack10.getItem()) + itemStack10.getDamage());
        this.random.setSeed(integer11);
        boolean boolean12 = false;
        if (this.bindEntityTexture(entity)) {
            this.renderManager.textureManager.getTexture(this.getTexture(entity)).pushFilter(false, false);
            boolean12 = true;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GuiLighting.enable();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final BakedModel bakedModel13 = this.itemRenderer.getModel(itemStack10, entity.world, null);
        final int integer12 = this.a(entity, x, y, z, tickDelta, bakedModel13);
        final float float15 = bakedModel13.getTransformation().ground.scale.x();
        final float float16 = bakedModel13.getTransformation().ground.scale.y();
        final float float17 = bakedModel13.getTransformation().ground.scale.z();
        final boolean boolean13 = bakedModel13.hasDepthInGui();
        if (!boolean13) {
            final float float18 = -0.0f * (integer12 - 1) * 0.5f * float15;
            final float float19 = -0.0f * (integer12 - 1) * 0.5f * float16;
            final float float20 = -0.09375f * (integer12 - 1) * 0.5f * float17;
            GlStateManager.translatef(float18, float19, float20);
        }
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        for (int integer13 = 0; integer13 < integer12; ++integer13) {
            if (boolean13) {
                GlStateManager.pushMatrix();
                if (integer13 > 0) {
                    final float float19 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    final float float20 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    final float float21 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    GlStateManager.translatef(float19, float20, float21);
                }
                bakedModel13.getTransformation().applyGl(ModelTransformation.Type.h);
                this.itemRenderer.renderItemAndGlow(itemStack10, bakedModel13);
                GlStateManager.popMatrix();
            }
            else {
                GlStateManager.pushMatrix();
                if (integer13 > 0) {
                    final float float19 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    final float float20 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    GlStateManager.translatef(float19, float20, 0.0f);
                }
                bakedModel13.getTransformation().applyGl(ModelTransformation.Type.h);
                this.itemRenderer.renderItemAndGlow(itemStack10, bakedModel13);
                GlStateManager.popMatrix();
                GlStateManager.translatef(0.0f * float15, 0.0f * float16, 0.09375f * float17);
            }
        }
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);
        if (boolean12) {
            this.renderManager.textureManager.getTexture(this.getTexture(entity)).popFilter();
        }
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final ItemEntity itemEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
