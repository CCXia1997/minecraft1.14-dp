package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.BoatEntity;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderer extends EntityRenderer<BoatEntity>
{
    private static final Identifier[] SKIN;
    protected final BoatEntityModel model;
    
    public BoatEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new BoatEntityModel();
        this.c = 0.8f;
    }
    
    @Override
    public void render(final BoatEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        this.a(x, y, z);
        this.a(entity, yaw, tickDelta);
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
    
    public void a(final BoatEntity boatEntity, final float float2, final float float3) {
        GlStateManager.rotatef(180.0f - float2, 0.0f, 1.0f, 0.0f);
        final float float4 = boatEntity.n() - float3;
        float float5 = boatEntity.m() - float3;
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        if (float4 > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(float4) * float4 * float5 / 10.0f * boatEntity.o(), 1.0f, 0.0f, 0.0f);
        }
        final float float6 = boatEntity.b(float3);
        if (!MathHelper.equalsApproximate(float6, 0.0f)) {
            GlStateManager.rotatef(boatEntity.b(float3), 1.0f, 0.0f, 1.0f);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
    }
    
    public void a(final double double1, final double double3, final double double5) {
        GlStateManager.translatef((float)double1, (float)double3 + 0.375f, (float)double5);
    }
    
    @Override
    protected Identifier getTexture(final BoatEntity boatEntity) {
        return BoatEntityRenderer.SKIN[boatEntity.getBoatType().ordinal()];
    }
    
    @Override
    public boolean hasSecondPass() {
        return true;
    }
    
    @Override
    public void renderSecondPass(final BoatEntity boatEntity, final double double2, final double double4, final double double6, final float float8, final float float9) {
        GlStateManager.pushMatrix();
        this.a(double2, double4, double6);
        this.a(boatEntity, float8, float9);
        this.bindEntityTexture(boatEntity);
        this.model.renderPass(boatEntity, float9, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        SKIN = new Identifier[] { new Identifier("textures/entity/boat/oak.png"), new Identifier("textures/entity/boat/spruce.png"), new Identifier("textures/entity/boat/birch.png"), new Identifier("textures/entity/boat/jungle.png"), new Identifier("textures/entity/boat/acacia.png"), new Identifier("textures/entity/boat/dark_oak.png") };
    }
}
