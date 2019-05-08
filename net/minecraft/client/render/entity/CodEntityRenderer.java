package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.entity.passive.CodEntity;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>>
{
    private static final Identifier SKIN;
    
    public CodEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CodEntityModel(), 0.3f);
    }
    
    @Nullable
    protected Identifier getTexture(final CodEntity codEntity) {
        return CodEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final CodEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        final float float5 = 4.3f * MathHelper.sin(0.6f * pitch);
        GlStateManager.rotatef(float5, 0.0f, 1.0f, 0.0f);
        if (!entity.isInsideWater()) {
            GlStateManager.translatef(0.1f, 0.1f, -0.1f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
    
    static {
        SKIN = new Identifier("textures/entity/fish/cod.png");
    }
}
