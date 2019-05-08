package net.minecraft.client.util;

import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import java.util.Iterator;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MonitorTracker
{
    private final Map<Long, Monitor> pointerToMonitorMap;
    private final Map<Long, Window> pointerToWindowMap;
    private final Map<Window, Monitor> windowToMonitorMap;
    private final MonitorFactory monitorFactory;
    
    public MonitorTracker(final MonitorFactory monitorFactory) {
        this.pointerToMonitorMap = Maps.newHashMap();
        this.pointerToWindowMap = Maps.newHashMap();
        this.windowToMonitorMap = Maps.newHashMap();
        this.monitorFactory = monitorFactory;
        GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
        final PointerBuffer pointerBuffer2 = GLFW.glfwGetMonitors();
        for (int integer3 = 0; integer3 < pointerBuffer2.limit(); ++integer3) {
            final long long4 = pointerBuffer2.get(integer3);
            this.pointerToMonitorMap.put(long4, monitorFactory.createMonitor(long4));
        }
    }
    
    private void handleMonitorEvent(final long monitor, final int event) {
        if (event == 262145) {
            this.pointerToMonitorMap.put(monitor, this.monitorFactory.createMonitor(monitor));
        }
        else if (event == 262146) {
            this.pointerToMonitorMap.remove(monitor);
        }
    }
    
    public Monitor getMonitor(final long pointer) {
        return this.pointerToMonitorMap.get(pointer);
    }
    
    public Monitor getMonitor(final Window window) {
        final long long2 = GLFW.glfwGetWindowMonitor(window.getHandle());
        if (long2 != 0L) {
            return this.getMonitor(long2);
        }
        Monitor monitor4 = this.pointerToMonitorMap.values().iterator().next();
        int integer5 = -1;
        final int integer6 = window.getPositionX();
        final int integer7 = integer6 + window.getWidth();
        final int integer8 = window.getPositionY();
        final int integer9 = integer8 + window.getHeight();
        for (final Monitor monitor5 : this.pointerToMonitorMap.values()) {
            final int integer10 = monitor5.getViewportX();
            final int integer11 = integer10 + monitor5.getCurrentVideoMode().getWidth();
            final int integer12 = monitor5.getViewportY();
            final int integer13 = integer12 + monitor5.getCurrentVideoMode().getHeight();
            final int integer14 = clamp(integer6, integer10, integer11);
            final int integer15 = clamp(integer7, integer10, integer11);
            final int integer16 = clamp(integer8, integer12, integer13);
            final int integer17 = clamp(integer9, integer12, integer13);
            final int integer18 = Math.max(0, integer15 - integer14);
            final int integer19 = Math.max(0, integer17 - integer16);
            final int integer20 = integer18 * integer19;
            if (integer20 > integer5) {
                monitor4 = monitor5;
                integer5 = integer20;
            }
        }
        if (monitor4 != this.windowToMonitorMap.get(window)) {
            this.windowToMonitorMap.put(window, monitor4);
        }
        return monitor4;
    }
    
    public static int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public void stop() {
        final GLFWMonitorCallback gLFWMonitorCallback1 = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
        if (gLFWMonitorCallback1 != null) {
            gLFWMonitorCallback1.free();
        }
    }
}
