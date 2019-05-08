package net.minecraft.client.util;

import java.nio.FloatBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlAllocationUtils
{
    public static synchronized int genLists(final int integer) {
        final int integer2 = GlStateManager.genLists(integer);
        if (integer2 == 0) {
            final int integer3 = GlStateManager.getError();
            String string4 = "No error code reported";
            if (integer3 != 0) {
                string4 = GLX.getErrorString(integer3);
            }
            throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + integer + ", GL error (" + integer3 + "): " + string4);
        }
        return integer2;
    }
    
    public static synchronized void deleteLists(final int integer1, final int integer2) {
        GlStateManager.deleteLists(integer1, integer2);
    }
    
    public static synchronized void deleteSingletonList(final int integer) {
        deleteLists(integer, 1);
    }
    
    public static synchronized ByteBuffer allocateByteBuffer(final int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
    
    public static FloatBuffer allocateFloatBuffer(final int size) {
        return allocateByteBuffer(size << 2).asFloatBuffer();
    }
}
