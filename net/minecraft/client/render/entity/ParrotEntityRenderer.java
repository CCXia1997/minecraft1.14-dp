package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;

@Environment(EnvType.CLIENT)
public class ParrotEntityRenderer extends MobEntityRenderer<ParrotEntity, ParrotEntityModel>
{
    public static final Identifier[] SKINS;
    
    public ParrotEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ParrotEntityModel(), 0.3f);
    }
    
    protected Identifier getTexture(final ParrotEntity parrotEntity) {
        return ParrotEntityRenderer.SKINS[parrotEntity.getVariant()];
    }
    
    public float getAge(final ParrotEntity entity, final float tickDelta) {
        final float float3 = MathHelper.lerp(tickDelta, entity.bG, entity.bD);
        final float float4 = MathHelper.lerp(tickDelta, entity.bF, entity.bE);
        return (MathHelper.sin(float3) + 1.0f) * float4;
    }
    
    static {
        SKINS = new Identifier[] { new Identifier("textures/entity/parrot/parrot_red_blue.png"), new Identifier("textures/entity/parrot/parrot_blue.png"), new Identifier("textures/entity/parrot/parrot_green.png"), new Identifier("textures/entity/parrot/parrot_yellow_blue.png"), new Identifier("textures/entity/parrot/parrot_grey.png") };
    }
}
