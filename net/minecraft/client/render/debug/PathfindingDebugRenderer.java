package net.minecraft.client.render.debug;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.ai.pathing.PathNode;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.util.math.BoundingBox;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.ai.pathing.Path;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    private final Map<Integer, Path> paths;
    private final Map<Integer, Float> c;
    private final Map<Integer, Long> pathTimes;
    private Camera camera;
    private double f;
    private double g;
    private double h;
    
    public PathfindingDebugRenderer(final MinecraftClient minecraftClient) {
        this.paths = Maps.newHashMap();
        this.c = Maps.newHashMap();
        this.pathTimes = Maps.newHashMap();
        this.client = minecraftClient;
    }
    
    public void addPath(final int id, final Path path, final float float3) {
        this.paths.put(id, path);
        this.pathTimes.put(id, SystemUtil.getMeasuringTimeMs());
        this.c.put(id, float3);
    }
    
    @Override
    public void render(final long long1) {
        if (this.paths.isEmpty()) {
            return;
        }
        this.camera = this.client.gameRenderer.getCamera();
        this.f = this.camera.getPos().x;
        this.g = this.camera.getPos().y;
        this.h = this.camera.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0f);
        this.b();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private void b() {
        final long long1 = SystemUtil.getMeasuringTimeMs();
        for (final Integer integer4 : this.paths.keySet()) {
            final Path path5 = this.paths.get(integer4);
            final float float6 = this.c.get(integer4);
            this.a(path5);
            final PathNode pathNode7 = path5.k();
            if (this.a(pathNode7) > 40.0f) {
                continue;
            }
            DebugRenderer.a(new BoundingBox(pathNode7.x + 0.25f, pathNode7.y + 0.25f, pathNode7.z + 0.25, pathNode7.x + 0.75f, pathNode7.y + 0.75f, pathNode7.z + 0.75f).offset(-this.f, -this.g, -this.h), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int integer5 = 0; integer5 < path5.getLength(); ++integer5) {
                final PathNode pathNode8 = path5.getNode(integer5);
                if (this.a(pathNode8) <= 40.0f) {
                    final float float7 = (integer5 == path5.getCurrentNodeIndex()) ? 1.0f : 0.0f;
                    final float float8 = (integer5 == path5.getCurrentNodeIndex()) ? 0.0f : 1.0f;
                    DebugRenderer.a(new BoundingBox(pathNode8.x + 0.5f - float6, pathNode8.y + 0.01f * integer5, pathNode8.z + 0.5f - float6, pathNode8.x + 0.5f + float6, pathNode8.y + 0.25f + 0.01f * integer5, pathNode8.z + 0.5f + float6).offset(-this.f, -this.g, -this.h), float7, 0.0f, float8, 0.5f);
                }
            }
        }
        for (final Integer integer4 : this.paths.keySet()) {
            final Path path5 = this.paths.get(integer4);
            for (final PathNode pathNode8 : path5.j()) {
                if (this.a(pathNode8) <= 40.0f) {
                    DebugRenderer.a(String.format("%s", pathNode8.type), pathNode8.x + 0.5, pathNode8.y + 0.75, pathNode8.z + 0.5, -65536);
                    DebugRenderer.a(String.format(Locale.ROOT, "%.2f", pathNode8.l), pathNode8.x + 0.5, pathNode8.y + 0.25, pathNode8.z + 0.5, -65536);
                }
            }
            for (final PathNode pathNode8 : path5.i()) {
                if (this.a(pathNode8) <= 40.0f) {
                    DebugRenderer.a(String.format("%s", pathNode8.type), pathNode8.x + 0.5, pathNode8.y + 0.75, pathNode8.z + 0.5, -16776961);
                    DebugRenderer.a(String.format(Locale.ROOT, "%.2f", pathNode8.l), pathNode8.x + 0.5, pathNode8.y + 0.25, pathNode8.z + 0.5, -16776961);
                }
            }
            for (int integer6 = 0; integer6 < path5.getLength(); ++integer6) {
                final PathNode pathNode7 = path5.getNode(integer6);
                if (this.a(pathNode7) <= 40.0f) {
                    DebugRenderer.a(String.format("%s", pathNode7.type), pathNode7.x + 0.5, pathNode7.y + 0.75, pathNode7.z + 0.5, -1);
                    DebugRenderer.a(String.format(Locale.ROOT, "%.2f", pathNode7.l), pathNode7.x + 0.5, pathNode7.y + 0.25, pathNode7.z + 0.5, -1);
                }
            }
        }
        for (final Integer integer7 : this.pathTimes.keySet().<Integer>toArray(new Integer[0])) {
            if (long1 - this.pathTimes.get(integer7) > 20000L) {
                this.paths.remove(integer7);
                this.pathTimes.remove(integer7);
            }
        }
    }
    
    public void a(final Path path) {
        final Tessellator tessellator2 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder3 = tessellator2.getBufferBuilder();
        bufferBuilder3.begin(3, VertexFormats.POSITION_COLOR);
        for (int integer4 = 0; integer4 < path.getLength(); ++integer4) {
            final PathNode pathNode5 = path.getNode(integer4);
            if (this.a(pathNode5) <= 40.0f) {
                final float float6 = integer4 / (float)path.getLength() * 0.33f;
                final int integer5 = (integer4 == 0) ? 0 : MathHelper.hsvToRgb(float6, 0.9f, 0.9f);
                final int integer6 = integer5 >> 16 & 0xFF;
                final int integer7 = integer5 >> 8 & 0xFF;
                final int integer8 = integer5 & 0xFF;
                bufferBuilder3.vertex(pathNode5.x - this.f + 0.5, pathNode5.y - this.g + 0.5, pathNode5.z - this.h + 0.5).color(integer6, integer7, integer8, 255).next();
            }
        }
        tessellator2.draw();
    }
    
    private float a(final PathNode pathNode) {
        return (float)(Math.abs(pathNode.x - this.camera.getPos().x) + Math.abs(pathNode.y - this.camera.getPos().y) + Math.abs(pathNode.z - this.camera.getPos().z));
    }
}
