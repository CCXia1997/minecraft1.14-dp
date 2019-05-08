package net.minecraft.client.particle;

import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.util.Iterator;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import java.io.Reader;
import net.minecraft.resource.Resource;
import java.io.IOException;
import net.minecraft.util.JsonHelper;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import net.minecraft.client.texture.Sprite;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.texture.MissingSprite;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.texture.TickableTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import com.google.common.collect.Queues;
import com.google.common.collect.Maps;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Random;
import net.minecraft.client.texture.TextureManager;
import java.util.Queue;
import java.util.Map;
import net.minecraft.world.World;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceReloadListener;

@Environment(EnvType.CLIENT)
public class ParticleManager implements ResourceReloadListener
{
    private static final List<ParticleTextureSheet> b;
    protected World world;
    private final Map<ParticleTextureSheet, Queue<Particle>> particleQueues;
    private final Queue<EmitterParticle> newEmitterParticles;
    private final TextureManager textureManager;
    private final Random random;
    private final Int2ObjectMap<ParticleFactory<?>> factories;
    private final Queue<Particle> newParticles;
    private final Map<Identifier, SimpleSpriteProvider> i;
    private final SpriteAtlasTexture particleAtlasTexture;
    
    public ParticleManager(final World world, final TextureManager textureManager) {
        this.particleQueues = Maps.newIdentityHashMap();
        this.newEmitterParticles = Queues.newArrayDeque();
        this.random = new Random();
        this.factories = (Int2ObjectMap<ParticleFactory<?>>)new Int2ObjectOpenHashMap();
        this.newParticles = Queues.newArrayDeque();
        this.i = Maps.newHashMap();
        this.particleAtlasTexture = new SpriteAtlasTexture("textures/particle");
        textureManager.registerTextureUpdateable(SpriteAtlasTexture.PARTICLE_ATLAS_TEX, this.particleAtlasTexture);
        this.world = world;
        this.textureManager = textureManager;
        this.registerDefaultFactories();
    }
    
