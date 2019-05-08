package net.minecraft.client.texture;

import org.lwjgl.stb.STBIWriteCallback;
import java.util.EnumSet;
import java.util.Base64;
import net.minecraft.client.util.UntrackMemoryUtil;
import org.lwjgl.stb.STBImageResize;
import java.nio.channels.WritableByteChannel;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.stb.STBImageWrite;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Path;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.stb.STBTTFontinfo;
import java.io.File;
import java.nio.file.FileSystems;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;
import java.nio.Buffer;
import com.mojang.blaze3d.platform.TextureUtil;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import org.lwjgl.system.MemoryUtil;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class NativeImage implements AutoCloseable
{
    private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS;
    private final Format format;
    private final int width;
    private final int height;
    private final boolean isStbImage;
    private long pointer;
    private final int sizeBytes;
    
    public NativeImage(final int integer1, final int integer2, final boolean boolean3) {
        this(Format.a, integer1, integer2, boolean3);
    }
    
    public NativeImage(final Format format, final int width, final int height, final boolean zeroFill) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.sizeBytes = width * height * format.getBytesPerPixel();
        this.isStbImage = false;
        if (zeroFill) {
            this.pointer = MemoryUtil.nmemCalloc(1L, (long)this.sizeBytes);
        }
        else {
            this.pointer = MemoryUtil.nmemAlloc((long)this.sizeBytes);
        }
    }
    
    private NativeImage(final Format format, final int width, final int height, final boolean boolean4, final long pointer) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.isStbImage = boolean4;
        this.pointer = pointer;
        this.sizeBytes = width * height * format.getBytesPerPixel();
    }
    
    @Override
    public String toString() {
        return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
    }
    
    public static NativeImage fromInputStream(final InputStream inputStream) throws IOException {
        return fromInputStream(Format.a, inputStream);
    }
    
    public static NativeImage fromInputStream(@Nullable final Format format, final InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer3 = null;
        try {
            byteBuffer3 = TextureUtil.readResource(inputStream);
            byteBuffer3.rewind();
            return fromByteBuffer(format, byteBuffer3);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer3);
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    public static NativeImage fromByteBuffer(final ByteBuffer byteBuffer) throws IOException {
        return fromByteBuffer(Format.a, byteBuffer);
    }
    
    public static NativeImage fromByteBuffer(@Nullable final Format format, final ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.i()) {
            throw new UnsupportedOperationException("Don't know how to read format " + format);
        }
        if (MemoryUtil.memAddress(byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (final MemoryStack memoryStack3 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer5 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer6 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer7 = memoryStack3.mallocInt(1);
            final ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer5, intBuffer6, intBuffer7, (format == null) ? 0 : format.bytesPerPixel);
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            return new NativeImage((format == null) ? b(intBuffer7.get(0)) : format, intBuffer5.get(0), intBuffer6.get(0), true, MemoryUtil.memAddress(byteBuffer2));
        }
    }
    
    private static void setTextureClamp(final boolean clamp) {
        if (clamp) {
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
        }
        else {
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
        }
    }
    
    private static void setTextureFilter(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            GlStateManager.texParameter(3553, 10241, boolean2 ? 9987 : 9729);
            GlStateManager.texParameter(3553, 10240, 9729);
        }
        else {
            GlStateManager.texParameter(3553, 10241, boolean2 ? 9986 : 9728);
            GlStateManager.texParameter(3553, 10240, 9728);
        }
    }
    
    private void checkAllocated() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }
    
    @Override
    public void close() {
        if (this.pointer != 0L) {
            if (this.isStbImage) {
                STBImage.nstbi_image_free(this.pointer);
            }
            else {
                MemoryUtil.nmemFree(this.pointer);
            }
        }
        this.pointer = 0L;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public Format getFormat() {
        return this.format;
    }
    
    public int getPixelRGBA(final int x, final int integer2) {
        if (this.format != Format.a) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
        }
        if (x > this.width || integer2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, integer2, this.width, this.height));
        }
        this.checkAllocated();
        return MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).get(x + integer2 * this.width);
    }
    
    public void setPixelRGBA(final int x, final int y, final int integer3) {
        if (this.format != Format.a) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).put(x + y * this.width, integer3);
    }
    
    public byte getAlphaOrLuminance(final int x, final int y) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        return MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes).get((x + y * this.width) * this.format.getBytesPerPixel() + this.format.h() / 8);
    }
    
    public void blendPixel(final int integer1, final int integer2, final int integer3) {
        if (this.format != Format.a) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        final int integer4 = this.getPixelRGBA(integer1, integer2);
        final float float5 = (integer3 >> 24 & 0xFF) / 255.0f;
        final float float6 = (integer3 >> 16 & 0xFF) / 255.0f;
        final float float7 = (integer3 >> 8 & 0xFF) / 255.0f;
        final float float8 = (integer3 >> 0 & 0xFF) / 255.0f;
        final float float9 = (integer4 >> 24 & 0xFF) / 255.0f;
        final float float10 = (integer4 >> 16 & 0xFF) / 255.0f;
        final float float11 = (integer4 >> 8 & 0xFF) / 255.0f;
        final float float12 = (integer4 >> 0 & 0xFF) / 255.0f;
        final float float13 = float5;
        final float float14 = 1.0f - float5;
        float float15 = float5 * float13 + float9 * float14;
        float float16 = float6 * float13 + float10 * float14;
        float float17 = float7 * float13 + float11 * float14;
        float float18 = float8 * float13 + float12 * float14;
        if (float15 > 1.0f) {
            float15 = 1.0f;
        }
        if (float16 > 1.0f) {
            float16 = 1.0f;
        }
        if (float17 > 1.0f) {
            float17 = 1.0f;
        }
        if (float18 > 1.0f) {
            float18 = 1.0f;
        }
        final int integer5 = (int)(float15 * 255.0f);
        final int integer6 = (int)(float16 * 255.0f);
        final int integer7 = (int)(float17 * 255.0f);
        final int integer8 = (int)(float18 * 255.0f);
        this.setPixelRGBA(integer1, integer2, integer5 << 24 | integer6 << 16 | integer7 << 8 | integer8 << 0);
    }
    
    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.a) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        final int[] arr1 = new int[this.getWidth() * this.getHeight()];
        for (int integer2 = 0; integer2 < this.getHeight(); ++integer2) {
            for (int integer3 = 0; integer3 < this.getWidth(); ++integer3) {
                final int integer4 = this.getPixelRGBA(integer3, integer2);
                final int integer5 = integer4 >> 24 & 0xFF;
                final int integer6 = integer4 >> 16 & 0xFF;
                final int integer7 = integer4 >> 8 & 0xFF;
                final int integer8 = integer4 >> 0 & 0xFF;
                final int integer9 = integer5 << 24 | integer8 << 16 | integer7 << 8 | integer6;
                arr1[integer3 + integer2 * this.getWidth()] = integer9;
            }
        }
        return arr1;
    }
    
    public void upload(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        this.upload(integer1, integer2, integer3, 0, 0, this.width, this.height, boolean4);
    }
    
    public void upload(final int level, final int xOffset, final int yOffset, final int unpackSkipPixels, final int unpackSkipRows, final int width, final int height, final boolean boolean8) {
        this.upload(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, width, height, false, false, boolean8);
    }
    
    public void upload(final int level, final int xOffset, final int yOffset, final int unpackSkipPixels, final int unpackSkipRows, final int width, final int height, final boolean boolean8, final boolean clamp, final boolean boolean10) {
        this.checkAllocated();
        setTextureFilter(boolean8, boolean10);
        setTextureClamp(clamp);
        if (width == this.getWidth()) {
            GlStateManager.pixelStore(3314, 0);
        }
        else {
            GlStateManager.pixelStore(3314, this.getWidth());
        }
        GlStateManager.pixelStore(3316, unpackSkipPixels);
        GlStateManager.pixelStore(3315, unpackSkipRows);
        this.format.setUnpackAlignment();
        GlStateManager.texSubImage2D(3553, level, xOffset, yOffset, width, height, this.format.getPixelDataFormat(), 5121, this.pointer);
    }
    
    public void loadFromTextureImage(final int integer, final boolean boolean2) {
        this.checkAllocated();
        this.format.setPackAlignment();
        GlStateManager.getTexImage(3553, integer, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (boolean2 && this.format.e()) {
            for (int integer2 = 0; integer2 < this.getHeight(); ++integer2) {
                for (int integer3 = 0; integer3 < this.getWidth(); ++integer3) {
                    this.setPixelRGBA(integer3, integer2, this.getPixelRGBA(integer3, integer2) | 255 << this.format.f());
                }
            }
        }
    }
    
    public void a(final boolean boolean1) {
        this.checkAllocated();
        this.format.setPackAlignment();
        if (boolean1) {
            GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
        }
        GlStateManager.readPixels(0, 0, this.width, this.height, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (boolean1) {
            GlStateManager.pixelTransfer(3357, 0.0f);
        }
    }
    
    public void writeFile(final String string) throws IOException {
        this.writeFile(FileSystems.getDefault().getPath(string));
    }
    
    public void writeFile(final File file) throws IOException {
        this.writeFile(file.toPath());
    }
    
    public void makeGlyphBitmapSubpixel(final STBTTFontinfo fontInfo, final int glyphIndex, final int width, final int height, final float scaleX, final float scaleY, final float shiftX, final float shiftY, final int integer9, final int glyph) {
        if (integer9 < 0 || integer9 + width > this.getWidth() || glyph < 0 || glyph + height > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", integer9, glyph, width, height, this.getWidth(), this.getHeight()));
        }
        if (this.format.getBytesPerPixel() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(fontInfo.address(), this.pointer + integer9 + glyph * this.getWidth(), width, height, this.getWidth(), scaleX, scaleY, shiftX, shiftY, glyphIndex);
    }
    
    public void writeFile(final Path path) throws IOException {
        if (!this.format.i()) {
            throw new UnsupportedOperationException("Don't know how to write format " + this.format);
        }
        this.checkAllocated();
        try (final WritableByteChannel writableByteChannel2 = Files.newByteChannel(path, NativeImage.WRITE_TO_FILE_OPEN_OPTIONS, new FileAttribute[0])) {
            final WriteCallback writeCallback4 = new WriteCallback(writableByteChannel2);
            try {
                if (!STBImageWrite.stbi_write_png_to_func((STBIWriteCallbackI)writeCallback4, 0L, this.getWidth(), this.getHeight(), this.format.getBytesPerPixel(), MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes), 0)) {
                    throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
                }
            }
            finally {
                writeCallback4.free();
            }
            writeCallback4.throwStoredException();
        }
    }
    
    public void copyFrom(final NativeImage image) {
        if (image.getFormat() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        final int integer2 = this.format.getBytesPerPixel();
        this.checkAllocated();
        image.checkAllocated();
        if (this.width == image.width) {
            MemoryUtil.memCopy(image.pointer, this.pointer, (long)Math.min(this.sizeBytes, image.sizeBytes));
        }
        else {
            final int integer3 = Math.min(this.getWidth(), image.getWidth());
            for (int integer4 = Math.min(this.getHeight(), image.getHeight()), integer5 = 0; integer5 < integer4; ++integer5) {
                final int integer6 = integer5 * image.getWidth() * integer2;
                final int integer7 = integer5 * this.getWidth() * integer2;
                MemoryUtil.memCopy(image.pointer + integer6, this.pointer + integer7, (long)integer3);
            }
        }
    }
    
    public void fillRGBA(final int x, final int y, final int width, final int height, final int integer5) {
        for (int integer6 = y; integer6 < y + height; ++integer6) {
            for (int integer7 = x; integer7 < x + width; ++integer7) {
                this.setPixelRGBA(integer7, integer6, integer5);
            }
        }
    }
    
    public void a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final boolean boolean7, final boolean boolean8) {
        for (int integer7 = 0; integer7 < integer6; ++integer7) {
            for (int integer8 = 0; integer8 < integer5; ++integer8) {
                final int integer9 = boolean7 ? (integer5 - 1 - integer8) : integer8;
                final int integer10 = boolean8 ? (integer6 - 1 - integer7) : integer7;
                final int integer11 = this.getPixelRGBA(integer1 + integer8, integer2 + integer7);
                this.setPixelRGBA(integer1 + integer3 + integer9, integer2 + integer4 + integer10, integer11);
            }
        }
    }
    
    public void e() {
        this.checkAllocated();
        try (final MemoryStack memoryStack1 = MemoryStack.stackPush()) {
            final int integer3 = this.format.getBytesPerPixel();
            final int integer4 = this.getWidth() * integer3;
            final long long5 = memoryStack1.nmalloc(integer4);
            for (int integer5 = 0; integer5 < this.getHeight() / 2; ++integer5) {
                final int integer6 = integer5 * this.getWidth() * integer3;
                final int integer7 = (this.getHeight() - 1 - integer5) * this.getWidth() * integer3;
                MemoryUtil.memCopy(this.pointer + integer6, long5, (long)integer4);
                MemoryUtil.memCopy(this.pointer + integer7, this.pointer + integer6, (long)integer4);
                MemoryUtil.memCopy(long5, this.pointer + integer7, (long)integer4);
            }
        }
    }
    
    public void resizeSubRectTo(final int integer1, final int integer2, final int integer3, final int integer4, final NativeImage nativeImage) {
        this.checkAllocated();
        if (nativeImage.getFormat() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        final int integer5 = this.format.getBytesPerPixel();
        STBImageResize.nstbir_resize_uint8(this.pointer + (integer1 + integer2 * this.getWidth()) * integer5, integer3, integer4, this.getWidth() * integer5, nativeImage.pointer, nativeImage.getWidth(), nativeImage.getHeight(), 0, integer5);
    }
    
    public void untrack() {
        UntrackMemoryUtil.untrack(this.pointer);
    }
    
    public static NativeImage fromBase64(final String string) throws IOException {
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final ByteBuffer byteBuffer4 = memoryStack2.UTF8((CharSequence)string.replaceAll("\n", ""), false);
            final ByteBuffer byteBuffer5 = Base64.getDecoder().decode(byteBuffer4);
            final ByteBuffer byteBuffer6 = memoryStack2.malloc(byteBuffer5.remaining());
            byteBuffer6.put(byteBuffer5);
            byteBuffer6.rewind();
            return fromByteBuffer(byteBuffer6);
        }
    }
    
    static {
        WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    @Environment(EnvType.CLIENT)
    static class WriteCallback extends STBIWriteCallback
    {
        private final WritableByteChannel channel;
        private IOException exception;
        
        private WriteCallback(final WritableByteChannel writableByteChannel) {
            this.channel = writableByteChannel;
        }
        
        public void invoke(final long long1, final long long3, final int integer5) {
            final ByteBuffer byteBuffer6 = getData(long3, integer5);
            try {
                this.channel.write(byteBuffer6);
            }
            catch (IOException iOException7) {
                this.exception = iOException7;
            }
        }
        
        public void throwStoredException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum b
    {
        a(6408), 
        b(6407), 
        c(6410), 
        d(6409), 
        e(32841);
        
        private final int f;
        
        private b(final int integer1) {
            this.f = integer1;
        }
        
        public int a() {
            return this.f;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum Format
    {
        a(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true), 
        b(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true), 
        c(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true), 
        d(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);
        
        private final int bytesPerPixel;
        private final int pixelDataFormat;
        private final boolean g;
        private final boolean h;
        private final boolean i;
        private final boolean j;
        private final boolean k;
        private final int l;
        private final int m;
        private final int n;
        private final int o;
        private final int p;
        private final boolean q;
        
        private Format(final int integer1, final int integer2, final boolean boolean3, final boolean boolean4, final boolean boolean5, final boolean boolean6, final boolean boolean7, final int integer8, final int integer9, final int integer10, final int integer11, final int integer12, final boolean boolean13) {
            this.bytesPerPixel = integer1;
            this.pixelDataFormat = integer2;
            this.g = boolean3;
            this.h = boolean4;
            this.i = boolean5;
            this.j = boolean6;
            this.k = boolean7;
            this.l = integer8;
            this.m = integer9;
            this.n = integer10;
            this.o = integer11;
            this.p = integer12;
            this.q = boolean13;
        }
        
        public int getBytesPerPixel() {
            return this.bytesPerPixel;
        }
        
        public void setPackAlignment() {
            GlStateManager.pixelStore(3333, this.getBytesPerPixel());
        }
        
        public void setUnpackAlignment() {
            GlStateManager.pixelStore(3317, this.getBytesPerPixel());
        }
        
        public int getPixelDataFormat() {
            return this.pixelDataFormat;
        }
        
        public boolean e() {
            return this.k;
        }
        
        public int f() {
            return this.p;
        }
        
        public boolean hasLuminanceOrAlpha() {
            return this.j || this.k;
        }
        
        public int h() {
            return this.j ? this.o : this.p;
        }
        
        public boolean i() {
            return this.q;
        }
        
        private static Format b(final int integer) {
            switch (integer) {
                case 1: {
                    return Format.d;
                }
                case 2: {
                    return Format.c;
                }
                case 3: {
                    return Format.b;
                }
                default: {
                    return Format.a;
                }
            }
        }
    }
}
