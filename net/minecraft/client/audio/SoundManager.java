package net.minecraft.client.audio;

import net.minecraft.text.TextComponent;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.client.render.Camera;
import java.util.Collection;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import java.lang.reflect.Type;
import java.io.Reader;
import net.minecraft.util.JsonHelper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import net.minecraft.util.registry.Registry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.resource.Resource;
import net.minecraft.util.profiler.Profiler;
import com.google.common.collect.Maps;
import net.minecraft.client.options.GameOptions;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SupplyingResourceReloadListener;

@Environment(EnvType.CLIENT)
public class SoundManager extends SupplyingResourceReloadListener<SoundList>
{
    public static final Sound SOUND_MISSING;
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final ParameterizedType TYPE;
    private final Map<Identifier, WeightedSoundSet> sounds;
    private final SoundSystem soundSystem;
    
    public SoundManager(final ResourceManager resourceManager, final GameOptions gameOptions) {
        this.sounds = Maps.newHashMap();
        this.soundSystem = new SoundSystem(this, gameOptions, resourceManager);
    }
    
    @Override
    protected SoundList load(final ResourceManager resourceManager, final Profiler profiler) {
        final SoundList soundList3 = new SoundList();
        profiler.startTick();
        for (final String string5 : resourceManager.getAllNamespaces()) {
            profiler.push(string5);
            try {
                final List<Resource> list6 = resourceManager.getAllResources(new Identifier(string5, "sounds.json"));
                for (final Resource resource8 : list6) {
                    profiler.push(resource8.getResourcePackName());
                    try {
                        profiler.push("parse");
                        final Map<String, SoundEntry> map9 = readSounds(resource8.getInputStream());
                        profiler.swap("register");
                        for (final Map.Entry<String, SoundEntry> entry11 : map9.entrySet()) {
                            soundList3.register(new Identifier(string5, entry11.getKey()), entry11.getValue(), resourceManager);
                        }
                        profiler.pop();
                    }
                    catch (RuntimeException runtimeException9) {
                        SoundManager.LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", resource8.getResourcePackName(), runtimeException9);
                    }
                    profiler.pop();
                }
            }
            catch (IOException ex) {}
            profiler.pop();
        }
        profiler.endTick();
        return soundList3;
    }
    
    @Override
    protected void apply(final SoundList result, final ResourceManager resourceManager, final Profiler profiler) {
        result.addTo(this.sounds, this.soundSystem);
        for (final Identifier identifier5 : this.sounds.keySet()) {
            final WeightedSoundSet weightedSoundSet6 = this.sounds.get(identifier5);
            if (weightedSoundSet6.getSubtitle() instanceof TranslatableTextComponent) {
                final String string7 = ((TranslatableTextComponent)weightedSoundSet6.getSubtitle()).getKey();
                if (I18n.hasTranslation(string7)) {
                    continue;
                }
                SoundManager.LOGGER.debug("Missing subtitle {} for event: {}", string7, identifier5);
            }
        }
        if (SoundManager.LOGGER.isDebugEnabled()) {
            for (final Identifier identifier5 : this.sounds.keySet()) {
                if (!Registry.SOUND_EVENT.containsId(identifier5)) {
                    SoundManager.LOGGER.debug("Not having sound event for: {}", identifier5);
                }
            }
        }
        this.soundSystem.reloadSounds();
    }
    
