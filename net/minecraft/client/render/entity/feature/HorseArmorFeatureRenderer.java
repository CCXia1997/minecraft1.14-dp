package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.item.HorseArmorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseEntity;

@Environment(EnvType.CLIENT)
public class HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>>
{
    private final HorseEntityModel<HorseEntity> model;
    
    public HorseArmorFeatureRenderer(final FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> context) {
        super(context);
        this.model = new HorseEntityModel<HorseEntity>(0.1f);
    }
    
    @Override
    public void render(final HorseEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getArmorType();
        if (itemStack9.getItem() instanceof HorseArmorItem) {
            final HorseArmorItem horseArmorItem10 = (HorseArmorItem)itemStack9.getItem();
            ((FeatureRenderer<T, HorseEntityModel<HorseEntity>>)this).getModel().copyStateTo(this.model);
            this.model.animateModel(entity, float2, float3, float4);
            this.bindTexture(horseArmorItem10.getHorseArmorTexture());
            if (horseArmorItem10 instanceof DyeableHorseArmorItem) {
                final int integer11 = ((DyeableHorseArmorItem)horseArmorItem10).getColor(itemStack9);
                final float float9 = (integer11 >> 16 & 0xFF) / 255.0f;
                final float float10 = (integer11 >> 8 & 0xFF) / 255.0f;
                final float float11 = (integer11 & 0xFF) / 255.0f;
                GlStateManager.color4f(float9, float10, float11, 1.0f);
                this.model.render(entity, float2, float3, float5, float6, float7, float8);
                return;
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.model.render(entity, float2, float3, float5, float6, float7, float8);
        }
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
