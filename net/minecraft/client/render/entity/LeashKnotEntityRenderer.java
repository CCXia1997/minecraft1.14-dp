package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.LeadKnotEntity;

@Environment(EnvType.CLIENT)
public class LeashKnotEntityRenderer extends EntityRenderer<LeadKnotEntity>
{
    private static final Identifier SKIN;
    private final LeashEntityModel<LeadKnotEntity> model;
    
    public LeashKnotEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new LeashEntityModel<LeadKnotEntity>();
    }
    
    @Override
    public void render(final LeadKnotEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        final float float10 = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.bindEntityTexture(entity);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.model.render(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final LeadKnotEntity leadKnotEntity) {
        return LeashKnotEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/lead_knot.png");
    }
}
