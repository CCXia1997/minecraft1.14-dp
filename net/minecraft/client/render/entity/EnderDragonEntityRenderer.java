package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.EnderDragonDeathFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.EnderDragonEyesFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends MobEntityRenderer<EnderDragonEntity, DragonEntityModel>
{
    public static final Identifier CRYSTAL_BEAM;
    private static final Identifier EXPLOSION_TEX;
    private static final Identifier SKIN;
    
    public EnderDragonEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DragonEntityModel(0.0f), 0.5f);
        this.addFeature(new EnderDragonEyesFeatureRenderer(this));
        this.addFeature(new EnderDragonDeathFeatureRenderer(this));
    }
    
    @Override
    protected void setupTransforms(final EnderDragonEntity entity, final float pitch, final float yaw, final float delta) {
        final float float5 = (float)entity.a(7, delta)[0];
        final float float6 = (float)(entity.a(5, delta)[1] - entity.a(10, delta)[1]);
        GlStateManager.rotatef(-float5, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float6 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, 1.0f);
        if (entity.deathTime > 0) {
            float float7 = (entity.deathTime + delta - 1.0f) / 20.0f * 1.6f;
            float7 = MathHelper.sqrt(float7);
            if (float7 > 1.0f) {
                float7 = 1.0f;
            }
            GlStateManager.rotatef(float7 * ((LivingEntityRenderer<EnderDragonEntity, M>)this).getLyingAngle(entity), 0.0f, 0.0f, 1.0f);
        }
    }
    
    @Override
    protected void render(final EnderDragonEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        if (entity.bL > 0) {
            final float float8 = entity.bL / 200.0f;
            GlStateManager.depthFunc(515);
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(516, float8);
            this.bindTexture(EnderDragonEntityRenderer.EXPLOSION_TEX);
            ((DragonEntityModel)this.model).render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.depthFunc(514);
        }
        this.bindEntityTexture((T)entity);
        ((DragonEntityModel)this.model).render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (entity.hurtTime > 0) {
            GlStateManager.depthFunc(514);
            GlStateManager.disableTexture();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color4f(1.0f, 0.0f, 0.0f, 0.5f);
            ((DragonEntityModel)this.model).render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.depthFunc(515);
        }
    }
    
    @Override
    public void render(final EnderDragonEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        super.render(entity, x, y, z, yaw, tickDelta);
        if (entity.enderCrystal != null) {
            this.bindTexture(EnderDragonEntityRenderer.CRYSTAL_BEAM);
            float float10 = MathHelper.sin((entity.enderCrystal.age + tickDelta) * 0.2f) / 2.0f + 0.5f;
            float10 = (float10 * float10 + float10) * 0.2f;
            renderCrystalBeam(x, y, z, tickDelta, MathHelper.lerp(1.0f - tickDelta, entity.x, entity.prevX), MathHelper.lerp(1.0f - tickDelta, entity.y, entity.prevY), MathHelper.lerp(1.0f - tickDelta, entity.z, entity.prevZ), entity.age, entity.enderCrystal.x, float10 + entity.enderCrystal.y, entity.enderCrystal.z);
        }
    }
    
    public static void renderCrystalBeam(final double double1, final double double3, final double double5, final float float7, final double double8, final double double10, final double double12, final int integer14, final double double15, final double double17, final double double19) {
        final float float8 = (float)(double15 - double8);
        final float float9 = (float)(double17 - 1.0 - double10);
        final float float10 = (float)(double19 - double12);
        final float float11 = MathHelper.sqrt(float8 * float8 + float10 * float10);
        final float float12 = MathHelper.sqrt(float8 * float8 + float9 * float9 + float10 * float10);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double1, (float)double3 + 2.0f, (float)double5);
        GlStateManager.rotatef((float)(-Math.atan2(float10, float8)) * 57.295776f - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(-Math.atan2(float11, float9)) * 57.295776f - 90.0f, 1.0f, 0.0f, 0.0f);
        final Tessellator tessellator26 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder27 = tessellator26.getBufferBuilder();
        GuiLighting.disable();
        GlStateManager.disableCull();
        GlStateManager.shadeModel(7425);
        final float float13 = 0.0f - (integer14 + float7) * 0.01f;
        final float float14 = MathHelper.sqrt(float8 * float8 + float9 * float9 + float10 * float10) / 32.0f - (integer14 + float7) * 0.01f;
        bufferBuilder27.begin(5, VertexFormats.POSITION_UV_COLOR);
        final int integer15 = 8;
        for (int integer16 = 0; integer16 <= 8; ++integer16) {
            final float float15 = MathHelper.sin(integer16 % 8 * 6.2831855f / 8.0f) * 0.75f;
            final float float16 = MathHelper.cos(integer16 % 8 * 6.2831855f / 8.0f) * 0.75f;
            final float float17 = integer16 % 8 / 8.0f;
            bufferBuilder27.vertex(float15 * 0.2f, float16 * 0.2f, 0.0).texture(float17, float13).color(0, 0, 0, 255).next();
            bufferBuilder27.vertex(float15, float16, float12).texture(float17, float14).color(255, 255, 255, 255).next();
        }
        tessellator26.draw();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(7424);
        GuiLighting.enable();
        GlStateManager.popMatrix();
    }
    
    protected Identifier getTexture(final EnderDragonEntity enderDragonEntity) {
        return EnderDragonEntityRenderer.SKIN;
    }
    
    static {
        CRYSTAL_BEAM = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
        EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
        SKIN = new Identifier("textures/entity/enderdragon/dragon.png");
    }
}
