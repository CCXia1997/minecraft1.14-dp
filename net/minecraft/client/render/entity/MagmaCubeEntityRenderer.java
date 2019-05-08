package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.entity.mob.MagmaCubeEntity;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityRenderer extends MobEntityRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>>
{
    private static final Identifier SKIN;
    
    public MagmaCubeEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new MagmaCubeEntityModel(), 0.25f);
    }
    
    protected Identifier getTexture(final MagmaCubeEntity magmaCubeEntity) {
        return MagmaCubeEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final MagmaCubeEntity entity, final float tickDelta) {
        final int integer3 = entity.getSize();
        final float float4 = MathHelper.lerp(tickDelta, entity.lastStretch, entity.stretch) / (integer3 * 0.5f + 1.0f);
        final float float5 = 1.0f / (float4 + 1.0f);
        GlStateManager.scalef(float5 * integer3, 1.0f / float5 * integer3, float5 * integer3);
    }
    
    static {
        SKIN = new Identifier("textures/entity/slime/magmacube.png");
    }
}
