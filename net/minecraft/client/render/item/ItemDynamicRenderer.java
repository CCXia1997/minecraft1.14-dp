package net.minecraft.client.render.item;

import java.util.function.Function;
import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.util.TagHelper;
import net.minecraft.block.AbstractSkullBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.Items;
import net.minecraft.block.BedBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemDynamicRenderer
{
    private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED;
    private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX;
    public static final ItemDynamicRenderer INSTANCE;
    private final ChestBlockEntity renderChestNormal;
    private final ChestBlockEntity renderChestTrapped;
    private final EnderChestBlockEntity renderChestEnder;
    private final BannerBlockEntity renderBanner;
    private final BedBlockEntity renderBed;
    private final SkullBlockEntity renderSkull;
    private final ConduitBlockEntity renderConduit;
    private final ShieldEntityModel modelShield;
    private final TridentEntityModel modelTrident;
    
    public ItemDynamicRenderer() {
        this.renderChestNormal = new ChestBlockEntity();
        this.renderChestTrapped = new TrappedChestBlockEntity();
        this.renderChestEnder = new EnderChestBlockEntity();
        this.renderBanner = new BannerBlockEntity();
        this.renderBed = new BedBlockEntity();
        this.renderSkull = new SkullBlockEntity();
        this.renderConduit = new ConduitBlockEntity();
        this.modelShield = new ShieldEntityModel();
        this.modelTrident = new TridentEntityModel();
    }
    
    public void render(final ItemStack itemStack) {
        final Item item2 = itemStack.getItem();
        if (item2 instanceof BannerItem) {
            this.renderBanner.deserialize(itemStack, ((BannerItem)item2).getColor());
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBanner);
        }
        else if (item2 instanceof BlockItem && ((BlockItem)item2).getBlock() instanceof BedBlock) {
            this.renderBed.setColor(((BedBlock)((BlockItem)item2).getBlock()).getColor());
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBed);
        }
        else if (item2 == Items.oW) {
            if (itemStack.getSubCompoundTag("BlockEntityTag") != null) {
                this.renderBanner.deserialize(itemStack, ShieldItem.getColor(itemStack));
                MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.SHIELD.get(this.renderBanner.getPatternCacheKey(), this.renderBanner.getPatterns(), this.renderBanner.getPatternColors()));
            }
            else {
                MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.DEFAULT_SHIELD);
            }
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, -1.0f, -1.0f);
            this.modelShield.renderItem();
            if (itemStack.hasEnchantmentGlint()) {
                this.renderEnchantmentGlint(this.modelShield::renderItem);
            }
            GlStateManager.popMatrix();
        }
        else if (item2 instanceof BlockItem && ((BlockItem)item2).getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile3 = null;
            if (itemStack.hasTag()) {
                final CompoundTag compoundTag4 = itemStack.getTag();
                if (compoundTag4.containsKey("SkullOwner", 10)) {
                    gameProfile3 = TagHelper.deserializeProfile(compoundTag4.getCompound("SkullOwner"));
                }
                else if (compoundTag4.containsKey("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)compoundTag4.getString("SkullOwner"))) {
                    gameProfile3 = new GameProfile((UUID)null, compoundTag4.getString("SkullOwner"));
                    gameProfile3 = SkullBlockEntity.loadProperties(gameProfile3);
                    compoundTag4.remove("SkullOwner");
                    compoundTag4.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile3));
                }
            }
            if (SkullBlockEntityRenderer.INSTANCE != null) {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                SkullBlockEntityRenderer.INSTANCE.render(0.0f, 0.0f, 0.0f, null, 180.0f, ((AbstractSkullBlock)((BlockItem)item2).getBlock()).getSkullType(), gameProfile3, -1, 0.0f);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
        else if (item2 == Items.pu) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TridentEntityModel.TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, -1.0f, -1.0f);
            this.modelTrident.renderItem();
            if (itemStack.hasEnchantmentGlint()) {
                this.renderEnchantmentGlint(this.modelTrident::renderItem);
            }
            GlStateManager.popMatrix();
        }
        else if (item2 instanceof BlockItem && ((BlockItem)item2).getBlock() == Blocks.kO) {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderConduit);
        }
        else if (item2 == Blocks.ec.getItem()) {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestEnder);
        }
        else if (item2 == Blocks.fj.getItem()) {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestTrapped);
        }
        else if (Block.getBlockFromItem(item2) instanceof ShulkerBoxBlock) {
            final DyeColor dyeColor3 = ShulkerBoxBlock.getColor(item2);
            if (dyeColor3 == null) {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(ItemDynamicRenderer.RENDER_SHULKER_BOX);
            }
            else {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(ItemDynamicRenderer.RENDER_SHULKER_BOX_DYED[dyeColor3.getId()]);
            }
        }
        else {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestNormal);
        }
    }
    
    private void renderEnchantmentGlint(final Runnable runnable) {
        GlStateManager.color3f(0.5019608f, 0.2509804f, 0.8f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(ItemRenderer.ENCHANTMENT_GLINT_TEX);
        ItemRenderer.renderGlint(MinecraftClient.getInstance().getTextureManager(), runnable, 1);
    }
    
    static {
        RENDER_SHULKER_BOX_DYED = Arrays.<DyeColor>stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).<ShulkerBoxBlockEntity>toArray(ShulkerBoxBlockEntity[]::new);
        RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity((DyeColor)null);
        INSTANCE = new ItemDynamicRenderer();
    }
}
