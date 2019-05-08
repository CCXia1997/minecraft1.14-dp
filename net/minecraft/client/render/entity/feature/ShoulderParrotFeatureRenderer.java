package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.entity.EntityType;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>>
{
    private final ParrotEntityModel a;
    
    public ShoulderParrotFeatureRenderer(final FeatureRendererContext<T, PlayerEntityModel<T>> context) {
        super(context);
        this.a = new ParrotEntityModel();
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderShoulderParrot(entity, float2, float3, float4, float6, float7, float8, true);
        this.renderShoulderParrot(entity, float2, float3, float4, float6, float7, float8, false);
        GlStateManager.disableRescaleNormal();
    }
    
    private void renderShoulderParrot(final T playerEntity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final boolean boolean8) {
        final CompoundTag compoundTag9 = boolean8 ? playerEntity.getShoulderEntityLeft() : playerEntity.getShoulderEntityRight();
        final CompoundTag compoundTag10;
        EntityType.get(compoundTag9.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(boolean8 ? 0.4f : -0.4f, playerEntity.isInSneakingPose() ? -1.3f : -1.5f, 0.0f);
            this.bindTexture(ParrotEntityRenderer.SKINS[compoundTag10.getInt("Variant")]);
            this.a.a(float2, float3, float5, float6, float7, playerEntity.age);
            GlStateManager.popMatrix();
        });
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
