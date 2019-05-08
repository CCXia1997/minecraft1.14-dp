package net.minecraft.client.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlfwUtil
{
    public static void a() {
        MemoryUtil.memSet(0L, 0, 1L);
    }
    
    public static double getTime() {
        return GLFW.glfwGetTime();
    }
}
