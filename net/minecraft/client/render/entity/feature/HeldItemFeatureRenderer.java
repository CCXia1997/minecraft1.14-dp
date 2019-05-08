package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.json.ModelTransformation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.AbsoluteHand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel> extends FeatureRenderer<T, M>
{
    public HeldItemFeatureRenderer(final FeatureRendererContext<T, M> context) {
        super(context);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final boolean boolean9 = entity.getMainHand() == AbsoluteHand.b;
        final ItemStack itemStack10 = boolean9 ? entity.getOffHandStack() : entity.getMainHandStack();
        final ItemStack itemStack11 = boolean9 ? entity.getMainHandStack() : entity.getOffHandStack();
        if (itemStack10.isEmpty() && itemStack11.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        if (((net.minecraft.client.render.entity.model.EntityModel)this.getModel()).isChild) {
            final float float9 = 0.5f;
            GlStateManager.translatef(0.0f, 0.75f, 0.0f);
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        }
        this.a(entity, itemStack11, ModelTransformation.Type.c, AbsoluteHand.b);
        this.a(entity, itemStack10, ModelTransformation.Type.b, AbsoluteHand.a);
        GlStateManager.popMatrix();
    }
    
    private void a(final LivingEntity livingEntity, final ItemStack itemStack, final ModelTransformation.Type type, final AbsoluteHand absoluteHand) {
        if (itemStack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        this.a(absoluteHand);
        if (livingEntity.isInSneakingPose()) {
            GlStateManager.translatef(0.0f, 0.2f, 0.0f);
        }
        GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        final boolean boolean5 = absoluteHand == AbsoluteHand.a;
        GlStateManager.translatef((boolean5 ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItemFromSide(livingEntity, itemStack, type, boolean5);
        GlStateManager.popMatrix();
    }
    
    protected void a(final AbsoluteHand absoluteHand) {
        this.getModel().setArmAngle(0.0625f, absoluteHand);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
