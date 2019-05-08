package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.MarkerManager;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.render.Camera;
import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import java.util.List;
import net.minecraft.sound.SoundCategory;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.Identifier;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundSystem
{
    private static final Marker MARKER;
    private static final Logger LOGGER;
    private static final Set<Identifier> unknownSounds;
    private final SoundManager loader;
    private final GameOptions settings;
    private boolean started;
    private final SoundEngine soundEngine;
    private final Listener listener;
    private final SoundLoader soundLoader;
    private final SoundExecutor taskQueue;
    private final Channel channel;
    private int ticks;
    private final Map<SoundInstance, Channel.SourceManager> sources;
    private final Multimap<SoundCategory, SoundInstance> sounds;
    private final List<TickableSoundInstance> tickingSounds;
    private final Map<SoundInstance, Integer> startTicks;
    private final Map<SoundInstance, Integer> soundEndTicks;
    private final List<ListenerSoundInstance> listeners;
    private final List<Sound> preloadedSounds;
    
    public SoundSystem(final SoundManager loader, final GameOptions gameOptions, final ResourceManager resourceManager) {
        this.soundEngine = new SoundEngine();
        this.listener = this.soundEngine.getListener();
        this.taskQueue = new SoundExecutor();
        this.channel = new Channel(this.soundEngine, this.taskQueue);
        this.sources = Maps.newHashMap();
        this.sounds = HashMultimap.create();
        this.tickingSounds = Lists.newArrayList();
        this.startTicks = Maps.newHashMap();
        this.soundEndTicks = Maps.newHashMap();
        this.listeners = Lists.newArrayList();
        this.preloadedSounds = Lists.newArrayList();
        this.loader = loader;
        this.settings = gameOptions;
        this.soundLoader = new SoundLoader(resourceManager);
    }
    
    public void reloadSounds() {
        SoundSystem.unknownSounds.clear();
        for (final SoundEvent soundEvent2 : Registry.SOUND_EVENT) {
            final Identifier identifier3 = soundEvent2.getId();
            if (this.loader.get(identifier3) == null) {
                SoundSystem.LOGGER.warn("Missing sound for event: {}", Registry.SOUND_EVENT.getId(soundEvent2));
                SoundSystem.unknownSounds.add(identifier3);
            }
        }
        this.stop();
        this.start();
    }
    
    private synchronized void start() {
        if (this.started) {
            return;
        }
        try {
            this.soundEngine.init();
            this.listener.init();
            this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.a));
            this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
            this.started = true;
            SoundSystem.LOGGER.info(SoundSystem.MARKER, "Sound engine started");
        }
        catch (RuntimeException runtimeException1) {
            SoundSystem.LOGGER.error(SoundSystem.MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException1);
        }
    }
    
    private float getSoundVolume(final SoundCategory soundCategory) {
        if (soundCategory == null || soundCategory == SoundCategory.a) {
            return 1.0f;
        }
        return this.settings.getSoundVolume(soundCategory);
    }
    
    public void updateSoundVolume(final SoundCategory soundCategory, final float volume) {
        if (!this.started) {
            return;
        }
        if (soundCategory == SoundCategory.a) {
            this.listener.setVolume(volume);
            return;
        }
        final float float3;
        final float volume2;
        this.sources.forEach((soundInstance, sourceManager) -> {
            float3 = this.getAdjustedVolume(soundInstance);
            sourceManager.run(source -> {
                if (volume2 <= 0.0f) {
                    source.stop();
                }
                else {
                    source.setVolume(volume2);
                }
            });
        });
    }
    
    public void stop() {
        if (this.started) {
            this.stopAll();
            this.soundLoader.close();
            this.soundEngine.close();
            this.started = false;
        }
    }
    
    public void stop(final SoundInstance soundInstance) {
        if (this.started) {
            final Channel.SourceManager sourceManager2 = this.sources.remove(soundInstance);
            if (sourceManager2 != null) {
                this.soundEndTicks.remove(soundInstance);
                sourceManager2.run(Source::stop);
            }
        }
    }
    
    public void stopAll() {
        if (this.started) {
            this.taskQueue.restart();
            this.sources.values().forEach(sourceManager -> sourceManager.run(Source::stop));
            this.sources.clear();
            this.channel.close();
            this.startTicks.clear();
            this.tickingSounds.clear();
            this.sounds.clear();
            this.soundEndTicks.clear();
        }
    }
    
    public void registerListener(final ListenerSoundInstance listenerSoundInstance) {
        this.listeners.add(listenerSoundInstance);
    }
    
    public void unregisterListener(final ListenerSoundInstance listenerSoundInstance) {
        this.listeners.remove(listenerSoundInstance);
    }
    
    public void tick(final boolean boolean1) {
        if (!boolean1) {
            this.tick();
        }
        this.channel.tick();
    }
    
    private void tick() {
        ++this.ticks;
        for (final TickableSoundInstance tickableSoundInstance2 : this.tickingSounds) {
            tickableSoundInstance2.tick();
            if (tickableSoundInstance2.isDone()) {
                this.stop(tickableSoundInstance2);
            }
            else {
                final float float3 = this.getAdjustedVolume(tickableSoundInstance2);
                final float float4 = this.getAdjustedPitch(tickableSoundInstance2);
                final Vec3d vec3d5 = new Vec3d(tickableSoundInstance2.getX(), tickableSoundInstance2.getY(), tickableSoundInstance2.getZ());
                final Channel.SourceManager sourceManager6 = this.sources.get(tickableSoundInstance2);
                if (sourceManager6 == null) {
                    continue;
                }
                final float volume;
                final float pitch;
                final Vec3d position;
                sourceManager6.run(source -> {
                    source.setVolume(volume);
                    source.setPitch(pitch);
                    source.setPosition(position);
                    return;
                });
            }
        }
        final Iterator<Map.Entry<SoundInstance, Channel.SourceManager>> iterator1 = this.sources.entrySet().iterator();
        while (iterator1.hasNext()) {
            final Map.Entry<SoundInstance, Channel.SourceManager> entry2 = iterator1.next();
            final Channel.SourceManager sourceManager7 = entry2.getValue();
            final SoundInstance soundInstance4 = entry2.getKey();
            final float float5 = this.settings.getSoundVolume(soundInstance4.getCategory());
            if (float5 <= 0.0f) {
                sourceManager7.run(Source::stop);
                iterator1.remove();
            }
            else {
                if (!sourceManager7.isStopped()) {
                    continue;
                }
                final int integer6 = this.soundEndTicks.get(soundInstance4);
                if (integer6 > this.ticks) {
                    continue;
                }
                final int integer7 = soundInstance4.getRepeatDelay();
                if (soundInstance4.isRepeatable() && integer7 > 0) {
                    this.startTicks.put(soundInstance4, this.ticks + integer7);
                }
                iterator1.remove();
                SoundSystem.LOGGER.debug(SoundSystem.MARKER, "Removed channel {} because it's not playing anymore", sourceManager7);
                this.soundEndTicks.remove(soundInstance4);
                try {
                    this.sounds.remove(soundInstance4.getCategory(), sourceManager7);
                }
                catch (RuntimeException ex) {}
                if (!(soundInstance4 instanceof TickableSoundInstance)) {
                    continue;
                }
                this.tickingSounds.remove(soundInstance4);
            }
        }
        final Iterator<Map.Entry<SoundInstance, Integer>> iterator2 = this.startTicks.entrySet().iterator();
        while (iterator2.hasNext()) {
            final Map.Entry<SoundInstance, Integer> entry3 = iterator2.next();
            if (this.ticks >= entry3.getValue()) {
                final SoundInstance soundInstance4 = entry3.getKey();
                if (soundInstance4 instanceof TickableSoundInstance) {
                    ((TickableSoundInstance)soundInstance4).tick();
                }
                this.play(soundInstance4);
                iterator2.remove();
            }
        }
    }
    
    public boolean isPlaying(final SoundInstance soundInstance) {
        return this.started && ((this.soundEndTicks.containsKey(soundInstance) && this.soundEndTicks.get(soundInstance) <= this.ticks) || this.sources.containsKey(soundInstance));
    }
    
    public void play(final SoundInstance soundInstance) {
        if (!this.started) {
            return;
        }
        final WeightedSoundSet weightedSoundSet2 = soundInstance.getSoundSet(this.loader);
        final Identifier identifier3 = soundInstance.getId();
        if (weightedSoundSet2 == null) {
            if (SoundSystem.unknownSounds.add(identifier3)) {
                SoundSystem.LOGGER.warn(SoundSystem.MARKER, "Unable to play unknown soundEvent: {}", identifier3);
            }
            return;
        }
        if (!this.listeners.isEmpty()) {
            for (final ListenerSoundInstance listenerSoundInstance5 : this.listeners) {
                listenerSoundInstance5.onSoundPlayed(soundInstance, weightedSoundSet2);
            }
        }
        if (this.listener.getVolume() <= 0.0f) {
            SoundSystem.LOGGER.debug(SoundSystem.MARKER, "Skipped playing soundEvent: {}, master volume was zero", identifier3);
            return;
        }
        final Sound sound4 = soundInstance.getSound();
        if (sound4 == SoundManager.SOUND_MISSING) {
            if (SoundSystem.unknownSounds.add(identifier3)) {
                SoundSystem.LOGGER.warn(SoundSystem.MARKER, "Unable to play empty soundEvent: {}", identifier3);
            }
            return;
        }
        final float float5 = soundInstance.getVolume();
        final float float6 = Math.max(float5, 1.0f) * sound4.getAttenuation();
        final SoundCategory soundCategory7 = soundInstance.getCategory();
        final float float7 = this.getAdjustedVolume(soundInstance);
        final float float8 = this.getAdjustedPitch(soundInstance);
        final SoundInstance.AttenuationType attenuationType10 = soundInstance.getAttenuationType();
        final boolean boolean11 = soundInstance.isLooping();
        if (float7 == 0.0f && !soundInstance.shouldAlwaysPlay()) {
            SoundSystem.LOGGER.debug(SoundSystem.MARKER, "Skipped playing sound {}, volume was zero.", sound4.getIdentifier());
            return;
        }
        final boolean boolean12 = soundInstance.isRepeatable() && soundInstance.getRepeatDelay() == 0;
        final Vec3d vec3d13 = new Vec3d(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ());
        final Channel.SourceManager sourceManager14 = this.channel.createSource(sound4.isStreamed() ? SoundEngine.RunMode.b : SoundEngine.RunMode.a);
        SoundSystem.LOGGER.debug(SoundSystem.MARKER, "Playing sound {} for event {}", sound4.getIdentifier(), identifier3);
        this.soundEndTicks.put(soundInstance, this.ticks + 20);
        this.sources.put(soundInstance, sourceManager14);
        this.sounds.put(soundCategory7, soundInstance);
        final float pitch;
        final float volume;
        final SoundInstance.AttenuationType attenuationType11;
        final float attenuation;
        final boolean looping;
        final Vec3d position;
        final boolean relative;
        sourceManager14.run(source -> {
            source.setPitch(pitch);
            source.setVolume(volume);
            if (attenuationType11 == SoundInstance.AttenuationType.LINEAR) {
                source.setAttenuation(attenuation);
            }
            else {
                source.disableAttenuation();
            }
            source.setLooping(looping);
            source.setPosition(position);
            source.setRelative(relative);
            return;
        });
        if (!sound4.isStreamed()) {
            this.soundLoader.loadStatic(sound4.getLocation()).thenAccept(staticSound -> sourceManager14.run(source -> {
                source.setBuffer(staticSound);
                source.play();
            }));
        }
        else {
            this.soundLoader.loadStreamed(sound4.getLocation()).thenAccept(audioStream -> sourceManager14.run(source -> {
                source.setStream(audioStream);
                source.play();
            }));
        }
        if (soundInstance instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)soundInstance);
        }
    }
    
    public void addPreloadedSound(final Sound sound) {
        this.preloadedSounds.add(sound);
    }
    
    private float getAdjustedPitch(final SoundInstance soundInstance) {
        return MathHelper.clamp(soundInstance.getPitch(), 0.5f, 2.0f);
    }
    
    private float getAdjustedVolume(final SoundInstance soundInstance) {
        return MathHelper.clamp(soundInstance.getVolume() * this.getSoundVolume(soundInstance.getCategory()), 0.0f, 1.0f);
    }
    
    public void pauseAll() {
        if (this.started) {
            this.channel.execute(stream -> stream.forEach(Source::pause));
        }
    }
    
    public void resumeAll() {
        if (this.started) {
            this.channel.execute(stream -> stream.forEach(Source::resume));
        }
    }
    
    public void play(final SoundInstance sound, final int delay) {
        this.startTicks.put(sound, this.ticks + delay);
    }
    
    public void updateListenerPosition(final Camera camera) {
        if (!this.started || !camera.isReady()) {
            return;
        }
        final Vec3d vec3d2 = camera.getPos();
        final Vec3d vec3d3 = camera.l();
        final Vec3d vec3d4 = camera.m();
        final Vec3d position;
        final Vec3d from;
        final Vec3d to;
        this.taskQueue.execute(() -> {
            this.listener.setPosition(position);
            this.listener.setOrientation(from, to);
        });
    }
    
    public void stopSounds(@Nullable final Identifier identifier, @Nullable final SoundCategory soundCategory) {
        if (soundCategory != null) {
            for (final SoundInstance soundInstance4 : this.sounds.get(soundCategory)) {
                if (identifier == null || soundInstance4.getId().equals(identifier)) {
                    this.stop(soundInstance4);
                }
            }
        }
        else if (identifier == null) {
            this.stopAll();
        }
        else {
            for (final SoundInstance soundInstance4 : this.sources.keySet()) {
                if (soundInstance4.getId().equals(identifier)) {
                    this.stop(soundInstance4);
                }
            }
        }
    }
    
    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }
    
    static {
        MARKER = MarkerManager.getMarker("SOUNDS");
        LOGGER = LogManager.getLogger();
        unknownSounds = Sets.newHashSet();
    }
}
