package net.minecraft.client.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.damage.DamageSource;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.world.ClientWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OtherClientPlayerEntity extends AbstractClientPlayerEntity
{
    public OtherClientPlayerEntity(final ClientWorld world, final GameProfile profile) {
        super(world, profile);
        this.stepHeight = 1.0f;
        this.noClip = true;
    }
    
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = this.getBoundingBox().averageDimension() * 10.0;
        if (Double.isNaN(double3)) {
            double3 = 1.0;
        }
        double3 *= 64.0 * getRenderDistanceMultiplier();
        return distance < double3 * double3;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return true;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.lastLimbDistance = this.limbDistance;
        final double double1 = this.x - this.prevX;
        final double double2 = this.z - this.prevZ;
        float float5 = MathHelper.sqrt(double1 * double1 + double2 * double2) * 4.0f;
        if (float5 > 1.0f) {
            float5 = 1.0f;
        }
        this.limbDistance += (float5 - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }
    
    @Override
    public void updateState() {
        if (this.bf > 0) {
            final double double1 = this.x + (this.bg - this.x) / this.bf;
            final double double2 = this.y + (this.bh - this.y) / this.bf;
            final double double3 = this.z + (this.bi - this.z) / this.bf;
            this.yaw += (float)(MathHelper.wrapDegrees(this.bj - this.yaw) / this.bf);
            this.pitch += (float)((this.bk - this.pitch) / this.bf);
            --this.bf;
            this.setPosition(double1, double2, double3);
            this.setRotation(this.yaw, this.pitch);
        }
        if (this.bm > 0) {
            this.headYaw += (float)(MathHelper.wrapDegrees(this.bl - this.headYaw) / this.bm);
            --this.bm;
        }
        this.bD = this.bE;
        this.tickHandSwing();
        float float1;
        if (!this.onGround || this.getHealth() <= 0.0f) {
            float1 = 0.0f;
        }
        else {
            float1 = Math.min(0.1f, MathHelper.sqrt(Entity.squaredHorizontalLength(this.getVelocity())));
        }
        if (this.onGround || this.getHealth() <= 0.0f) {
            final float float2 = 0.0f;
        }
        else {
            final float float2 = (float)Math.atan(-this.getVelocity().y * 0.20000000298023224) * 15.0f;
        }
        this.bE += (float1 - this.bE) * 0.4f;
        this.world.getProfiler().push("push");
        this.doPushLogic();
        this.world.getProfiler().pop();
    }
    
    @Override
    protected void updateSize() {
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }
}
