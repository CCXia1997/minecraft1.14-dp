package net.minecraft.client.render;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.World;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.MinecraftClient;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer
{
    private final FloatBuffer blackColorBuffer;
    private final FloatBuffer colorBuffer;
    private float red;
    private float green;
    private float blue;
    private float bufferRed;
    private float bufferGreen;
    private float bufferBlue;
    private int waterFogColor;
    private int nextWaterFogColor;
    private long lastWaterFogColorUpdateTime;
    private final GameRenderer gameRenderer;
    private final MinecraftClient client;
    
    public BackgroundRenderer(final GameRenderer gameRenderer) {
        this.blackColorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
        this.colorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
        this.bufferRed = -1.0f;
        this.bufferGreen = -1.0f;
        this.bufferBlue = -1.0f;
        this.waterFogColor = -1;
        this.nextWaterFogColor = -1;
        this.lastWaterFogColorUpdateTime = -1L;
        this.gameRenderer = gameRenderer;
        this.client = gameRenderer.getClient();
        this.blackColorBuffer.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
    }
    
    public void renderBackground(final Camera camera, final float tickDelta) {
        final World world3 = this.client.world;
        final FluidState fluidState4 = camera.getSubmergedFluidState();
        if (fluidState4.matches(FluidTags.a)) {
            this.updateColorInWater(camera, world3);
        }
        else if (fluidState4.matches(FluidTags.b)) {
            this.red = 0.6f;
            this.green = 0.1f;
            this.blue = 0.0f;
            this.lastWaterFogColorUpdateTime = -1L;
        }
        else {
            this.updateColorNotInWater(camera, world3, tickDelta);
            this.lastWaterFogColorUpdateTime = -1L;
        }
        double double5 = camera.getPos().y * world3.dimension.getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.o)) {
            final int integer7 = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.o).getDuration();
            if (integer7 < 20) {
                double5 *= 1.0f - integer7 / 20.0f;
            }
            else {
                double5 = 0.0;
            }
        }
        if (double5 < 1.0) {
            if (double5 < 0.0) {
                double5 = 0.0;
            }
            double5 *= double5;
            this.red *= (float)double5;
            this.green *= (float)double5;
            this.blue *= (float)double5;
        }
        if (this.gameRenderer.getSkyDarkness(tickDelta) > 0.0f) {
            final float float7 = this.gameRenderer.getSkyDarkness(tickDelta);
            this.red = this.red * (1.0f - float7) + this.red * 0.7f * float7;
            this.green = this.green * (1.0f - float7) + this.green * 0.6f * float7;
            this.blue = this.blue * (1.0f - float7) + this.blue * 0.6f * float7;
        }
        if (fluidState4.matches(FluidTags.a)) {
            float float7 = 0.0f;
            if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                final ClientPlayerEntity clientPlayerEntity8 = (ClientPlayerEntity)camera.getFocusedEntity();
                float7 = clientPlayerEntity8.L();
            }
            float float8 = 1.0f / this.red;
            if (float8 > 1.0f / this.green) {
                float8 = 1.0f / this.green;
            }
            if (float8 > 1.0f / this.blue) {
                float8 = 1.0f / this.blue;
            }
            this.red = this.red * (1.0f - float7) + this.red * float8 * float7;
            this.green = this.green * (1.0f - float7) + this.green * float8 * float7;
            this.blue = this.blue * (1.0f - float7) + this.blue * float8 * float7;
        }
        else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.p)) {
            final float float7 = this.gameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), tickDelta);
            float float8 = 1.0f / this.red;
            if (float8 > 1.0f / this.green) {
                float8 = 1.0f / this.green;
            }
            if (float8 > 1.0f / this.blue) {
                float8 = 1.0f / this.blue;
            }
            this.red = this.red * (1.0f - float7) + this.red * float8 * float7;
            this.green = this.green * (1.0f - float7) + this.green * float8 * float7;
            this.blue = this.blue * (1.0f - float7) + this.blue * float8 * float7;
        }
        GlStateManager.clearColor(this.red, this.green, this.blue, 0.0f);
    }
    
    private void updateColorNotInWater(final Camera camera, final World world, final float tickDelta) {
        float float4 = 0.25f + 0.75f * this.client.options.viewDistance / 32.0f;
        float4 = 1.0f - (float)Math.pow(float4, 0.25);
        final Vec3d vec3d5 = world.getSkyColor(camera.getBlockPos(), tickDelta);
        final float float5 = (float)vec3d5.x;
        final float float6 = (float)vec3d5.y;
        final float float7 = (float)vec3d5.z;
        final Vec3d vec3d6 = world.getFogColor(tickDelta);
        this.red = (float)vec3d6.x;
        this.green = (float)vec3d6.y;
        this.blue = (float)vec3d6.z;
        if (this.client.options.viewDistance >= 4) {
            final double double10 = (MathHelper.sin(world.b(tickDelta)) > 0.0f) ? -1.0 : 1.0;
            final Vec3d vec3d7 = new Vec3d(double10, 0.0, 0.0);
            float float8 = (float)camera.l().dotProduct(vec3d7);
            if (float8 < 0.0f) {
                float8 = 0.0f;
            }
            if (float8 > 0.0f) {
                final float[] arr14 = world.dimension.getBackgroundColor(world.getSkyAngle(tickDelta), tickDelta);
                if (arr14 != null) {
                    float8 *= arr14[3];
                    this.red = this.red * (1.0f - float8) + arr14[0] * float8;
                    this.green = this.green * (1.0f - float8) + arr14[1] * float8;
                    this.blue = this.blue * (1.0f - float8) + arr14[2] * float8;
                }
            }
        }
        this.red += (float5 - this.red) * float4;
        this.green += (float6 - this.green) * float4;
        this.blue += (float7 - this.blue) * float4;
        final float float9 = world.getRainGradient(tickDelta);
        if (float9 > 0.0f) {
            final float float10 = 1.0f - float9 * 0.5f;
            final float float11 = 1.0f - float9 * 0.4f;
            this.red *= float10;
            this.green *= float10;
            this.blue *= float11;
        }
        final float float10 = world.getThunderGradient(tickDelta);
        if (float10 > 0.0f) {
            final float float11 = 1.0f - float10 * 0.5f;
            this.red *= float11;
            this.green *= float11;
            this.blue *= float11;
        }
    }
    
    private void updateColorInWater(final Camera camera, final ViewableWorld world) {
        final long long3 = SystemUtil.getMeasuringTimeMs();
        final int integer5 = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
        if (this.lastWaterFogColorUpdateTime < 0L) {
            this.waterFogColor = integer5;
            this.nextWaterFogColor = integer5;
            this.lastWaterFogColorUpdateTime = long3;
        }
        final int integer6 = this.waterFogColor >> 16 & 0xFF;
        final int integer7 = this.waterFogColor >> 8 & 0xFF;
        final int integer8 = this.waterFogColor & 0xFF;
        final int integer9 = this.nextWaterFogColor >> 16 & 0xFF;
        final int integer10 = this.nextWaterFogColor >> 8 & 0xFF;
        final int integer11 = this.nextWaterFogColor & 0xFF;
        final float float12 = MathHelper.clamp((long3 - this.lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
        final float float13 = MathHelper.lerp(float12, (float)integer9, (float)integer6);
        final float float14 = MathHelper.lerp(float12, (float)integer10, (float)integer7);
        final float float15 = MathHelper.lerp(float12, (float)integer11, (float)integer8);
        this.red = float13 / 255.0f;
        this.green = float14 / 255.0f;
        this.blue = float15 / 255.0f;
        if (this.waterFogColor != integer5) {
            this.waterFogColor = integer5;
            this.nextWaterFogColor = (MathHelper.floor(float13) << 16 | MathHelper.floor(float14) << 8 | MathHelper.floor(float15));
            this.lastWaterFogColorUpdateTime = long3;
        }
    }
    
    public void applyFog(final Camera camera, final int integer) {
        this.setFogBlack(false);
        GlStateManager.normal3f(0.0f, -1.0f, 0.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final FluidState fluidState3 = camera.getSubmergedFluidState();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.o)) {
            float float4 = 5.0f;
            final int integer2 = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.o).getDuration();
            if (integer2 < 20) {
                float4 = MathHelper.lerp(1.0f - integer2 / 20.0f, 5.0f, this.gameRenderer.getViewDistance());
            }
            GlStateManager.fogMode(GlStateManager.FogMode.a);
            if (integer == -1) {
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(float4 * 0.8f);
            }
            else {
                GlStateManager.fogStart(float4 * 0.25f);
                GlStateManager.fogEnd(float4);
            }
            GLX.setupNvFogDistance();
        }
        else if (fluidState3.matches(FluidTags.a)) {
            GlStateManager.fogMode(GlStateManager.FogMode.c);
            if (camera.getFocusedEntity() instanceof LivingEntity) {
                if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                    final ClientPlayerEntity clientPlayerEntity4 = (ClientPlayerEntity)camera.getFocusedEntity();
                    float float5 = 0.05f - clientPlayerEntity4.L() * clientPlayerEntity4.L() * 0.03f;
                    final Biome biome6 = clientPlayerEntity4.world.getBiome(new BlockPos(clientPlayerEntity4));
                    if (biome6 == Biomes.h || biome6 == Biomes.ag) {
                        float5 += 0.005f;
                    }
                    GlStateManager.fogDensity(float5);
                }
                else {
                    GlStateManager.fogDensity(0.05f);
                }
            }
            else {
                GlStateManager.fogDensity(0.1f);
            }
        }
        else if (fluidState3.matches(FluidTags.b)) {
            GlStateManager.fogMode(GlStateManager.FogMode.b);
            GlStateManager.fogDensity(2.0f);
        }
        else {
            final float float4 = this.gameRenderer.getViewDistance();
            GlStateManager.fogMode(GlStateManager.FogMode.a);
            if (integer == -1) {
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(float4);
            }
            else {
                GlStateManager.fogStart(float4 * 0.75f);
                GlStateManager.fogEnd(float4);
            }
            GLX.setupNvFogDistance();
            if (this.client.world.dimension.shouldRenderFog((int)camera.getPos().x, (int)camera.getPos().z) || this.client.inGameHud.getBossBarHud().shouldThickenFog()) {
                GlStateManager.fogStart(float4 * 0.05f);
                GlStateManager.fogEnd(Math.min(float4, 192.0f) * 0.5f);
            }
        }
        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }
    
    public void setFogBlack(final boolean fogBlack) {
        if (fogBlack) {
            GlStateManager.fog(2918, this.blackColorBuffer);
        }
        else {
            GlStateManager.fog(2918, this.getColorAsBuffer());
        }
    }
    
    private FloatBuffer getColorAsBuffer() {
        if (this.bufferRed != this.red || this.bufferGreen != this.green || this.bufferBlue != this.blue) {
            this.colorBuffer.clear();
            this.colorBuffer.put(this.red).put(this.green).put(this.blue).put(1.0f);
            this.colorBuffer.flip();
            this.bufferRed = this.red;
            this.bufferGreen = this.green;
            this.bufferBlue = this.blue;
        }
        return this.colorBuffer;
    }
}
