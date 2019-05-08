package net.minecraft.client.texture;

import org.apache.logging.log4j.LogManager;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import net.minecraft.resource.Resource;
import net.minecraft.util.SystemUtil;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.util.PngFile;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.crash.CrashReportSection;
import java.util.Iterator;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import com.mojang.blaze3d.platform.TextureUtil;
import java.util.Collection;
import java.io.IOException;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.MinecraftClient;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.Set;
import java.util.List;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements TickableTexture
{
    private static final Logger LOGGER;
    public static final Identifier BLOCK_ATLAS_TEX;
    public static final Identifier PARTICLE_ATLAS_TEX;
    public static final Identifier PAINTING_ATLAS_TEX;
    public static final Identifier STATUS_EFFECT_ATLAS_TEX;
    private final List<Sprite> animatedSprites;
    private final Set<Identifier> spritesToLoad;
    private final Map<Identifier, Sprite> sprites;
    private final String pathPrefix;
    private final int maxTextureSize;
    private int mipLevel;
    private final Sprite missingSprite;
    
    public SpriteAtlasTexture(final String string) {
        this.animatedSprites = Lists.newArrayList();
        this.spritesToLoad = Sets.newHashSet();
        this.sprites = Maps.newHashMap();
        this.missingSprite = MissingSprite.getMissingSprite();
        this.pathPrefix = string;
        this.maxTextureSize = MinecraftClient.getMaxTextureSize();
    }
    
    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
    }
    
    public void upload(final Data data) {
        this.spritesToLoad.clear();
        this.spritesToLoad.addAll(data.a);
        SpriteAtlasTexture.LOGGER.info("Created: {}x{} {}-atlas", data.b, data.c, this.pathPrefix);
        TextureUtil.prepareImage(this.getGlId(), this.mipLevel, data.b, data.c);
        this.clear();
        for (final Sprite sprite3 : data.d) {
            this.sprites.put(sprite3.getId(), sprite3);
            try {
                sprite3.upload();
            }
            catch (Throwable throwable4) {
                final CrashReport crashReport5 = CrashReport.create(throwable4, "Stitching texture atlas");
                final CrashReportSection crashReportSection6 = crashReport5.addElement("Texture being stitched together");
                crashReportSection6.add("Atlas path", this.pathPrefix);
                crashReportSection6.add("Sprite", sprite3);
                throw new CrashException(crashReport5);
            }
            if (sprite3.isAnimated()) {
                this.animatedSprites.add(sprite3);
            }
        }
    }
    
    public Data stitch(final ResourceManager resourceManager, final Iterable<Identifier> iterable, final Profiler profiler) {
        final Set<Identifier> set4 = Sets.newHashSet();
        profiler.push("preparing");
        final Set<Identifier> set5;
        iterable.forEach(identifier -> {
            if (identifier == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
            else {
                set5.add(identifier);
                return;
            }
        });
        final int integer5 = this.maxTextureSize;
        final TextureStitcher textureStitcher6 = new TextureStitcher(integer5, integer5, this.mipLevel);
        int integer6 = Integer.MAX_VALUE;
        int integer7 = 1 << this.mipLevel;
        profiler.swap("extracting_frames");
        for (final Sprite sprite10 : this.loadSprites(resourceManager, set4)) {
            integer6 = Math.min(integer6, Math.min(sprite10.getWidth(), sprite10.getHeight()));
            final int integer8 = Math.min(Integer.lowestOneBit(sprite10.getWidth()), Integer.lowestOneBit(sprite10.getHeight()));
            if (integer8 < integer7) {
                SpriteAtlasTexture.LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", sprite10.getId(), sprite10.getWidth(), sprite10.getHeight(), MathHelper.log2(integer7), MathHelper.log2(integer8));
                integer7 = integer8;
            }
            textureStitcher6.add(sprite10);
        }
        final int integer9 = Math.min(integer6, integer7);
        final int integer10 = MathHelper.log2(integer9);
        if (integer10 < this.mipLevel) {
            SpriteAtlasTexture.LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.pathPrefix, this.mipLevel, integer10, integer9);
            this.mipLevel = integer10;
        }
        profiler.swap("mipmapping");
        this.missingSprite.generateMipmaps(this.mipLevel);
        profiler.swap("register");
        textureStitcher6.add(this.missingSprite);
        profiler.swap("stitching");
        try {
            textureStitcher6.stitch();
        }
        catch (TextureStitcherCannotFitException textureStitcherCannotFitException11) {
            throw textureStitcherCannotFitException11;
        }
        profiler.swap("loading");
        final List<Sprite> list11 = this.a(resourceManager, textureStitcher6);
        profiler.pop();
        return new Data(set4, textureStitcher6.getWidth(), textureStitcher6.getHeight(), list11);
    }
    
    private Collection<Sprite> loadSprites(final ResourceManager resourceManager, final Set<Identifier> set) {
        final List<CompletableFuture<?>> list3 = new ArrayList<CompletableFuture<?>>();
        final ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue4 = new ConcurrentLinkedQueue<Sprite>();
        for (final Identifier identifier6 : set) {
            if (this.missingSprite.getId().equals(identifier6)) {
                continue;
            }
            final Identifier identifier8;
            final Identifier identifier7;
            Resource resource6;
            PngFile pngFile8;
            AnimationResourceMetadata animationResourceMetadata9;
            Sprite sprite5;
            final Throwable t2;
            final ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue5;
            list3.add(CompletableFuture.runAsync(() -> {
                identifier7 = this.getTexturePath(identifier8);
                try {
                    resource6 = resourceManager.getResource(identifier7);
                    try {
                        pngFile8 = new PngFile(resource6.toString(), resource6.getInputStream());
                        animationResourceMetadata9 = resource6.<AnimationResourceMetadata>getMetadata((ResourceMetadataReader<AnimationResourceMetadata>)AnimationResourceMetadata.READER);
                        sprite5 = new Sprite(identifier8, pngFile8, animationResourceMetadata9);
                    }
                    catch (Throwable t) {
                        throw t;
                    }
                    finally {
                        if (resource6 != null) {
                            if (t2 != null) {
                                try {
                                    resource6.close();
                                }
                                catch (Throwable t3) {
                                    t2.addSuppressed(t3);
                                }
                            }
                            else {
                                resource6.close();
                            }
                        }
                    }
                }
                catch (RuntimeException runtimeException6) {
                    SpriteAtlasTexture.LOGGER.error("Unable to parse metadata from {} : {}", identifier7, runtimeException6);
                    return;
                }
                catch (IOException iOException6) {
                    SpriteAtlasTexture.LOGGER.error("Using missing texture, unable to load {} : {}", identifier7, iOException6);
                    return;
                }
                concurrentLinkedQueue5.add(sprite5);
                return;
            }, SystemUtil.getServerWorkerExecutor()));
        }
        CompletableFuture.allOf(list3.<CompletableFuture<?>>toArray(new CompletableFuture[0])).join();
        return concurrentLinkedQueue4;
    }
    
    private List<Sprite> a(final ResourceManager resourceManager, final TextureStitcher textureStitcher) {
        final ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue3 = new ConcurrentLinkedQueue<Sprite>();
        final List<CompletableFuture<?>> list4 = new ArrayList<CompletableFuture<?>>();
        for (final Sprite sprite6 : textureStitcher.getStitchedSprites()) {
            if (sprite6 == this.missingSprite) {
                concurrentLinkedQueue3.add(sprite6);
            }
            else {
                final Sprite sprite7;
                final ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue4;
                list4.add(CompletableFuture.runAsync(() -> {
                    if (this.loadSprite(resourceManager, sprite7)) {
                        concurrentLinkedQueue4.add(sprite7);
                    }
                    return;
                }, SystemUtil.getServerWorkerExecutor()));
            }
        }
        CompletableFuture.allOf(list4.<CompletableFuture<?>>toArray(new CompletableFuture[0])).join();
        return new ArrayList<Sprite>(concurrentLinkedQueue3);
    }
    
    private boolean loadSprite(final ResourceManager container, final Sprite sprite) {
        final Identifier identifier3 = this.getTexturePath(sprite.getId());
        Resource resource4 = null;
        try {
            resource4 = container.getResource(identifier3);
            sprite.load(resource4, this.mipLevel + 1);
        }
        catch (RuntimeException runtimeException5) {
            SpriteAtlasTexture.LOGGER.error("Unable to parse metadata from {}", identifier3, runtimeException5);
            return false;
        }
        catch (IOException iOException5) {
            SpriteAtlasTexture.LOGGER.error("Using missing texture, unable to load {}", identifier3, iOException5);
            return false;
        }
        finally {
            IOUtils.closeQuietly((Closeable)resource4);
        }
        try {
            sprite.generateMipmaps(this.mipLevel);
        }
        catch (Throwable throwable5) {
            final CrashReport crashReport6 = CrashReport.create(throwable5, "Applying mipmap");
            final CrashReportSection crashReportSection7 = crashReport6.addElement("Sprite being mipmapped");
            crashReportSection7.add("Sprite name", () -> sprite.getId().toString());
            crashReportSection7.add("Sprite size", () -> sprite.getWidth() + " x " + sprite.getHeight());
            crashReportSection7.add("Sprite frames", () -> sprite.getFrameCount() + " frames");
            crashReportSection7.add("Mipmap levels", this.mipLevel);
            throw new CrashException(crashReport6);
        }
        return true;
    }
    
    private Identifier getTexturePath(final Identifier identifier) {
        return new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.pathPrefix, identifier.getPath(), ".png"));
    }
    
    public Sprite getSprite(final String string) {
        return this.getSprite(new Identifier(string));
    }
    
    public void updateAnimatedSprites() {
        this.bindTexture();
        for (final Sprite sprite2 : this.animatedSprites) {
            sprite2.tick();
        }
    }
    
    @Override
    public void tick() {
        this.updateAnimatedSprites();
    }
    
    public void setMipLevel(final int integer) {
        this.mipLevel = integer;
    }
    
    public Sprite getSprite(final Identifier identifier) {
        final Sprite sprite2 = this.sprites.get(identifier);
        if (sprite2 == null) {
            return this.missingSprite;
        }
        return sprite2;
    }
    
    public void clear() {
        for (final Sprite sprite2 : this.sprites.values()) {
            sprite2.destroy();
        }
        this.sprites.clear();
        this.animatedSprites.clear();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BLOCK_ATLAS_TEX = new Identifier("textures/atlas/blocks.png");
        PARTICLE_ATLAS_TEX = new Identifier("textures/atlas/particles.png");
        PAINTING_ATLAS_TEX = new Identifier("textures/atlas/paintings.png");
        STATUS_EFFECT_ATLAS_TEX = new Identifier("textures/atlas/mob_effects.png");
    }
    
    @Environment(EnvType.CLIENT)
    public static class Data
    {
        final Set<Identifier> a;
        final int b;
        final int c;
        final List<Sprite> d;
        
        public Data(final Set<Identifier> set, final int integer2, final int integer3, final List<Sprite> list) {
            this.a = set;
            this.b = integer2;
            this.c = integer3;
            this.d = list;
        }
    }
}
