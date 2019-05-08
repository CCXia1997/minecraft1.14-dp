package net.minecraft.client.gui.hud;

import java.util.EnumMap;
import net.minecraft.util.MetricsData;
import net.minecraft.util.SystemUtil;
import net.minecraft.fluid.FluidState;
import java.util.Iterator;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.state.property.Property;
import net.minecraft.text.TextFormat;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import net.minecraft.world.chunk.light.LightingProvider;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.util.math.MathHelper;
import java.util.Locale;
import net.minecraft.world.dimension.DimensionType;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.server.world.ServerWorld;
import java.util.Objects;
import com.google.common.collect.Lists;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.google.common.base.Strings;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.chunk.WorldChunk;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.Heightmap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class DebugHud extends DrawableHelper
{
    private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES;
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos pos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;
    
    public DebugHud(final MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }
    
    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }
    
    public void draw() {
        this.client.getProfiler().push("debug");
        GlStateManager.pushMatrix();
        final Entity entity1 = this.client.getCameraEntity();
        this.blockHit = entity1.rayTrace(20.0, 0.0f, false);
        this.fluidHit = entity1.rayTrace(20.0, 0.0f, true);
        this.drawLeftText();
        this.drawRightText();
        GlStateManager.popMatrix();
        if (this.client.options.debugTpsEnabled) {
            final int integer2 = this.client.window.getScaledWidth();
            this.drawMetricsData(this.client.getMetricsData(), 0, integer2 / 2, true);
            final IntegratedServer integratedServer3 = this.client.getServer();
            if (integratedServer3 != null) {
                this.drawMetricsData(integratedServer3.getMetricsData(), integer2 - Math.min(integer2 / 2, 240), integer2 / 2, false);
            }
        }
        this.client.getProfiler().pop();
    }
    
    protected void drawLeftText() {
        final List<String> list1 = this.getLeftText();
        list1.add("");
        final boolean boolean2 = this.client.getServer() != null;
        list1.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + (boolean2 ? " FPS + TPS" : " FPS") + " [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
        list1.add("For help: press F3 + Q");
        for (int integer3 = 0; integer3 < list1.size(); ++integer3) {
            final String string4 = list1.get(integer3);
            if (!Strings.isNullOrEmpty(string4)) {
                this.fontRenderer.getClass();
                final int integer4 = 9;
                final int integer5 = this.fontRenderer.getStringWidth(string4);
                final int integer6 = 2;
                final int integer7 = 2 + integer4 * integer3;
                DrawableHelper.fill(1, integer7 - 1, 2 + integer5 + 1, integer7 + integer4 - 1, -1873784752);
                this.fontRenderer.draw(string4, 2.0f, (float)integer7, 14737632);
            }
        }
    }
    
    protected void drawRightText() {
        final List<String> list1 = this.getRightText();
        for (int integer2 = 0; integer2 < list1.size(); ++integer2) {
            final String string3 = list1.get(integer2);
            if (!Strings.isNullOrEmpty(string3)) {
                this.fontRenderer.getClass();
                final int integer3 = 9;
                final int integer4 = this.fontRenderer.getStringWidth(string3);
                final int integer5 = this.client.window.getScaledWidth() - 2 - integer4;
                final int integer6 = 2 + integer3 * integer2;
                DrawableHelper.fill(integer5 - 1, integer6 - 1, integer5 + integer4 + 1, integer6 + integer3 - 1, -1873784752);
                this.fontRenderer.draw(string3, (float)integer5, (float)integer6, 14737632);
            }
        }
    }
    
    protected List<String> getLeftText() {
        final IntegratedServer integratedServer2 = this.client.getServer();
        final ClientConnection clientConnection3 = this.client.getNetworkHandler().getClientConnection();
        final float float4 = clientConnection3.getAveragePacketsSent();
        final float float5 = clientConnection3.getAveragePacketsReceived();
        String string1;
        if (integratedServer2 != null) {
            string1 = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer2.getTickTime(), float4, float5);
        }
        else {
            string1 = String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), float4, float5);
        }
        final BlockPos blockPos6 = new BlockPos(this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z);
        if (this.client.hasReducedDebugInfo()) {
            return Lists.<String>newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.client.fpsDebugString, string1, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getChunkProviderStatus(), "", String.format("Chunk-relative: %d %d %d", blockPos6.getX() & 0xF, blockPos6.getY() & 0xF, blockPos6.getZ() & 0xF));
        }
        final Entity entity7 = this.client.getCameraEntity();
        final Direction direction8 = entity7.getHorizontalFacing();
        String string2 = null;
        switch (direction8) {
            case NORTH: {
                string2 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                string2 = "Towards positive Z";
                break;
            }
            case WEST: {
                string2 = "Towards negative X";
                break;
            }
            case EAST: {
                string2 = "Towards positive X";
                break;
            }
            default: {
                string2 = "Invalid";
                break;
            }
        }
        final ChunkPos chunkPos10 = new ChunkPos(blockPos6);
        if (!Objects.equals(this.pos, chunkPos10)) {
            this.pos = chunkPos10;
            this.resetChunk();
        }
        final World world11 = this.getWorld();
        final LongSet longSet12 = (world11 instanceof ServerWorld) ? ((ServerWorld)world11).getForcedChunks() : LongSets.EMPTY_SET;
        final List<String> list13 = Lists.<String>newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : ("/" + this.client.getVersionType())) + ")", this.client.fpsDebugString, string1, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getChunkProviderStatus(), DimensionType.getId(this.client.world.dimension.getType()).toString() + " FC: " + Integer.toString(longSet12.size()), "", String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z), String.format("Block: %d %d %d", blockPos6.getX(), blockPos6.getY(), blockPos6.getZ()), String.format("Chunk: %d %d %d in %d %d %d", blockPos6.getX() & 0xF, blockPos6.getY() & 0xF, blockPos6.getZ() & 0xF, blockPos6.getX() >> 4, blockPos6.getY() >> 4, blockPos6.getZ() >> 4), String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction8, string2, MathHelper.wrapDegrees(entity7.yaw), MathHelper.wrapDegrees(entity7.pitch)));
        if (this.client.world != null) {
            if (this.client.world.isBlockLoaded(blockPos6)) {
                final WorldChunk worldChunk14 = this.getClientChunk();
                if (worldChunk14.isEmpty()) {
                    list13.add("Waiting for chunk...");
                }
                else {
                    list13.add("Client Light: " + worldChunk14.getLightLevel(blockPos6, 0) + " (" + this.client.world.getLightLevel(LightType.SKY, blockPos6) + " sky, " + this.client.world.getLightLevel(LightType.BLOCK, blockPos6) + " block)");
                    final WorldChunk worldChunk15 = this.getChunk();
                    if (worldChunk15 != null) {
                        final LightingProvider lightingProvider16 = world11.getChunkManager().getLightingProvider();
                        list13.add("Server Light: (" + lightingProvider16.get(LightType.SKY).getLightLevel(blockPos6) + " sky, " + lightingProvider16.get(LightType.BLOCK).getLightLevel(blockPos6) + " block)");
                    }
                    final StringBuilder stringBuilder16 = new StringBuilder("CH");
                    for (final Heightmap.Type type20 : Heightmap.Type.values()) {
                        if (type20.shouldSendToClient()) {
                            stringBuilder16.append(" ").append(DebugHud.HEIGHT_MAP_TYPES.get(type20)).append(": ").append(worldChunk14.sampleHeightmap(type20, blockPos6.getX(), blockPos6.getZ()));
                        }
                    }
                    list13.add(stringBuilder16.toString());
                    if (worldChunk15 != null) {
                        stringBuilder16.setLength(0);
                        stringBuilder16.append("SH");
                        for (final Heightmap.Type type20 : Heightmap.Type.values()) {
                            if (type20.isStoredServerSide()) {
                                stringBuilder16.append(" ").append(DebugHud.HEIGHT_MAP_TYPES.get(type20)).append(": ").append(worldChunk15.sampleHeightmap(type20, blockPos6.getX(), blockPos6.getZ()));
                            }
                        }
                        list13.add(stringBuilder16.toString());
                    }
                    if (blockPos6.getY() >= 0 && blockPos6.getY() < 256) {
                        list13.add("Biome: " + Registry.BIOME.getId(worldChunk14.getBiome(blockPos6)));
                        long long17 = 0L;
                        float float6 = 0.0f;
                        if (worldChunk15 != null) {
                            float6 = world11.getMoonSize();
                            long17 = worldChunk15.getInhabitedTime();
                        }
                        final LocalDifficulty localDifficulty20 = new LocalDifficulty(world11.getDifficulty(), world11.getTimeOfDay(), long17, float6);
                        list13.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", localDifficulty20.getLocalDifficulty(), localDifficulty20.getClampedLocalDifficulty(), this.client.world.getTimeOfDay() / 24000L));
                    }
                }
            }
            else {
                list13.add("Outside of world...");
            }
        }
        else {
            list13.add("Outside of world...");
        }
        if (this.client.gameRenderer != null && this.client.gameRenderer.isShaderEnabled()) {
            list13.add("Shader: " + this.client.gameRenderer.getShader().getName());
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos7 = ((BlockHitResult)this.blockHit).getBlockPos();
            list13.add(String.format("Looking at block: %d %d %d", blockPos7.getX(), blockPos7.getY(), blockPos7.getZ()));
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos7 = ((BlockHitResult)this.fluidHit).getBlockPos();
            list13.add(String.format("Looking at liquid: %d %d %d", blockPos7.getX(), blockPos7.getY(), blockPos7.getZ()));
        }
        list13.add(this.client.getSoundManager().getDebugString());
        return list13;
    }
    
    private World getWorld() {
        return (World)DataFixUtils.orElse((Optional)Optional.<IntegratedServer>ofNullable(this.client.getServer()).map(integratedServer -> integratedServer.getWorld(this.client.world.dimension.getType())), this.client.world);
    }
    
    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            final IntegratedServer integratedServer1 = this.client.getServer();
            if (integratedServer1 != null) {
                final ServerWorld serverWorld2 = integratedServer1.getWorld(this.client.world.dimension.getType());
                if (serverWorld2 != null) {
                    this.chunkFuture = serverWorld2.getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, false);
                }
            }
            if (this.chunkFuture == null) {
                this.chunkFuture = CompletableFuture.<WorldChunk>completedFuture(this.getClientChunk());
            }
        }
        return this.chunkFuture.getNow(null);
    }
    
    private WorldChunk getClientChunk() {
        if (this.chunk == null) {
            this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
        }
        return this.chunk;
    }
    
    protected List<String> getRightText() {
        final long long1 = Runtime.getRuntime().maxMemory();
        final long long2 = Runtime.getRuntime().totalMemory();
        final long long3 = Runtime.getRuntime().freeMemory();
        final long long4 = long2 - long3;
        final List<String> list9 = Lists.<String>newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", long4 * 100L / long1, a(long4), a(long1)), String.format("Allocated: % 2d%% %03dMB", long2 * 100L / long1, a(long2)), "", String.format("CPU: %s", GLX.getCpuInfo()), "", String.format("Display: %dx%d (%s)", MinecraftClient.getInstance().window.getFramebufferWidth(), MinecraftClient.getInstance().window.getFramebufferHeight(), GLX.getVendor()), GLX.getRenderer(), GLX.getOpenGLVersion());
        if (this.client.hasReducedDebugInfo()) {
            return list9;
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos10 = ((BlockHitResult)this.blockHit).getBlockPos();
            final BlockState blockState11 = this.client.world.getBlockState(blockPos10);
            list9.add("");
            list9.add(TextFormat.t + "Targeted Block");
            list9.add(String.valueOf(Registry.BLOCK.getId(blockState11.getBlock())));
            for (final Map.Entry<Property<?>, Comparable<?>> entry13 : blockState11.getEntries().entrySet()) {
                list9.add(this.propertyToString(entry13));
            }
            for (final Identifier identifier13 : this.client.getNetworkHandler().getTagManager().blocks().getTagsFor(blockState11.getBlock())) {
                list9.add("#" + identifier13);
            }
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos10 = ((BlockHitResult)this.fluidHit).getBlockPos();
            final FluidState fluidState11 = this.client.world.getFluidState(blockPos10);
            list9.add("");
            list9.add(TextFormat.t + "Targeted Fluid");
            list9.add(String.valueOf(Registry.FLUID.getId(fluidState11.getFluid())));
            for (final Map.Entry<Property<?>, Comparable<?>> entry13 : fluidState11.getEntries().entrySet()) {
                list9.add(this.propertyToString(entry13));
            }
            for (final Identifier identifier13 : this.client.getNetworkHandler().getTagManager().fluids().getTagsFor(fluidState11.getFluid())) {
                list9.add("#" + identifier13);
            }
        }
        final Entity entity10 = this.client.targetedEntity;
        if (entity10 != null) {
            list9.add("");
            list9.add(TextFormat.t + "Targeted Entity");
            list9.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity10.getType())));
        }
        return list9;
    }
    
    private String propertyToString(final Map.Entry<Property<?>, Comparable<?>> propEntry) {
        final Property<?> property2 = propEntry.getKey();
        final Comparable<?> comparable3 = propEntry.getValue();
        String string4 = SystemUtil.getValueAsString(property2, comparable3);
        if (Boolean.TRUE.equals(comparable3)) {
            string4 = TextFormat.k + string4;
        }
        else if (Boolean.FALSE.equals(comparable3)) {
            string4 = TextFormat.m + string4;
        }
        return property2.getName() + ": " + string4;
    }
    
    private void drawMetricsData(final MetricsData metricsData, final int startY, final int firstSample, final boolean isClient) {
        GlStateManager.disableDepthTest();
        final int integer5 = metricsData.getStartIndex();
        final int integer6 = metricsData.getCurrentIndex();
        final long[] arr7 = metricsData.getSamples();
        int integer7 = integer5;
        int integer8 = startY;
        final int integer9 = Math.max(0, arr7.length - firstSample);
        final int integer10 = arr7.length - integer9;
        integer7 = metricsData.wrapIndex(integer7 + integer9);
        long long12 = 0L;
        int integer11 = Integer.MAX_VALUE;
        int integer12 = Integer.MIN_VALUE;
        for (int integer13 = 0; integer13 < integer10; ++integer13) {
            final int integer14 = (int)(arr7[metricsData.wrapIndex(integer7 + integer13)] / 1000000L);
            integer11 = Math.min(integer11, integer14);
            integer12 = Math.max(integer12, integer14);
            long12 += integer14;
        }
        int integer13 = this.client.window.getScaledHeight();
        DrawableHelper.fill(startY, integer13 - 60, startY + integer10, integer13, -1873784752);
        while (integer7 != integer6) {
            final int integer14 = metricsData.a(arr7[integer7], isClient ? 30 : 60, isClient ? 60 : 20);
            final int integer15 = isClient ? 100 : 60;
            final int integer16 = this.a(MathHelper.clamp(integer14, 0, integer15), 0, integer15 / 2, integer15);
            this.vLine(integer8, integer13, integer13 - integer14, integer16);
            ++integer8;
            integer7 = metricsData.wrapIndex(integer7 + 1);
        }
        if (isClient) {
            DrawableHelper.fill(startY + 1, integer13 - 30 + 1, startY + 14, integer13 - 30 + 10, -1873784752);
            this.fontRenderer.draw("60 FPS", (float)(startY + 2), (float)(integer13 - 30 + 2), 14737632);
            this.hLine(startY, startY + integer10 - 1, integer13 - 30, -1);
            DrawableHelper.fill(startY + 1, integer13 - 60 + 1, startY + 14, integer13 - 60 + 10, -1873784752);
            this.fontRenderer.draw("30 FPS", (float)(startY + 2), (float)(integer13 - 60 + 2), 14737632);
            this.hLine(startY, startY + integer10 - 1, integer13 - 60, -1);
        }
        else {
            DrawableHelper.fill(startY + 1, integer13 - 60 + 1, startY + 14, integer13 - 60 + 10, -1873784752);
            this.fontRenderer.draw("20 TPS", (float)(startY + 2), (float)(integer13 - 60 + 2), 14737632);
            this.hLine(startY, startY + integer10 - 1, integer13 - 60, -1);
        }
        this.hLine(startY, startY + integer10 - 1, integer13 - 1, -1);
        this.vLine(startY, integer13 - 60, integer13, -1);
        this.vLine(startY + integer10 - 1, integer13 - 60, integer13, -1);
        if (isClient && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
            this.hLine(startY, startY + integer10 - 1, integer13 - 1 - (int)(1800.0 / this.client.options.maxFps), -16711681);
        }
        final String string17 = integer11 + " ms min";
        final String string18 = long12 / integer10 + " ms avg";
        final String string19 = integer12 + " ms max";
        final TextRenderer fontRenderer = this.fontRenderer;
        final String string20 = string17;
        final float x = (float)(startY + 2);
        final int n = integer13 - 60;
        this.fontRenderer.getClass();
        fontRenderer.drawWithShadow(string20, x, (float)(n - 9), 14737632);
        final TextRenderer fontRenderer2 = this.fontRenderer;
        final String string21 = string18;
        final float x2 = (float)(startY + integer10 / 2 - this.fontRenderer.getStringWidth(string18) / 2);
        final int n2 = integer13 - 60;
        this.fontRenderer.getClass();
        fontRenderer2.drawWithShadow(string21, x2, (float)(n2 - 9), 14737632);
        final TextRenderer fontRenderer3 = this.fontRenderer;
        final String string22 = string19;
        final float x3 = (float)(startY + integer10 - this.fontRenderer.getStringWidth(string19));
        final int n3 = integer13 - 60;
        this.fontRenderer.getClass();
        fontRenderer3.drawWithShadow(string22, x3, (float)(n3 - 9), 14737632);
        GlStateManager.enableDepthTest();
    }
    
    private int a(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (integer1 < integer3) {
            return this.interpolateColor(-16711936, -256, integer1 / (float)integer3);
        }
        return this.interpolateColor(-256, -65536, (integer1 - integer3) / (float)(integer4 - integer3));
    }
    
    private int interpolateColor(final int color1, final int color2, final float dt) {
        final int integer4 = color1 >> 24 & 0xFF;
        final int integer5 = color1 >> 16 & 0xFF;
        final int integer6 = color1 >> 8 & 0xFF;
        final int integer7 = color1 & 0xFF;
        final int integer8 = color2 >> 24 & 0xFF;
        final int integer9 = color2 >> 16 & 0xFF;
        final int integer10 = color2 >> 8 & 0xFF;
        final int integer11 = color2 & 0xFF;
        final int integer12 = MathHelper.clamp((int)MathHelper.lerp(dt, (float)integer4, (float)integer8), 0, 255);
        final int integer13 = MathHelper.clamp((int)MathHelper.lerp(dt, (float)integer5, (float)integer9), 0, 255);
        final int integer14 = MathHelper.clamp((int)MathHelper.lerp(dt, (float)integer6, (float)integer10), 0, 255);
        final int integer15 = MathHelper.clamp((int)MathHelper.lerp(dt, (float)integer7, (float)integer11), 0, 255);
        return integer12 << 24 | integer13 << 16 | integer14 << 8 | integer15;
    }
    
    private static long a(final long long1) {
        return long1 / 1024L / 1024L;
    }
    
    static {
        HEIGHT_MAP_TYPES = SystemUtil.<EnumMap<Heightmap.Type, String>>consume(new EnumMap<Heightmap.Type, String>(Heightmap.Type.class), enumMap -> {
            enumMap.put(Heightmap.Type.a, "SW");
            enumMap.put(Heightmap.Type.b, "S");
            enumMap.put(Heightmap.Type.c, "OW");
            enumMap.put(Heightmap.Type.d, "O");
            enumMap.put(Heightmap.Type.e, "M");
            enumMap.put(Heightmap.Type.f, "ML");
        });
    }
}
