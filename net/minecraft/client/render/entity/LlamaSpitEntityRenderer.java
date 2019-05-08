package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.LlamaSpitEntity;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity>
{
    private static final Identifier SKIN;
    private final LlamaSpitEntityModel<LlamaSpitEntity> model;
    
    public LlamaSpitEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new LlamaSpitEntityModel<LlamaSpitEntity>();
    }
    
    @Override
    public void render(final LlamaSpitEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y + 0.15f, (float)z);
        GlStateManager.rotatef(MathHelper.lerp(tickDelta, entity.prevYaw, entity.yaw) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch), 0.0f, 0.0f, 1.0f);
        this.bindEntityTexture(entity);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.model.render(entity, tickDelta, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final LlamaSpitEntity llamaSpitEntity) {
        return LlamaSpitEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/llama/spit.png");
    }
}
