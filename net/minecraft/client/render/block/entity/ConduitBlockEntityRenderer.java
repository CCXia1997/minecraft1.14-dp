package net.minecraft.client.render.block.entity;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer extends BlockEntityRenderer<ConduitBlockEntity>
{
    private static final Identifier BASE_TEX;
    private static final Identifier CAGE_TEX;
    private static final Identifier WIND_TEX;
    private static final Identifier WIND_VERTICAL_TEX;
    private static final Identifier OPEN_EYE_TEX;
    private static final Identifier CLOSED_EYE_TEX;
    private final BaseModel baseModel;
    private final CageModel cageModel;
    private final WindModel windModel;
    private final EyeModel eyeModel;
    
    public ConduitBlockEntityRenderer() {
        this.baseModel = new BaseModel();
        this.cageModel = new CageModel();
        this.windModel = new WindModel();
        this.eyeModel = new EyeModel();
    }
    
    @Override
    public void render(final ConduitBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final float float10 = entity.ticks + tickDelta;
        if (!entity.isActive()) {
            final float float11 = entity.getRotation(0.0f);
            this.bindTexture(ConduitBlockEntityRenderer.BASE_TEX);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
            GlStateManager.rotatef(float11, 0.0f, 1.0f, 0.0f);
            this.baseModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
            GlStateManager.popMatrix();
        }
        else if (entity.isActive()) {
            final float float11 = entity.getRotation(tickDelta) * 57.295776f;
            float float12 = MathHelper.sin(float10 * 0.1f) / 2.0f + 0.5f;
            float12 += float12 * float12;
            this.bindTexture(ConduitBlockEntityRenderer.CAGE_TEX);
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.3f + float12 * 0.2f, (float)zOffset + 0.5f);
            GlStateManager.rotatef(float11, 0.5f, 1.0f, 0.5f);
            this.cageModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
            GlStateManager.popMatrix();
            final int integer13 = 3;
            final int integer14 = entity.ticks / 3 % 22;
            this.windModel.a(integer14);
            final int integer15 = entity.ticks / 66 % 3;
            switch (integer15) {
                case 0: {
                    this.bindTexture(ConduitBlockEntityRenderer.WIND_TEX);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                    GlStateManager.scalef(0.875f, 0.875f, 0.875f);
                    GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    break;
                }
                case 1: {
                    this.bindTexture(ConduitBlockEntityRenderer.WIND_VERTICAL_TEX);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                    GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                    GlStateManager.scalef(0.875f, 0.875f, 0.875f);
                    GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    break;
                }
                case 2: {
                    this.bindTexture(ConduitBlockEntityRenderer.WIND_TEX);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                    GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
                    GlStateManager.scalef(0.875f, 0.875f, 0.875f);
                    GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    break;
                }
            }
            final Camera camera16 = this.renderManager.cameraEntity;
            if (entity.isEyeOpen()) {
                this.bindTexture(ConduitBlockEntityRenderer.OPEN_EYE_TEX);
            }
            else {
                this.bindTexture(ConduitBlockEntityRenderer.CLOSED_EYE_TEX);
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.3f + float12 * 0.2f, (float)zOffset + 0.5f);
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.rotatef(-camera16.getYaw(), 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(camera16.getPitch(), 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            this.eyeModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.083333336f);
            GlStateManager.popMatrix();
        }
        super.render(entity, xOffset, yOffset, zOffset, tickDelta, blockBreakStage);
    }
    
    static {
        BASE_TEX = new Identifier("textures/entity/conduit/base.png");
        CAGE_TEX = new Identifier("textures/entity/conduit/cage.png");
        WIND_TEX = new Identifier("textures/entity/conduit/wind.png");
        WIND_VERTICAL_TEX = new Identifier("textures/entity/conduit/wind_vertical.png");
        OPEN_EYE_TEX = new Identifier("textures/entity/conduit/open_eye.png");
        CLOSED_EYE_TEX = new Identifier("textures/entity/conduit/closed_eye.png");
    }
    
    @Environment(EnvType.CLIENT)
    static class BaseModel extends Model
    {
        private final Cuboid cuboid;
        
        public BaseModel() {
            this.textureWidth = 32;
            this.textureHeight = 16;
            (this.cuboid = new Cuboid(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.cuboid.render(float6);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class CageModel extends Model
    {
        private final Cuboid cuboid;
        
        public CageModel() {
            this.textureWidth = 32;
            this.textureHeight = 16;
            (this.cuboid = new Cuboid(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.cuboid.render(float6);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class WindModel extends Model
    {
        private final Cuboid[] cuboids;
        private int b;
        
        public WindModel() {
            this.cuboids = new Cuboid[22];
            this.textureWidth = 64;
            this.textureHeight = 1024;
            for (int integer1 = 0; integer1 < 22; ++integer1) {
                (this.cuboids[integer1] = new Cuboid(this, 0, 32 * integer1)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
            }
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.cuboids[this.b].render(float6);
        }
        
        public void a(final int integer) {
            this.b = integer;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class EyeModel extends Model
    {
        private final Cuboid cuboid;
        
        public EyeModel() {
            this.textureWidth = 8;
            this.textureHeight = 8;
            (this.cuboid = new Cuboid(this, 0, 0)).addBox(-4.0f, -4.0f, 0.0f, 8, 8, 0, 0.01f);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.cuboid.render(float6);
        }
    }
}
