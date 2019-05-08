package net.minecraft.client.render.entity;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.IllusionerEntity;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderer extends IllagerEntityRenderer<IllusionerEntity>
{
    private static final Identifier SKIN;
    
    public IllusionerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvilVillagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature((FeatureRenderer<T, EvilVillagerEntityModel<T>>)new HeldItemFeatureRenderer<IllusionerEntity, EvilVillagerEntityModel<IllusionerEntity>>(this) {
            @Override
            public void render(final IllusionerEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
                if (entity.isSpellcasting() || entity.isAttacking()) {
                    super.render(entity, float2, float3, float4, float5, float6, float7, float8);
                }
            }
        });
        ((EvilVillagerEntityModel)this.model).b().visible = true;
    }
    
    protected Identifier getTexture(final IllusionerEntity illusionerEntity) {
        return IllusionerEntityRenderer.SKIN;
    }
    
    public void render(final IllusionerEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (entity.isInvisible()) {
            final Vec3d[] arr10 = entity.v(tickDelta);
            final float float11 = ((LivingEntityRenderer<T, M>)this).getAge((T)entity, tickDelta);
            for (int integer12 = 0; integer12 < arr10.length; ++integer12) {
                super.render((T)entity, x + arr10[integer12].x + MathHelper.cos(integer12 + float11 * 0.5f) * 0.025, y + arr10[integer12].y + MathHelper.cos(integer12 + float11 * 0.75f) * 0.0125, z + arr10[integer12].z + MathHelper.cos(integer12 + float11 * 0.7f) * 0.025, yaw, tickDelta);
            }
        }
        else {
            super.render((T)entity, x, y, z, yaw, tickDelta);
        }
    }
    
    protected boolean b(final IllusionerEntity illusionerEntity) {
        return true;
    }
    
    static {
        SKIN = new Identifier("textures/entity/illager/illusioner.png");
    }
}
