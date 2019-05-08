package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>
{
    public static final Identifier TEX;
    
    public ArmorStandEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0f);
        this.addFeature((FeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, new ArmorStandArmorEntityModel(0.5f), new ArmorStandArmorEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
    }
    
    protected Identifier getTexture(final ArmorStandEntity armorStandEntity) {
        return ArmorStandEntityRenderer.TEX;
    }
    
    @Override
    protected void setupTransforms(final ArmorStandEntity entity, final float pitch, final float yaw, final float delta) {
        GlStateManager.rotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        final float float5 = entity.world.getTime() - entity.bt + delta;
        if (float5 < 5.0f) {
            GlStateManager.rotatef(MathHelper.sin(float5 / 1.5f * 3.1415927f) * 3.0f, 0.0f, 1.0f, 0.0f);
        }
    }
    
    @Override
    protected boolean hasLabel(final ArmorStandEntity entity) {
        return entity.isCustomNameVisible();
    }
    
    @Override
    public void render(final ArmorStandEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (entity.isMarker()) {
            this.disableOutlineRender = true;
        }
        super.render(entity, x, y, z, yaw, tickDelta);
        if (entity.isMarker()) {
            this.disableOutlineRender = false;
        }
    }
    
    static {
        TEX = new Identifier("textures/entity/armorstand/wood.png");
    }
}
