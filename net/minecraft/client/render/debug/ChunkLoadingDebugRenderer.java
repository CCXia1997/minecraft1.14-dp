package net.minecraft.client.render.debug;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.render.Camera;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.world.ClientWorld;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.CompletableFuture;
import java.util.Iterator;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Map;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.SystemUtil;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkLoadingDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    private double lastUpdateTime;
    private final int c = 12;
    @Nullable
    private ServerData serverData;
    
    public ChunkLoadingDebugRenderer(final MinecraftClient minecraftClient) {
        this.lastUpdateTime = Double.MIN_VALUE;
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final double double3 = (double)SystemUtil.getMeasuringTimeNano();
        if (double3 - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = double3;
            final IntegratedServer integratedServer5 = this.client.getServer();
            if (integratedServer5 != null) {
                this.serverData = new ServerData(integratedServer5);
            }
            else {
                this.serverData = null;
            }
        }
        if (this.serverData != null) {
            GlStateManager.disableFog();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(2.0f);
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            final Map<ChunkPos, String> map5 = this.serverData.c.getNow(null);
            final double double4 = this.client.gameRenderer.getCamera().getPos().y * 0.85;
            for (final Map.Entry<ChunkPos, String> entry9 : this.serverData.b.entrySet()) {
                final ChunkPos chunkPos10 = entry9.getKey();
                String string11 = entry9.getValue();
                if (map5 != null) {
                    string11 += map5.get(chunkPos10);
                }
                final String[] arr12 = string11.split("\n");
                int integer13 = 0;
                for (final String string12 : arr12) {
                    DebugRenderer.a(string12, (chunkPos10.x << 4) + 8, double4 + integer13, (chunkPos10.z << 4) + 8, -1, 0.15f);
                    integer13 -= 2;
                }
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.enableFog();
        }
    }
    
    @Environment(EnvType.CLIENT)
    final class ServerData
    {
        private final Map<ChunkPos, String> b;
        private final CompletableFuture<Map<ChunkPos, String>> c;
        
        private ServerData(final IntegratedServer integratedServer) {
            final ClientWorld clientWorld3 = ChunkLoadingDebugRenderer.this.client.world;
            final DimensionType dimensionType4 = ChunkLoadingDebugRenderer.this.client.world.dimension.getType();
            if (integratedServer.getWorld(dimensionType4) != null) {
                final ServerWorld serverWorld5 = integratedServer.getWorld(dimensionType4);
            }
            else {
                final ServerWorld serverWorld5 = null;
            }
            final Camera camera6 = ChunkLoadingDebugRenderer.this.client.gameRenderer.getCamera();
            final int integer7 = (int)camera6.getPos().x >> 4;
            final int integer8 = (int)camera6.getPos().z >> 4;
            final ImmutableMap.Builder<ChunkPos, String> builder9 = ImmutableMap.<ChunkPos, String>builder();
            final ClientChunkManager clientChunkManager10 = clientWorld3.getChunkManager();
            for (int integer9 = integer7 - 12; integer9 <= integer7 + 12; ++integer9) {
                for (int integer10 = integer8 - 12; integer10 <= integer8 + 12; ++integer10) {
                    final ChunkPos chunkPos13 = new ChunkPos(integer9, integer10);
                    String string14 = "";
                    final WorldChunk worldChunk15 = clientChunkManager10.getWorldChunk(integer9, integer10, false);
                    string14 += "Client: ";
                    if (worldChunk15 == null) {
                        string14 += "0n/a\n";
                    }
                    else {
                        string14 += (worldChunk15.isEmpty() ? " E" : "");
                        string14 += "\n";
                    }
                    builder9.put(chunkPos13, string14);
                }
            }
            this.b = builder9.build();
            final ImmutableMap.Builder<ChunkPos, String> builder10;
            final ServerWorld serverWorld6;
            final ServerChunkManager serverChunkManager5;
            final int n;
            int integer11;
            final int n2;
            int integer12;
            ChunkPos chunkPos14;
            this.c = integratedServer.executeFuture(() -> {
                builder10 = ImmutableMap.<ChunkPos, String>builder();
                serverChunkManager5 = serverWorld6.getChunkManager();
                for (integer11 = n - 12; integer11 <= n + 12; ++integer11) {
                    for (integer12 = n2 - 12; integer12 <= n2 + 12; ++integer12) {
                        chunkPos14 = new ChunkPos(integer11, integer12);
                        builder10.put(chunkPos14, "Server: " + serverChunkManager5.getDebugString(chunkPos14));
                    }
                }
                return builder10.build();
            });
        }
    }
}
