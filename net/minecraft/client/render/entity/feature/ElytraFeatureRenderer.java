package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>
{
    private static final Identifier SKIN;
    private final ElytraEntityModel<T> elytra;
    
    public ElytraFeatureRenderer(final FeatureRendererContext<T, M> context) {
        super(context);
        this.elytra = new ElytraEntityModel<T>();
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack9.getItem() != Items.oX) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (entity instanceof AbstractClientPlayerEntity) {
            final AbstractClientPlayerEntity abstractClientPlayerEntity10 = (AbstractClientPlayerEntity)entity;
            if (abstractClientPlayerEntity10.canRenderElytraTexture() && abstractClientPlayerEntity10.getElytraTexture() != null) {
                this.bindTexture(abstractClientPlayerEntity10.getElytraTexture());
            }
            else if (abstractClientPlayerEntity10.canRenderCapeTexture() && abstractClientPlayerEntity10.getCapeTexture() != null && abstractClientPlayerEntity10.isSkinOverlayVisible(PlayerModelPart.CAPE)) {
                this.bindTexture(abstractClientPlayerEntity10.getCapeTexture());
            }
            else {
                this.bindTexture(ElytraFeatureRenderer.SKIN);
            }
        }
        else {
            this.bindTexture(ElytraFeatureRenderer.SKIN);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 0.125f);
        this.elytra.render(entity, float2, float3, float5, float6, float7, float8);
        this.elytra.render(entity, float2, float3, float5, float6, float7, float8);
        if (itemStack9.hasEnchantments()) {
            ArmorFeatureRenderer.<T>renderEnchantedGlint(this::bindTexture, entity, this.elytra, float2, float3, float4, float5, float6, float7, float8);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    static {
        SKIN = new Identifier("textures/entity/elytra.png");
    }
}
