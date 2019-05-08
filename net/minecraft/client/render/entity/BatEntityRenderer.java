package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;

@Environment(EnvType.CLIENT)
public class BatEntityRenderer extends MobEntityRenderer<BatEntity, BatEntityModel>
{
    private static final Identifier SKIN;
    
    public BatEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BatEntityModel(), 0.25f);
    }
    
    protected Identifier getTexture(final BatEntity batEntity) {
        return BatEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final BatEntity entity, final float tickDelta) {
        GlStateManager.scalef(0.35f, 0.35f, 0.35f);
    }
    
    @Override
    protected void setupTransforms(final BatEntity entity, final float pitch, final float yaw, final float delta) {
        if (entity.isRoosting()) {
            GlStateManager.translatef(0.0f, -0.1f, 0.0f);
        }
        else {
            GlStateManager.translatef(0.0f, MathHelper.cos(pitch * 0.3f) * 0.1f, 0.0f);
        }
        super.setupTransforms(entity, pitch, yaw, delta);
    }
    
    static {
        SKIN = new Identifier("textures/entity/bat.png");
    }
}
