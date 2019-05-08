package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.GuardianEntity;

@Environment(EnvType.CLIENT)
public class GuardianEntityModel extends EntityModel<GuardianEntity>
{
    private static final float[] a;
    private static final float[] b;
    private static final float[] f;
    private static final float[] g;
    private static final float[] h;
    private static final float[] i;
    private final Cuboid j;
    private final Cuboid k;
    private final Cuboid[] l;
    private final Cuboid[] m;
    
    public GuardianEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.l = new Cuboid[12];
        this.j = new Cuboid(this);
        this.j.setTextureOffset(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12, 12, 16);
        this.j.setTextureOffset(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2, 12, 12);
        this.j.setTextureOffset(0, 28).addBox(6.0f, 10.0f, -6.0f, 2, 12, 12, true);
        this.j.setTextureOffset(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12, 2, 12);
        this.j.setTextureOffset(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12, 2, 12);
        for (int integer1 = 0; integer1 < this.l.length; ++integer1) {
            (this.l[integer1] = new Cuboid(this, 0, 0)).addBox(-1.0f, -4.5f, -1.0f, 2, 9, 2);
            this.j.addChild(this.l[integer1]);
        }
        (this.k = new Cuboid(this, 8, 0)).addBox(-1.0f, 15.0f, 0.0f, 2, 2, 1);
        this.j.addChild(this.k);
        this.m = new Cuboid[3];
        (this.m[0] = new Cuboid(this, 40, 0)).addBox(-2.0f, 14.0f, 7.0f, 4, 4, 8);
        (this.m[1] = new Cuboid(this, 0, 54)).addBox(0.0f, 14.0f, 0.0f, 3, 3, 7);
        this.m[2] = new Cuboid(this);
        this.m[2].setTextureOffset(41, 32).addBox(0.0f, 14.0f, 0.0f, 2, 2, 6);
        this.m[2].setTextureOffset(25, 19).addBox(1.0f, 10.5f, 3.0f, 1, 9, 9);
        this.j.addChild(this.m[0]);
        this.m[0].addChild(this.m[1]);
        this.m[1].addChild(this.m[2]);
    }
    
    @Override
    public void render(final GuardianEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.j.render(scale);
    }
    
    @Override
    public void render(final GuardianEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final float float8 = age - entity.age;
        this.j.yaw = headYaw * 0.017453292f;
        this.j.pitch = headPitch * 0.017453292f;
        final float float9 = (1.0f - entity.getTailAngle(float8)) * 0.55f;
        for (int integer10 = 0; integer10 < 12; ++integer10) {
            this.l[integer10].pitch = 3.1415927f * GuardianEntityModel.a[integer10];
            this.l[integer10].yaw = 3.1415927f * GuardianEntityModel.b[integer10];
            this.l[integer10].roll = 3.1415927f * GuardianEntityModel.f[integer10];
            this.l[integer10].rotationPointX = GuardianEntityModel.g[integer10] * (1.0f + MathHelper.cos(age * 1.5f + integer10) * 0.01f - float9);
            this.l[integer10].rotationPointY = 16.0f + GuardianEntityModel.h[integer10] * (1.0f + MathHelper.cos(age * 1.5f + integer10) * 0.01f - float9);
            this.l[integer10].rotationPointZ = GuardianEntityModel.i[integer10] * (1.0f + MathHelper.cos(age * 1.5f + integer10) * 0.01f - float9);
        }
        this.k.rotationPointZ = -8.25f;
        Entity entity2 = MinecraftClient.getInstance().getCameraEntity();
        if (entity.hasBeamTarget()) {
            entity2 = entity.getBeamTarget();
        }
        if (entity2 != null) {
            final Vec3d vec3d11 = entity2.getCameraPosVec(0.0f);
            final Vec3d vec3d12 = entity.getCameraPosVec(0.0f);
            final double double13 = vec3d11.y - vec3d12.y;
            if (double13 > 0.0) {
                this.k.rotationPointY = 0.0f;
            }
            else {
                this.k.rotationPointY = 1.0f;
            }
            Vec3d vec3d13 = entity.getRotationVec(0.0f);
            vec3d13 = new Vec3d(vec3d13.x, 0.0, vec3d13.z);
            final Vec3d vec3d14 = new Vec3d(vec3d12.x - vec3d11.x, 0.0, vec3d12.z - vec3d11.z).normalize().rotateY(1.5707964f);
            final double double14 = vec3d13.dotProduct(vec3d14);
            this.k.rotationPointX = MathHelper.sqrt((float)Math.abs(double14)) * 2.0f * (float)Math.signum(double14);
        }
        this.k.visible = true;
        final float float10 = entity.getSpikesExtension(float8);
        this.m[0].yaw = MathHelper.sin(float10) * 3.1415927f * 0.05f;
        this.m[1].yaw = MathHelper.sin(float10) * 3.1415927f * 0.1f;
        this.m[1].rotationPointX = -1.5f;
        this.m[1].rotationPointY = 0.5f;
        this.m[1].rotationPointZ = 14.0f;
        this.m[2].yaw = MathHelper.sin(float10) * 3.1415927f * 0.15f;
        this.m[2].rotationPointX = 0.5f;
        this.m[2].rotationPointY = 0.5f;
        this.m[2].rotationPointZ = 6.0f;
    }
    
    static {
        a = new float[] { 1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f };
        b = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f };
        f = new float[] { 0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f };
        g = new float[] { 0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f };
        h = new float[] { -8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f };
        i = new float[] { 8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f };
    }
}
