package net.minecraft.client.render.debug;

import java.util.Iterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.World;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkyLightDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    
    public SkyLightDebugRenderer(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.client.gameRenderer.getCamera();
        final World world4 = this.client.world;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final BlockPos blockPos5 = new BlockPos(camera3.getPos());
        final LongSet longSet6 = (LongSet)new LongOpenHashSet();
        for (final BlockPos blockPos6 : BlockPos.iterate(blockPos5.add(-10, -10, -10), blockPos5.add(10, 10, 10))) {
            final int integer9 = world4.getLightLevel(LightType.SKY, blockPos6);
            final float float10 = (15 - integer9) / 15.0f * 0.5f + 0.16f;
            final int integer10 = MathHelper.hsvToRgb(float10, 0.9f, 0.9f);
            final long long2 = ChunkSectionPos.toChunkLong(blockPos6.asLong());
            if (longSet6.add(long2)) {
                DebugRenderer.a(world4.getChunkManager().getLightingProvider().a(LightType.SKY, ChunkSectionPos.from(long2)), ChunkSectionPos.unpackLongX(long2) * 16 + 8, ChunkSectionPos.unpackLongY(long2) * 16 + 8, ChunkSectionPos.unpackLongZ(long2) * 16 + 8, 16711680, 0.3f);
            }
            if (integer9 != 15) {
                DebugRenderer.a(String.valueOf(integer9), blockPos6.getX() + 0.5, blockPos6.getY() + 0.25, blockPos6.getZ() + 0.5, integer10);
            }
        }
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
