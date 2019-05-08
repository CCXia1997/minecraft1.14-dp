package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.openal.ALC10;
import java.nio.IntBuffer;
import org.lwjgl.openal.ALC;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundEngine
{
    private static final Logger LOGGER;
    private long devicePointer;
    private long contextPointer;
    private static final SourceSet EMPTY_SOURCE_SET;
    private SourceSet streamingSources;
    private SourceSet staticSources;
    private final Listener listener;
    
    public SoundEngine() {
        this.streamingSources = SoundEngine.EMPTY_SOURCE_SET;
        this.staticSources = SoundEngine.EMPTY_SOURCE_SET;
        this.listener = new Listener();
    }
    
    public void init() {
        this.devicePointer = openDevice();
        final ALCCapabilities aLCCapabilities1 = ALC.createCapabilities(this.devicePointer);
        if (AlUtil.checkAlcErrors(this.devicePointer, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!aLCCapabilities1.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        ALC10.alcMakeContextCurrent(this.contextPointer = ALC10.alcCreateContext(this.devicePointer, (IntBuffer)null));
        final int integer2 = this.e();
        final int integer3 = MathHelper.clamp((int)MathHelper.sqrt((float)integer2), 2, 8);
        final int integer4 = MathHelper.clamp(integer2 - integer3, 8, 255);
        this.streamingSources = new SourceSetImpl(integer4);
        this.staticSources = new SourceSetImpl(integer3);
        final ALCapabilities aLCapabilities5 = AL.createCapabilities(aLCCapabilities1);
        AlUtil.checkErrors("Initialization");
        if (!aLCapabilities5.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable(512);
        if (!aLCapabilities5.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        AlUtil.checkErrors("Enable per-source distance models");
        SoundEngine.LOGGER.info("OpenAL initialized.");
    }
    
    private int e() {
        try (final MemoryStack memoryStack1 = MemoryStack.stackPush()) {
            final int integer3 = ALC10.alcGetInteger(this.devicePointer, 4098);
            if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            final IntBuffer intBuffer4 = memoryStack1.mallocInt(integer3);
            ALC10.alcGetIntegerv(this.devicePointer, 4099, intBuffer4);
            if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int integer4 = 0;
            while (integer4 < integer3) {
                final int integer5 = intBuffer4.get(integer4++);
                if (integer5 == 0) {
                    break;
                }
                final int integer6 = intBuffer4.get(integer4++);
                if (integer5 == 4112) {
                    return integer6;
                }
            }
        }
        return 30;
    }
    
    private static long openDevice() {
        for (int integer1 = 0; integer1 < 3; ++integer1) {
            final long long2 = ALC10.alcOpenDevice((ByteBuffer)null);
            if (long2 != 0L && !AlUtil.checkAlcErrors(long2, "Open device")) {
                return long2;
            }
        }
        throw new IllegalStateException("Failed to open OpenAL device");
    }
    
    public void close() {
        this.streamingSources.close();
        this.staticSources.close();
        ALC10.alcDestroyContext(this.contextPointer);
        if (this.devicePointer != 0L) {
            ALC10.alcCloseDevice(this.devicePointer);
        }
    }
    
    public Listener getListener() {
        return this.listener;
    }
    
    @Nullable
    public Source createSource(final RunMode mode) {
        return ((mode == RunMode.b) ? this.staticSources : this.streamingSources).createSource();
    }
    
    public void release(final Source source) {
        if (!this.streamingSources.release(source) && !this.staticSources.release(source)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }
    
    public String getDebugString() {
        return String.format("Sounds: %d/%d + %d/%d", this.streamingSources.getSourceCount(), this.streamingSources.getMaxSourceCount(), this.staticSources.getSourceCount(), this.staticSources.getMaxSourceCount());
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY_SOURCE_SET = new SourceSet() {
            @Nullable
            @Override
            public Source createSource() {
                return null;
            }
            
            @Override
            public boolean release(final Source source) {
                return false;
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public int getMaxSourceCount() {
                return 0;
            }
            
            @Override
            public int getSourceCount() {
                return 0;
            }
        };
    }
    
    @Environment(EnvType.CLIENT)
    public enum RunMode
    {
        a, 
        b;
    }
    
    @Environment(EnvType.CLIENT)
    static class SourceSetImpl implements SourceSet
    {
        private final int maxSourceCount;
        private final Set<Source> sources;
        
        public SourceSetImpl(final int maxSourceCount) {
            this.sources = Sets.<Source>newIdentityHashSet();
            this.maxSourceCount = maxSourceCount;
        }
        
        @Nullable
        @Override
        public Source createSource() {
            if (this.sources.size() >= this.maxSourceCount) {
                return null;
            }
            final Source source1 = Source.create();
            if (source1 != null) {
                this.sources.add(source1);
            }
            return source1;
        }
        
        @Override
        public boolean release(final Source source) {
            if (!this.sources.remove(source)) {
                return false;
            }
            source.close();
            return true;
        }
        
        @Override
        public void close() {
            this.sources.forEach(Source::close);
            this.sources.clear();
        }
        
        @Override
        public int getMaxSourceCount() {
            return this.maxSourceCount;
        }
        
        @Override
        public int getSourceCount() {
            return this.sources.size();
        }
    }
    
    @Environment(EnvType.CLIENT)
    interface SourceSet
    {
        @Nullable
        Source createSource();
        
        boolean release(final Source arg1);
        
        void close();
        
        int getMaxSourceCount();
        
        int getSourceCount();
    }
}
