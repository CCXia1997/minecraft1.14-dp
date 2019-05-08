package net.minecraft.client.util;

import java.io.EOFException;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.io.FileInputStream;
import java.nio.IntBuffer;
import java.io.IOException;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBIEOFCallbackI;
import org.lwjgl.stb.STBISkipCallbackI;
import org.lwjgl.stb.STBIReadCallbackI;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.system.MemoryStack;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PngFile
{
    public final int width;
    public final int height;
    
    public PngFile(final String string, final InputStream inputStream) throws IOException {
        try (final MemoryStack memoryStack3 = MemoryStack.stackPush();
             final a a5 = a(inputStream);
             final STBIReadCallback sTBIReadCallback7 = STBIReadCallback.create(a5::a);
             final STBISkipCallback sTBISkipCallback9 = STBISkipCallback.create(a5::a);
             final STBIEOFCallback sTBIEOFCallback11 = STBIEOFCallback.create(a5::a)) {
            final STBIIOCallbacks sTBIIOCallbacks13 = STBIIOCallbacks.mallocStack(memoryStack3);
            sTBIIOCallbacks13.read((STBIReadCallbackI)sTBIReadCallback7);
            sTBIIOCallbacks13.skip((STBISkipCallbackI)sTBISkipCallback9);
            sTBIIOCallbacks13.eof((STBIEOFCallbackI)sTBIEOFCallback11);
            final IntBuffer intBuffer14 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer15 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer16 = memoryStack3.mallocInt(1);
            if (!STBImage.stbi_info_from_callbacks(sTBIIOCallbacks13, 0L, intBuffer14, intBuffer15, intBuffer16)) {
                throw new IOException("Could not read info from the PNG file " + string + " " + STBImage.stbi_failure_reason());
            }
            this.width = intBuffer14.get(0);
            this.height = intBuffer15.get(0);
        }
    }
    
    private static a a(final InputStream inputStream) {
        if (inputStream instanceof FileInputStream) {
            return new c((SeekableByteChannel)((FileInputStream)inputStream).getChannel());
        }
        return new b(Channels.newChannel(inputStream));
    }
    
    @Environment(EnvType.CLIENT)
    abstract static class a implements AutoCloseable
    {
        protected boolean a;
        
        private a() {
        }
        
        int a(final long long1, final long long3, final int integer5) {
            try {
                return this.b(long3, integer5);
            }
            catch (IOException iOException6) {
                this.a = true;
                return 0;
            }
        }
        
        void a(final long long1, final int integer3) {
            try {
                this.a(integer3);
            }
            catch (IOException iOException4) {
                this.a = true;
            }
        }
        
        int a(final long long1) {
            return this.a ? 1 : 0;
        }
        
        protected abstract int b(final long arg1, final int arg2) throws IOException;
        
        protected abstract void a(final int arg1) throws IOException;
        
        @Override
        public abstract void close() throws IOException;
    }
    
    @Environment(EnvType.CLIENT)
    static class c extends a
    {
        private final SeekableByteChannel b;
        
        private c(final SeekableByteChannel seekableByteChannel) {
            this.b = seekableByteChannel;
        }
        
        public int b(final long long1, final int integer3) throws IOException {
            final ByteBuffer byteBuffer4 = MemoryUtil.memByteBuffer(long1, integer3);
            return this.b.read(byteBuffer4);
        }
        
        public void a(final int integer) throws IOException {
            this.b.position(this.b.position() + integer);
        }
        
        public int a(final long long1) {
            return (super.a(long1) != 0 && this.b.isOpen()) ? 1 : 0;
        }
        
        @Override
        public void close() throws IOException {
            this.b.close();
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class b extends a
    {
        private final ReadableByteChannel b;
        private long c;
        private int d;
        private int e;
        private int f;
        
        private b(final ReadableByteChannel readableByteChannel) {
            this.c = MemoryUtil.nmemAlloc(128L);
            this.d = 128;
            this.b = readableByteChannel;
        }
        
        private void b(final int integer) throws IOException {
            ByteBuffer byteBuffer2 = MemoryUtil.memByteBuffer(this.c, this.d);
            if (integer + this.f > this.d) {
                this.d = integer + this.f;
                byteBuffer2 = MemoryUtil.memRealloc(byteBuffer2, this.d);
                this.c = MemoryUtil.memAddress(byteBuffer2);
            }
            byteBuffer2.position(this.e);
            while (integer + this.f > this.e) {
                try {
                    final int integer2 = this.b.read(byteBuffer2);
                    if (integer2 == -1) {
                        break;
                    }
                    continue;
                }
                finally {
                    this.e = byteBuffer2.position();
                }
            }
        }
        
        public int b(final long long1, int integer3) throws IOException {
            this.b(integer3);
            if (integer3 + this.f > this.e) {
                integer3 = this.e - this.f;
            }
            MemoryUtil.memCopy(this.c + this.f, long1, (long)integer3);
            this.f += integer3;
            return integer3;
        }
        
        public void a(final int integer) throws IOException {
            if (integer > 0) {
                this.b(integer);
                if (integer + this.f > this.e) {
                    throw new EOFException("Can't skip past the EOF.");
                }
            }
            if (this.f + integer < 0) {
                throw new IOException("Can't seek before the beginning: " + (this.f + integer));
            }
            this.f += integer;
        }
        
        @Override
        public void close() throws IOException {
            MemoryUtil.nmemFree(this.c);
            this.b.close();
        }
    }
}
