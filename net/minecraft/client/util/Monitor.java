package net.minecraft.client.util;

import java.util.Iterator;
import java.util.Optional;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Monitor
{
    private final MonitorTracker monitorTracker;
    private final long handle;
    private final List<VideoMode> videoModes;
    private VideoMode currentVideoMode;
    private int x;
    private int y;
    
    public Monitor(final MonitorTracker monitorTracker, final long long2) {
        this.monitorTracker = monitorTracker;
        this.handle = long2;
        this.videoModes = Lists.newArrayList();
        this.populateVideoModes();
    }
    
    public void populateVideoModes() {
        this.videoModes.clear();
        final GLFWVidMode.Buffer buffer1 = GLFW.glfwGetVideoModes(this.handle);
        for (int integer2 = 0; integer2 < buffer1.limit(); ++integer2) {
            buffer1.position(integer2);
            final VideoMode videoMode3 = new VideoMode(buffer1);
            if (videoMode3.getRedBits() >= 8 && videoMode3.getGreenBits() >= 8 && videoMode3.getBlueBits() >= 8) {
                this.videoModes.add(videoMode3);
            }
        }
        final int[] arr2 = { 0 };
        final int[] arr3 = { 0 };
        GLFW.glfwGetMonitorPos(this.handle, arr2, arr3);
        this.x = arr2[0];
        this.y = arr3[0];
        final GLFWVidMode gLFWVidMode4 = GLFW.glfwGetVideoMode(this.handle);
        this.currentVideoMode = new VideoMode(gLFWVidMode4);
    }
    
    public VideoMode findClosestVideoMode(final Optional<VideoMode> optional) {
        if (optional.isPresent()) {
            final VideoMode videoMode2 = optional.get();
            for (final VideoMode videoMode3 : Lists.<VideoMode>reverse(this.videoModes)) {
                if (videoMode3.equals(videoMode2)) {
                    return videoMode3;
                }
            }
        }
        return this.getCurrentVideoMode();
    }
    
    public int findClosestVideoModeIndex(final Optional<VideoMode> optional) {
        if (optional.isPresent()) {
            final VideoMode videoMode2 = optional.get();
            for (int integer3 = this.videoModes.size() - 1; integer3 >= 0; --integer3) {
                if (videoMode2.equals(this.videoModes.get(integer3))) {
                    return integer3;
                }
            }
        }
        return this.videoModes.indexOf(this.getCurrentVideoMode());
    }
    
    public VideoMode getCurrentVideoMode() {
        return this.currentVideoMode;
    }
    
    public int getViewportX() {
        return this.x;
    }
    
    public int getViewportY() {
        return this.y;
    }
    
    public VideoMode getVideoMode(final int integer) {
        return this.videoModes.get(integer);
    }
    
    public int getVideoModeCount() {
        return this.videoModes.size();
    }
    
    public long getHandle() {
        return this.handle;
    }
    
    @Override
    public String toString() {
        return String.format("Monitor[%s %sx%s %s]", this.handle, this.x, this.y, this.currentVideoMode);
    }
}