    @Nullable
    protected static Map<String, SoundEntry> readSounds(final InputStream inputStream) {
        try {
            return JsonHelper.<Map<String, SoundEntry>>deserialize(SoundManager.GSON, new InputStreamReader(inputStream, StandardCharsets.UTF_8), SoundManager.TYPE);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private static boolean isSoundResourcePresent(final Sound sound, final Identifier identifier, final ResourceManager resourceManager) {
        final Identifier identifier2 = sound.getLocation();
        if (!resourceManager.containsResource(identifier2)) {
            SoundManager.LOGGER.warn("File {} does not exist, cannot add it to event {}", identifier2, identifier);
            return false;
        }
        return true;
    }
    
    @Nullable
    public WeightedSoundSet get(final Identifier identifier) {
        return this.sounds.get(identifier);
    }
    
    public Collection<Identifier> getKeys() {
        return this.sounds.keySet();
    }
    
    public void play(final SoundInstance soundInstance) {
        this.soundSystem.play(soundInstance);
    }
    
    public void play(final SoundInstance sound, final int delay) {
        this.soundSystem.play(sound, delay);
    }
    
    public void updateListenerPosition(final Camera camera) {
        this.soundSystem.updateListenerPosition(camera);
    }
    
    public void pauseAll() {
        this.soundSystem.pauseAll();
    }
    
    public void stopAll() {
        this.soundSystem.stopAll();
    }
    
    public void close() {
        this.soundSystem.stop();
    }
    
    public void tick(final boolean boolean1) {
        this.soundSystem.tick(boolean1);
    }
    
    public void resumeAll() {
        this.soundSystem.resumeAll();
    }
    
    public void updateSoundVolume(final SoundCategory category, final float volume) {
        if (category == SoundCategory.a && volume <= 0.0f) {
            this.stopAll();
        }
        this.soundSystem.updateSoundVolume(category, volume);
    }
    
    public void stop(final SoundInstance soundInstance) {
        this.soundSystem.stop(soundInstance);
    }
    
    public boolean isPlaying(final SoundInstance soundInstance) {
        return this.soundSystem.isPlaying(soundInstance);
    }
    
    public void registerListener(final ListenerSoundInstance listenerSoundInstance) {
        this.soundSystem.registerListener(listenerSoundInstance);
    }
    
    public void unregisterListener(final ListenerSoundInstance listenerSoundInstance) {
        this.soundSystem.unregisterListener(listenerSoundInstance);
    }
    
    public void stopSounds(@Nullable final Identifier identifier, @Nullable final SoundCategory soundCategory) {
        this.soundSystem.stopSounds(identifier, soundCategory);
    }
    
    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }
    
    static {
        SOUND_MISSING = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.RegistrationType.FILE, false, false, 16);
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer()).registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer()).create();
        TYPE = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class, SoundEntry.class };
            }
            
            @Override
            public Type getRawType() {
                return Map.class;
            }
            
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    @Environment(EnvType.CLIENT)
    public static class SoundList
    {
        private final Map<Identifier, WeightedSoundSet> loadedSounds;
        
        protected SoundList() {
            this.loadedSounds = Maps.newHashMap();
        }
        
        private void register(final Identifier id, final SoundEntry entry, final ResourceManager resourceManager) {
            WeightedSoundSet weightedSoundSet4 = this.loadedSounds.get(id);
            final boolean boolean5 = weightedSoundSet4 == null;
            if (boolean5 || entry.canReplace()) {
                if (!boolean5) {
                    SoundManager.LOGGER.debug("Replaced sound event location {}", id);
                }
                weightedSoundSet4 = new WeightedSoundSet(id, entry.getSubtitle());
                this.loadedSounds.put(id, weightedSoundSet4);
            }
            for (final Sound sound7 : entry.getSounds()) {
                final Identifier identifier8 = sound7.getIdentifier();
                SoundContainer<Sound> soundContainer9 = null;
                switch (sound7.getRegistrationType()) {
                    case FILE: {
                        if (!isSoundResourcePresent(sound7, id, resourceManager)) {
                            continue;
                        }
                        soundContainer9 = sound7;
                        break;
                    }
                    case EVENT: {
                        soundContainer9 = new SoundContainer<Sound>() {
                            @Override
                            public int getWeight() {
                                final WeightedSoundSet weightedSoundSet1 = SoundList.this.loadedSounds.get(identifier8);
                                return (weightedSoundSet1 == null) ? 0 : weightedSoundSet1.getWeight();
                            }
                            
                            @Override
                            public Sound getSound() {
                                final WeightedSoundSet weightedSoundSet1 = SoundList.this.loadedSounds.get(identifier8);
                                if (weightedSoundSet1 == null) {
                                    return SoundManager.SOUND_MISSING;
                                }
                                final Sound sound2 = weightedSoundSet1.getSound();
                                return new Sound(sound2.getIdentifier().toString(), sound2.getVolume() * sound7.getVolume(), sound2.getPitch() * sound7.getPitch(), sound7.getWeight(), Sound.RegistrationType.FILE, sound2.isStreamed() || sound7.isStreamed(), sound2.isPreloaded(), sound2.getAttenuation());
                            }
                            
                            @Override
                            public void preload(final SoundSystem soundSystem) {
                                final WeightedSoundSet weightedSoundSet2 = SoundList.this.loadedSounds.get(identifier8);
                                if (weightedSoundSet2 == null) {
                                    return;
                                }
                                weightedSoundSet2.preload(soundSystem);
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound7.getRegistrationType());
                    }
                }
                weightedSoundSet4.add(soundContainer9);
            }
        }
        
        public void addTo(final Map<Identifier, WeightedSoundSet> map, final SoundSystem soundSystem) {
            map.clear();
            for (final Map.Entry<Identifier, WeightedSoundSet> entry4 : this.loadedSounds.entrySet()) {
                map.put(entry4.getKey(), entry4.getValue());
                entry4.getValue().preload(soundSystem);
            }
        }
    }
}
