package com.mojang.blaze3d.platform;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import java.io.File;
import java.nio.Buffer;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.Channels;
import org.lwjgl.system.MemoryUtil;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.nio.IntBuffer;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureUtil
{
    private static final Logger LOGGER;
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;
    
    public static int generateTextureId() {
        return GlStateManager.genTexture();
    }
    
    public static void releaseTextureId(final int integer) {
        GlStateManager.deleteTexture(integer);
    }
    
    public static void prepareImage(final int texture, final int width, final int height) {
        prepareImage(NativeImage.b.a, texture, 0, width, height);
    }
    
    public static void prepareImage(final NativeImage.b pixelFormat, final int texture, final int width, final int height) {
        prepareImage(pixelFormat, texture, 0, width, height);
    }
    
    public static void prepareImage(final int texture, final int levels, final int width, final int height) {
        prepareImage(NativeImage.b.a, texture, levels, width, height);
    }
    
    public static void prepareImage(final NativeImage.b pixelFormat, final int texture, final int levels, final int width, final int height) {
        bind(texture);
        if (levels >= 0) {
            GlStateManager.texParameter(3553, 33085, levels);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, levels);
            GlStateManager.texParameter(3553, 34049, 0.0f);
        }
        for (int integer6 = 0; integer6 <= levels; ++integer6) {
            GlStateManager.texImage2D(3553, integer6, pixelFormat.a(), width >> integer6, height >> integer6, 0, 6408, 5121, null);
        }
    }
    
    private static void bind(final int texture) {
        GlStateManager.bindTexture(texture);
    }
    
    public static ByteBuffer readResource(final InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer2;
        if (inputStream instanceof FileInputStream) {
            final FileInputStream fileInputStream3 = (FileInputStream)inputStream;
            final FileChannel fileChannel4 = fileInputStream3.getChannel();
            byteBuffer2 = MemoryUtil.memAlloc((int)fileChannel4.size() + 1);
            while (fileChannel4.read(byteBuffer2) != -1) {}
        }
        else {
            byteBuffer2 = MemoryUtil.memAlloc(8192);
            for (ReadableByteChannel readableByteChannel3 = Channels.newChannel(inputStream); readableByteChannel3.read(byteBuffer2) != -1; byteBuffer2 = MemoryUtil.memRealloc(byteBuffer2, byteBuffer2.capacity() * 2)) {
                if (byteBuffer2.remaining() == 0) {}
            }
        }
        return byteBuffer2;
    }
    
    public static String readResourceAsString(final InputStream inputStream) {
        ByteBuffer byteBuffer2 = null;
        try {
            byteBuffer2 = readResource(inputStream);
            final int integer3 = byteBuffer2.position();
            byteBuffer2.rewind();
            return MemoryUtil.memASCII(byteBuffer2, integer3);
        }
        catch (IOException ex) {}
        finally {
            if (byteBuffer2 != null) {
                MemoryUtil.memFree((Buffer)byteBuffer2);
            }
        }
        return null;
    }
    
    public static void writeAsPNG(final String string, final int integer2, final int integer3, final int integer4, final int integer5) {
        bind(integer2);
        for (int integer6 = 0; integer6 <= integer3; ++integer6) {
            final String string2 = string + "_" + integer6 + ".png";
            final int integer7 = integer4 >> integer6;
            final int integer8 = integer5 >> integer6;
            try (final NativeImage nativeImage10 = new NativeImage(integer7, integer8, false)) {
                nativeImage10.loadFromTextureImage(integer6, false);
                nativeImage10.writeFile(string2);
                TextureUtil.LOGGER.debug("Exported png to: {}", new File(string2).getAbsolutePath());
            }
            catch (IOException iOException10) {
                TextureUtil.LOGGER.debug("Unable to write: ", (Throwable)iOException10);
            }
        }
    }
    
    public static void initTexture(final IntBuffer intBuffer, final int integer2, final int integer3) {
        GL11.glPixelStorei(3312, 0);
        GL11.glPixelStorei(3313, 0);
        GL11.glPixelStorei(3314, 0);
        GL11.glPixelStorei(3315, 0);
        GL11.glPixelStorei(3316, 0);
        GL11.glPixelStorei(3317, 4);
        GL11.glTexImage2D(3553, 0, 6408, integer2, integer3, 0, 32993, 33639, intBuffer);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10241, 9729);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
