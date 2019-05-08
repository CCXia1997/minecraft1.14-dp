package net.minecraft.client.audio;

import org.apache.logging.log4j.LogManager;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraft.util.math.Vec3d;
import java.io.IOException;
import org.lwjgl.openal.AL10;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Source
{
    private static final Logger LOGGER;
    private final int pointer;
    private AtomicBoolean playing;
    private int bufferSize;
    @Nullable
    private AudioStream stream;
    
    @Nullable
    static Source create() {
        final int[] arr1 = { 0 };
        AL10.alGenSources(arr1);
        if (AlUtil.checkErrors("Allocate new source")) {
            return null;
        }
        return new Source(arr1[0]);
    }
    
    private Source(final int pointer) {
        this.playing = new AtomicBoolean(true);
        this.bufferSize = 16384;
        this.pointer = pointer;
    }
    
    public void close() {
        if (this.playing.compareAndSet(true, false)) {
            AL10.alSourceStop(this.pointer);
            AlUtil.checkErrors("Stop");
            if (this.stream != null) {
                try {
                    this.stream.close();
                }
                catch (IOException iOException1) {
                    Source.LOGGER.error("Failed to close audio stream", (Throwable)iOException1);
                }
                this.removeProcessedBuffers();
                this.stream = null;
            }
            AL10.alDeleteSources(new int[] { this.pointer });
            AlUtil.checkErrors("Cleanup");
        }
    }
    
    public void play() {
        AL10.alSourcePlay(this.pointer);
    }
    
    private int getSourceState() {
        if (!this.playing.get()) {
            return 4116;
        }
        return AL10.alGetSourcei(this.pointer, 4112);
    }
    
    public void pause() {
        if (this.getSourceState() == 4114) {
            AL10.alSourcePause(this.pointer);
        }
    }
    
    public void resume() {
        if (this.getSourceState() == 4115) {
            AL10.alSourcePlay(this.pointer);
        }
    }
    
    public void stop() {
        if (this.playing.get()) {
            AL10.alSourceStop(this.pointer);
            AlUtil.checkErrors("Stop");
        }
    }
    
    public boolean isStopped() {
        return this.getSourceState() == 4116;
    }
    
    public void setPosition(final Vec3d vec3d) {
        AL10.alSourcefv(this.pointer, 4100, new float[] { (float)vec3d.x, (float)vec3d.y, (float)vec3d.z });
    }
    
    public void setPitch(final float float1) {
        AL10.alSourcef(this.pointer, 4099, float1);
    }
    
    public void setLooping(final boolean boolean1) {
        AL10.alSourcei(this.pointer, 4103, (int)(boolean1 ? 1 : 0));
    }
    
    public void setVolume(final float float1) {
        AL10.alSourcef(this.pointer, 4106, float1);
    }
    
    public void disableAttenuation() {
        AL10.alSourcei(this.pointer, 53248, 0);
    }
    
    public void setAttenuation(final float float1) {
        AL10.alSourcei(this.pointer, 53248, 53251);
        AL10.alSourcef(this.pointer, 4131, float1);
        AL10.alSourcef(this.pointer, 4129, 1.0f);
        AL10.alSourcef(this.pointer, 4128, 0.0f);
    }
    
    public void setRelative(final boolean boolean1) {
        AL10.alSourcei(this.pointer, 514, (int)(boolean1 ? 1 : 0));
    }
    
    public void setBuffer(final StaticSound staticSound) {
        staticSound.getStreamBufferPointer().ifPresent(integer -> AL10.alSourcei(this.pointer, 4105, integer));
    }
    
    public void setStream(final AudioStream stream) {
        this.stream = stream;
        final AudioFormat audioFormat2 = stream.getFormat();
        this.bufferSize = getBufferSize(audioFormat2, 1);
        this.a(4);
    }
    
    private static int getBufferSize(final AudioFormat format, final int time) {
        return (int)(time * format.getSampleSizeInBits() / 8.0f * format.getChannels() * format.getSampleRate());
    }
    
    private void a(final int integer) {
        if (this.stream != null) {
            try {
                for (int integer2 = 0; integer2 < integer; ++integer2) {
                    final ByteBuffer byteBuffer3 = this.stream.a(this.bufferSize);
                    if (byteBuffer3 != null) {
                        new StaticSound(byteBuffer3, this.stream.getFormat()).takeStreamBufferPointer().ifPresent(integer -> AL10.alSourceQueueBuffers(this.pointer, new int[] { integer }));
                    }
                }
            }
            catch (IOException iOException2) {
                Source.LOGGER.error("Failed to read from audio stream", (Throwable)iOException2);
            }
        }
    }
    
    public void tick() {
        if (this.stream != null) {
            final int integer1 = this.removeProcessedBuffers();
            this.a(integer1);
        }
    }
    
    private int removeProcessedBuffers() {
        final int integer1 = AL10.alGetSourcei(this.pointer, 4118);
        if (integer1 > 0) {
            final int[] arr2 = new int[integer1];
            AL10.alSourceUnqueueBuffers(this.pointer, arr2);
            AlUtil.checkErrors("Unqueue buffers");
            AL10.alDeleteBuffers(arr2);
            AlUtil.checkErrors("Remove processed buffers");
        }
        return integer1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
