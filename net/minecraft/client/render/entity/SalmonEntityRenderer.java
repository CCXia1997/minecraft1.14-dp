package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.entity.passive.SalmonEntity;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>>
{
    private static final Identifier SKIN;
    
    public SalmonEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SalmonEntityModel(), 0.4f);
    }
    
    @Nullable
    protected Identifier getTexture(final SalmonEntity salmonEntity) {
        return SalmonEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final SalmonEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        float float5 = 1.0f;
        float float6 = 1.0f;
        if (!entity.isInsideWater()) {
            float5 = 1.3f;
            float6 = 1.7f;
        }
        final float float7 = float5 * 4.3f * MathHelper.sin(float6 * 0.6f * pitch);
        GlStateManager.rotatef(float7, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, -0.4f);
        if (!entity.isInsideWater()) {
            GlStateManager.translatef(0.2f, 0.1f, 0.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
    
    static {
        SKIN = new Identifier("textures/entity/fish/salmon.png");
    }
}
