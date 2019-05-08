package net.minecraft.client.render.debug;

import java.util.List;
import java.util.Iterator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.IWorld;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Maps;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient a;
    private final Map<Integer, Map<String, MutableIntBoundingBox>> b;
    private final Map<Integer, Map<String, MutableIntBoundingBox>> c;
    private final Map<Integer, Map<String, Boolean>> d;
    
    public StructureDebugRenderer(final MinecraftClient minecraftClient) {
        this.b = Maps.newHashMap();
        this.c = Maps.newHashMap();
        this.d = Maps.newHashMap();
        this.a = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.a.gameRenderer.getCamera();
        final IWorld iWorld4 = this.a.world;
        final int integer5 = iWorld4.getLevelProperties().getDimension();
        final double double6 = camera3.getPos().x;
        final double double7 = camera3.getPos().y;
        final double double8 = camera3.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        GlStateManager.disableDepthTest();
        final BlockPos blockPos12 = new BlockPos(camera3.getPos().x, 0.0, camera3.getPos().z);
        final Tessellator tessellator13 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder14 = tessellator13.getBufferBuilder();
        bufferBuilder14.begin(3, VertexFormats.POSITION_COLOR);
        GlStateManager.lineWidth(1.0f);
        if (this.b.containsKey(integer5)) {
            for (final MutableIntBoundingBox mutableIntBoundingBox16 : this.b.get(integer5).values()) {
                if (blockPos12.isWithinDistance(mutableIntBoundingBox16.f(), 500.0)) {
                    WorldRenderer.buildBoxOutline(bufferBuilder14, mutableIntBoundingBox16.minX - double6, mutableIntBoundingBox16.minY - double7, mutableIntBoundingBox16.minZ - double8, mutableIntBoundingBox16.maxX + 1 - double6, mutableIntBoundingBox16.maxY + 1 - double7, mutableIntBoundingBox16.maxZ + 1 - double8, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        if (this.c.containsKey(integer5)) {
            for (final Map.Entry<String, MutableIntBoundingBox> entry16 : this.c.get(integer5).entrySet()) {
                final String string17 = entry16.getKey();
                final MutableIntBoundingBox mutableIntBoundingBox17 = entry16.getValue();
                final Boolean boolean19 = this.d.get(integer5).get(string17);
                if (blockPos12.isWithinDistance(mutableIntBoundingBox17.f(), 500.0)) {
                    if (boolean19) {
                        WorldRenderer.buildBoxOutline(bufferBuilder14, mutableIntBoundingBox17.minX - double6, mutableIntBoundingBox17.minY - double7, mutableIntBoundingBox17.minZ - double8, mutableIntBoundingBox17.maxX + 1 - double6, mutableIntBoundingBox17.maxY + 1 - double7, mutableIntBoundingBox17.maxZ + 1 - double8, 0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    else {
                        WorldRenderer.buildBoxOutline(bufferBuilder14, mutableIntBoundingBox17.minX - double6, mutableIntBoundingBox17.minY - double7, mutableIntBoundingBox17.minZ - double8, mutableIntBoundingBox17.maxX + 1 - double6, mutableIntBoundingBox17.maxY + 1 - double7, mutableIntBoundingBox17.maxZ + 1 - double8, 0.0f, 0.0f, 1.0f, 1.0f);
                    }
                }
            }
        }
        tessellator13.draw();
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
    
    public void a(final MutableIntBoundingBox mutableIntBoundingBox, final List<MutableIntBoundingBox> list2, final List<Boolean> list3, final int integer) {
        if (!this.b.containsKey(integer)) {
            this.b.put(integer, Maps.newHashMap());
        }
        if (!this.c.containsKey(integer)) {
            this.c.put(integer, Maps.newHashMap());
            this.d.put(integer, Maps.newHashMap());
        }
        this.b.get(integer).put(mutableIntBoundingBox.toString(), mutableIntBoundingBox);
        for (int integer2 = 0; integer2 < list2.size(); ++integer2) {
            final MutableIntBoundingBox mutableIntBoundingBox2 = list2.get(integer2);
            final Boolean boolean7 = list3.get(integer2);
            this.c.get(integer).put(mutableIntBoundingBox2.toString(), mutableIntBoundingBox2);
            this.d.get(integer).put(mutableIntBoundingBox2.toString(), boolean7);
        }
    }
}
