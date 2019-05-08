package net.minecraft.client.render;

import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.math.Direction;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Camera
{
    private boolean ready;
    private BlockView area;
    private Entity focusedEntity;
    private Vec3d pos;
    private final BlockPos.Mutable blockPos;
    private Vec3d f;
    private Vec3d g;
    private Vec3d h;
    private float pitch;
    private float yaw;
    private boolean thirdPerson;
    private boolean inverseView;
    private float m;
    private float n;
    
    public Camera() {
        this.pos = Vec3d.ZERO;
        this.blockPos = new BlockPos.Mutable();
    }
    
    public void update(final BlockView area, final Entity focusedEntity, final boolean thirdPerson, final boolean inverseView, final float tickDelta) {
        this.ready = true;
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = thirdPerson;
        this.inverseView = inverseView;
        this.setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
        this.setPos(MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.x), MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.y) + MathHelper.lerp(tickDelta, this.n, this.m), MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.z));
        if (thirdPerson) {
            if (inverseView) {
                this.yaw += 180.0f;
                this.pitch += -this.pitch * 2.0f;
                this.updateRotation();
            }
            this.moveBy(-this.a(4.0), 0.0, 0.0);
        }
        else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
            final Direction direction6 = ((LivingEntity)focusedEntity).getSleepingDirection();
            this.setRotation((direction6 != null) ? (direction6.asRotation() - 180.0f) : 0.0f, 0.0f);
            this.moveBy(0.0, 0.3, 0.0);
        }
        else {
            this.moveBy(-0.05000000074505806, 0.0, 0.0);
        }
        GlStateManager.rotatef(this.pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(this.yaw + 180.0f, 0.0f, 1.0f, 0.0f);
    }
    
    public void updateEyeHeight() {
        if (this.focusedEntity != null) {
            this.n = this.m;
            this.m += (this.focusedEntity.getStandingEyeHeight() - this.m) * 0.5f;
        }
    }
    
    private double a(double double1) {
        for (int integer3 = 0; integer3 < 8; ++integer3) {
            float float4 = (float)((integer3 & 0x1) * 2 - 1);
            float float5 = (float)((integer3 >> 1 & 0x1) * 2 - 1);
            float float6 = (float)((integer3 >> 2 & 0x1) * 2 - 1);
            float4 *= 0.1f;
            float5 *= 0.1f;
            float6 *= 0.1f;
            final Vec3d vec3d7 = this.pos.add(float4, float5, float6);
            final Vec3d vec3d8 = new Vec3d(this.pos.x - this.f.x * double1 + float4 + float6, this.pos.y - this.f.y * double1 + float5, this.pos.z - this.f.z * double1 + float6);
            final HitResult hitResult9 = this.area.rayTrace(new RayTraceContext(vec3d7, vec3d8, RayTraceContext.ShapeType.a, RayTraceContext.FluidHandling.NONE, this.focusedEntity));
            if (hitResult9.getType() != HitResult.Type.NONE) {
                final double double2 = hitResult9.getPos().distanceTo(this.pos);
                if (double2 < double1) {
                    double1 = double2;
                }
            }
        }
        return double1;
    }
    
    protected void moveBy(final double x, final double y, final double z) {
        final double double7 = this.f.x * x + this.g.x * y + this.h.x * z;
        final double double8 = this.f.y * x + this.g.y * y + this.h.y * z;
        final double double9 = this.f.z * x + this.g.z * y + this.h.z * z;
        this.setPos(new Vec3d(this.pos.x + double7, this.pos.y + double8, this.pos.z + double9));
    }
    
    protected void updateRotation() {
        final float float1 = MathHelper.cos((this.yaw + 90.0f) * 0.017453292f);
        final float float2 = MathHelper.sin((this.yaw + 90.0f) * 0.017453292f);
        final float float3 = MathHelper.cos(-this.pitch * 0.017453292f);
        final float float4 = MathHelper.sin(-this.pitch * 0.017453292f);
        final float float5 = MathHelper.cos((-this.pitch + 90.0f) * 0.017453292f);
        final float float6 = MathHelper.sin((-this.pitch + 90.0f) * 0.017453292f);
        this.f = new Vec3d(float1 * float3, float4, float2 * float3);
        this.g = new Vec3d(float1 * float5, float6, float2 * float5);
        this.h = this.f.crossProduct(this.g).multiply(-1.0);
    }
    
    protected void setRotation(final float yaw, final float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.updateRotation();
    }
    
    protected void setPos(final double x, final double y, final double z) {
        this.setPos(new Vec3d(x, y, z));
    }
    
    protected void setPos(final Vec3d pos) {
        this.pos = pos;
        this.blockPos.set(pos.x, pos.y, pos.z);
    }
    
    public Vec3d getPos() {
        return this.pos;
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public Entity getFocusedEntity() {
        return this.focusedEntity;
    }
    
    public boolean isReady() {
        return this.ready;
    }
    
    public boolean isThirdPerson() {
        return this.thirdPerson;
    }
    
    public FluidState getSubmergedFluidState() {
        if (!this.ready) {
            return Fluids.EMPTY.getDefaultState();
        }
        final FluidState fluidState1 = this.area.getFluidState(this.blockPos);
        if (!fluidState1.isEmpty() && this.pos.y >= this.blockPos.getY() + fluidState1.getHeight(this.area, this.blockPos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        return fluidState1;
    }
    
    public final Vec3d l() {
        return this.f;
    }
    
    public final Vec3d m() {
        return this.g;
    }
    
    public void reset() {
        this.area = null;
        this.focusedEntity = null;
        this.ready = false;
    }
}
