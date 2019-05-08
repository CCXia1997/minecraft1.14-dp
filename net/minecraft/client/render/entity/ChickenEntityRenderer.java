package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.entity.passive.ChickenEntity;

@Environment(EnvType.CLIENT)
public class ChickenEntityRenderer extends MobEntityRenderer<ChickenEntity, ChickenEntityModel<ChickenEntity>>
{
    private static final Identifier SKIN;
    
    public ChickenEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ChickenEntityModel(), 0.3f);
    }
    
    protected Identifier getTexture(final ChickenEntity chickenEntity) {
        return ChickenEntityRenderer.SKIN;
    }
    
    @Override
    protected float getAge(final ChickenEntity entity, final float tickDelta) {
        final float float3 = MathHelper.lerp(tickDelta, entity.bD, entity.bz);
        final float float4 = MathHelper.lerp(tickDelta, entity.bB, entity.bA);
        return (MathHelper.sin(float3) + 1.0f) * float4;
    }
    
    static {
        SKIN = new Identifier("textures/entity/chicken.png");
    }
}
