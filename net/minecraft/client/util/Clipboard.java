package net.minecraft.client.util;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import net.minecraft.SharedConstants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Clipboard
{
    private final ByteBuffer clipboardBuffer;
    
    public Clipboard() {
        this.clipboardBuffer = ByteBuffer.allocateDirect(1024);
    }
    
    public String getClipboard(final long window, final GLFWErrorCallbackI gLFWErrorCallbackI3) {
        final GLFWErrorCallback gLFWErrorCallback4 = GLFW.glfwSetErrorCallback(gLFWErrorCallbackI3);
        String string5 = GLFW.glfwGetClipboardString(window);
        string5 = ((string5 != null) ? SharedConstants.stripSupplementaryChars(string5) : "");
        final GLFWErrorCallback gLFWErrorCallback5 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback4);
        if (gLFWErrorCallback5 != null) {
            gLFWErrorCallback5.free();
        }
        return string5;
    }
    
    private void setClipboard(final long window, final ByteBuffer byteBuffer, final String string4) {
        MemoryUtil.memUTF8((CharSequence)string4, true, byteBuffer);
        GLFW.glfwSetClipboardString(window, byteBuffer);
    }
    
    public void setClipboard(final long window, final String string3) {
        final int integer4 = MemoryUtil.memLengthUTF8((CharSequence)string3, true);
        if (integer4 < this.clipboardBuffer.capacity()) {
            this.setClipboard(window, this.clipboardBuffer, string3);
            this.clipboardBuffer.clear();
        }
        else {
            final ByteBuffer byteBuffer5 = ByteBuffer.allocateDirect(integer4);
            this.setClipboard(window, byteBuffer5, string3);
        }
    }
}
