package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Environment(EnvType.CLIENT)
public class DragonEntityModel extends EntityModel<EnderDragonEntity>
{
    private final Cuboid head;
    private final Cuboid neck;
    private final Cuboid jaw;
    private final Cuboid body;
    private final Cuboid rearLeg;
    private final Cuboid frontLeg;
    private final Cuboid rearLegTip;
    private final Cuboid frontLegTip;
    private final Cuboid rearFoot;
    private final Cuboid frontFoot;
    private final Cuboid wing;
    private final Cuboid wingTip;
    private float delta;
    
    public DragonEntityModel(final float float1) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        final float float2 = -16.0f;
        (this.head = new Cuboid(this, "head")).addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, float1, 176, 44);
        this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, float1, 112, 30);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, float1, 0, 0);
        this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, float1, 112, 0);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, float1, 0, 0);
        this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, float1, 112, 0);
        (this.jaw = new Cuboid(this, "jaw")).setRotationPoint(0.0f, 4.0f, -8.0f);
        this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, float1, 176, 65);
        this.head.addChild(this.jaw);
        (this.neck = new Cuboid(this, "neck")).addBox("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, float1, 192, 104);
        this.neck.addBox("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, float1, 48, 0);
        (this.body = new Cuboid(this, "body")).setRotationPoint(0.0f, 4.0f, 8.0f);
        this.body.addBox("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, float1, 0, 0);
        this.body.addBox("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, float1, 220, 53);
        this.body.addBox("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, float1, 220, 53);
        this.body.addBox("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, float1, 220, 53);
        (this.wing = new Cuboid(this, "wing")).setRotationPoint(-12.0f, 5.0f, 2.0f);
        this.wing.addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, float1, 112, 88);
        this.wing.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, float1, -56, 88);
        (this.wingTip = new Cuboid(this, "wingtip")).setRotationPoint(-56.0f, 0.0f, 0.0f);
        this.wingTip.addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, float1, 112, 136);
        this.wingTip.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, float1, -56, 144);
        this.wing.addChild(this.wingTip);
        (this.frontLeg = new Cuboid(this, "frontleg")).setRotationPoint(-12.0f, 20.0f, 2.0f);
        this.frontLeg.addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, float1, 112, 104);
        (this.frontLegTip = new Cuboid(this, "frontlegtip")).setRotationPoint(0.0f, 20.0f, -1.0f);
        this.frontLegTip.addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, float1, 226, 138);
        this.frontLeg.addChild(this.frontLegTip);
        (this.frontFoot = new Cuboid(this, "frontfoot")).setRotationPoint(0.0f, 23.0f, 0.0f);
        this.frontFoot.addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, float1, 144, 104);
        this.frontLegTip.addChild(this.frontFoot);
        (this.rearLeg = new Cuboid(this, "rearleg")).setRotationPoint(-16.0f, 16.0f, 42.0f);
        this.rearLeg.addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, float1, 0, 0);
        (this.rearLegTip = new Cuboid(this, "rearlegtip")).setRotationPoint(0.0f, 32.0f, -4.0f);
        this.rearLegTip.addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, float1, 196, 0);
        this.rearLeg.addChild(this.rearLegTip);
        (this.rearFoot = new Cuboid(this, "rearfoot")).setRotationPoint(0.0f, 31.0f, 4.0f);
        this.rearFoot.addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, float1, 112, 0);
        this.rearLegTip.addChild(this.rearFoot);
    }
    
    @Override
    public void animateModel(final EnderDragonEntity entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.delta = tickDelta;
    }
    
    @Override
    public void render(final EnderDragonEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        GlStateManager.pushMatrix();
        final float float8 = MathHelper.lerp(this.delta, entity.bI, entity.bJ);
        this.jaw.pitch = (float)(Math.sin(float8 * 6.2831855f) + 1.0) * 0.2f;
        float float9 = (float)(Math.sin(float8 * 6.2831855f - 1.0f) + 1.0);
        float9 = (float9 * float9 + float9 * 2.0f) * 0.05f;
        GlStateManager.translatef(0.0f, float9 - 2.0f, -3.0f);
        GlStateManager.rotatef(float9 * 2.0f, 1.0f, 0.0f, 0.0f);
        float float10 = 0.0f;
        float float11 = 20.0f;
        float float12 = -12.0f;
        final float float13 = 1.5f;
        double[] arr14 = entity.a(6, this.delta);
        final float float14 = this.updateRotations(entity.a(5, this.delta)[0] - entity.a(10, this.delta)[0]);
        final float float15 = this.updateRotations(entity.a(5, this.delta)[0] + float14 / 2.0f);
        float float16 = float8 * 6.2831855f;
        for (int integer18 = 0; integer18 < 5; ++integer18) {
            final double[] arr15 = entity.a(5 - integer18, this.delta);
            final float float17 = (float)Math.cos(integer18 * 0.45f + float16) * 0.15f;
            this.neck.yaw = this.updateRotations(arr15[0] - arr14[0]) * 0.017453292f * 1.5f;
            this.neck.pitch = float17 + entity.a(integer18, arr14, arr15) * 0.017453292f * 1.5f * 5.0f;
            this.neck.roll = -this.updateRotations(arr15[0] - float15) * 0.017453292f * 1.5f;
            this.neck.rotationPointY = float11;
            this.neck.rotationPointZ = float12;
            this.neck.rotationPointX = float10;
            float11 += (float)(Math.sin(this.neck.pitch) * 10.0);
            float12 -= (float)(Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            float10 -= (float)(Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            this.neck.render(scale);
        }
        this.head.rotationPointY = float11;
        this.head.rotationPointZ = float12;
        this.head.rotationPointX = float10;
        double[] arr16 = entity.a(0, this.delta);
        this.head.yaw = this.updateRotations(arr16[0] - arr14[0]) * 0.017453292f;
        this.head.pitch = this.updateRotations(entity.a(6, arr14, arr16)) * 0.017453292f * 1.5f * 5.0f;
        this.head.roll = -this.updateRotations(arr16[0] - float15) * 0.017453292f;
        this.head.render(scale);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-float14 * 1.5f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(0.0f, -1.0f, 0.0f);
        this.body.roll = 0.0f;
        this.body.render(scale);
        for (int integer19 = 0; integer19 < 2; ++integer19) {
            GlStateManager.enableCull();
            final float float17 = float8 * 6.2831855f;
            this.wing.pitch = 0.125f - (float)Math.cos(float17) * 0.2f;
            this.wing.yaw = 0.25f;
            this.wing.roll = (float)(Math.sin(float17) + 0.125) * 0.8f;
            this.wingTip.roll = -(float)(Math.sin(float17 + 2.0f) + 0.5) * 0.75f;
            this.rearLeg.pitch = 1.0f + float9 * 0.1f;
            this.rearLegTip.pitch = 0.5f + float9 * 0.1f;
            this.rearFoot.pitch = 0.75f + float9 * 0.1f;
            this.frontLeg.pitch = 1.3f + float9 * 0.1f;
            this.frontLegTip.pitch = -0.5f - float9 * 0.1f;
            this.frontFoot.pitch = 0.75f + float9 * 0.1f;
            this.wing.render(scale);
            this.frontLeg.render(scale);
            this.rearLeg.render(scale);
            GlStateManager.scalef(-1.0f, 1.0f, 1.0f);
            if (integer19 == 0) {
                GlStateManager.cullFace(GlStateManager.FaceSides.a);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.FaceSides.b);
        GlStateManager.disableCull();
        float float18 = -(float)Math.sin(float8 * 6.2831855f) * 0.0f;
        float16 = float8 * 6.2831855f;
        float11 = 10.0f;
        float12 = 60.0f;
        float10 = 0.0f;
        arr14 = entity.a(11, this.delta);
        for (int integer20 = 0; integer20 < 12; ++integer20) {
            arr16 = entity.a(12 + integer20, this.delta);
            float18 += (float)(Math.sin(integer20 * 0.45f + float16) * 0.05000000074505806);
            this.neck.yaw = (this.updateRotations(arr16[0] - arr14[0]) * 1.5f + 180.0f) * 0.017453292f;
            this.neck.pitch = float18 + (float)(arr16[1] - arr14[1]) * 0.017453292f * 1.5f * 5.0f;
            this.neck.roll = this.updateRotations(arr16[0] - float15) * 0.017453292f * 1.5f;
            this.neck.rotationPointY = float11;
            this.neck.rotationPointZ = float12;
            this.neck.rotationPointX = float10;
            float11 += (float)(Math.sin(this.neck.pitch) * 10.0);
            float12 -= (float)(Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            float10 -= (float)(Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            this.neck.render(scale);
        }
        GlStateManager.popMatrix();
    }
    
    private float updateRotations(double double1) {
        while (double1 >= 180.0) {
            double1 -= 360.0;
        }
        while (double1 < -180.0) {
            double1 += 360.0;
        }
        return (float)double1;
    }
}
