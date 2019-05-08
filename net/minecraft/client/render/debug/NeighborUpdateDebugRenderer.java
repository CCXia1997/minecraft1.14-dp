package net.minecraft.client.render.debug;

import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BoundingBox;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NeighborUpdateDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient a;
    private final Map<Long, Map<BlockPos, Integer>> b;
    
    NeighborUpdateDebugRenderer(final MinecraftClient minecraftClient) {
        this.b = Maps.newTreeMap(Ordering.<Comparable>natural().reverse());
        this.a = minecraftClient;
    }
    
    public void a(final long long1, final BlockPos blockPos3) {
        Map<BlockPos, Integer> map4 = this.b.get(long1);
        if (map4 == null) {
            map4 = Maps.newHashMap();
            this.b.put(long1, map4);
        }
        Integer integer5 = map4.get(blockPos3);
        if (integer5 == null) {
            integer5 = 0;
        }
        map4.put(blockPos3, integer5 + 1);
    }
    
    @Override
    public void render(final long long1) {
        final long long2 = this.a.world.getTime();
        final Camera camera5 = this.a.gameRenderer.getCamera();
        final double double6 = camera5.getPos().x;
        final double double7 = camera5.getPos().y;
        final double double8 = camera5.getPos().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        final int integer12 = 200;
        final double double9 = 0.0025;
        final Set<BlockPos> set15 = Sets.newHashSet();
        final Map<BlockPos, Integer> map16 = Maps.newHashMap();
        final Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> iterator17 = this.b.entrySet().iterator();
        while (iterator17.hasNext()) {
            final Map.Entry<Long, Map<BlockPos, Integer>> entry18 = iterator17.next();
            final Long long3 = entry18.getKey();
            final Map<BlockPos, Integer> map17 = entry18.getValue();
            final long long4 = long2 - long3;
            if (long4 > 200L) {
                iterator17.remove();
            }
            else {
                for (final Map.Entry<BlockPos, Integer> entry19 : map17.entrySet()) {
                    final BlockPos blockPos25 = entry19.getKey();
                    final Integer integer13 = entry19.getValue();
                    if (set15.add(blockPos25)) {
                        WorldRenderer.drawBoxOutline(new BoundingBox(BlockPos.ORIGIN).expand(0.002).contract(0.0025 * long4).offset(blockPos25.getX(), blockPos25.getY(), blockPos25.getZ()).offset(-double6, -double7, -double8), 1.0f, 1.0f, 1.0f, 1.0f);
                        map16.put(blockPos25, integer13);
                    }
                }
            }
        }
        for (final Map.Entry<BlockPos, Integer> entry20 : map16.entrySet()) {
            final BlockPos blockPos26 = entry20.getKey();
            final Integer integer14 = entry20.getValue();
            DebugRenderer.a(String.valueOf(integer14), blockPos26.getX(), blockPos26.getY(), blockPos26.getZ(), -1);
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
