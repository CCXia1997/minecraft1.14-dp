package net.minecraft.client.render.debug;

import java.util.Iterator;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.util.SystemUtil;
import java.util.Collections;
import net.minecraft.util.shape.VoxelShape;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VoxelDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient a;
    private double b;
    private List<VoxelShape> c;
    
    public VoxelDebugRenderer(final MinecraftClient minecraftClient) {
        this.b = Double.MIN_VALUE;
        this.c = Collections.<VoxelShape>emptyList();
        this.a = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.a.gameRenderer.getCamera();
        final double double4 = (double)SystemUtil.getMeasuringTimeNano();
        if (double4 - this.b > 1.0E8) {
            this.b = double4;
            this.c = camera3.getFocusedEntity().world.getCollisionShapes(camera3.getFocusedEntity(), camera3.getFocusedEntity().getBoundingBox().expand(6.0), Collections.<Entity>emptySet()).collect(Collectors.toList());
        }
        final double double5 = camera3.getPos().x;
        final double double6 = camera3.getPos().y;
        final double double7 = camera3.getPos().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        for (final VoxelShape voxelShape13 : this.c) {
            WorldRenderer.drawDebugShapeOutline(voxelShape13, -double5, -double6, -double7, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