    private void registerDefaultFactories() {
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.a, SpellParticle.EntityAmbientFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.b, EmotionParticle.AngryVillagerFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.c, (ParticleFactory<ParticleParameters>)new BarrierParticle.Factory());
        this.<BlockStateParticleParameters>registerFactory(ParticleTypes.d, new BlockCrackParticle.Factory());
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.e, WaterBubbleParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.ab, BubbleColumnUpParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.Z, BubblePopParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.ae, CampfireSmokeParticle.a::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.af, CampfireSmokeParticle.b::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.f, CloudParticle.CloudFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.D, SuspendParticle.a::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.g, DamageParticle.c::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.aa, CurrentDownParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.h, DamageParticle.DefaultFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.i, DragonBreathParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.ad, SuspendParticle.DolphinFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.j, BlockLeakParticle.DrippingLavaFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.k, BlockLeakParticle.FallingLavaFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.l, BlockLeakParticle.LandingLavaFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.m, BlockLeakParticle.DrippingWaterFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.n, BlockLeakParticle.FallingWaterFactory::new);
        this.<DustParticleParameters>registerFactory(ParticleTypes.o, RedDustParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.p, SpellParticle.DefaultFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.q, (ParticleFactory<ParticleParameters>)new ElderGuardianAppearanceParticle.Factory());
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.r, DamageParticle.EnchantedHitFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.s, EnchantGlyphParticle.EnchantFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.t, EndRodParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.u, SpellParticle.EntityFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.v, (ParticleFactory<ParticleParameters>)new ExplosionEmitterParticle.Factory());
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.w, ExplosionLargeParticle.Factory::new);
        this.<BlockStateParticleParameters>registerFactory(ParticleTypes.x, BlockFallingDustParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.y, FireworksSparkParticle.ExplosionFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.z, FishingParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.A, FlameParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.B, FireworksSparkParticle.FlashFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.C, SuspendParticle.HappyVillagerFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.E, EmotionParticle.HeartFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.F, SpellParticle.InstantFactory::new);
        this.<ItemStackParticleParameters>registerFactory(ParticleTypes.G, new CrackParticle.ItemFactory());
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.H, (ParticleFactory<ParticleParameters>)new CrackParticle.SlimeballFactory());
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.I, (ParticleFactory<ParticleParameters>)new CrackParticle.SnowballFactory());
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.J, FireSmokeLargeParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.K, LavaEmberParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.L, SuspendParticle.MyceliumFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.ac, EnchantGlyphParticle.NautilusFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.M, NoteParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.N, ExplosionSmokeParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.O, PortalParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.P, RainSplashParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.Q, FireSmokeParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.R, CloudParticle.SneezeFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.S, SpitParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.U, SweepAttackParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.V, TotemParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.T, SquidInkParticle.Factory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.W, WaterSuspendParticle.UnderwaterFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.X, WaterSplashParticle.SplashFactory::new);
        this.<ParticleParameters>registerFactory((ParticleType<ParticleParameters>)ParticleTypes.Y, SpellParticle.WitchFactory::new);
    }
    
    private <T extends ParticleParameters> void registerFactory(final ParticleType<T> type, final ParticleFactory<T> factory) {
        this.factories.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
    }
    
    private <T extends ParticleParameters> void registerFactory(final ParticleType<T> particleType, final b<T> b) {
        final SimpleSpriteProvider simpleSpriteProvider3 = new SimpleSpriteProvider();
        this.i.put(Registry.PARTICLE_TYPE.getId(particleType), simpleSpriteProvider3);
        this.factories.put(Registry.PARTICLE_TYPE.getRawId(particleType), b.create(simpleSpriteProvider3));
    }
    
    @Override
    public CompletableFuture<Void> a(final Helper helper, final ResourceManager resourceManager, final Profiler prepareProfiler, final Profiler applyProfiler, final Executor prepareExecutor, final Executor applyExecutor) {
        final Map<Identifier, List<Identifier>> map7 = Maps.newConcurrentMap();
        final CompletableFuture<?>[] arr8 = Registry.PARTICLE_TYPE.getIds().stream().map(identifier -> CompletableFuture.runAsync(() -> this.a(resourceManager, identifier, map7), prepareExecutor)).<CompletableFuture<?>>toArray(CompletableFuture[]::new);
        final Map<K, Object> map8;
        final Set<Identifier> set5;
        final SpriteAtlasTexture.Data data2;
        final Sprite sprite4;
        final Map map9;
        final Sprite element;
        final ImmutableList<Sprite> immutableList4;
        return CompletableFuture.allOf(arr8).thenApplyAsync(void4 -> {
            prepareProfiler.startTick();
            prepareProfiler.push("stitching");
            set5 = map8.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            data2 = this.particleAtlasTexture.stitch(resourceManager, set5, prepareProfiler);
            prepareProfiler.pop();
            prepareProfiler.endTick();
            return data2;
        }, prepareExecutor).thenCompose(helper::waitForAll).thenAcceptAsync(data -> {
            applyProfiler.startTick();
            applyProfiler.push("upload");
            this.particleAtlasTexture.upload(data);
            applyProfiler.swap("bindSpriteSets");
            sprite4 = this.particleAtlasTexture.getSprite(MissingSprite.getMissingSpriteId());
            map9.forEach((identifier, list) -> {
                immutableList4 = (ImmutableList<Sprite>)(list.isEmpty() ? ImmutableList.<Sprite>of(element) : (list.stream().map(this.particleAtlasTexture::getSprite).collect(ImmutableList.toImmutableList())));
                this.i.get(identifier).setSprites(immutableList4);
                return;
            });
            applyProfiler.pop();
            applyProfiler.endTick();
        }, applyExecutor);
    }
    
    public void clearAtlas() {
        this.particleAtlasTexture.clear();
    }
    
    private void a(final ResourceManager resourceManager, final Identifier identifier, final Map<Identifier, List<Identifier>> map) {
        final Identifier identifier2 = new Identifier(identifier.getNamespace(), "particles/" + identifier.getPath() + ".json");
        try (final Resource resource5 = resourceManager.getResource(identifier2);
             final Reader reader7 = new InputStreamReader(resource5.getInputStream(), Charsets.UTF_8)) {
            final ParticleTextureData particleTextureData9 = ParticleTextureData.load(JsonHelper.deserialize(reader7));
            final List<Identifier> list10 = particleTextureData9.getTextureList();
            final boolean boolean11 = this.i.containsKey(identifier);
            if (list10 == null) {
                if (boolean11) {
                    throw new IllegalStateException("Missing texture list for particle " + identifier);
                }
            }
            else {
                if (!boolean11) {
                    throw new IllegalStateException("Redundant texture list for particle " + identifier);
                }
                map.put(identifier, list10);
            }
        }
        catch (IOException iOException5) {
            throw new IllegalStateException("Failed to load description for particle " + identifier, iOException5);
        }
    }
    
    public void addEmitter(final Entity entity, final ParticleParameters parameters) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters));
    }
    
    public void addEmitter(final Entity entity, final ParticleParameters parameters, final int maxAge) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters, maxAge));
    }
    
    @Nullable
    public Particle addParticle(final ParticleParameters parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        final Particle particle14 = this.<ParticleParameters>createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        if (particle14 != null) {
            this.addParticle(particle14);
            return particle14;
        }
        return null;
    }
    
    @Nullable
    private <T extends ParticleParameters> Particle createParticle(final T parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        final ParticleFactory<T> particleFactory14 = (ParticleFactory<T>)this.factories.get(Registry.PARTICLE_TYPE.getRawId(parameters.getType()));
        if (particleFactory14 == null) {
            return null;
        }
        return particleFactory14.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    public void addParticle(final Particle particle) {
        this.newParticles.add(particle);
    }
    
    public void tick() {
        this.particleQueues.forEach((particleTextureSheet, queue) -> {
            this.world.getProfiler().push(particleTextureSheet.toString());
            this.updateParticleQueue(queue);
            this.world.getProfiler().pop();
            return;
        });
        if (!this.newEmitterParticles.isEmpty()) {
            final List<EmitterParticle> list1 = Lists.newArrayList();
            for (final EmitterParticle emitterParticle3 : this.newEmitterParticles) {
                emitterParticle3.update();
                if (!emitterParticle3.isAlive()) {
                    list1.add(emitterParticle3);
                }
            }
            this.newEmitterParticles.removeAll(list1);
        }
        if (!this.newParticles.isEmpty()) {
            Particle particle1;
            while ((particle1 = this.newParticles.poll()) != null) {
                this.particleQueues.computeIfAbsent(particle1.getTextureSheet(), particleTextureSheet -> EvictingQueue.create(16384)).add(particle1);
            }
        }
    }
    
    private void updateParticleQueue(final Collection<Particle> collection) {
        if (!collection.isEmpty()) {
            final Iterator<Particle> iterator2 = collection.iterator();
            while (iterator2.hasNext()) {
                final Particle particle3 = iterator2.next();
                this.updateParticle(particle3);
                if (!particle3.isAlive()) {
                    iterator2.remove();
                }
            }
        }
    }
    
    private void updateParticle(final Particle particle) {
        try {
            particle.update();
        }
        catch (Throwable throwable2) {
            final CrashReport crashReport3 = CrashReport.create(throwable2, "Ticking Particle");
            final CrashReportSection crashReportSection4 = crashReport3.addElement("Particle being ticked");
            crashReportSection4.add("Particle", particle::toString);
            crashReportSection4.add("Particle Type", particle.getTextureSheet()::toString);
            throw new CrashException(crashReport3);
        }
    }
    
    public void renderParticles(final Camera camera, final float tickDelta) {
        final float float3 = MathHelper.cos(camera.getYaw() * 0.017453292f);
        final float float4 = MathHelper.sin(camera.getYaw() * 0.017453292f);
        final float float5 = -float4 * MathHelper.sin(camera.getPitch() * 0.017453292f);
        final float float6 = float3 * MathHelper.sin(camera.getPitch() * 0.017453292f);
        final float float7 = MathHelper.cos(camera.getPitch() * 0.017453292f);
        Particle.cameraX = camera.getPos().x;
        Particle.cameraY = camera.getPos().y;
        Particle.cameraZ = camera.getPos().z;
        for (final ParticleTextureSheet particleTextureSheet9 : ParticleManager.b) {
            final Iterable<Particle> iterable10 = this.particleQueues.get(particleTextureSheet9);
            if (iterable10 == null) {
                continue;
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            final Tessellator tessellator11 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
            particleTextureSheet9.begin(bufferBuilder12, this.textureManager);
            for (final Particle particle14 : iterable10) {
                try {
                    particle14.buildGeometry(bufferBuilder12, camera, tickDelta, float3, float7, float4, float5, float6);
                }
                catch (Throwable throwable15) {
                    final CrashReport crashReport16 = CrashReport.create(throwable15, "Rendering Particle");
                    final CrashReportSection crashReportSection17 = crashReport16.addElement("Particle being rendered");
                    crashReportSection17.add("Particle", particle14::toString);
                    crashReportSection17.add("Particle Type", particleTextureSheet9::toString);
                    throw new CrashException(crashReport16);
                }
            }
            particleTextureSheet9.draw(tessellator11);
        }
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
    }
    
    public void setWorld(@Nullable final World world) {
        this.world = world;
        this.particleQueues.clear();
        this.newEmitterParticles.clear();
    }
    
    public void addBlockBreakParticles(final BlockPos pos, final BlockState state) {
        if (state.isAir()) {
            return;
        }
        final VoxelShape voxelShape3 = state.getOutlineShape(this.world, pos);
        final double double14 = 0.25;
        final double double15;
        final double double16;
        final double double17;
        final int integer21;
        final int integer22;
        final int integer23;
        int integer24;
        int integer25;
        int integer26;
        double double18;
        double double19;
        double double20;
        double double21;
        double double22;
        double double23;
        voxelShape3.forEachBox((double3, double5, double7, double9, double11, double13) -> {
            double15 = Math.min(1.0, double9 - double3);
            double16 = Math.min(1.0, double11 - double5);
            double17 = Math.min(1.0, double13 - double7);
            integer21 = Math.max(2, MathHelper.ceil(double15 / 0.25));
            integer22 = Math.max(2, MathHelper.ceil(double16 / 0.25));
            integer23 = Math.max(2, MathHelper.ceil(double17 / 0.25));
            for (integer24 = 0; integer24 < integer21; ++integer24) {
                for (integer25 = 0; integer25 < integer22; ++integer25) {
                    for (integer26 = 0; integer26 < integer23; ++integer26) {
                        double18 = (integer24 + 0.5) / integer21;
                        double19 = (integer25 + 0.5) / integer22;
                        double20 = (integer26 + 0.5) / integer23;
                        double21 = double18 * double15 + double3;
                        double22 = double19 * double16 + double5;
                        double23 = double20 * double17 + double7;
                        this.addParticle(new BlockCrackParticle(this.world, pos.getX() + double21, pos.getY() + double22, pos.getZ() + double23, double18 - 0.5, double19 - 0.5, double20 - 0.5, state).setBlockPos(pos));
                    }
                }
            }
        });
    }
    
    public void addBlockBreakingParticles(final BlockPos blockPos, final Direction direction) {
        final BlockState blockState3 = this.world.getBlockState(blockPos);
        if (blockState3.getRenderType() == BlockRenderType.a) {
            return;
        }
        final int integer4 = blockPos.getX();
        final int integer5 = blockPos.getY();
        final int integer6 = blockPos.getZ();
        final float float7 = 0.1f;
        final BoundingBox boundingBox8 = blockState3.getOutlineShape(this.world, blockPos).getBoundingBox();
        double double9 = integer4 + this.random.nextDouble() * (boundingBox8.maxX - boundingBox8.minX - 0.20000000298023224) + 0.10000000149011612 + boundingBox8.minX;
        double double10 = integer5 + this.random.nextDouble() * (boundingBox8.maxY - boundingBox8.minY - 0.20000000298023224) + 0.10000000149011612 + boundingBox8.minY;
        double double11 = integer6 + this.random.nextDouble() * (boundingBox8.maxZ - boundingBox8.minZ - 0.20000000298023224) + 0.10000000149011612 + boundingBox8.minZ;
        if (direction == Direction.DOWN) {
            double10 = integer5 + boundingBox8.minY - 0.10000000149011612;
        }
        if (direction == Direction.UP) {
            double10 = integer5 + boundingBox8.maxY + 0.10000000149011612;
        }
        if (direction == Direction.NORTH) {
            double11 = integer6 + boundingBox8.minZ - 0.10000000149011612;
        }
        if (direction == Direction.SOUTH) {
            double11 = integer6 + boundingBox8.maxZ + 0.10000000149011612;
        }
        if (direction == Direction.WEST) {
            double9 = integer4 + boundingBox8.minX - 0.10000000149011612;
        }
        if (direction == Direction.EAST) {
            double9 = integer4 + boundingBox8.maxX + 0.10000000149011612;
        }
        this.addParticle(new BlockCrackParticle(this.world, double9, double10, double11, 0.0, 0.0, 0.0, blockState3).setBlockPos(blockPos).move(0.2f).e(0.6f));
    }
    
    public String getDebugString() {
        return String.valueOf(this.particleQueues.values().stream().mapToInt(Collection::size).sum());
    }
    
    static {
        b = ImmutableList.<ParticleTextureSheet>of(ParticleTextureSheet.TERRAIN_SHEET, ParticleTextureSheet.PARTICLE_SHEET_OPAQUE, ParticleTextureSheet.PARTICLE_SHEET_LIT, ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT, ParticleTextureSheet.CUSTOM);
    }
    
    @Environment(EnvType.CLIENT)
    class SimpleSpriteProvider implements SpriteProvider
    {
        private List<Sprite> sprites;
        
        private SimpleSpriteProvider() {
        }
        
        @Override
        public Sprite getSprite(final int integer1, final int integer2) {
            return this.sprites.get(integer1 * (this.sprites.size() - 1) / integer2);
        }
        
        @Override
        public Sprite getSprite(final Random random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }
        
        public void setSprites(final List<Sprite> sprites) {
            this.sprites = ImmutableList.copyOf(sprites);
        }
    }
    
    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface b<T extends ParticleParameters>
    {
        ParticleFactory<T> create(final SpriteProvider arg1);
    }
}
