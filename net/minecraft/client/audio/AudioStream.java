package net.minecraft.client.audio;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.Closeable;

@Environment(EnvType.CLIENT)
public interface AudioStream extends Closeable
{
    AudioFormat getFormat();
    
    ByteBuffer getBuffer() throws IOException;
    
    @Nullable
    ByteBuffer a(final int arg1) throws IOException;
}
