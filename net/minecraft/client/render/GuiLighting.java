package net.minecraft.client.render;

import net.minecraft.client.util.GlAllocationUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.util.math.Vector3f;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuiLighting
{
    private static final FloatBuffer buffer;
    private static final Vector3f towardLight;
    private static final Vector3f oppositeLight;
    
    private static Vector3f createNormalVector(final float float1, final float float2, final float float3) {
        final Vector3f vector3f4 = new Vector3f(float1, float2, float3);
        vector3f4.reciprocal();
        return vector3f4;
    }
    
    public static void disable() {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }
    
    public static void enable() {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        GlStateManager.light(16384, 4611, singletonBuffer(GuiLighting.towardLight.x(), GuiLighting.towardLight.y(), GuiLighting.towardLight.z(), 0.0f));
        final float float1 = 0.6f;
        GlStateManager.light(16384, 4609, singletonBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16384, 4608, singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16384, 4610, singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4611, singletonBuffer(GuiLighting.oppositeLight.x(), GuiLighting.oppositeLight.y(), GuiLighting.oppositeLight.z(), 0.0f));
        GlStateManager.light(16385, 4609, singletonBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16385, 4608, singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4610, singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.shadeModel(7424);
        final float float2 = 0.4f;
        GlStateManager.lightModel(2899, singletonBuffer(0.4f, 0.4f, 0.4f, 1.0f));
    }
    
    public static FloatBuffer singletonBuffer(final float float1, final float float2, final float float3, final float float4) {
        GuiLighting.buffer.clear();
        GuiLighting.buffer.put(float1).put(float2).put(float3).put(float4);
        GuiLighting.buffer.flip();
        return GuiLighting.buffer;
    }
    
    public static void enableForItems() {
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(-30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(165.0f, 1.0f, 0.0f, 0.0f);
        enable();
        GlStateManager.popMatrix();
    }
    
    static {
        buffer = GlAllocationUtils.allocateFloatBuffer(4);
        towardLight = createNormalVector(0.2f, 1.0f, -0.7f);
        oppositeLight = createNormalVector(-0.2f, 1.0f, 0.7f);
    }
}
