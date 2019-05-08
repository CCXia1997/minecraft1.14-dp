package net.minecraft.client.audio;

import java.util.Collection;
import java.util.function.Consumer;
import java.nio.ByteBuffer;
import java.io.InputStream;
import net.minecraft.resource.Resource;
import net.minecraft.util.SystemUtil;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import com.google.common.collect.Maps;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.resource.ResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundLoader
{
    private final ResourceManager resourceManager;
    private final Map<Identifier, CompletableFuture<StaticSound>> loadedSounds;
    
    public SoundLoader(final ResourceManager resourceManager) {
        this.loadedSounds = Maps.newHashMap();
        this.resourceManager = resourceManager;
    }
    
    public CompletableFuture<StaticSound> loadStatic(final Identifier id) {
        Resource resource2;
        InputStream inputStream4;
        AudioStream audioStream6;
        ByteBuffer byteBuffer8;
        StaticSound staticSound;
        final Throwable t2;
        final Throwable t5;
        final Throwable t8;
        return this.loadedSounds.computeIfAbsent(id, identifier -> CompletableFuture.<StaticSound>supplyAsync(() -> {
            try {
                resource2 = this.resourceManager.getResource(identifier);
                try {
                    inputStream4 = resource2.getInputStream();
                    try {
                        audioStream6 = new OggAudioStream(inputStream4);
                        try {
                            byteBuffer8 = audioStream6.getBuffer();
                            staticSound = new StaticSound(byteBuffer8, audioStream6.getFormat());
                            return staticSound;
                        }
                        catch (Throwable t) {
                            throw t;
                        }
                        finally {
                            if (audioStream6 != null) {
                                if (t2 != null) {
                                    try {
                                        audioStream6.close();
                                    }
                                    catch (Throwable t3) {
                                        t2.addSuppressed(t3);
                                    }
                                }
                                else {
                                    audioStream6.close();
                                }
                            }
                        }
                    }
                    catch (Throwable t4) {
                        throw t4;
                    }
                    finally {
                        if (inputStream4 != null) {
                            if (t5 != null) {
                                try {
                                    inputStream4.close();
                                }
                                catch (Throwable t6) {
                                    t5.addSuppressed(t6);
                                }
                            }
                            else {
                                inputStream4.close();
                            }
                        }
                    }
                }
                catch (Throwable t7) {
                    throw t7;
                }
                finally {
                    if (resource2 != null) {
                        if (t8 != null) {
                            try {
                                resource2.close();
                            }
                            catch (Throwable t9) {
                                t8.addSuppressed(t9);
                            }
                        }
                        else {
                            resource2.close();
                        }
                    }
                }
            }
            catch (IOException iOException2) {
                throw new CompletionException(iOException2);
            }
        }, SystemUtil.getServerWorkerExecutor()));
    }
    
    public CompletableFuture<AudioStream> loadStreamed(final Identifier id) {
        Resource resource2;
        InputStream inputStream3;
        return CompletableFuture.supplyAsync(() -> {
            try {
                resource2 = this.resourceManager.getResource(id);
                inputStream3 = resource2.getInputStream();
                return new OggAudioStream(inputStream3);
            }
            catch (IOException iOException2) {
                throw new CompletionException(iOException2);
            }
        }, SystemUtil.getServerWorkerExecutor());
    }
    
    public void close() {
        this.loadedSounds.values().forEach(completableFuture -> completableFuture.thenAccept((Consumer)StaticSound::close));
        this.loadedSounds.clear();
    }
    
    public CompletableFuture<?> loadStatic(final Collection<Sound> sounds) {
        return CompletableFuture.allOf(sounds.stream().map(sound -> this.loadStatic(sound.getLocation())).<CompletableFuture<?>>toArray(CompletableFuture[]::new));
    }
}
