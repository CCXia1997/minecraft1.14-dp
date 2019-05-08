package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;

@Environment(EnvType.CLIENT)
public class VexEntityRenderer extends BipedEntityRenderer<VexEntity, VexEntityModel>
{
    private static final Identifier TEXTURE;
    private static final Identifier CHARGING_TEXTURE;
    
    public VexEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new VexEntityModel(), 0.3f);
    }
    
    @Override
    protected Identifier getTexture(final VexEntity vexEntity) {
        if (vexEntity.isCharging()) {
            return VexEntityRenderer.CHARGING_TEXTURE;
        }
        return VexEntityRenderer.TEXTURE;
    }
    
    @Override
    protected void scale(final VexEntity entity, final float tickDelta) {
        GlStateManager.scalef(0.4f, 0.4f, 0.4f);
    }
    
    static {
        TEXTURE = new Identifier("textures/entity/illager/vex.png");
        CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");
    }
}
