package net.minecraft.client.render.entity.feature;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>>
{
    public static final Identifier TEXTURE;
    private final a b;
    
    public TridentRiptideFeatureRenderer(final FeatureRendererContext<T, PlayerEntityModel<T>> context) {
        super(context);
        this.b = new a();
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.isUsingRiptide()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TridentRiptideFeatureRenderer.TEXTURE);
        for (int integer9 = 0; integer9 < 3; ++integer9) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(float5 * -(45 + integer9 * 5), 0.0f, 1.0f, 0.0f);
            final float float9 = 0.75f * integer9;
            GlStateManager.scalef(float9, float9, float9);
            GlStateManager.translatef(0.0f, -0.2f + 0.6f * integer9, 0.0f);
            this.b.a(float2, float3, float5, float6, float7, float8);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    static {
        TEXTURE = new Identifier("textures/entity/trident_riptide.png");
    }
    
    @Environment(EnvType.CLIENT)
    static class a extends Model
    {
        private final Cuboid a;
        
        public a() {
            this.textureWidth = 64;
            this.textureHeight = 64;
            (this.a = new Cuboid(this, 0, 0)).addBox(-8.0f, -16.0f, -8.0f, 16, 32, 16);
        }
        
        public void a(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.a.render(float6);
        }
    }
}
