package net.minecraft.client.render.debug;

import java.util.function.Predicate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.Entity;
import java.util.Collection;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import net.minecraft.client.util.DebugNameGenerator;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Position;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.UUID;
import net.minecraft.util.math.ChunkSectionPos;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.Renderer
{
    private static final Logger a;
    private final MinecraftClient b;
    private final Map<BlockPos, b> pointsOfInterest;
    private final Set<ChunkSectionPos> d;
    private final Map<UUID, a> e;
    private UUID f;
    
    public PointOfInterestDebugRenderer(final MinecraftClient minecraftClient) {
        this.pointsOfInterest = Maps.newHashMap();
        this.d = Sets.newHashSet();
        this.e = Maps.newHashMap();
        this.b = minecraftClient;
    }
    
    @Override
    public void a() {
        this.pointsOfInterest.clear();
        this.d.clear();
        this.e.clear();
        this.f = null;
    }
    
    public void a(final b b) {
        this.pointsOfInterest.put(b.a, b);
    }
    
    public void removePointOfInterest(final BlockPos blockPos) {
        this.pointsOfInterest.remove(blockPos);
    }
    
    public void a(final BlockPos blockPos, final int integer) {
        final b b3 = this.pointsOfInterest.get(blockPos);
        if (b3 == null) {
            PointOfInterestDebugRenderer.a.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
            return;
        }
        b3.c = integer;
    }
    
    public void a(final ChunkSectionPos chunkSectionPos) {
        this.d.add(chunkSectionPos);
    }
    
    public void b(final ChunkSectionPos chunkSectionPos) {
        this.d.remove(chunkSectionPos);
    }
    
    public void addPointOfInterest(final a a) {
        this.e.put(a.a, a);
    }
    
    @Override
    public void render(final long long1) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        this.b();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        this.d();
    }
    
    private void b() {
        final BlockPos blockPos1 = this.getCamera().getBlockPos();
        for (final BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
            if (blockPos1.isWithinDistance(blockPos2, 160.0)) {
                b(blockPos2);
            }
        }
        for (final ChunkSectionPos chunkSectionPos3 : this.d) {
            if (blockPos1.isWithinDistance(chunkSectionPos3.getCenterPos(), 160.0)) {
                c(chunkSectionPos3);
            }
        }
        for (final a a3 : this.e.values()) {
            if (this.d(a3)) {
                this.b(a3);
            }
        }
        for (final b b3 : this.pointsOfInterest.values()) {
            if (blockPos1.isWithinDistance(b3.a, 160.0)) {
                this.b(b3);
            }
        }
    }
    
    private static void c(final ChunkSectionPos chunkSectionPos) {
        final float float2 = 1.0f;
        final BlockPos blockPos3 = chunkSectionPos.getCenterPos();
        final BlockPos blockPos4 = blockPos3.add(-1.0, -1.0, -1.0);
        final BlockPos blockPos5 = blockPos3.add(1.0, 1.0, 1.0);
        DebugRenderer.a(blockPos4, blockPos5, 0.2f, 1.0f, 0.2f, 0.15f);
    }
    
    private static void b(final BlockPos blockPos) {
        final float float2 = 0.05f;
        DebugRenderer.a(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }
    
    private void b(final b b) {
        int integer2 = 0;
        a("Ticket holders: " + this.c(b), b, integer2, -256);
        ++integer2;
        a("Free tickets: " + b.c, b, integer2, -256);
        ++integer2;
        a(b.b, b, integer2, -1);
    }
    
    private void b(final a a) {
        int integer2 = 0;
        a(a.d, integer2, a.c, -1, 0.03f);
        ++integer2;
        for (final String string4 : a.f) {
            a(a.d, integer2, string4, -16711681, 0.02f);
            ++integer2;
        }
        for (final String string4 : a.e) {
            a(a.d, integer2, string4, -16711936, 0.02f);
            ++integer2;
        }
        if (this.c(a)) {
            for (final String string4 : Lists.<String>reverse(a.g)) {
                a(a.d, integer2, string4, -3355444, 0.02f);
                ++integer2;
            }
        }
    }
    
    private static void a(final String string, final b b, final int integer3, final int integer4) {
        final double double5 = 1.3;
        final double double6 = 0.2;
        final double double7 = b.a.getX() + 0.5;
        final double double8 = b.a.getY() + 1.3 + integer3 * 0.2;
        final double double9 = b.a.getZ() + 0.5;
        DebugRenderer.a(string, double7, double8, double9, integer4, 0.02f, true, 0.0f, true);
    }
    
    private static void a(final Position position, final int integer2, final String string, final int integer4, final float float5) {
        final double double6 = 2.4;
        final double double7 = 0.25;
        final BlockPos blockPos10 = new BlockPos(position);
        final double double8 = blockPos10.getX() + 0.5;
        final double double9 = position.getY() + 2.4 + integer2 * 0.25;
        final double double10 = blockPos10.getZ() + 0.5;
        final float float6 = 0.5f;
        DebugRenderer.a(string, double8, double9, double10, integer4, float5, false, 0.5f, true);
    }
    
    private Camera getCamera() {
        return this.b.gameRenderer.getCamera();
    }
    
    private Set<String> c(final b b) {
        return this.c(b.a).stream().map(DebugNameGenerator::getDebugName).collect(Collectors.toSet());
    }
    
    private boolean c(final a a) {
        return Objects.equals(this.f, a.a);
    }
    
    private boolean d(final a a) {
        final PlayerEntity playerEntity2 = this.b.player;
        final BlockPos blockPos3 = new BlockPos(playerEntity2.x, 0.0, playerEntity2.z);
        final BlockPos blockPos4 = new BlockPos(a.d);
        return blockPos3.isWithinDistance(blockPos4, 160.0);
    }
    
    private Collection<UUID> c(final BlockPos blockPos) {
        return this.e.values().stream().filter(a -> a.a(blockPos)).map(PointOfInterestDebugRenderer.a::a).collect(Collectors.toSet());
    }
    
    private void d() {
        DebugRenderer.a(this.b.getCameraEntity(), 16).ifPresent(entity -> this.f = entity.getUuid());
    }
    
    static {
        a = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public static class b
    {
        public final BlockPos a;
        public String b;
        public int c;
        
        public b(final BlockPos blockPos, final String string, final int integer) {
            this.a = blockPos;
            this.b = string;
            this.c = integer;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class a
    {
        public final UUID a;
        public final int b;
        public final String c;
        public final Position d;
        public final List<String> e;
        public final List<String> f;
        public final List<String> g;
        public final Set<BlockPos> h;
        
        public a(final UUID uUID, final int integer, final String string, final Position position) {
            this.e = Lists.newArrayList();
            this.f = Lists.newArrayList();
            this.g = Lists.newArrayList();
            this.h = Sets.newHashSet();
            this.a = uUID;
            this.b = integer;
            this.c = string;
            this.d = position;
        }
        
        private boolean a(final BlockPos blockPos) {
            return this.h.stream().anyMatch(blockPos::equals);
        }
        
        public UUID a() {
            return this.a;
        }
    }
}
