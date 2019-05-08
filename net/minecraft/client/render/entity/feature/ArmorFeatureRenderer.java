package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.entity.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.entity.EquipmentSlot;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M>
{
    protected static final Identifier SKIN;
    protected final A modelLeggings;
    protected final A modelBody;
    private float alpha;
    private float red;
    private float green;
    private float blue;
    private boolean ignoreGlint;
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE;
    
    protected ArmorFeatureRenderer(final FeatureRendererContext<T, M> featureRendererContext, final A bipedEntityModel2, final A bipedEntityModel3) {
        super(featureRendererContext);
        this.alpha = 1.0f;
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.modelLeggings = bipedEntityModel2;
        this.modelBody = bipedEntityModel3;
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.renderArmor(entity, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.CHEST);
        this.renderArmor(entity, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.LEGS);
        this.renderArmor(entity, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.FEET);
        this.renderArmor(entity, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.HEAD);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    private void renderArmor(final T livingEntity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8, final EquipmentSlot equipmentSlot) {
        final ItemStack itemStack10 = livingEntity.getEquippedStack(equipmentSlot);
        if (!(itemStack10.getItem() instanceof ArmorItem)) {
            return;
        }
        final ArmorItem armorItem11 = (ArmorItem)itemStack10.getItem();
        if (armorItem11.getSlotType() != equipmentSlot) {
            return;
        }
        final A bipedEntityModel12 = this.getArmor(equipmentSlot);
        this.getModel().setAttributes(bipedEntityModel12);
        bipedEntityModel12.animateModel(livingEntity, float2, float3, float4);
        this.a(bipedEntityModel12, equipmentSlot);
        final boolean boolean13 = this.isLegs(equipmentSlot);
        this.bindTexture(this.getArmorTexture(armorItem11, boolean13));
        if (armorItem11 instanceof DyeableArmorItem) {
            final int integer14 = ((DyeableArmorItem)armorItem11).getColor(itemStack10);
            final float float9 = (integer14 >> 16 & 0xFF) / 255.0f;
            final float float10 = (integer14 >> 8 & 0xFF) / 255.0f;
            final float float11 = (integer14 & 0xFF) / 255.0f;
            GlStateManager.color4f(this.red * float9, this.green * float10, this.blue * float11, this.alpha);
            bipedEntityModel12.render(livingEntity, float2, float3, float5, float6, float7, float8);
            this.bindTexture(this.a(armorItem11, boolean13, "overlay"));
        }
        GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
        bipedEntityModel12.render(livingEntity, float2, float3, float5, float6, float7, float8);
        if (!this.ignoreGlint && itemStack10.hasEnchantments()) {
            ArmorFeatureRenderer.<T>renderEnchantedGlint(this::bindTexture, livingEntity, bipedEntityModel12, float2, float3, float4, float5, float6, float7, float8);
        }
    }
    
    public A getArmor(final EquipmentSlot equipmentSlot) {
        return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
    }
    
    private boolean isLegs(final EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }
    
    public static <T extends Entity> void renderEnchantedGlint(final Consumer<Identifier> consumer, final T entity, final EntityModel<T> entityModel, final float float4, final float float5, final float float6, final float float7, final float float8, final float float9, final float float10) {
        final float float11 = entity.age + float6;
        consumer.accept(ArmorFeatureRenderer.SKIN);
        final GameRenderer gameRenderer12 = MinecraftClient.getInstance().gameRenderer;
        gameRenderer12.setFogBlack(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        final float float12 = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        for (int integer14 = 0; integer14 < 2; ++integer14) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            final float float13 = 0.76f;
            GlStateManager.color4f(0.38f, 0.19f, 0.608f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            final float float14 = 0.33333334f;
            GlStateManager.scalef(0.33333334f, 0.33333334f, 0.33333334f);
            GlStateManager.rotatef(30.0f - integer14 * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translatef(0.0f, float11 * (0.001f + integer14 * 0.003f) * 20.0f, 0.0f);
            GlStateManager.matrixMode(5888);
            entityModel.render(entity, float4, float5, float7, float8, float9, float10);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        gameRenderer12.setFogBlack(false);
    }
    
    private Identifier getArmorTexture(final ArmorItem armor, final boolean boolean2) {
        return this.a(armor, boolean2, null);
    }
    
    private Identifier a(final ArmorItem armorItem, final boolean boolean2, @Nullable final String string) {
        final String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (boolean2 ? 2 : 1) + ((string == null) ? "" : ("_" + string)) + ".png";
        return ArmorFeatureRenderer.ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
    }
    
    protected abstract void a(final A arg1, final EquipmentSlot arg2);
    
    protected abstract void a(final A arg1);
    
    static {
        SKIN = new Identifier("textures/misc/enchanted_item_glint.png");
        ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    }
}
