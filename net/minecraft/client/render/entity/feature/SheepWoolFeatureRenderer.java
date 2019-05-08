package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepWoolFeatureRenderer extends FeatureRenderer<SheepEntity, SheepWoolEntityModel<SheepEntity>>
{
    private static final Identifier SKIN;
    private final SheepEntityModel<SheepEntity> b;
    
    public SheepWoolFeatureRenderer(final FeatureRendererContext<SheepEntity, SheepWoolEntityModel<SheepEntity>> context) {
        super(context);
        this.b = new SheepEntityModel<SheepEntity>();
    }
    
    @Override
    public void render(final SheepEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.isSheared() || entity.isInvisible()) {
            return;
        }
        this.bindTexture(SheepWoolFeatureRenderer.SKIN);
        if (entity.hasCustomName() && "jeb_".equals(entity.getName().getText())) {
            final int integer9 = 25;
            final int integer10 = entity.age / 25 + entity.getEntityId();
            final int integer11 = DyeColor.values().length;
            final int integer12 = integer10 % integer11;
            final int integer13 = (integer10 + 1) % integer11;
            final float float9 = (entity.age % 25 + float4) / 25.0f;
            final float[] arr15 = SheepEntity.getRgbColor(DyeColor.byId(integer12));
            final float[] arr16 = SheepEntity.getRgbColor(DyeColor.byId(integer13));
            GlStateManager.color3f(arr15[0] * (1.0f - float9) + arr16[0] * float9, arr15[1] * (1.0f - float9) + arr16[1] * float9, arr15[2] * (1.0f - float9) + arr16[2] * float9);
        }
        else {
            final float[] arr17 = SheepEntity.getRgbColor(entity.getColor());
            GlStateManager.color3f(arr17[0], arr17[1], arr17[2]);
        }
        ((FeatureRenderer<T, SheepWoolEntityModel<SheepEntity>>)this).getModel().copyStateTo(this.b);
        this.b.animateModel(entity, float2, float3, float4);
        this.b.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
    
    static {
        SKIN = new Identifier("textures/entity/sheep/sheep_fur.png");
    }
}
