package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.item.Items;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.client.render.model.json.ModelTransformation;
import java.util.List;
import net.minecraft.util.math.Vec3i;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.SystemUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import java.util.Random;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.BakedModel;
import java.util.Iterator;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.Item;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloadListener
{
    public static final Identifier ENCHANTMENT_GLINT_TEX;
    private static final Set<Item> WITHOUT_MODELS;
    public float zOffset;
    private final ItemModels models;
    private final TextureManager textureManager;
    private final ItemColorMap colorMap;
    
    public ItemRenderer(final TextureManager textureManager, final BakedModelManager bakedModelManager, final ItemColorMap itemColorMap) {
        this.textureManager = textureManager;
        this.models = new ItemModels(bakedModelManager);
        for (final Item item5 : Registry.ITEM) {
            if (!ItemRenderer.WITHOUT_MODELS.contains(item5)) {
                this.models.putModel(item5, new ModelIdentifier(Registry.ITEM.getId(item5), "inventory"));
            }
        }
        this.colorMap = itemColorMap;
    }
    
    public ItemModels getModels() {
        return this.models;
    }
    
    private void renderItemModel(final BakedModel bakedModel, final ItemStack itemStack) {
        this.renderModel(bakedModel, -1, itemStack);
    }
    
    private void renderModelWithTint(final BakedModel bakedModel, final int integer) {
        this.renderModel(bakedModel, integer, ItemStack.EMPTY);
    }
    
    private void renderModel(final BakedModel bakedModel, final int integer, final ItemStack itemStack) {
        final Tessellator tessellator4 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder5 = tessellator4.getBufferBuilder();
        bufferBuilder5.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
        final Random random6 = new Random();
        final long long7 = 42L;
        for (final Direction direction12 : Direction.values()) {
            random6.setSeed(42L);
            this.renderQuads(bufferBuilder5, bakedModel.getQuads(null, direction12, random6), integer, itemStack);
        }
        random6.setSeed(42L);
        this.renderQuads(bufferBuilder5, bakedModel.getQuads(null, null, random6), integer, itemStack);
        tessellator4.draw();
    }
    
    public void renderItemAndGlow(final ItemStack itemStack, final BakedModel bakedModel) {
        if (itemStack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        if (bakedModel.isBuiltin()) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableRescaleNormal();
            ItemDynamicRenderer.INSTANCE.render(itemStack);
        }
        else {
            this.renderItemModel(bakedModel, itemStack);
            if (itemStack.hasEnchantmentGlint()) {
                renderGlint(this.textureManager, () -> this.renderModelWithTint(bakedModel, -8372020), 8);
            }
        }
        GlStateManager.popMatrix();
    }
    
    public static void renderGlint(final TextureManager textureManager, final Runnable renderer, final int integer) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        textureManager.bindTexture(ItemRenderer.ENCHANTMENT_GLINT_TEX);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scalef((float)integer, (float)integer, (float)integer);
        final float float4 = SystemUtil.getMeasuringTimeMs() % 3000L / 3000.0f / integer;
        GlStateManager.translatef(float4, 0.0f, 0.0f);
        GlStateManager.rotatef(-50.0f, 0.0f, 0.0f, 1.0f);
        renderer.run();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scalef((float)integer, (float)integer, (float)integer);
        final float float5 = SystemUtil.getMeasuringTimeMs() % 4873L / 4873.0f / integer;
        GlStateManager.translatef(-float5, 0.0f, 0.0f);
        GlStateManager.rotatef(10.0f, 0.0f, 0.0f, 1.0f);
        renderer.run();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
    }
    
    private void postNormalQuad(final BufferBuilder buffer, final BakedQuad bakedQuad) {
        final Vec3i vec3i3 = bakedQuad.getFace().getVector();
        buffer.postNormal((float)vec3i3.getX(), (float)vec3i3.getY(), (float)vec3i3.getZ());
    }
    
    private void renderQuad(final BufferBuilder bufferBuilder, final BakedQuad bakedQuad, final int color) {
        bufferBuilder.putVertexData(bakedQuad.getVertexData());
        bufferBuilder.setQuadColor(color);
        this.postNormalQuad(bufferBuilder, bakedQuad);
    }
    
    private void renderQuads(final BufferBuilder bufferBuilder, final List<BakedQuad> quads, final int forcedColor, final ItemStack stack) {
        final boolean boolean5 = forcedColor == -1 && !stack.isEmpty();
        for (int integer6 = 0, integer7 = quads.size(); integer6 < integer7; ++integer6) {
            final BakedQuad bakedQuad8 = quads.get(integer6);
            int integer8 = forcedColor;
            if (boolean5 && bakedQuad8.hasColor()) {
                integer8 = this.colorMap.getRenderColor(stack, bakedQuad8.getColorIndex());
                integer8 |= 0xFF000000;
            }
            this.renderQuad(bufferBuilder, bakedQuad8, integer8);
        }
    }
    
    public boolean hasDepthInGui(final ItemStack itemStack) {
        final BakedModel bakedModel2 = this.models.getModel(itemStack);
        return bakedModel2 != null && bakedModel2.hasDepthInGui();
    }
    
    public void renderItem(final ItemStack stack, final ModelTransformation.Type type) {
        if (stack.isEmpty()) {
            return;
        }
        final BakedModel bakedModel3 = this.getModel(stack);
        this.renderItem(stack, bakedModel3, type, false);
    }
    
    public BakedModel getModel(final ItemStack itemStack, @Nullable final World world, @Nullable final LivingEntity livingEntity) {
        final BakedModel bakedModel4 = this.models.getModel(itemStack);
        final Item item5 = itemStack.getItem();
        if (!item5.hasProperties()) {
            return bakedModel4;
        }
        return this.getOverriddenModel(bakedModel4, itemStack, world, livingEntity);
    }
    
    public BakedModel getHeldItemModel(final ItemStack itemStack, final World world, final LivingEntity livingEntity) {
        final Item item5 = itemStack.getItem();
        BakedModel bakedModel4;
        if (item5 == Items.pu) {
            bakedModel4 = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        }
        else {
            bakedModel4 = this.models.getModel(itemStack);
        }
        if (!item5.hasProperties()) {
            return bakedModel4;
        }
        return this.getOverriddenModel(bakedModel4, itemStack, world, livingEntity);
    }
    
    public BakedModel getModel(final ItemStack itemStack) {
        return this.getModel(itemStack, null, null);
    }
    
    private BakedModel getOverriddenModel(final BakedModel bakedModel, final ItemStack itemStack, @Nullable final World world, @Nullable final LivingEntity livingEntity) {
        final BakedModel bakedModel2 = bakedModel.getItemPropertyOverrides().apply(bakedModel, itemStack, world, livingEntity);
        return (bakedModel2 == null) ? this.models.getModelManager().getMissingModel() : bakedModel2;
    }
    
    public void renderHeldItem(final ItemStack stack, final LivingEntity entity, final ModelTransformation.Type type, final boolean boolean4) {
        if (stack.isEmpty() || entity == null) {
            return;
        }
        final BakedModel bakedModel5 = this.getHeldItemModel(stack, entity.world, entity);
        this.renderItem(stack, bakedModel5, type, boolean4);
    }
    
    protected void renderItem(final ItemStack stack, final BakedModel model, final ModelTransformation.Type type, final boolean boolean4) {
        if (stack.isEmpty()) {
            return;
        }
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final ModelTransformation modelTransformation5 = model.getTransformation();
        ModelTransformation.applyGl(modelTransformation5.getTransformation(type), boolean4);
        if (this.areFacesFlippedBy(modelTransformation5.getTransformation(type))) {
            GlStateManager.cullFace(GlStateManager.FaceSides.a);
        }
        this.renderItemAndGlow(stack, model);
        GlStateManager.cullFace(GlStateManager.FaceSides.b);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
    }
    
    private boolean areFacesFlippedBy(final Transformation transformation) {
        return transformation.scale.x() < 0.0f ^ transformation.scale.y() < 0.0f ^ transformation.scale.z() < 0.0f;
    }
    
    public void renderGuiItemIcon(final ItemStack stack, final int x, final int y) {
        this.renderGuiItemModel(stack, x, y, this.getModel(stack));
    }
    
    protected void renderGuiItemModel(final ItemStack itemStack, final int x, final int y, final BakedModel bakedModel) {
        GlStateManager.pushMatrix();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.prepareGuiItemRender(x, y, bakedModel.hasDepthInGui());
        bakedModel.getTransformation().applyGl(ModelTransformation.Type.g);
        this.renderItemAndGlow(itemStack, bakedModel);
        GlStateManager.disableAlphaTest();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
    }
    
    private void prepareGuiItemRender(final int x, final int y, final boolean depth) {
        GlStateManager.translatef((float)x, (float)y, 100.0f + this.zOffset);
        GlStateManager.translatef(8.0f, 8.0f, 0.0f);
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.scalef(16.0f, 16.0f, 16.0f);
        if (depth) {
            GlStateManager.enableLighting();
        }
        else {
            GlStateManager.disableLighting();
        }
    }
    
    public void renderGuiItem(final ItemStack stack, final int x, final int y) {
        this.renderGuiItem(MinecraftClient.getInstance().player, stack, x, y);
    }
    
    public void renderGuiItem(@Nullable final LivingEntity livingEntity, final ItemStack itemStack, final int x, final int y) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.zOffset += 50.0f;
        try {
            this.renderGuiItemModel(itemStack, x, y, this.getModel(itemStack, null, livingEntity));
        }
        catch (Throwable throwable5) {
            final CrashReport crashReport6 = CrashReport.create(throwable5, "Rendering item");
            final CrashReportSection crashReportSection7 = crashReport6.addElement("Item being rendered");
            crashReportSection7.add("Item Type", () -> String.valueOf(itemStack.getItem()));
            crashReportSection7.add("Item Damage", () -> String.valueOf(itemStack.getDamage()));
            crashReportSection7.add("Item NBT", () -> String.valueOf(itemStack.getTag()));
            crashReportSection7.add("Item Foil", () -> String.valueOf(itemStack.hasEnchantmentGlint()));
            throw new CrashException(crashReport6);
        }
        this.zOffset -= 50.0f;
    }
    
    public void renderGuiItemOverlay(final TextRenderer fontRenderer, final ItemStack stack, final int x, final int y) {
        this.renderGuiItemOverlay(fontRenderer, stack, x, y, null);
    }
    
    public void renderGuiItemOverlay(final TextRenderer fontRenderer, final ItemStack stack, final int x, final int y, @Nullable final String amountText) {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getAmount() != 1 || amountText != null) {
            final String string6 = (amountText == null) ? String.valueOf(stack.getAmount()) : amountText;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            fontRenderer.drawWithShadow(string6, (float)(x + 19 - 2 - fontRenderer.getStringWidth(string6)), (float)(y + 6 + 3), 16777215);
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
        if (stack.isDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            final Tessellator tessellator6 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder7 = tessellator6.getBufferBuilder();
            final float float8 = (float)stack.getDamage();
            final float float9 = (float)stack.getDurability();
            final float float10 = Math.max(0.0f, (float9 - float8) / float9);
            final int integer11 = Math.round(13.0f - float8 * 13.0f / float9);
            final int integer12 = MathHelper.hsvToRgb(float10 / 3.0f, 1.0f, 1.0f);
            this.renderGuiQuad(bufferBuilder7, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
            this.renderGuiQuad(bufferBuilder7, x + 2, y + 13, integer11, 1, integer12 >> 16 & 0xFF, integer12 >> 8 & 0xFF, integer12 & 0xFF, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
        final ClientPlayerEntity clientPlayerEntity6 = MinecraftClient.getInstance().player;
        final float float11 = (clientPlayerEntity6 == null) ? 0.0f : clientPlayerEntity6.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
        if (float11 > 0.0f) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            final Tessellator tessellator7 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder8 = tessellator7.getBufferBuilder();
            this.renderGuiQuad(bufferBuilder8, x, y + MathHelper.floor(16.0f * (1.0f - float11)), 16, MathHelper.ceil(16.0f * float11), 255, 255, 255, 127);
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
    }
    
    private void renderGuiQuad(final BufferBuilder bufferBuilder, final int x, final int y, final int width, final int height, final int red, final int green, final int blue, final int alpha) {
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x + 0, y + 0, 0.0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(x + 0, y + height, 0.0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(x + width, y + 0, 0.0).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.models.reloadModels();
    }
    
    static {
        ENCHANTMENT_GLINT_TEX = new Identifier("textures/misc/enchanted_item_glint.png");
        WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
    }
}
