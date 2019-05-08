package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.EnderCrystalEntity;

@Environment(EnvType.CLIENT)
public class EnderCrystalEntityRenderer extends EntityRenderer<EnderCrystalEntity>
{
    private static final Identifier SKIN;
    private final EntityModel<EnderCrystalEntity> f;
    private final EntityModel<EnderCrystalEntity> g;
    
    public EnderCrystalEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.f = new EndCrystalEntityModel<EnderCrystalEntity>(0.0f, true);
        this.g = new EndCrystalEntityModel<EnderCrystalEntity>(0.0f, false);
        this.c = 0.5f;
    }
    
    @Override
    public void render(final EnderCrystalEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final float float10 = entity.b + tickDelta;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        this.bindTexture(EnderCrystalEntityRenderer.SKIN);
        float float11 = MathHelper.sin(float10 * 0.2f) / 2.0f + 0.5f;
        float11 += float11 * float11;
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        if (entity.getShowBottom()) {
            this.f.render(entity, 0.0f, float10 * 3.0f, float11 * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        else {
            this.g.render(entity, 0.0f, float10 * 3.0f, float11 * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        final BlockPos blockPos12 = entity.getBeamTarget();
        if (blockPos12 != null) {
            this.bindTexture(EnderDragonEntityRenderer.CRYSTAL_BEAM);
            final float float12 = blockPos12.getX() + 0.5f;
            final float float13 = blockPos12.getY() + 0.5f;
            final float float14 = blockPos12.getZ() + 0.5f;
            final double double16 = float12 - entity.x;
            final double double17 = float13 - entity.y;
            final double double18 = float14 - entity.z;
            EnderDragonEntityRenderer.renderCrystalBeam(x + double16, y - 0.3 + float11 * 0.4f + double17, z + double18, tickDelta, float12, float13, float14, entity.b, entity.x, entity.y, entity.z);
        }
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final EnderCrystalEntity enderCrystalEntity) {
        return EnderCrystalEntityRenderer.SKIN;
    }
    
    @Override
    public boolean isVisible(final EnderCrystalEntity enderCrystalEntity, final VisibleRegion visibleRegion, final double double3, final double double5, final double double7) {
        return super.isVisible(enderCrystalEntity, visibleRegion, double3, double5, double7) || enderCrystalEntity.getBeamTarget() != null;
    }
    
    static {
        SKIN = new Identifier("textures/entity/end_crystal/end_crystal.png");
    }
}
