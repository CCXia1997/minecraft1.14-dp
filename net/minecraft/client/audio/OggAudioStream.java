package net.minecraft.client.audio;

import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import java.nio.FloatBuffer;
import org.lwjgl.PointerBuffer;
import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbis;
import java.io.IOException;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OggAudioStream implements AudioStream
{
    private long pointer;
    private final AudioFormat format;
    private final InputStream inputStream;
    private ByteBuffer buffer;
    
    public OggAudioStream(final InputStream inputStream) throws IOException {
        this.buffer = MemoryUtil.memAlloc(8192);
        this.inputStream = inputStream;
        this.buffer.limit(0);
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer4 = memoryStack2.mallocInt(1);
            final IntBuffer intBuffer5 = memoryStack2.mallocInt(1);
            while (this.pointer == 0L) {
                if (!this.readHeader()) {
                    throw new IOException("Failed to find Ogg header");
                }
                final int integer6 = this.buffer.position();
                this.buffer.position(0);
                this.pointer = STBVorbis.stb_vorbis_open_pushdata(this.buffer, intBuffer4, intBuffer5, (STBVorbisAlloc)null);
                this.buffer.position(integer6);
                final int integer7 = intBuffer5.get(0);
                if (integer7 == 1) {
                    this.increaseBufferSize();
                }
                else {
                    if (integer7 != 0) {
                        throw new IOException("Failed to read Ogg file " + integer7);
                    }
                    continue;
                }
            }
            this.buffer.position(this.buffer.position() + intBuffer4.get(0));
            final STBVorbisInfo sTBVorbisInfo6 = STBVorbisInfo.mallocStack(memoryStack2);
            STBVorbis.stb_vorbis_get_info(this.pointer, sTBVorbisInfo6);
            this.format = new AudioFormat((float)sTBVorbisInfo6.sample_rate(), 16, sTBVorbisInfo6.channels(), true, false);
        }
    }
    
    private boolean readHeader() throws IOException {
        final int integer1 = this.buffer.limit();
        final int integer2 = this.buffer.capacity() - integer1;
        if (integer2 == 0) {
            return true;
        }
        final byte[] arr3 = new byte[integer2];
        final int integer3 = this.inputStream.read(arr3);
        if (integer3 == -1) {
            return false;
        }
        final int integer4 = this.buffer.position();
        this.buffer.limit(integer1 + integer3);
        this.buffer.position(integer1);
        this.buffer.put(arr3, 0, integer3);
        this.buffer.position(integer4);
        return true;
    }
    
    private void increaseBufferSize() {
        final boolean boolean1 = this.buffer.position() == 0;
        final boolean boolean2 = this.buffer.position() == this.buffer.limit();
        if (boolean2 && !boolean1) {
            this.buffer.position(0);
            this.buffer.limit(0);
        }
        else {
            final ByteBuffer byteBuffer3 = MemoryUtil.memAlloc(boolean1 ? (2 * this.buffer.capacity()) : this.buffer.capacity());
            byteBuffer3.put(this.buffer);
            MemoryUtil.memFree((Buffer)this.buffer);
            byteBuffer3.flip();
            this.buffer = byteBuffer3;
        }
    }
    
    private boolean readOggFile(final ChannelList channelList) throws IOException {
        if (this.pointer == 0L) {
            return false;
        }
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final PointerBuffer pointerBuffer4 = memoryStack2.mallocPointer(1);
            final IntBuffer intBuffer5 = memoryStack2.mallocInt(1);
            final IntBuffer intBuffer6 = memoryStack2.mallocInt(1);
            while (true) {
                final int integer7 = STBVorbis.stb_vorbis_decode_frame_pushdata(this.pointer, this.buffer, intBuffer5, pointerBuffer4, intBuffer6);
                this.buffer.position(this.buffer.position() + integer7);
                final int integer8 = STBVorbis.stb_vorbis_get_error(this.pointer);
                if (integer8 == 1) {
                    this.increaseBufferSize();
                    if (!this.readHeader()) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (integer8 != 0) {
                        throw new IOException("Failed to read Ogg file " + integer8);
                    }
                    final int integer9 = intBuffer6.get(0);
                    if (integer9 == 0) {
                        continue;
                    }
                    final int integer10 = intBuffer5.get(0);
                    final PointerBuffer pointerBuffer5 = pointerBuffer4.getPointerBuffer(integer10);
                    if (integer10 == 1) {
                        this.readChannels(pointerBuffer5.getFloatBuffer(0, integer9), channelList);
                        return true;
                    }
                    if (integer10 == 2) {
                        this.readChannels(pointerBuffer5.getFloatBuffer(0, integer9), pointerBuffer5.getFloatBuffer(1, integer9), channelList);
                        return true;
                    }
                    throw new IllegalStateException("Invalid number of channels: " + integer10);
                }
            }
        }
    }
    
    private void readChannels(final FloatBuffer floatBuffer, final ChannelList channelList) {
        while (floatBuffer.hasRemaining()) {
            channelList.addChannel(floatBuffer.get());
        }
    }
    
    private void readChannels(final FloatBuffer floatBuffer1, final FloatBuffer floatBuffer2, final ChannelList channelList) {
        while (floatBuffer1.hasRemaining() && floatBuffer2.hasRemaining()) {
            channelList.addChannel(floatBuffer1.get());
            channelList.addChannel(floatBuffer2.get());
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.pointer != 0L) {
            STBVorbis.stb_vorbis_close(this.pointer);
            this.pointer = 0L;
        }
        MemoryUtil.memFree((Buffer)this.buffer);
        this.inputStream.close();
    }
    
    @Override
    public AudioFormat getFormat() {
        return this.format;
    }
    
    @Nullable
    @Override
    public ByteBuffer a(final int integer) throws IOException {
        final ChannelList channelList2 = new ChannelList(integer + 8192);
        while (this.readOggFile(channelList2) && channelList2.c < integer) {}
        return channelList2.getBuffer();
    }
    
    @Override
    public ByteBuffer getBuffer() throws IOException {
        final ChannelList channelList1 = new ChannelList(16384);
        while (this.readOggFile(channelList1)) {}
        return channelList1.getBuffer();
    }
    
    @Environment(EnvType.CLIENT)
    static class ChannelList
    {
        private final List<ByteBuffer> buffers;
        private final int size;
        private int c;
        private ByteBuffer buffer;
        
        public ChannelList(final int integer) {
            this.buffers = Lists.newArrayList();
            this.size = (integer + 1 & 0xFFFFFFFE);
            this.init();
        }
        
        private void init() {
            this.buffer = BufferUtils.createByteBuffer(this.size);
        }
        
        public void addChannel(final float float1) {
            if (this.buffer.remaining() == 0) {
                this.buffer.flip();
                this.buffers.add(this.buffer);
                this.init();
            }
            final int integer2 = MathHelper.clamp((int)(float1 * 32767.5f - 0.5f), -32768, 32767);
            this.buffer.putShort((short)integer2);
            this.c += 2;
        }
        
        public ByteBuffer getBuffer() {
            this.buffer.flip();
            if (this.buffers.isEmpty()) {
                return this.buffer;
            }
            final ByteBuffer byteBuffer1 = BufferUtils.createByteBuffer(this.c);
            this.buffers.forEach(byteBuffer1::put);
            byteBuffer1.put(this.buffer);
            byteBuffer1.flip();
            return byteBuffer1;
        }
    }
}
