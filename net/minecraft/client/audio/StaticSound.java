package net.minecraft.client.audio;

import org.lwjgl.openal.AL10;
import java.util.OptionalInt;
import javax.sound.sampled.AudioFormat;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StaticSound
{
    @Nullable
    private ByteBuffer sample;
    private final AudioFormat format;
    private boolean hasBuffer;
    private int streamBufferPointer;
    
    public StaticSound(final ByteBuffer sample, final AudioFormat format) {
        this.sample = sample;
        this.format = format;
    }
    
    OptionalInt getStreamBufferPointer() {
        if (!this.hasBuffer) {
            if (this.sample == null) {
                return OptionalInt.empty();
            }
            final int integer1 = AlUtil.getFormatId(this.format);
            final int[] arr2 = { 0 };
            AL10.alGenBuffers(arr2);
            if (AlUtil.checkErrors("Creating buffer")) {
                return OptionalInt.empty();
            }
            AL10.alBufferData(arr2[0], integer1, this.sample, (int)this.format.getSampleRate());
            if (AlUtil.checkErrors("Assigning buffer data")) {
                return OptionalInt.empty();
            }
            this.streamBufferPointer = arr2[0];
            this.hasBuffer = true;
            this.sample = null;
        }
        return OptionalInt.of(this.streamBufferPointer);
    }
    
    public void close() {
        if (this.hasBuffer) {
            AL10.alDeleteBuffers(new int[] { this.streamBufferPointer });
            if (AlUtil.checkErrors("Deleting stream buffers")) {
                return;
            }
        }
        this.hasBuffer = false;
    }
    
    public OptionalInt takeStreamBufferPointer() {
        final OptionalInt optionalInt1 = this.getStreamBufferPointer();
        this.hasBuffer = false;
        return optionalInt1;
    }
}
