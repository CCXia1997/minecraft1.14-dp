package net.minecraft.client.render.debug;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GoalSelectorDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    private final Map<Integer, List<a>> goalSelectors;
    
    @Override
    public void a() {
        this.goalSelectors.clear();
    }
    
    public void setGoalSelectorList(final int integer, final List<a> list) {
        this.goalSelectors.put(integer, list);
    }
    
    public GoalSelectorDebugRenderer(final MinecraftClient minecraftClient) {
        this.goalSelectors = Maps.newHashMap();
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.client.gameRenderer.getCamera();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final BlockPos blockPos4 = new BlockPos(camera3.getPos().x, 0.0, camera3.getPos().z);
        int integer4;
        a a5;
        final Vec3i vec3i;
        double double6;
        double double7;
        double double8;
        int integer5;
        this.goalSelectors.forEach((integer, list) -> {
            for (integer4 = 0; integer4 < list.size(); ++integer4) {
                a5 = list.get(integer4);
                if (vec3i.isWithinDistance(a5.a, 160.0)) {
                    double6 = a5.a.getX() + 0.5;
                    double7 = a5.a.getY() + 2.0 + integer4 * 0.25;
                    double8 = a5.a.getZ() + 0.5;
                    integer5 = (a5.d ? -16711936 : -3355444);
                    DebugRenderer.a(a5.c, double6, double7, double8, integer5);
                }
            }
            return;
        });
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
    
    @Environment(EnvType.CLIENT)
    public static class a
    {
        public final BlockPos a;
        public final int b;
        public final String c;
        public final boolean d;
        
        public a(final BlockPos blockPos, final int integer, final String string, final boolean boolean4) {
            this.a = blockPos;
            this.b = integer;
            this.c = string;
            this.d = boolean4;
        }
    }
}
