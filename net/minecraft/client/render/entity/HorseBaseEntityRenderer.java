package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>> extends MobEntityRenderer<T, M>
{
    private final float scale;
    
    public HorseBaseEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final M horseEntityModel, final float float3) {
        super(entityRenderDispatcher, horseEntityModel, 0.75f);
        this.scale = float3;
    }
    
    @Override
    protected void scale(final T entity, final float tickDelta) {
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        super.scale(entity, tickDelta);
    }
}
